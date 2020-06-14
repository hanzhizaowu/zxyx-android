package cn.zhaoxi.zxyx.module.main.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import cn.zhaoxi.zxyx.common.api.FeedApis;
import cn.zhaoxi.zxyx.common.config.ApiClient;
import cn.zhaoxi.zxyx.common.config.ExceptionMsg;
import cn.zhaoxi.zxyx.common.result.RetrofitResponseData;
import cn.zhaoxi.zxyx.data.dto.FeedDto;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FeedRepository {
    private static final FeedRepository instance = new FeedRepository();

    private FeedRepository() {
    }

    public static FeedRepository getInstance() {
        return instance;
    }

    public LiveData<RetrofitResponseData<List<FeedDto>>> getFeedPage(Long userId, Long photoId, Integer feedType) {
        MutableLiveData<RetrofitResponseData<List<FeedDto>>> loginResult = new MutableLiveData<>();
        FeedApis feedApis = ApiClient.getRetrofit().create(FeedApis.class);

        Observable<RetrofitResponseData<List<FeedDto>>> observable = feedApis.getFeedPage(userId, photoId, 10, feedType);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RetrofitResponseData<List<FeedDto>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RetrofitResponseData<List<FeedDto>> retrofitResponse) {
                        if ((retrofitResponse != null) && (ExceptionMsg.SUCCESS.getCode().equals(retrofitResponse.getCode()))) {
                            loginResult.setValue(retrofitResponse);
                        } else {
                            RetrofitResponseData<List<FeedDto>> retrofitResponseData = new RetrofitResponseData<List<FeedDto>>();
                            retrofitResponseData.setCode(ExceptionMsg.FAILED.getCode());
                            loginResult.setValue(retrofitResponseData);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        RetrofitResponseData<List<FeedDto>> retrofitResponseData = new RetrofitResponseData<List<FeedDto>>();
                        retrofitResponseData.setCode(ExceptionMsg.FAILED.getCode());
                        loginResult.setValue(retrofitResponseData);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return loginResult;
    }
}
