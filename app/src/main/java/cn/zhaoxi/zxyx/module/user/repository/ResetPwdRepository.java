package cn.zhaoxi.zxyx.module.user.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import cn.zhaoxi.zxyx.common.api.UserApis;
import cn.zhaoxi.zxyx.common.config.ApiClient;
import cn.zhaoxi.zxyx.common.result.RetrofitResponse;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ResetPwdRepository {
    private static final ResetPwdRepository instance = new ResetPwdRepository();

    private ResetPwdRepository() {
    }

    public static ResetPwdRepository getInstance() {
        return instance;
    }

    public LiveData<Integer> getResetPwd(String userName, String userMobile, String userPassword) {
        final MutableLiveData<Integer> isResetPwd = new MutableLiveData<>();

        UserApis userApis = ApiClient.getRetrofit().create(UserApis.class);
        //对 发送请求 进行封装
        Observable<RetrofitResponse> observable = userApis.postUserResetPassword(userName, userMobile, userPassword);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RetrofitResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RetrofitResponse retrofitResponse) {
                        if (retrofitResponse != null) {
                            isResetPwd.setValue(retrofitResponse.getCode());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return isResetPwd;
    }
}
