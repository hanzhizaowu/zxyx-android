﻿package cn.zhaoxi.zxyx.common.config;


import cn.zhaoxi.zxyx.BuildConfig;

/**
 * api manage
 */
public class RequestUrl {

    /**
     * token
     */
    public static final String X_APP_TOKEN = "userToken";

    /**
     * 收束gradle的flavor控制，将url变量在此接管
     */
    public static String baseUrl = "xxxx";
    public static String rssUrl = "xxxx";

    static {
        String flavor = BuildConfig.FLAVOR;
        switch (flavor) {
            case "develop":
                baseUrl = "http://101.133.141.80:8080/";
                rssUrl = "http://101.133.141.80:8080/rss";
                break;
            case "integeration":
                baseUrl = "http://101.133.141.80:8080/";
                rssUrl = "http://101.133.141.80:8080/rss";
                break;
            case "online":
                baseUrl = "http://101.133.141.80:8080/";
                rssUrl = "http://101.133.141.80:8080/rss";
                break;
        }
    }

    /**
     * 用户是否登录
     */
    public static String userIsLogin = baseUrl + "/user/islogin";
    /**
     * 用户注册
     */
    public static String userRegister = baseUrl + "/user/register";
    /**
     * 用户登录
     */
    public static String userLogin = baseUrl + "/user/login";
    /**
     * 重置密码
     */
    public static String resetPassword = baseUrl + "/user/reset";
    /**
     * 更新用户信息
     */
    public static String updateUser = baseUrl + "/user/update";
    /**
     * 获取用户信息
     */
    public static String userInfo = baseUrl + "/user/info";
    /**
     * 查询用户信息
     */
    public static String searchUser = baseUrl + "/user/search";
    /**
     * 融云用户列表
     */
    public static String listRcUser = baseUrl + "/user/rc/list";
    /**
     * 动态列表
     */
    public static String pageFeed = baseUrl + "/publish/page";
    /**
     * 发布动态
     */
    public static String saveFeed = baseUrl + "/publish/save";
    /**
     * 查看动态
     */
    public static String viewFeed = baseUrl + "/publish/view";
    /**
     * 与我相关
     */
    public static String relevant = baseUrl + "/publish/relevant";
    /**
     * 我的回复
     */
    public static String mineReply = baseUrl + "/publish/mine/reply";
    /**
     * 新增动态操作,如点赞
     */
    public static String saveAction = baseUrl + "/publish/action/save";
    /**
     * 移除动态操作,如取消赞
     */
    public static String removeAction = baseUrl + "/publish/action/remove";
    /**
     * 动态评论列表
     */
    public static String pageComment = baseUrl + "/publish/comment/page";
    /**
     * 新增动态评论
     */
    public static String saveComment = baseUrl + "/publish/comment/save";
    /**
     * 获取最新app版本
     */
    public static String latestVersion = baseUrl + "/app/version/latest";
    /**
     * 上传用户图片
     */
    public static String uploadUserImage = baseUrl + "/rss/upload/user/image";
    /**
     * 上传动态图片
     */
    public static String uploadFeedImage = baseUrl + "/rss/upload/publish/image";
    /**
     * 未读条数
     */
    public static String unreadComment = baseUrl + "/publish/comment/unread";
    /**
     * 更新未读为已读
     */
    public static String updateUnread = baseUrl + "/publish/comment/unread/update";
    /**
     * 保存写给未来
     */
    public static String saveFuture = baseUrl + "/future/save";
    /**
     * 资源采集
     */
    public static String incApi = baseUrl + "/inc/parse/api";
}
