package cn.zhaoxi.zxyx.module.user.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import cn.zhaoxi.library.base.BaseActivity;
import cn.zhaoxi.library.view.LoadingDialog;
import cn.zhaoxi.library.view.MoeToast;
import cn.zhaoxi.zxyx.R;
import cn.zhaoxi.zxyx.common.config.ExceptionMsg;
import cn.zhaoxi.zxyx.common.config.Constants;
import cn.zhaoxi.zxyx.common.util.SPUtil;
import cn.zhaoxi.zxyx.databinding.ActivityLoginBinding;
import cn.zhaoxi.zxyx.module.main.ui.MainActivity;
import cn.zhaoxi.zxyx.module.user.viewmodel.LoginViewModel;

/**
 * 用户登录
 */
public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";

    private long mExitTime = 0;
    private LoadingDialog loginProgress;
    private ActivityLoginBinding activityLoginBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        loginProgress = new LoadingDialog(this, R.string.dialog_loading_login);

        String saveName = SPUtil.build().getString(Constants.SP_USER_NAME);
        if(!TextUtils.isEmpty(saveName)) {
            activityLoginBinding.username.setText(saveName);
            activityLoginBinding.username.setSelection(saveName.length());
        }
    }

    public void login(View view) {
        String username = activityLoginBinding.username.getText().toString().trim();
        String password = activityLoginBinding.password.getText().toString().trim();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            showToast(R.string.toast_login_null);
            return;
        } else {
            showProcessDiaglog();
            postLogin(username, password);
        }
    }

    // 登录请求
    private void postLogin(final String userName, String userPwd) {
        LoginViewModel loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        loginViewModel.getLogin(userName, userPwd).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer loginResult) {
                if(ExceptionMsg.SUCCESS.getCode().equals(loginResult)) {
                    showProcessDiaglog();
                    goHome();
                } else if(ExceptionMsg.LoginNameOrPassWordError.getCode().equals(loginResult)) {
                    showToast(R.string.toast_usernamepwd_error);
                } else {
                    showToast(R.string.toast_exception_error);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                MoeToast.makeText(this, R.string.toast_again_exit);
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void goUpdatePwd(View view) {
        Intent intent = new Intent(this, ResetPwdActivity.class);
        startActivity(intent);
    }

    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showProcessDiaglog() {
        if(!loginProgress.isShowing()) {
            loginProgress.create();
        }
    }

    private void shutProcessDiaglog() {
        if (loginProgress.isShowing()) {
            loginProgress.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        shutProcessDiaglog();
        super.onDestroy();
    }
}
