package cn.zhaoxi.zxyx.module.video.ui.view;

/**
 * Author：J.Chou
 * Date：  2016.08.01 2:23 PM
 * Email： who_know_me@163.com
 * Describe:
 */
public interface VideoTrimListener {
    void onStartTrim();
    void onFinishTrim(String url, int videoTime); //use millseconds
    void onCancel();
}
