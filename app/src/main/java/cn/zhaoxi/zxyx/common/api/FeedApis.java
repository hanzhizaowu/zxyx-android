package cn.zhaoxi.zxyx.common.api;

import java.util.List;

import cn.zhaoxi.zxyx.common.result.RetrofitResponseData;
import cn.zhaoxi.zxyx.data.dto.FeedDto;
import cn.zhaoxi.zxyx.data.dto.PhotoDto;
import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface FeedApis {
    /**
     * 帖子图片上传
     * @param requestBody     帖子文件body
     */
    @POST("/rss/upload/feed/image")
    Observable<RetrofitResponseData<List<String>>> postPhotos(@Body RequestBody requestBody);

    /**
     * 保存帖子
     * @param requestBody     帖子vo参数
     */
    @POST("/feed/save")
    Observable<RetrofitResponseData<FeedDto>> saveFeed(@Body RequestBody requestBody);

    /**
     * 获取一页帖子
     * @param userId     用户id
     * @param photoId    当前展示的帖子id，服务端返回大于该id的帖子。如果当前没有帖子，则帖子id为0
     */
    @POST("/feed/page")
    Observable<RetrofitResponseData<List<FeedDto>>> getFeedPage(@Query("userId") Long userId, @Query("photoId") Long photoId, @Query("pageSize") Integer pageSize);
}
