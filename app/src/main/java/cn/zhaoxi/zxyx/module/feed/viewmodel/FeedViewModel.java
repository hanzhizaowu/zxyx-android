package cn.zhaoxi.zxyx.module.feed.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import cn.zhaoxi.zxyx.common.result.RetrofitResponseData;
import cn.zhaoxi.zxyx.data.dto.FeedDto;
import cn.zhaoxi.zxyx.module.feed.repository.FeedRepository;

public class FeedViewModel extends ViewModel {
    private FeedRepository feedRespository = FeedRepository.getInstance();

    public LiveData<RetrofitResponseData<List<FeedDto>>> getFeedPage(Long userId, Long photoId) {
        LiveData<RetrofitResponseData<List<FeedDto>>> results = feedRespository.getFeedPage(userId, photoId);
        return results;
    }
}
