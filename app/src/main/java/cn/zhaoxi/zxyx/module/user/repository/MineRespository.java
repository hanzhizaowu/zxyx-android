package cn.zhaoxi.zxyx.module.user.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.util.List;

import cn.zhaoxi.zxyx.common.api.UserApis;
import cn.zhaoxi.zxyx.common.config.ApiClient;
import cn.zhaoxi.zxyx.common.config.ExceptionMsg;
import cn.zhaoxi.zxyx.common.result.RetrofitResponse;
import cn.zhaoxi.zxyx.common.result.RetrofitResponseData;
import cn.zhaoxi.zxyx.data.dto.UserDto;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MineRespository {
    private static final MineRespository instance = new MineRespository();

    private MineRespository() {
    }

    public static MineRespository getInstance() {
        return instance;
    }

    public LiveData<UserDto> getUserInfo(Long userId) {
        MutableLiveData<UserDto> mineResult = new MutableLiveData<>();
        UserApis userApis = ApiClient.getRetrofit().create(UserApis.class);
        //对 发送请求 进行封装
        Observable<RetrofitResponseData<UserDto>> observable = userApis.postUserInfo(userId);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RetrofitResponseData<UserDto>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RetrofitResponseData<UserDto> retrofitResponse) {
                        if ((retrofitResponse != null) && (ExceptionMsg.SUCCESS.getCode().equals(retrofitResponse.getCode()))) {
                            mineResult.setValue(retrofitResponse.getData());
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
        return mineResult;
    }

    public LiveData<RetrofitResponseData<UserDto>> updateUserImage(File file, Long userId) {
        MutableLiveData<RetrofitResponseData<UserDto>> uploadResult = new MutableLiveData<>();
        UserApis userApis = ApiClient.getRetrofit().create(UserApis.class);
        //对 发送请求 进行封装

        String fileName = verifyImageType(file.getName());
        if(fileName == null) {
            return uploadResult;
        }
        RequestBody requestFile = RequestBody.create(MediaType.parse(fileName), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        Observable<RetrofitResponseData<UserDto>> observable = userApis.uploadUserImage(body, userId);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RetrofitResponseData<UserDto>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RetrofitResponseData<UserDto> retrofitResponse) {
                        uploadResult.setValue(retrofitResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        RetrofitResponseData<UserDto> retrofitResponse = new RetrofitResponseData<>();
                        retrofitResponse.setCode(ExceptionMsg.FAILED.getCode());
                        uploadResult.setValue(retrofitResponse);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return uploadResult;
    }

    public LiveData<RetrofitResponseData<UserDto>> updateUserInfo(String userName, Long userId) {
        MutableLiveData<RetrofitResponseData<UserDto>> mineResult = new MutableLiveData<>();
        UserApis userApis = ApiClient.getRetrofit().create(UserApis.class);
        //对 发送请求 进行封装
        Observable<RetrofitResponseData<UserDto>> observable = userApis.uploadUserInfo(userName, userId);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RetrofitResponseData<UserDto>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RetrofitResponseData<UserDto> retrofitResponse) {
                        mineResult.setValue(retrofitResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        RetrofitResponseData<UserDto> retrofitResponse = new RetrofitResponseData<>();
                        retrofitResponse.setCode(ExceptionMsg.FAILED.getCode());
                        mineResult.setValue(retrofitResponse);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return mineResult;
    }

    /**
     * 验证文件类型
     */
    private String verifyImageType(String fileName) {
        String[] types = new String[]{".jpg", ".png", ".jpeg"};
        String fileType = fileName.substring(fileName.lastIndexOf("."), fileName.length());

        for (String str : types) {
            if (str.equalsIgnoreCase(fileType)) {
                String type = str.substring(1);
                return "img/" + type;
            }
        }
        return null;
    }
}
