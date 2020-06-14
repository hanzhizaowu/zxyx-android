package cn.zhaoxi.zxyx.module.video.util;

public interface VideoCoverListener {
    void onStartCover();
    void onFinishCover(String videoUrl, String coverUrl);
    void onFailCover();
}
