package cn.zhaoxi.zxyx.module.user.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.text.TextUtils;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import cn.zhaoxi.library.base.BaseActivity;
import cn.zhaoxi.library.view.LoadingDialog;
import cn.zhaoxi.zxyx.R;
import cn.zhaoxi.zxyx.common.config.ExceptionMsg;
import cn.zhaoxi.zxyx.databinding.ActivityRegisterBinding;
import cn.zhaoxi.zxyx.module.main.MainActivity;
import cn.zhaoxi.zxyx.module.user.viewmodel.RegisterViewModel;

/**
 * 用户注册
 */
public class RegisterActivity extends BaseActivity {

    private LoadingDialog registerProgress;
    private ActivityRegisterBinding activityRegisterBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        registerProgress = new LoadingDialog(this, R.string.dialog_loading_reg);
        activityRegisterBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);
    }

    public void goRegister(View view) {
        String uName = activityRegisterBinding.username.getText().toString().trim();
        String uPwd = activityRegisterBinding.password.getText().toString().trim();
        String uDoPwd = activityRegisterBinding.doPassword.getText().toString().trim();
        String uPhone = activityRegisterBinding.phone.getText().toString().trim();
        if (TextUtils.isEmpty(uName) || TextUtils.isEmpty(uPwd) || TextUtils.isEmpty(uDoPwd) || TextUtils.isEmpty(uPhone)) {
            showToast(R.string.toast_reg_null);
            return;
        }
        if (!uPwd.equals(uDoPwd)) {
            showToast(R.string.toast_again_error);
            return;
        }
        if (uPhone.length() != 11) {
            showToast(R.string.toast_phone_format_error);
            return;
        }
        if (!isMobileNum(uPhone)) {
            showToast(R.string.toast_phone_format_error);
            return;
        }
        showProcessDiaglog();
        postRegister(uName, uPwd, uPhone);
    }

    /**
     * 验证手机
     */
    public static boolean isMobileNum(String mobiles) {
        Pattern p = Pattern.compile("1[3-9]\\d{9}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 注册请求
     */
    public void postRegister(String userName, String userPwd, String phone) {
        RegisterViewModel registerViewModel = ViewModelProviders.of(this).get(RegisterViewModel.class);
        registerViewModel.getRegister(userName, phone, userPwd).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer registerResult) {
                shutProcessDiaglog();
                if(ExceptionMsg.SUCCESS.getCode().equals(registerResult)) {
                    showToast(R.string.toast_reg_success);
                    goHome();
                } else if(ExceptionMsg.MobileUsed.getCode().equals(registerResult)) {
                    showToast(R.string.toast_phone_being);
                } else if(ExceptionMsg.UserNameUsed.getCode().equals(registerResult)) {
                    showToast(R.string.toast_username_being);
                } else {
                    showToast(R.string.toast_reg_error);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        shutProcessDiaglog();
        super.onDestroy();
    }

    private void showProcessDiaglog() {
        if(!registerProgress.isShowing()) {
            registerProgress.create();
        }
    }

    private void shutProcessDiaglog() {
        if (registerProgress.isShowing()) {
            registerProgress.dismiss();
        }
    }

    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
