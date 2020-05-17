package cn.zhaoxi.zxyx.module.splash.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import cn.zhaoxi.zxyx.common.api.UserApis;
import cn.zhaoxi.zxyx.common.config.ApiClient;
import cn.zhaoxi.zxyx.common.config.ExceptionMsg;
import cn.zhaoxi.zxyx.common.result.RetrofitResponse;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SplashRepository {
    private static final SplashRepository instance = new SplashRepository();

    private SplashRepository() {
    }

    public static SplashRepository getInstance() {
        return instance;
    }

    public LiveData<Boolean> getIsLogin(String userToken) {
        final MutableLiveData<Boolean> isLogin = new MutableLiveData<>();

        UserApis userApis = ApiClient.getRetrofit().create(UserApis.class);
        //对 发送请求 进行封装
        Observable<RetrofitResponse> observable = userApis.postUserIsLogin(userToken);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RetrofitResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RetrofitResponse retrofitResponse) {
                        if (retrofitResponse != null) {
                            if (ExceptionMsg.SUCCESS.getCode().equals(retrofitResponse.getCode())) {
                                isLogin.setValue(true);
                            } else {
                                isLogin.setValue(false);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        isLogin.setValue(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return isLogin;
    }
}
