package cn.zhaoxi.zxyx.module.video.videoplayer.videocache;

import cn.zhaoxi.zxyx.module.video.videoplayer.videocache.file.DiskUsage;
import cn.zhaoxi.zxyx.module.video.videoplayer.videocache.file.FileNameGenerator;
import cn.zhaoxi.zxyx.module.video.videoplayer.videocache.headers.HeaderInjector;
import cn.zhaoxi.zxyx.module.video.videoplayer.videocache.sourcestorage.SourceInfoStorage;

import java.io.File;

/**
 * Configuration for proxy cache.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
class Config {

    public final File cacheRoot;
    public final FileNameGenerator fileNameGenerator;
    public final DiskUsage diskUsage;
    public final SourceInfoStorage sourceInfoStorage;
    public final HeaderInjector headerInjector;

    Config(File cacheRoot, FileNameGenerator fileNameGenerator, DiskUsage diskUsage, SourceInfoStorage sourceInfoStorage, HeaderInjector headerInjector) {
        this.cacheRoot = cacheRoot;
        this.fileNameGenerator = fileNameGenerator;
        this.diskUsage = diskUsage;
        this.sourceInfoStorage = sourceInfoStorage;
        this.headerInjector = headerInjector;
    }

    File generateCacheFile(String url) {
        String name = fileNameGenerator.generate(url);
        return new File(cacheRoot, name);
    }

}
