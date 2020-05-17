package cn.zhaoxi.zxyx.module.user.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import cn.zhaoxi.zxyx.module.user.repository.RegisterRespository;

public class RegisterViewModel extends ViewModel {
    private RegisterRespository registerRespository = RegisterRespository.getInstance();
    private LiveData<Integer> registerResult;

    public LiveData<Integer> getRegister(String userName, String userMobile, String userPassword) {
        if (null == registerResult)
            registerResult = registerRespository.getRegister(userName, userMobile, userPassword);
        return registerResult;
    }
}
