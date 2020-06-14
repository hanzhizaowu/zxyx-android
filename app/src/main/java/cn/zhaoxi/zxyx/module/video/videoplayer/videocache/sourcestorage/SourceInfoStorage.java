package cn.zhaoxi.zxyx.module.video.videoplayer.videocache.sourcestorage;

import cn.zhaoxi.zxyx.module.video.videoplayer.videocache.SourceInfo;

/**
 * Storage for {@link SourceInfo}.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public interface SourceInfoStorage {

    SourceInfo get(String url);

    void put(String url, SourceInfo sourceInfo);

    void release();
}
