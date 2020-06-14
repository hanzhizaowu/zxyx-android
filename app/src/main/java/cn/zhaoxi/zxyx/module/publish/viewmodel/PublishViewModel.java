package cn.zhaoxi.zxyx.module.publish.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.util.List;

import cn.zhaoxi.zxyx.common.result.RetrofitResponseData;
import cn.zhaoxi.zxyx.data.dto.FeedDto;
import cn.zhaoxi.zxyx.data.dto.PhotoDto;
import cn.zhaoxi.zxyx.data.dto.VideoDto;
import cn.zhaoxi.zxyx.module.publish.repository.PublishRepository;

public class PublishViewModel extends ViewModel {
    private PublishRepository publishRespository = PublishRepository.getInstance();

    public LiveData<RetrofitResponseData<List<PhotoDto>>> publishPhotos(List<File> files, Long userId) {
        LiveData<RetrofitResponseData<List<PhotoDto>>> results = publishRespository.publishPhotos(files,userId);
        return results;
    }

    public LiveData<RetrofitResponseData<FeedDto>> saveFeed(Long postUserId, List<PhotoDto> photos, String feedTitle, String feedDescription) {
        LiveData<RetrofitResponseData<FeedDto>> results = publishRespository.saveFeed(postUserId, photos, feedTitle, feedDescription);
        return results;
    }

    public LiveData<RetrofitResponseData<VideoDto>> publishVideo(List<File> files, Long userId) {
        LiveData<RetrofitResponseData<VideoDto>> results = publishRespository.publishVideo(files, userId);
        return results;
    }

    public LiveData<RetrofitResponseData<FeedDto>> saveVideoFeed(Long postUserId, VideoDto videoDto, String feedTitle, String feedDescription) {
        LiveData<RetrofitResponseData<FeedDto>> results = publishRespository.saveVideoFeed(postUserId, videoDto, feedTitle, feedDescription);
        return results;
    }
}
