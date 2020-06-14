package cn.zhaoxi.zxyx.module.video.util;

import android.content.Context;

import cn.zhaoxi.library.util.SimpleCallback;

public class VideoLoadManager {

    private ILoader mLoader;

    public void setLoader(ILoader loader) {
        this.mLoader = loader;
    }

    public void load(final Context context, final SimpleCallback listener) {
        mLoader.load(context, listener);
    }
}
