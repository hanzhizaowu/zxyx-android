package cn.zhaoxi.zxyx.module.user.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.util.List;

import cn.zhaoxi.zxyx.common.result.RetrofitResponseData;
import cn.zhaoxi.zxyx.data.dto.UserDto;
import cn.zhaoxi.zxyx.module.user.repository.MineRespository;

public class MineViewModel extends ViewModel {
    private MineRespository mineRespository = MineRespository.getInstance();

    public LiveData<UserDto> getUserInfo(Long userId) {
        LiveData<UserDto> mineResult = mineRespository.getUserInfo(userId);
        return mineResult;
    }

    public LiveData<RetrofitResponseData<UserDto>> updateUserImage(File file, Long userId) {
        LiveData<RetrofitResponseData<UserDto>> results = mineRespository.updateUserImage(file, userId);
        return results;
    }

    public LiveData<RetrofitResponseData<UserDto>> updateUserInfo(String userName, Long userId) {
        LiveData<RetrofitResponseData<UserDto>> mineResult = mineRespository.updateUserInfo(userName, userId);
        return mineResult;
    }
}
