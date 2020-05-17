package cn.zhaoxi.zxyx.common.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import cn.zhaoxi.zxyx.BuildConfig;
import cn.zhaoxi.zxyx.data.entity.User;

public class UserDB {
    private final static String TAG = "UserDB";
    private final static String TABLE_USER_NAME = "tb_user";

    // 往该表添加一条记录
    public static long insert(User info, SQLiteDatabase mDB) {
        ArrayList<User> infoArray = new ArrayList<User>();
        infoArray.add(info);
        return insert(infoArray, mDB);
    }

    // 往该表添加多条记录
    public static long insert(ArrayList<User> infoArray, SQLiteDatabase mDB) {
        long result = -1;
        for (int i = 0; i < infoArray.size(); i++) {
            User info = infoArray.get(i);
            ArrayList<User> tempArray = new ArrayList<User>();
            // 如果存在同名记录，则更新记录
            Long userId = info.getUserId();
            if (userId != null) {
                if (BuildConfig.DEBUG) Log.i(TAG, "userId is:" + userId);
                String condition = String.format("userId=%d", userId);
                tempArray = query(condition, mDB);
                if (BuildConfig.DEBUG) Log.i(TAG, "tempArray is:" + tempArray);
                if (tempArray.size() > 0) {
                    update(info, condition, mDB);
                    result = tempArray.get(0).getId();
                    continue;
                } else {
                    // 不存在唯一性重复的记录，则插入新记录
                    if (BuildConfig.DEBUG) Log.i(TAG, "info is:" + info.toString());
                    ContentValues cv = new ContentValues();
                    cv.put("userId", info.getUserId());
                    cv.put("userName", info.getUserName());
                    cv.put("userToken", info.getUserToken());
                    cv.put("userAvatar", info.getUserAvatar());
                    cv.put("userPassword", info.getUserPassword());
                    // 执行插入记录动作，该语句返回插入记录的行号
                    result = mDB.insert(TABLE_USER_NAME, "", cv);
                    // 添加成功后返回行号，失败后返回-1
                }
            }
        }

        if (BuildConfig.DEBUG) Log.i(TAG, "result is:" + result);
        return result;
    }

    // 根据条件更新指定的表记录
    public static int update(User info, String condition, SQLiteDatabase mDB) {
        ContentValues cv = new ContentValues();
        cv.put("userId", info.getUserId());
        cv.put("userName", info.getUserName());
        cv.put("userToken", info.getUserToken());
        cv.put("userAvatar", info.getUserAvatar());
        cv.put("userPassword", info.getUserPassword());
        // 执行更新记录动作，该语句返回记录更新的数目
        return mDB.update(TABLE_USER_NAME, cv, condition, null);
    }

    public static int update(User info, SQLiteDatabase mDB) {
        // 执行更新记录动作，该语句返回记录更新的数目
        return update(info, "rowid=" + info.getId(), mDB);
    }

    // 根据指定条件查询记录，并返回结果数据队列
    public static ArrayList<User> query(String condition, SQLiteDatabase mDB) {
        String sql = String.format("select * from %s where %s;", TABLE_USER_NAME, condition);
        Log.i(TAG, "query sql: " + sql);
        ArrayList<User> infoArray = new ArrayList<User>();
        // 执行记录查询动作，该语句返回结果集的游标
        Cursor cursor = mDB.rawQuery(sql, null);
        // 循环取出游标指向的每条记录
        while (cursor.moveToNext()) {
            User info = new User();
            info.setId(cursor.getInt(0));
            info.setUserName(cursor.getString(1));
            info.setUserId(cursor.getLong(2));
            info.setUserToken(cursor.getString(3));
            info.setUserAvatar(cursor.getString(4));
            info.setUserPassword(cursor.getString(5));
            infoArray.add(info);
        }
        cursor.close(); // 查询完毕，关闭游标
        return infoArray;
    }
}
