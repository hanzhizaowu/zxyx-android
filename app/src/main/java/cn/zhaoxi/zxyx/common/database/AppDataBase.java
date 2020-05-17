package cn.zhaoxi.zxyx.common.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import cn.zhaoxi.zxyx.common.config.Constants;
import cn.zhaoxi.zxyx.data.entity.User;

@Database(entities = {User.class}, version = Constants.DB_VERSION,exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {
    private static final Object mLock = new Object();

    private static AppDataBase sInstance;

    public static AppDataBase getInstance(Context context) {
        synchronized (mLock){
            if(sInstance == null){
                sInstance = Room.databaseBuilder(context.getApplicationContext(),AppDataBase.class, Constants.DB_NAME).build();
            }
            return sInstance;
        }
    }

    public abstract UserDao UserDao();
}
