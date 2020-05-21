package cn.zhaoxi.zxyx.module.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import cn.zhaoxi.library.BuildConfig;
import cn.zhaoxi.library.base.BaseActivity;
import cn.zhaoxi.zxyx.R;
import cn.zhaoxi.zxyx.common.config.Constants;
import cn.zhaoxi.zxyx.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity {

    private FragmentManager mFragmentManager;
    private FeedFragment mFeedFragment;
    private MineFragment mMineFragment;
    private ActivityMainBinding activityMainBinding;

    private String mExit = "MM";
    private long mExitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        init();
    }

    private void init() {
        initFragment();
        initBottomNavigation();
    }

    private void initFragment() {
        mFragmentManager = getSupportFragmentManager();
        mFeedFragment = new FeedFragment();
        mMineFragment = new MineFragment();
        activityMainBinding.bottomNavigation.setSelectedItemId(R.id.navigation_home);
        switchFragment(mFeedFragment);
    }

    //底部导航
    private void initBottomNavigation() {
        activityMainBinding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        switchFragment(mFeedFragment);
                        return true;
                    case R.id.navigation_mine:
                        if (BuildConfig.DEBUG) Log.i("mainactivity", "switch mine fragment");
                        switchFragment(mMineFragment);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Constants.ACTIVITY_PUBLISH:
                int id = data.getIntExtra(Constants.GO_INDEX, R.id.navigation_home);
                // 非导航本身事件，手动切换
                activityMainBinding.bottomNavigation.setSelectedItemId(id);
                break;
            case Constants.ACTIVITY_PERSONAL:
                mMineFragment.onActivityResult(requestCode, resultCode, data);
                break;
            default:
                break;
        }
    }

    private Fragment currentFragment;

    /**
     * 切换Fragment
     */
    private void switchFragment(Fragment targetFragment) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (!targetFragment.isAdded()) {
            // 首次currentFragment为null
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
            transaction.add(R.id.fragment_container, targetFragment, targetFragment.getClass().getName());
        } else {
            transaction.hide(currentFragment).show(targetFragment);
        }
        currentFragment = targetFragment;
        transaction.commitAllowingStateLoss();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                showMoeToast("(ಥ _ ಥ)你难道要再按一次离开我么");
                mExitTime = System.currentTimeMillis();
            } else {
                int x = (int) (Math.random() * 10) + 1;
                if ("MM".equals(mExit)) {
                    if (x == 10) {
                        showMoeToast("恭喜你找到隐藏的偶，Game over!");
                        finish();
                    } else {
                        showMoeToast("你果然想要离开我(＠￣ー￣＠)");
                    }
                    mExitTime = System.currentTimeMillis();
                    mExit = "mm";
                } else if ("mm".equals(mExit)) {
                    mExit = "MM";
                    finish();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
