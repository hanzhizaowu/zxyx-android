package cn.zhaoxi.zxyx.common.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = "DBHelper";
    private static final String DB_NAME = "photo.db"; // 数据库的名称
    private static final int DB_VERSION = 1; // 数据库的版本号
    private static DBHelper mHelper = null; // 数据库帮助器的实例
    private SQLiteDatabase mDB = null; // 数据库的实例
    public static final String TABLE_USR_NAME = "tb_user"; // 表的名称

    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    private DBHelper(Context context, int version) {
        super(context, DB_NAME, null, version);
    }

    // 利用单例模式获取数据库帮助器的唯一实例
    public static DBHelper getInstance(Context context, int version) {
        if (version > 0 && mHelper == null) {
            mHelper = new DBHelper(context, version);
        } else if (mHelper == null) {
            mHelper = new DBHelper(context);
        }
        return mHelper;
    }

    // 打开数据库的读连接
    public SQLiteDatabase openReadLink() {
        if (mDB == null || !mDB.isOpen()) {
            mDB = mHelper.getReadableDatabase();
        }
        return mDB;
    }

    // 打开数据库的写连接
    public SQLiteDatabase openWriteLink() {
        if (mDB == null || !mDB.isOpen()) {
            mDB = mHelper.getWritableDatabase();
        }
        return mDB;
    }

    // 关闭数据库连接
    public void closeLink() {
        if (mDB != null && mDB.isOpen()) {
            mDB.close();
            mDB = null;
        }
    }

    // 创建数据库，执行建表语句
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        String create_sql = "CREATE TABLE IF NOT EXISTS " + TABLE_USR_NAME + " ("
                + "_id INTEGER PRIMARY KEY  AUTOINCREMENT NOT NULL,"
                + "userName VARCHAR UNIQUE NOT NULL," + "userId LONG UNIQUE NOT NULL,"
                + "userToken VARCHAR NOT NULL," + "userAvatar VARCHAR,"
                + "userPassword VARCHAR"
                + ");";
        Log.d(TAG, "create_sql:" + create_sql);
        db.execSQL(create_sql);
    }

    // 修改数据库，执行表结构变更语句
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade oldVersion=" + oldVersion + ", newVersion=" + newVersion);
    }

    // 根据指定条件删除表记录
    public int delete(String condition, String tableName) {
        // 执行删除记录动作，该语句返回删除记录的数目
        return mDB.delete(tableName, condition, null);
    }

    // 删除该表的所有记录
    public int deleteAll(String tableName) {
        // 执行删除记录动作，该语句返回删除记录的数目
        return mDB.delete(tableName, "1=1", null);
    }
}
