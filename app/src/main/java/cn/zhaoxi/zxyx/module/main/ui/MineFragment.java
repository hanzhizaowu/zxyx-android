package cn.zhaoxi.zxyx.module.main.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.zhaoxi.library.BuildConfig;
import cn.zhaoxi.library.base.BaseFragment;
import cn.zhaoxi.zxyx.R;
import cn.zhaoxi.zxyx.common.config.Constants;
import cn.zhaoxi.zxyx.common.util.ContentUtil;
import cn.zhaoxi.zxyx.common.util.Presenter;
import cn.zhaoxi.zxyx.common.util.SPUtil;
import cn.zhaoxi.zxyx.data.dto.UserDto;
import cn.zhaoxi.zxyx.databinding.FragmentMineBinding;
import cn.zhaoxi.zxyx.module.main.ui.MainActivity;
import cn.zhaoxi.zxyx.view.dialog.LogoutDialog;
import cn.zhaoxi.zxyx.module.setting.AboutActivity;
import cn.zhaoxi.zxyx.module.setting.SettingsActivity;
import cn.zhaoxi.zxyx.module.user.ui.LoginActivity;
import cn.zhaoxi.zxyx.module.user.ui.PersonalInfoActivity;
import cn.zhaoxi.zxyx.module.user.ui.RelevantActivity;
import cn.zhaoxi.zxyx.module.user.viewmodel.MineViewModel;

/**
 * 我的界面
 */
public class MineFragment extends BaseFragment implements Presenter {

    private Long mUserId;
    private OperateBroadcastReceiver receiver;
    private FragmentMineBinding fragmentMineBinding;
    private final static String TAG = "MineFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentMineBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_mine, container, false);
        View view = fragmentMineBinding.getRoot();
        fragmentMineBinding.setPresenter(this);
        init();
        initReceiver();
        return view;
    }

    private void init() {
        mUserId = SPUtil.build().getLong(Constants.SP_USER_ID);
        // 获取用户信息
        postUserInfo(mUserId);
    }


    /**
     * 广播接收者
     * 用于更新用户信息
     */
    private final class OperateBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case Constants.UPDATE_USER_IMG:
                        postUserInfo(mUserId);
                        break;
                }
            }
        }
    }

    private void initReceiver() {
        receiver = new OperateBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.UPDATE_USER_IMG);
        getActivity().registerReceiver(receiver, filter);
    }

    private void postUserInfo(Long id) {
        MineViewModel mineViewModel = ViewModelProviders.of(this).get(MineViewModel.class);
        mineViewModel.getUserInfo(id).observe(this, new Observer<UserDto>() {
            @Override
            public void onChanged(UserDto mineResult) {
                initUser(mineResult);
            }
        });
    }

    private void initUser(UserDto userInfo) {
        String username = "";
        String avatar = "";
        if (userInfo != null) {
            username = userInfo.getUserName();
            avatar = userInfo.getUserAvatar();
        }
        fragmentMineBinding.userName.setText(username);
        ContentUtil.loadUserAvatar(fragmentMineBinding.userImg, avatar);
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_body:
                gotoPersonal();
                break;
            case R.id.action_reply:
                gotoRelevant(Constants.REPLY_MY);
                break;
            case R.id.action_relevant:
                gotoRelevant(Constants.REPLY_RELEVANT);
                break;
            case R.id.action_setting:
                gotoSettings();
                break;
            case R.id.action_about:
                gotoAbout();
                break;
            case R.id.action_sign_out:
                showLogoutDialog();
                break;
        }
    }

    /**
     * 展示登出Dialog
     */
    public void showLogoutDialog() {
        String tag = "logout";
        FragmentActivity activity = getActivity();
        if (BuildConfig.DEBUG) Log.d(TAG, "activity is:" + activity);
        if (activity == null) {
            return;
        }
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        // 清除已经存在的，同样的fragment
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            transaction.remove(fragment);
        }
        transaction.addToBackStack(null);
        // 展示dialog
        LogoutDialog logoutDialog = LogoutDialog.newInstance();
        logoutDialog.show(transaction, tag);
        logoutDialog.setLogoutListener(new LogoutDialog.LogoutListener() {
            @Override
            public void onLogout() {
                gotoLogin();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        // 解决Activity has leaked window that was originally added here
        // 如果dialog存在或显示，dismiss
    }

    // 前往信息修改
    private void gotoPersonal() {
        Intent goPerson = new Intent(getActivity(), PersonalInfoActivity.class);
        startActivity(goPerson);
    }

    // 前往关于
    private void gotoAbout() {
        Intent goAbout = new Intent(getActivity(), AboutActivity.class);
        startActivity(goAbout);
    }

    private void gotoSettings() {
        Intent goSettings = new Intent(getActivity(), SettingsActivity.class);
        startActivity(goSettings);
    }

    // 前往与我相关
    private void gotoRelevant(String type) {
        Intent goRelevant = new Intent(getActivity(), RelevantActivity.class);
        goRelevant.putExtra(Constants.REPLY_TYPE, type);
        startActivity(goRelevant);
    }

    // 前往登录
    private void gotoLogin() {
        MainActivity activity = (MainActivity) getActivity();
        SPUtil.build().putBoolean(Constants.SP_BEEN_LOGIN, false);
        Intent intent = new Intent(activity, LoginActivity.class);
        startActivity(intent);
        if (activity != null) {
            activity.finish();
        }
    }

}
