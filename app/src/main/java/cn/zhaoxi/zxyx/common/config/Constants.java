package cn.zhaoxi.zxyx.common.config;

/**
 * 常量
 */
public class Constants {

    // 回复已读
    public static boolean isRead = true;

    // action
    public static final String UPDATE_USER_IMG = "me.cl.update.img";

    // 与我相关&我的回复
    public static final String REPLY_TYPE = "reply_type";
    public static final String REPLY_MY = "reply_my";
    public static final String REPLY_RELEVANT = "reply_relevant";

    // 本地缓存key
    public static final String SP_USER_ID = "user_id";
    public static final String SP_USER_NAME = "user_name";
    public static final String SP_BEEN_LOGIN = "been_login";
    public static final String SP_FEED_ID = "feed_id";
    public static final String SP_FEED_VIDEO_ID = "feed_video_id";
    public static final String SP_UPDATE_FLAG = "update_flag";
    public static final String SP_FUTURE_INFO = "sp_future_info";
    public static final String SP_MESSAGE_ID = "message_id";
    public static final String SP_MESSAGE_USER_TIME = "message_user_time";

    // 参数传递
    public static final String PASSED_UNREAD_NUM = "unread_num";
    public static final String PASSED_USER_NAME = "user_name";
    public static final String PASSED_USER_INFO = "user_info";

    // 页面标识
    public static final int ACTIVITY_MAIN = 10001;
    public static final int ACTIVITY_PUBLISH = 10002;
    public static final int ACTIVITY_MOOD = 10003;
    public static final int ACTIVITY_PERSONAL = 10004;
    public static final int ACTIVITY_VIDEO_PUBLISH = 10005;

    // 回退标识
    public static final String GO_INDEX = "go_index";

    // 服务器rss图片
    public static final String IMG_URL = RequestUrl.rssUrl;

    //数据库
    public static final String DB_NAME = "zxyx.db"; // 数据库的名称
    public static final int DB_VERSION = 1; // 数据库的版本号

    public static final String PUBLISH_TYPE = "publish_type";
    public static final String VIDEO_CUT_URL = "video_cut_url";
    public static final String VIDEO_COVER_URL = "video_cover_url";
    public static final String VIDEO_TIME = "video_time";
    public static final Integer PHOTO_TYPE_IMAGE = 0;
    public static final Integer PHOTO_TYPE_VIDEO = 1;
    public static final int FEED_TYPE_TRIM = 5;
    public static final int FEED_TYPE_PHOTO = 0;
    public static final int FEED_TYPE_VIDEO= 1;
}
