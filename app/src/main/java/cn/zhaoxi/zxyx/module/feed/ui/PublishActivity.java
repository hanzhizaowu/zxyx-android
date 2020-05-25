package cn.zhaoxi.zxyx.module.feed.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.zhaoxi.library.base.BaseActivity;
import cn.zhaoxi.library.util.ToolbarUtil;
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
import cn.zhaoxi.zxyx.databinding.ActivityPublishBinding;
import cn.zhaoxi.zxyx.module.feed.viewmodel.PublishViewModel;
import cn.zhaoxi.zxyx.module.main.MainActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPublishBinding = DataBindingUtil.setContentView(this, R.layout.activity_publish);
        activityPublishBinding.setPresenter(this);
        init();
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

    private void initRecycleView() {
        activityPublishBinding.recyclerView.setLayoutManager(new GridLayoutManager(PublishActivity.this, 3));
        mPhotoSelAdapter = new PhotoSelAdapter(mPhotos);
        activityPublishBinding.recyclerView.setAdapter(mPhotoSelAdapter);
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

                postUpload(mPhotos);
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

    // 上传图片
    private void postUpload(List<String> photos) {
        removePhotoAdd(photos);

        // 压缩图片
        photos = ImageUtil.compressorImage(this, photos);

        List<File> files = ImageUtil.pathToImageFile(photos);

        if (files != null) {
            PublishViewModel publishViewModel = ViewModelProviders.of(this).get(PublishViewModel.class);
            publishViewModel.publishPhotos(files).observe(this, new Observer<RetrofitResponseData<List<PhotoDto>>>() {
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
        for(PhotoDto photoDto : photos) {
            photoDto.setPhotoType(Constants.PHOTO_TYPE_IMAGE);
        }

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
