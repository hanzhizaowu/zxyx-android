package cn.zhaoxi.zxyx.module.publish.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.room.util.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.zhaoxi.library.BuildConfig;
import cn.zhaoxi.library.base.BaseActivity;
import cn.zhaoxi.library.util.StorageUtil;
import cn.zhaoxi.library.util.ToastUtil;
import cn.zhaoxi.zxyx.R;
import cn.zhaoxi.zxyx.adapter.PhotoSelAdapter;
import cn.zhaoxi.zxyx.common.config.Constants;
import cn.zhaoxi.zxyx.common.config.ExceptionMsg;
import cn.zhaoxi.zxyx.common.result.RetrofitResponseData;
import cn.zhaoxi.zxyx.common.util.ImageUtil;
import cn.zhaoxi.zxyx.common.util.Presenter;
import cn.zhaoxi.zxyx.common.util.SPUtil;
import cn.zhaoxi.zxyx.data.dto.FeedDto;
import cn.zhaoxi.zxyx.data.dto.PhotoDto;
import cn.zhaoxi.zxyx.data.dto.VideoDto;
import cn.zhaoxi.zxyx.databinding.ActivityPublishBinding;
import cn.zhaoxi.zxyx.module.publish.viewmodel.PublishViewModel;
import cn.zhaoxi.zxyx.module.main.ui.MainActivity;
import cn.zhaoxi.zxyx.module.video.ui.VideoSelectActivity;
import cn.zhaoxi.zxyx.module.video.ui.VideoTrimmerActivity;
import cn.zhaoxi.zxyx.module.video.util.VideoCompressListener;
import cn.zhaoxi.zxyx.module.video.util.VideoCompressor;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

public class PublishActivity extends BaseActivity implements Presenter {

    //@BindView(R.id.toolbar)
    //Toolbar mToolbar;

    private ActivityPublishBinding activityPublishBinding;
    private PhotoSelAdapter mPhotoSelAdapter;
    private List<String> mPhotos = new ArrayList<>();

    private Long mUid;
    private String mInfo = "";
    private String videoUrl;
    private int publishType;
    private String coverUrl;
    private int videoTime;
    private static final String COMPRESSED_VIDEO_FILE_NAME = "compress.mp4";
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPublishBinding = DataBindingUtil.setContentView(this, R.layout.activity_publish);
        activityPublishBinding.setPresenter(this);
        init();
        Intent intent = getIntent();
        publishType = dealTypeIntent(intent);
        mContext = this;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        publishType = dealTypeIntent(intent);
        if (BuildConfig.DEBUG) Log.i(TAG, "publish type is:"+publishType);
        if(publishType == Constants.FEED_TYPE_TRIM) {
            Bundle bundle = intent.getExtras();
            videoUrl = bundle.getString(Constants.VIDEO_CUT_URL);
            coverUrl = bundle.getString(Constants.VIDEO_COVER_URL);
            videoTime = bundle.getInt(Constants.VIDEO_TIME);
            mPhotos.add(coverUrl);
            mPhotoSelAdapter.setVideoPhoto(mPhotos);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initAdpter();
    }

