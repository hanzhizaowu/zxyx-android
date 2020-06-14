package cn.zhaoxi.zxyx.module.main.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import cn.zhaoxi.zxyx.common.result.RetrofitResponse;
import cn.zhaoxi.zxyx.common.result.RetrofitResponseData;
import cn.zhaoxi.zxyx.data.dto.ConversationDto;
import cn.zhaoxi.zxyx.data.dto.LetterDto;
import cn.zhaoxi.zxyx.module.main.repository.MessageRepository;

public class MessageViewModel extends ViewModel {
    private MessageRepository messageRepository = MessageRepository.getInstance();

    public LiveData<RetrofitResponseData<List<ConversationDto>>> getMessagePage(Long userId, Long msgTime) {
        LiveData<RetrofitResponseData<List<ConversationDto>>> results = messageRepository.getMessagePage(userId, msgTime);
        return results;
    }

    public LiveData<Boolean> deleteSingleConversation(Long postUserId, Long receiverUserId) {
        LiveData<Boolean> result = messageRepository.deleteSingleConversation(postUserId, receiverUserId);
        return result;
    }

    public LiveData<RetrofitResponseData<List<LetterDto>>> getLetterPage(Long userId, Long letterId, Long receiverUserId, Integer pageSize) {
        LiveData<RetrofitResponseData<List<LetterDto>>> results = messageRepository.getLetterPage(userId, letterId, receiverUserId, pageSize);
        return results;
    }

    public LiveData<RetrofitResponseData<LetterDto>> publishChat(Long postUserId, Long receiverUserId, String content) {
        LiveData<RetrofitResponseData<LetterDto>> result = messageRepository.publishChat(postUserId, receiverUserId, content);
        return result;
    }
}
