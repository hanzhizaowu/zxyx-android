package cn.zhaoxi.zxyx.module.splash.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import cn.zhaoxi.library.base.BaseActivity;
import cn.zhaoxi.zxyx.module.main.MainActivity;
import cn.zhaoxi.zxyx.R;
import cn.zhaoxi.zxyx.common.config.RequestUrl;
import cn.zhaoxi.zxyx.common.config.Constants;
import cn.zhaoxi.zxyx.common.util.SPUtil;
import cn.zhaoxi.zxyx.module.splash.viewmodel.SplashViewModel;
import cn.zhaoxi.zxyx.module.user.ui.LoginActivity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        boolean isLogin = SPUtil.build().getBoolean(Constants.SP_BEEN_LOGIN);
        String token = SPUtil.build().getString(RequestUrl.X_APP_TOKEN);
        if(isLogin && !TextUtils.isEmpty(token)) {
            SplashViewModel splashViewModel = ViewModelProviders.of(this).get(SplashViewModel.class);
            splashViewModel.getIsLogin(token).observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean isLogin) {
                    if(isLogin) {
                        goHome();
                    } else {
                        goLogin();
                    }
                }
            });
        } else {
            goLogin();
        }
    }

    /**
     * 前往主页
     */
    private void goHome() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 前往登录
     */
    private void goLogin() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
