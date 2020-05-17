package cn.zhaoxi.zxyx.module.user.ui;

import android.os.Bundle;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.text.TextUtils;
import android.view.View;
import cn.zhaoxi.library.base.BaseActivity;
import cn.zhaoxi.library.view.LoadingDialog;
import cn.zhaoxi.zxyx.R;
import cn.zhaoxi.zxyx.common.config.ExceptionMsg;
import cn.zhaoxi.zxyx.databinding.ActivityResetpwdBinding;
import cn.zhaoxi.zxyx.module.user.viewmodel.ResetPwdViewModel;

/**
 * 忘记密码重置
 */
public class ResetPwdActivity extends BaseActivity {

    private LoadingDialog updateProgress;
    private ActivityResetpwdBinding activityResetpwdBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        updateProgress = new LoadingDialog(this, R.string.dialog_loading_reset_wd);
        activityResetpwdBinding = DataBindingUtil.setContentView(this, R.layout.activity_resetpwd);
    }

    public void goUpdatePwd(View view) {
        String uName = activityResetpwdBinding.username.getText().toString().trim();
        String uPhone = activityResetpwdBinding.phone.getText().toString().trim();
        String uPwd = activityResetpwdBinding.password.getText().toString().trim();
        String uDoPwd = activityResetpwdBinding.doPassword.getText().toString().trim();
        if (TextUtils.isEmpty(uName) || TextUtils.isEmpty(uPwd) || TextUtils.isEmpty(uDoPwd) || TextUtils.isEmpty(uPhone)) {
            showToast(R.string.toast_reg_null);
        }
        if (uPhone.length() != 11) {
            showToast(R.string.toast_phone_format_error);
            return;
        }
        if (!uPwd.equals(uDoPwd)) {
            showToast(R.string.toast_again_error);
            return;
        }
        showProcessDiaglog();
        postUpdatePwd(uName, uPwd, uPhone);
    }

    public void postUpdatePwd(String userName, String userPwd, String phone) {
        ResetPwdViewModel resetPwdViewModel = ViewModelProviders.of(this).get(ResetPwdViewModel.class);
        resetPwdViewModel.getResetPwd(userName, phone, userPwd).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer registerResult) {
                shutProcessDiaglog();
                if(ExceptionMsg.SUCCESS.getCode().equals(registerResult)) {
                    showToast(R.string.toast_reset_ped_success);
                    onBackPressed();
                } else if(ExceptionMsg.MobileUsed.getCode().equals(registerResult)) {
                    showToast(R.string.toast_phone_being);
                } else if(ExceptionMsg.UserNameUsed.getCode().equals(registerResult)) {
                    showToast(R.string.toast_username_being);
                } else {
                    showToast(R.string.toast_reset_pwd_error);
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
        if(!updateProgress.isShowing()) {
            updateProgress.create();
        }
    }

    private void shutProcessDiaglog() {
        if (updateProgress.isShowing()) {
            updateProgress.dismiss();
        }
    }
}
