package cn.zhaoxi.zxyx.module.video.videoplayer.videocache.sourcestorage;

import cn.zhaoxi.zxyx.module.video.videoplayer.videocache.SourceInfo;

/**
 * {@link SourceInfoStorage} that does nothing.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class NoSourceInfoStorage implements SourceInfoStorage {

    @Override
    public SourceInfo get(String url) {
        return null;
    }

    @Override
    public void put(String url, SourceInfo sourceInfo) {
    }

    @Override
    public void release() {
    }
}
