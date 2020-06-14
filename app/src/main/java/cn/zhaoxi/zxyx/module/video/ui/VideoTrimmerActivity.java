package cn.zhaoxi.zxyx.module.video.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

import java.io.File;

import cn.zhaoxi.library.base.BaseActivity;
import cn.zhaoxi.library.util.StorageUtil;
import cn.zhaoxi.library.util.ToastUtil;
import cn.zhaoxi.zxyx.R;
import cn.zhaoxi.zxyx.common.config.Constants;
import cn.zhaoxi.zxyx.databinding.ActivityVideoTrimBinding;
import cn.zhaoxi.zxyx.module.publish.ui.PublishActivity;
import cn.zhaoxi.zxyx.module.video.ui.view.VideoTrimListener;
import cn.zhaoxi.zxyx.module.video.util.VideoCoverListener;
import cn.zhaoxi.zxyx.module.video.util.VideoCoverUtil;

public class VideoTrimmerActivity extends BaseActivity implements VideoTrimListener, VideoCoverListener {

    private static final String TAG = "jason";
    private static final String VIDEO_PATH_KEY = "video-file-path";
    public static final int VIDEO_TRIM_REQUEST_CODE = 0x001;
    private ActivityVideoTrimBinding mBinding;
    private ProgressDialog mProgressDialog;
    private int videoTime;

    public static void call(AppCompatActivity from, String videoPath) {
        if (!TextUtils.isEmpty(videoPath)) {
            Bundle bundle = new Bundle();
            bundle.putString(VIDEO_PATH_KEY, videoPath);
            Intent intent = new Intent(from, VideoTrimmerActivity.class);
            intent.putExtras(bundle);
            from.startActivity(intent);
            //from.startActivityForResult(intent, VIDEO_TRIM_REQUEST_CODE);
        }
    }

    @Override public void initUI() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_video_trim);
        Bundle bd = getIntent().getExtras();
        String path = "";
        if (bd != null) path = bd.getString(VIDEO_PATH_KEY);
        if (mBinding.trimmerView != null) {
            mBinding.trimmerView.setOnTrimVideoListener(this);
            mBinding.trimmerView.initVideoByURI(Uri.parse(path));
        }
    }

    @Override public void onResume() {
        super.onResume();
    }

    @Override public void onPause() {
        super.onPause();
        mBinding.trimmerView.onVideoPause();
        mBinding.trimmerView.setRestoreState(true);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        mBinding.trimmerView.onDestroy();
    }

    @Override public void onStartTrim() {
        buildDialog(getResources().getString(R.string.trimming)).show();
    }

    @Override public void onFinishTrim(String in, int videoTime) {
        this.videoTime = videoTime;
        VideoCoverUtil.getVideoCover(this, in, StorageUtil.getCacheDir(this), this);
    }

    @Override
    public void onStartCover() {

    }

    @Override
    public void onFinishCover(String videoUrl, String coverUrl) {
        if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
        ToastUtil.showToast(this, getString(R.string.trimmed_done));
        goPublish(videoUrl, coverUrl);
    }

    @Override
    public void onFailCover() {
        if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
        ToastUtil.showToast(this, getString(R.string.trimmed_fail));
    }

    @Override public void onCancel() {
        mBinding.trimmerView.onDestroy();
        finish();
    }

    private void goPublish(String videoUrl,  String coverUrl) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.PUBLISH_TYPE, Constants.FEED_TYPE_TRIM);
        bundle.putString(Constants.VIDEO_CUT_URL, videoUrl);
        bundle.putString(Constants.VIDEO_COVER_URL, coverUrl);
        bundle.putInt(Constants.VIDEO_TIME, videoTime);
        Intent intent = new Intent(this, PublishActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private ProgressDialog buildDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(this, "", msg);
        }
        mProgressDialog.setMessage(msg);
        return mProgressDialog;
    }
}
