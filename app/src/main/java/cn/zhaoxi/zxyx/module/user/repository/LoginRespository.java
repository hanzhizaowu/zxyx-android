package cn.zhaoxi.zxyx.module.user.repository;

import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import cn.zhaoxi.library.BuildConfig;
import cn.zhaoxi.library.util.crypt.Crypt;
import cn.zhaoxi.zxyx.common.api.UserApis;
import cn.zhaoxi.zxyx.common.config.ApiClient;
import cn.zhaoxi.zxyx.common.config.Constants;
import cn.zhaoxi.zxyx.common.config.ExceptionMsg;
import cn.zhaoxi.zxyx.common.config.RequestUrl;
import cn.zhaoxi.zxyx.common.result.RetrofitResponseData;
import cn.zhaoxi.zxyx.common.util.SPUtil;
import cn.zhaoxi.zxyx.data.dto.UserDto;
import cn.zhaoxi.zxyx.data.entity.User;
import cn.zhaoxi.zxyx.common.database.DBHelper;
import cn.zhaoxi.zxyx.common.database.UserDB;
import cn.zhaoxi.zxyx.module.ZxyxApplication;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class LoginRespository {
    private static final LoginRespository instance = new LoginRespository();
    private final MutableLiveData<Integer> loginResult = new MutableLiveData<>();

        private LoginRespository() {
        }

        public static LoginRespository getInstance() {
            return instance;
        }

        public LiveData<Integer> getLogin(String userName, String userPassword) {
            UserApis userApis = ApiClient.getRetrofit().create(UserApis.class);
            //对 发送请求 进行封装
            String userEncodePwd = Crypt.Encrypt(userPassword);
            Observable<RetrofitResponseData<UserDto>> observable = userApis.postUserLogin(userName, userEncodePwd);
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<RetrofitResponseData<UserDto>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(RetrofitResponseData<UserDto> retrofitResponse) {
                            if (retrofitResponse != null) {
                                if (ExceptionMsg.SUCCESS.getCode().equals(retrofitResponse.getCode())) {
                                    DBThread dbThread = new DBThread(retrofitResponse.getData());
                                    dbThread.start();
                                } else {
                                    loginResult.setValue(retrofitResponse.getCode());
                                }
                            } else {
                                loginResult.setValue(ExceptionMsg.FAILED.getCode());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            loginResult.setValue(ExceptionMsg.FAILED.getCode());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
            return loginResult;
        }

    // 定义一个数据库线程
    class DBThread extends Thread {
        private Object result;

        public DBThread(Object result) {
            this.result = result;
        }

        @Override
        public void run() {
            UserDto userDto = (UserDto) result;
            User user = new User(userDto);
            /*DBHelper dbHelper = ZxyxApplication.getDbHelperInstance();
            SQLiteDatabase mDB = dbHelper.openWriteLink();
            long insertResult = UserDB.insert(user, mDB);*/
            long insertResult = ZxyxApplication.getUserDataSoure().insertUser(user);
            if (BuildConfig.DEBUG) Log.i("DBThread", "result is:" + insertResult);
            if(insertResult == -1) {
                mHandler.sendEmptyMessage(ERROR);
            } else {
                SPUtil.build().putBoolean(Constants.SP_BEEN_LOGIN, true);
                SPUtil.build().putString(RequestUrl.X_APP_TOKEN, userDto.getUserToken());
                SPUtil.build().putLong(Constants.SP_USER_ID, userDto.getUserId());
                mHandler.sendEmptyMessage(SUCCESS);
            }
        }
    }

    private final static int SUCCESS = 0, ERROR = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    loginResult.setValue(ExceptionMsg.SUCCESS.getCode());
                    break;
                case ERROR:
                    loginResult.setValue(ExceptionMsg.FAILED.getCode());
                    break;
                default:
                    break;
            }
        }
    };
}
