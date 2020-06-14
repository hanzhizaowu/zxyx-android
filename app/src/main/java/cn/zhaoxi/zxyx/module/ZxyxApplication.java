package cn.zhaoxi.zxyx.module;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import cn.zhaoxi.zxyx.common.util.SPUtil;
import cn.zhaoxi.zxyx.common.database.DBHelper;
import cn.zhaoxi.zxyx.module.user.repository.UserDataSource;
import nl.bravobit.ffmpeg.FFmpeg;

public class ZxyxApplication extends Application {
    private static DBHelper dbHelper;
    private static UserDataSource userDataSource;

    @Override
    public void onCreate() {
        super.onCreate();

        // 一定要注册
        SPUtil.newInstance().init(this);
        dbHelper = DBHelper.getInstance(this, 0);
        userDataSource = new UserDataSource(this);
        initFFmpegBinary(this);
        // 根据需求使用
        //initOkUtil();
    }

    private void initFFmpegBinary(Context context) {
        if (!FFmpeg.getInstance(context).isSupported()) {
            Log.e("ZApplication","Android cup arch not supported!");
        }
    }

    public static DBHelper getDbHelperInstance() {
        return dbHelper;
    }

    public static UserDataSource getUserDataSoure() {return userDataSource;}

    /**
     * 初始化OkUtil
     */
    /*private void initOkUtil() {
        // 公共请求头
        Map<String, String> headers = new HashMap<>(1);
        String token = SPUtil.build().getString(RequestUrl.X_APP_TOKEN);
        headers.put(RequestUrl.X_APP_TOKEN, token);
        // 注册添加公共请求头
        OkUtil.newInstance().init(this)
                .addCommonHeaders(headers);
    }*/
}
