package cn.zhaoxi.zxyx.module.user.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import cn.zhaoxi.zxyx.module.user.repository.LoginRespository;

public class LoginViewModel extends ViewModel {
    private LoginRespository loginRespository = LoginRespository.getInstance();
    private LiveData<Integer> loginResult;

    public LiveData<Integer> getLogin(String userName, String userPassword) {
        if (null == loginResult)
            loginResult = loginRespository.getLogin(userName, userPassword);
        return loginResult;
    }
}
