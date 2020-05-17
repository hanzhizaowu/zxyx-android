package cn.zhaoxi.zxyx.module.user.repository;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

import cn.zhaoxi.zxyx.BuildConfig;
import cn.zhaoxi.zxyx.common.database.AppDataBase;
import cn.zhaoxi.zxyx.data.entity.User;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class UserDataSource {
    private Context mContext;

    public UserDataSource(@NonNull Application application){
        this.mContext = application;
    }

    /**
     * 获取搜索历史
     * @param count
     * @return
     */
    public LiveData<List<User>> getUser(int count){
        return AppDataBase.getInstance(mContext).UserDao().getUser(count);
    }

    public long insertUser(User user) {
        long result = -1;
        Long userId = user.getUserId();
        if(userId != null) {
            List<User> users = AppDataBase.getInstance(mContext).UserDao().getUser(userId, 1);
            if(users.size() > 0) {
                User userNew = users.get(0);
                userNew.setUserName(user.getUserName());
                userNew.setUserAvatar(user.getUserAvatar());
                userNew.setUserPassword(user.getUserPassword());
                userNew.setUserToken(user.getUserToken());
                result = AppDataBase.getInstance(mContext).UserDao().update(userNew);
            } else {
                result = AppDataBase.getInstance(mContext).UserDao().insert(user);
            }
        }

        return result;
    }
}
