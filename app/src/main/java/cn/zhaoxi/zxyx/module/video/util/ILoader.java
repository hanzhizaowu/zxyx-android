package cn.zhaoxi.zxyx.module.video.util;

import android.content.Context;
import android.provider.MediaStore;

import cn.zhaoxi.library.util.SimpleCallback;

public interface ILoader {
    String SELECTION = MediaStore.Files.FileColumns.MEDIA_TYPE
            + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

    String[] SELECTION_ARGS = {
            "video/mp4",
            "video/3gp",
            "video/aiv",
            "video/rmvb",
            "video/vob",
            "video/flv",
            "video/mkv",
            "video/mov",
            "video/mpg"
    };

    String ORDER_BY = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";

    String[] MEDIA_PROJECTION = {
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.PARENT
    };

    void load(final Context mContext, final SimpleCallback listener);
}
