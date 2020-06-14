package cn.zhaoxi.zxyx.common.api;

import java.util.List;

import cn.zhaoxi.zxyx.common.result.RetrofitResponse;
import cn.zhaoxi.zxyx.common.result.RetrofitResponseData;
import cn.zhaoxi.zxyx.data.dto.ConversationDto;
import cn.zhaoxi.zxyx.data.dto.FeedDto;
import cn.zhaoxi.zxyx.data.dto.LetterDto;
import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MessageApis {
    /**
     * 获取一页聊天
     * @param postUserId     发送用户id
     * @param receiverUserId  接收用户id
     * @param letterId    当前展示的消息id，服务端返回小于该id的消息。如果当前没有消息，则id为0
     * @param pageSize  需要返回的消息数量
     */
    @POST("/message/letter/page")
    Observable<RetrofitResponseData<List<LetterDto>>> getLetterPage(@Query("postUserId") Long postUserId, @Query("letterId") Long letterId,
                                                                    @Query("receiverUserId") Long receiverUserId, @Query("pageSize") Integer pageSize);

    /**
     * 删除会话
     * @param postUserId     发送方用户id
     * @param receiverUserId    接收方用户id
     */
    @POST("/message/deleteSingleConversation")
    Observable<RetrofitResponse> deleteSingleConversation(@Query("postUserId") Long postUserId,
                                                          @Query("receiverUserId") Long receiverUserId);

    /**
     * 获取一页聊天用户
     * @param userId     用户id
     * @param letterUserTime,    当前展示的最近聊天用户更新时间，服务端返回小于该时间的用户。如果当前没有用户，则为0
     * @param pageSize  需要返回的消息数量
     */
    @POST("/message/page")
    Observable<RetrofitResponseData<List<ConversationDto>>> getMessagePage(@Query("userId") Long userId, @Query("letterUserTime") Long letterUserTime,
                                                                           @Query("pageSize") Integer pageSize);

    /**
     * 发布会话
     * @param postUserId     发送方用户id
     * @param receiverUserId    接收方用户id
     * @param content 发送内容
     */
    @POST("/message/letter/publish")
    Observable<RetrofitResponseData<LetterDto>> publishChat(@Query("postUserId") Long postUserId, @Query("receiverUserId") Long receiverUserId,
                                             @Query("content") String content);
}
