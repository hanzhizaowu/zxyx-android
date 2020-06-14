package cn.zhaoxi.zxyx.module.main.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import cn.zhaoxi.zxyx.common.api.MessageApis;
import cn.zhaoxi.zxyx.common.api.UserApis;
import cn.zhaoxi.zxyx.common.config.ApiClient;
import cn.zhaoxi.zxyx.common.config.ExceptionMsg;
import cn.zhaoxi.zxyx.common.result.RetrofitResponse;
import cn.zhaoxi.zxyx.common.result.RetrofitResponseData;
import cn.zhaoxi.zxyx.data.dto.ConversationDto;
import cn.zhaoxi.zxyx.data.dto.LetterDto;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MessageRepository {
    private static final MessageRepository instance = new MessageRepository();

    private MessageRepository() {
    }

    public static MessageRepository getInstance() {
        return instance;
    }

    public LiveData<RetrofitResponseData<List<ConversationDto>>> getMessagePage(Long userId, Long msgTime) {
        MutableLiveData<RetrofitResponseData<List<ConversationDto>>> conversationResult = new MutableLiveData<>();
        MessageApis messageApis = ApiClient.getRetrofit().create(MessageApis.class);

        Observable<RetrofitResponseData<List<ConversationDto>>> observable = messageApis.getMessagePage(userId, msgTime, 10);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RetrofitResponseData<List<ConversationDto>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RetrofitResponseData<List<ConversationDto>> retrofitResponse) {
                        if ((retrofitResponse != null) && (ExceptionMsg.SUCCESS.getCode().equals(retrofitResponse.getCode()))) {
                            conversationResult.setValue(retrofitResponse);
                        } else {
                            RetrofitResponseData<List<ConversationDto>> retrofitResponseData = new RetrofitResponseData<List<ConversationDto>>();
                            retrofitResponseData.setCode(ExceptionMsg.FAILED.getCode());
                            conversationResult.setValue(retrofitResponseData);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        RetrofitResponseData<List<ConversationDto>> retrofitResponseData = new RetrofitResponseData<List<ConversationDto>>();
                        retrofitResponseData.setCode(ExceptionMsg.FAILED.getCode());
                        conversationResult.setValue(retrofitResponseData);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return conversationResult;
    }

    public LiveData<Boolean> deleteSingleConversation(Long postUserId, Long receiverUserId) {
        final MutableLiveData<Boolean> deleteResult = new MutableLiveData<>();

        MessageApis messageApis = ApiClient.getRetrofit().create(MessageApis.class);
        //对 发送请求 进行封装
        Observable<RetrofitResponse> observable = messageApis.deleteSingleConversation(postUserId, receiverUserId);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RetrofitResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RetrofitResponse retrofitResponse) {
                        if (retrofitResponse != null) {
                            if (ExceptionMsg.SUCCESS.getCode().equals(retrofitResponse.getCode())) {
                                deleteResult.setValue(true);
                            } else {
                                deleteResult.setValue(false);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        deleteResult.setValue(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return deleteResult;
    }

    public LiveData<RetrofitResponseData<List<LetterDto>>> getLetterPage(Long postUserId, Long letterId, Long receiverUserId, Integer pageSize) {
        MutableLiveData<RetrofitResponseData<List<LetterDto>>> chatResult = new MutableLiveData<>();
        MessageApis messageApis = ApiClient.getRetrofit().create(MessageApis.class);

        Observable<RetrofitResponseData<List<LetterDto>>> observable = messageApis.getLetterPage(postUserId, letterId, receiverUserId,10);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RetrofitResponseData<List<LetterDto>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RetrofitResponseData<List<LetterDto>> retrofitResponse) {
                        if ((retrofitResponse != null) && (ExceptionMsg.SUCCESS.getCode().equals(retrofitResponse.getCode()))) {
                            chatResult.setValue(retrofitResponse);
                        } else {
                            RetrofitResponseData<List<LetterDto>> retrofitResponseData = new RetrofitResponseData<List<LetterDto>>();
                            retrofitResponseData.setCode(ExceptionMsg.FAILED.getCode());
                            chatResult.setValue(retrofitResponseData);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        RetrofitResponseData<List<LetterDto>> retrofitResponseData = new RetrofitResponseData<List<LetterDto>>();
                        retrofitResponseData.setCode(ExceptionMsg.FAILED.getCode());
                        chatResult.setValue(retrofitResponseData);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return chatResult;
    }

    public LiveData<RetrofitResponseData<LetterDto>> publishChat(Long postUserId, Long receiverUserId, String content) {
        final MutableLiveData<RetrofitResponseData<LetterDto>> publishResult = new MutableLiveData<>();

        MessageApis messageApis = ApiClient.getRetrofit().create(MessageApis.class);
        //对 发送请求 进行封装
        Observable<RetrofitResponseData<LetterDto>> observable = messageApis.publishChat(postUserId, receiverUserId, content);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RetrofitResponseData<LetterDto>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RetrofitResponseData<LetterDto> retrofitResponse) {
                        if (retrofitResponse != null) {
                            if (ExceptionMsg.SUCCESS.getCode().equals(retrofitResponse.getCode())) {
                                publishResult.setValue(retrofitResponse);
                            } else {
                                RetrofitResponseData<LetterDto> retrofitResponseData = new RetrofitResponseData<LetterDto>();
                                retrofitResponseData.setCode(ExceptionMsg.FAILED.getCode());
                                publishResult.setValue(retrofitResponseData);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        RetrofitResponseData<LetterDto> retrofitResponseData = new RetrofitResponseData<LetterDto>();
                        retrofitResponseData.setCode(ExceptionMsg.FAILED.getCode());
                        publishResult.setValue(retrofitResponseData);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return publishResult;
    }
}
