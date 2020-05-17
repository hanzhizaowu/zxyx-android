package cn.zhaoxi.zxyx.module.user.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.List;

import cn.zhaoxi.library.BuildConfig;
import cn.zhaoxi.library.base.BaseActivity;
import cn.zhaoxi.library.util.PathUtils;
import cn.zhaoxi.library.view.LoadingDialog;
import cn.zhaoxi.zxyx.R;
import cn.zhaoxi.zxyx.common.config.Constants;
import cn.zhaoxi.zxyx.common.config.ExceptionMsg;
import cn.zhaoxi.zxyx.common.result.RetrofitResponseData;
import cn.zhaoxi.zxyx.common.util.ContentUtil;
import cn.zhaoxi.zxyx.common.util.ImageUtil;
import cn.zhaoxi.zxyx.common.util.Presenter;
import cn.zhaoxi.zxyx.common.util.SPUtil;

import cn.zhaoxi.zxyx.data.dto.UserDto;
import cn.zhaoxi.zxyx.databinding.ActivityPersonalInfoBinding;
import cn.zhaoxi.zxyx.dialog.EditTextDialog;
import cn.zhaoxi.zxyx.module.user.viewmodel.MineViewModel;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

/**
 * 用户资料
 */
public class PersonalInfoActivity extends BaseActivity implements Presenter {

    private static final int PHOTO_REQUEST_CUT = 456;
    private ActivityPersonalInfoBinding activityPersonalInfoBinding;

    private Long mUserId;
    private String saveName;

    private LoadingDialog loadingProgress;

    // 用户更新的参数
    private String avatar;
    private Integer sex;
    private String qq;
    private String signature;

    // 是否为更新头像
    private boolean isUpdateAvatar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPersonalInfoBinding = DataBindingUtil.setContentView(this, R.layout.activity_personal_info);
        activityPersonalInfoBinding.setPresenter(this);
        init();
    }

    private void init() {
        loadingProgress = new LoadingDialog(this, R.string.dialog_update_avatar);

        mUserId = SPUtil.build().getLong(Constants.SP_USER_ID);
        saveName = SPUtil.build().getString(Constants.SP_USER_NAME);
        activityPersonalInfoBinding.personName.setText(saveName);
        postUserInfo();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.person_img:
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setShowCamera(true)
                        .setShowGif(false)
                        .setPreviewEnabled(false)
                        .start(PersonalInfoActivity.this, PhotoPicker.REQUEST_CODE);
                break;
            case R.id.person_name:
                EditTextDialog editTextDialog = EditTextDialog.newInstance("修改用户名", saveName, 24);
                editTextDialog.show(getSupportFragmentManager(), "edit");
                editTextDialog.setPositiveListener(new EditTextDialog.PositiveListener() {
                    @Override
                    public void Positive(String value) {
                        if (TextUtils.isEmpty(value) || saveName.equals(value)) {
                            showToast("暂不支持修改用户名");
                        } else {
                            postUpdateUserInfo(value);
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        // 图片选择
        if (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE) {
            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
            if (photos != null) {
                String photo = photos.get(0);
                File file = new File(photo);
                Uri uri = ImageUtil.getFileUri(this, file);
                String imagePath = ImageUtil.getImagePath();
                Uri destUri = Uri.fromFile(new File(imagePath));
                if (BuildConfig.DEBUG) Log.i("PersonalInfoActivity", "destUri is:" + destUri.toString());

                UCrop.Options options = new UCrop.Options();
                options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                options.setShowCropGrid(false);
                options.setMaxScaleMultiplier(3);
                options.setShowCropFrame(false);
                options.setCompressionQuality(80);
                UCrop.of(uri, destUri)
                        .withAspectRatio(1, 1)
                        .withMaxResultSize(240, 240)
                        .withOptions(options)
                        .start(this);
            }
        }  else if (requestCode == UCrop.REQUEST_CROP) {
            Uri resultUri = UCrop.getOutput(data);
            postUserImage(resultUri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获取用户信息
     */
    private void postUserInfo() {
        MineViewModel mineViewModel = ViewModelProviders.of(this).get(MineViewModel.class);
        mineViewModel.getUserInfo(mUserId).observe(this, new Observer<UserDto>() {
            @Override
            public void onChanged(UserDto mineResult) {
                setUserInfo(mineResult);
            }
        });
    }

    /**
     * 上传用户头像
     */
    private void postUserImage(Uri resultUri) {
        String resultPath;
        try {
            resultPath = PathUtils.getPath(this, resultUri);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (BuildConfig.DEBUG) Log.i(TAG, "resultPath is : " + resultPath);
        if (resultPath != null) {
            File file = new File(resultPath);
            if (!file.exists()) {
                showUserImageUpdateError();
                return;
            }

            if (BuildConfig.DEBUG) Log.i(TAG, "file length is : " + file.length());
            MineViewModel mineViewModel = ViewModelProviders.of(this).get(MineViewModel.class);
            mineViewModel.updateUserImage(file, mUserId).observe(this, new Observer<RetrofitResponseData<UserDto>>() {
                @Override
                public void onChanged(RetrofitResponseData<UserDto> response) {
                    Integer code = response.getCode();
                    UserDto userDto = response.getData();
                    if (!ExceptionMsg.SUCCESS.getCode().equals(code) || userDto == null) {
                        showUserImageUpdateError();
                        return;
                    }
                    if(!TextUtils.isEmpty(userDto.getUserAvatar())) {
                        ContentUtil.loadUserAvatar(activityPersonalInfoBinding.personImg, userDto.getUserAvatar());
                        file.delete();
                    }
                }
            });
        }
    }

    /**
     * 更新用户信息
     */
    private void postUpdateUserInfo(String userName) {
        MineViewModel mineViewModel = ViewModelProviders.of(this).get(MineViewModel.class);
        mineViewModel.updateUserInfo(userName, mUserId).observe(this, new Observer<RetrofitResponseData<UserDto>>() {
            @Override
            public void onChanged(RetrofitResponseData<UserDto> mineResult) {
                if(ExceptionMsg.SUCCESS.getCode().equals(mineResult.getCode())) {
                    showUserUpdateSuccess();
                    setUserInfo(mineResult.getData());
                } else {
                    showUserUpdateError();
                }
            }
        });
    }

    /**
     * 设置用户信息
     */
    private void setUserInfo(UserDto userInfo) {
        if(userInfo != null) {
            if(!TextUtils.isEmpty(userInfo.getUserAvatar())) {
                ContentUtil.loadUserAvatar(activityPersonalInfoBinding.personImg, userInfo.getUserAvatar());
            }

            if (!TextUtils.isEmpty(userInfo.getUserName())) {
                activityPersonalInfoBinding.personName.setText(userInfo.getUserName());
            }
        }

        cleanData();
    }

    /**
     * 清除数据
     */
    private void cleanData() {
        avatar = null;
        sex = null;
        qq = null;
        signature = null;
    }

    /**
     * 提示头像修改失败
     */
    private void showUserImageUpdateError() {
        showToast("更新头像失败");
    }

    /**
     * 提示用户信息更新失败
     */
    private void showUserUpdateSuccess() {
        showToast("更新用户信息成功");
    }

    /**
     * 提示用户信息更新失败
     */
    private void showUserUpdateError() {
        showToast("更新用户信息失败");
    }
}
