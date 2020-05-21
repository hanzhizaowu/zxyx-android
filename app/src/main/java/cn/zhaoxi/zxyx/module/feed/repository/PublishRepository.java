package cn.zhaoxi.zxyx.module.feed.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.List;

import cn.zhaoxi.library.BuildConfig;
import cn.zhaoxi.zxyx.common.api.FeedApis;
import cn.zhaoxi.zxyx.common.config.ApiClient;
import cn.zhaoxi.zxyx.common.config.ExceptionMsg;
import cn.zhaoxi.zxyx.common.result.RetrofitResponseData;
import cn.zhaoxi.zxyx.data.dto.FeedDto;
import cn.zhaoxi.zxyx.data.dto.PhotoDto;
import cn.zhaoxi.zxyx.data.dto.UserDto;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PublishRepository {
    private static final PublishRepository instance = new PublishRepository();

    private PublishRepository() {
    }

    public static PublishRepository getInstance() {
        return instance;
    }

    public LiveData<RetrofitResponseData<List<String>>> publishPhotos(List<File> files) {
        MutableLiveData<RetrofitResponseData<List<String>>> uploadResult = new MutableLiveData<>();
        FeedApis feedApis = ApiClient.getRetrofit().create(FeedApis.class);
        //对 发送请求 进行封装
        RequestBody requestBody = getMultipartBody(files);
        Observable<RetrofitResponseData<List<String>>> observable = feedApis.postPhotos(requestBody);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RetrofitResponseData<List<String>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RetrofitResponseData<List<String>> retrofitResponse) {
                        uploadResult.setValue(retrofitResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        RetrofitResponseData<List<String>> retrofitResponse = new RetrofitResponseData<>();
                        retrofitResponse.setCode(ExceptionMsg.FAILED.getCode());
                        uploadResult.setValue(retrofitResponse);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return uploadResult;
    }

    public LiveData<RetrofitResponseData<FeedDto>> saveFeed(Long postUserId, List<PhotoDto> photos, String feedTitle, String feedDescription) {
        MutableLiveData<RetrofitResponseData<FeedDto>> uploadResult = new MutableLiveData<>();

        FeedDto feedDto = new FeedDto();
        feedDto.setPhotos(photos);
        feedDto.setFeedTitle(feedTitle);
        feedDto.setFeedDescription(feedDescription);
        UserDto userDto = new UserDto();
        userDto.setUserId(postUserId);
        feedDto.setPostUser(userDto);

        Gson gson = new Gson();
        String json = gson.toJson(feedDto);
        if (BuildConfig.DEBUG) Log.i("PublishRepository", "json is :" + json);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),json);

        FeedApis feedApis = ApiClient.getRetrofit().create(FeedApis.class);
        Observable<RetrofitResponseData<FeedDto>> observable = feedApis.saveFeed(body);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RetrofitResponseData<FeedDto>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RetrofitResponseData<FeedDto> retrofitResponse) {
                        uploadResult.setValue(retrofitResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        RetrofitResponseData<FeedDto> retrofitResponse = new RetrofitResponseData<>();
                        retrofitResponse.setCode(ExceptionMsg.FAILED.getCode());
                        uploadResult.setValue(retrofitResponse);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return uploadResult;
    }

    /**
     * Multipart body
     */
    private RequestBody getMultipartBody(List<File> files) {
        MultipartBody.Builder multipartBodybuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        for (File file : files) {
            RequestBody fileBody = RequestBody.create(getMimeType(file.getName()), file);
            multipartBodybuilder.addFormDataPart("files", file.getName(), fileBody);
        }
        return multipartBodybuilder.build();
    }

    /**
     * 根据文件名获取MIME类型
     */
    private MediaType getMimeType(String fileName) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        // 解决文件名中含有#号异常的问题
        fileName = fileName.replace("#", "");
        String contentType = fileNameMap.getContentTypeFor(fileName);
        if (contentType == null) {
            return MediaType.parse("application/octet-stream");
        }
        return MediaType.parse(contentType);
    }
}
