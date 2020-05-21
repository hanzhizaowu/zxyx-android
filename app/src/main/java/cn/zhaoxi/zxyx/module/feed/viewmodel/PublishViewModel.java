package cn.zhaoxi.zxyx.module.feed.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.util.List;

import cn.zhaoxi.zxyx.common.result.RetrofitResponseData;
import cn.zhaoxi.zxyx.data.dto.FeedDto;
import cn.zhaoxi.zxyx.data.dto.PhotoDto;
import cn.zhaoxi.zxyx.module.feed.repository.PublishRepository;

public class PublishViewModel extends ViewModel {
    private PublishRepository publishRespository = PublishRepository.getInstance();

    public LiveData<RetrofitResponseData<List<String>>> publishPhotos(List<File> files) {
        LiveData<RetrofitResponseData<List<String>>> results = publishRespository.publishPhotos(files);
        return results;
    }

    public LiveData<RetrofitResponseData<FeedDto>> saveFeed(Long postUserId, List<PhotoDto> photos, String feedTitle, String feedDescription) {
        LiveData<RetrofitResponseData<FeedDto>> results = publishRespository.saveFeed(postUserId, photos, feedTitle, feedDescription);
        return results;
    }
}
