package cn.zhaoxi.zxyx.module.video.util;

import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;

public class VideoCompressListener extends ExecuteBinaryResponseHandler {
    @Override public void onSuccess(String url) {
    }

    @Override public void onFailure(String message) {
    }

    @Override
    public void onProgress(String message) {

    }
}