    private int dealTypeIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        int publishType = bundle.getInt(Constants.PUBLISH_TYPE, -1);
        return publishType;
    }

    private void init() {
        /*ToolbarUtil.init(mToolbar, this)
                .setTitle("发布新动态")
                .setBack()
                .setTitleCenter(R.style.AppTheme_Toolbar_TextAppearance)
                .build();*/

        mUid = SPUtil.build().getLong(Constants.SP_USER_ID);
        setLoading("发布中...");
        initRecycleView();
    }

    private void initAdpter() {
        if (BuildConfig.DEBUG) Log.i(TAG, "publish type is:"+publishType);

        switch (publishType) {
            case Constants.FEED_TYPE_VIDEO:
                mPhotoSelAdapter.setOnItemClickListener(new PhotoSelAdapter.OnItemClickListener() {

                    @Override
                    public void onPhotoClick(int position) {
                        Intent intent1 = new Intent(PublishActivity.this, VideoSelectActivity.class);
                        startActivity(intent1);
                    }

                    @Override
                    public void onDelete(int position) {

                    }
                });
                break;
            case Constants.FEED_TYPE_PHOTO:
                mPhotoSelAdapter.setOnItemClickListener(new PhotoSelAdapter.OnItemClickListener() {
                    @Override
                    public void onPhotoClick(int position) {
                        if (mPhotos.get(position).equals(PhotoSelAdapter.mPhotoAdd)) {
                            mPhotos.remove(position);
                            PhotoPicker.builder()
                                    .setPhotoCount(6)
                                    .setShowCamera(true)
                                    .setShowGif(true)
                                    .setSelected((ArrayList<String>) mPhotos)
                                    .setPreviewEnabled(false)
                                    .start(PublishActivity.this, PhotoPicker.REQUEST_CODE);
                        } else {
                            mPhotos.remove(PhotoSelAdapter.mPhotoAdd);
                            PhotoPreview.builder()
                                    .setPhotos((ArrayList<String>) mPhotos)
                                    .setCurrentItem(position)
                                    .setShowDeleteButton(true)
                                    .start(PublishActivity.this);
                        }
                    }

                    @Override
                    public void onDelete(int position) {
                        mPhotos.remove(position);
                        mPhotoSelAdapter.setPhotos(mPhotos);
                    }
                });
                break;
            case Constants.FEED_TYPE_TRIM:
                mPhotoSelAdapter.setOnItemClickListener(new PhotoSelAdapter.OnItemClickListener() {

                    @Override
                    public void onPhotoClick(int position) {
                        Intent intent1 = new Intent(PublishActivity.this, VideoTrimmerActivity.class);
                        startActivity(intent1);
                    }

                    @Override
                    public void onDelete(int position) {

                    }
                });
                break;
        }
    }

    private void initRecycleView() {
        activityPublishBinding.recyclerView.setLayoutManager(new GridLayoutManager(PublishActivity.this, 3));
        mPhotoSelAdapter = new PhotoSelAdapter(mPhotos);
        activityPublishBinding.recyclerView.setAdapter(mPhotoSelAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_submit:
                mInfo = activityPublishBinding.feedInfo.getText().toString().trim();
                if (TextUtils.isEmpty(mInfo)) {
                    showToast("好歹写点什么吧！");
                    return;
                }

                if(!TextUtils.isEmpty(videoUrl)) {
                    compressVideo(videoUrl);
                } else {
                    postUploadImage(mPhotos);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                switch (requestCode) {
                    case PhotoPicker.REQUEST_CODE:
                    case PhotoPreview.REQUEST_CODE:
                        mPhotos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                        break;
                }
            }
        }
        mPhotoSelAdapter.setPhotos(mPhotos);
    }

    private void compressVideo(String in) {
        String out = StorageUtil.getCacheDir(this) + File.separator + COMPRESSED_VIDEO_FILE_NAME;
        VideoCompressor.compress(this, in, out, new VideoCompressListener() {
            @Override public void onSuccess(String outUrl) {
                if (BuildConfig.DEBUG) Log.i(TAG, "outUrl is:" + outUrl);

                postUploadVideo(mPhotos, outUrl);
            }

            @Override public void onFailure(String message) {
                ToastUtil.showToast(mContext, R.string.compressed_fail);
            }

            @Override public void onProgress(String message) {

            }
        });
    }

    // 上传视频
    private void postUploadVideo(List<String> photos, String videoUrl) {
        removePhotoAdd(photos);

        photos.add(videoUrl);
        if (BuildConfig.DEBUG) Log.i(TAG, "photos is:"+photos);
        List<File> files = ImageUtil.pathToFile(photos);
        Long userId = SPUtil.build().getLong(Constants.SP_USER_ID);

        if ((files != null) && (userId != null)) {
            PublishViewModel publishViewModel = ViewModelProviders.of(this).get(PublishViewModel.class);
            publishViewModel.publishVideo(files, userId).observe(this, new Observer<RetrofitResponseData<VideoDto>>() {
                @Override
                public void onChanged(RetrofitResponseData<VideoDto> response) {
                    Integer code = response.getCode();
                    VideoDto videoDto = response.getData();
                    if (!ExceptionMsg.SUCCESS.getCode().equals(code) || videoDto == null) {
                        showToast(response.getMsg());
                        return;
                    }

                    // 发送动态
                    videoDto.setVideoTime(videoTime);
                    postSaveVideoFeed(videoDto);
                    for (File fileResult : files) {
                        fileResult.delete();
                    }
                }
            });
        }
    }

    // 上传图片
    private void postUploadImage(List<String> photos) {
        removePhotoAdd(photos);

        // 压缩图片
        photos = ImageUtil.compressorImage(this, photos);

        if (BuildConfig.DEBUG) Log.i(TAG, "photos is:"+photos);
        List<File> files = ImageUtil.pathToFile(photos);
        Long userId = SPUtil.build().getLong(Constants.SP_USER_ID);

        if ((files != null) && (userId != null)) {
            PublishViewModel publishViewModel = ViewModelProviders.of(this).get(PublishViewModel.class);
            publishViewModel.publishPhotos(files, userId).observe(this, new Observer<RetrofitResponseData<List<PhotoDto>>>() {
                @Override
                public void onChanged(RetrofitResponseData<List<PhotoDto>> response) {
                    Integer code = response.getCode();
                    List<PhotoDto> photoDtos = response.getData();
                    if (!ExceptionMsg.SUCCESS.getCode().equals(code) || photoDtos == null) {
                        showToast(response.getMsg());
                        addPhotoAdd(mPhotos);
                        return;
                    }

                    // 发送动态
                    postSaveFeed(photoDtos);
                    for (File fileResult : files) {
                        fileResult.delete();
                    }
                }
            });
        }
    }

    // 发布动态
    private void postSaveFeed(List<PhotoDto> photos) {
        PublishViewModel publishViewModel = ViewModelProviders.of(this).get(PublishViewModel.class);
        publishViewModel.saveFeed(mUid, photos, mInfo, "").observe(this, new Observer<RetrofitResponseData<FeedDto>>() {
            @Override
            public void onChanged(RetrofitResponseData<FeedDto> response) {
                dismissLoading();
                Integer code = response.getCode();
                if (!ExceptionMsg.SUCCESS.getCode().equals(code)) {
                    showToast("发布失败");
                    addPhotoAdd(mPhotos);
                    return;
                }

                showToast("发布成功");
                onBackPressed();
            }
        });
    }

    // 发布动态
    private void postSaveVideoFeed(VideoDto videoDto) {
        PublishViewModel publishViewModel = ViewModelProviders.of(this).get(PublishViewModel.class);
        publishViewModel.saveVideoFeed(mUid, videoDto, mInfo, "").observe(this, new Observer<RetrofitResponseData<FeedDto>>() {
            @Override
            public void onChanged(RetrofitResponseData<FeedDto> response) {
                dismissLoading();
                Integer code = response.getCode();
                if (!ExceptionMsg.SUCCESS.getCode().equals(code)) {
                    showToast("发布失败");
                    addPhotoAdd(mPhotos);
                    return;
                }

                showToast("发布成功");
                onBackPressed();
            }
        });
    }

    // 添加添加图片按钮
    private void addPhotoAdd(List<String> photList) {
        if (!photList.contains(PhotoSelAdapter.mPhotoAdd)) {
            photList.add(PhotoSelAdapter.mPhotoAdd);
        }
    }

    // 去除添加图片按钮
    private void removePhotoAdd(List<String> photoList) {
        photoList.remove(PhotoSelAdapter.mPhotoAdd);
    }

    @Override
    public void onBackPressed() {
        // 此处监听回退，通知首页刷新
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.GO_INDEX, R.id.navigation_home);
        intent.putExtras(bundle);
        setResult(Constants.ACTIVITY_PUBLISH, intent);
        finish();
    }
}
