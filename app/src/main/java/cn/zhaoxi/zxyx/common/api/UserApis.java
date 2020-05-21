package cn.zhaoxi.zxyx.common.api;

import cn.zhaoxi.zxyx.common.result.RetrofitResponse;
import cn.zhaoxi.zxyx.common.result.RetrofitResponseData;
import cn.zhaoxi.zxyx.data.dto.UserDto;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface UserApis {
    /**
     * 用户登录
     * @param userName     用户名
     * @param userPassword 用户密码
     */
    @POST("user/login")
    Observable<RetrofitResponseData<UserDto>> postUserLogin(@Query("userName") String userName, @Query("userPassword") String userPassword);

    /**
     * 用户登录校验
     * @param userToken     用户token
     */
    @POST("user/islogin")
    Observable<RetrofitResponse> postUserIsLogin(@Query("userToken") String userToken);

    /**
     * 用户更改密码
     * @param userName     用户名
     * @param userMobile     用户手机号
     * @param userPassword     用户密码
     */
    @POST("user/resetPassword")
    Observable<RetrofitResponse> postUserResetPassword(@Query("userName") String userName, @Query("userMobile") String userMobile,
                                                 @Query("userPassword") String userPassword);

    /**
     * 用户注册
     * @param userName     用户名
     * @param userMobile     用户手机号
     * @param userPassword     用户密码
     */
    @POST("user/register")
    Observable<RetrofitResponseData<UserDto>> postUserRegister(@Query("userName") String userName, @Query("userMobile") String userMobile,
                                                 @Query("userPassword") String userPassword);

    /**
     * 用户信息
     * @param userId     用户id
     */
    @POST("user/info")
    Observable<RetrofitResponseData<UserDto>> postUserInfo(@Query("userId") Long userId);

    /**
     * 用户头像上传
     * @param file     用户头像文件
     */
    @Multipart
    @POST("rss/upload/user/image")
    Observable<RetrofitResponseData<UserDto>> uploadUserImage(@Part MultipartBody.Part file);

    /**
     * 用户信息更改
     * @param userName     用户名
     * @param userId     用户id
     */
    @POST("user/updateInfo")
    Observable<RetrofitResponseData<UserDto>> uploadUserInfo(@Query("userName") String userName, @Query("userId") Long userId);
}
