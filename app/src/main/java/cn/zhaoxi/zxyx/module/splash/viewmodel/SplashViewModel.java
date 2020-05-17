package cn.zhaoxi.zxyx.module.splash.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import cn.zhaoxi.zxyx.module.splash.repository.SplashRepository;

public class SplashViewModel extends ViewModel {
    private SplashRepository splashRepositoryy = SplashRepository.getInstance();
    private LiveData<Boolean> isLogin;

    public LiveData<Boolean> getIsLogin(String userToken) {
        if (null == isLogin)
            isLogin = splashRepositoryy.getIsLogin(userToken);
        return isLogin;
    }
}
