package cn.zhaoxi.zxyx.module.user.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import cn.zhaoxi.zxyx.module.user.repository.ResetPwdRepository;

public class ResetPwdViewModel extends ViewModel {
    private ResetPwdRepository resetPwdRepository = ResetPwdRepository.getInstance();
    private LiveData<Integer> resetPwdResult;

    public LiveData<Integer> getResetPwd(String userName, String userMobile, String userPassword) {
        if (null == resetPwdResult)
            resetPwdResult = resetPwdRepository.getResetPwd(userName, userMobile, userPassword);
        return resetPwdResult;
    }
}
