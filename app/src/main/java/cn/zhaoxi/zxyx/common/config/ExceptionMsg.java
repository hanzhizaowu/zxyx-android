package cn.zhaoxi.zxyx.common.config;

public enum ExceptionMsg {
    SUCCESS(0, "操作成功"),
    FAILED(1,"操作失败"),
    ParamError(1001, "参数错误！"),
    LoginNameOrPassWordError(1002, "用户名或者密码错误！"),
    EmailUsed(1003,"该邮箱已被注册"),
    UserNameUsed(1004,"该登录名称已存在"),
    EmailNotRegister(1005,"该邮箱地址未注册"),
    PassWordError(1006,"密码输入错误"),
    LoginNameNotExists(1007,"该用户未注册"),
    UserNameSame(1008,"新用户名与原用户名一致"),
    MobileUsed(1009,"该手机号已被注册"),
    FileEmpty(1010,"上传文件为空"),
    LimitPictureSize(1011,"图片大小必须小于2M"),
    LimitPictureType(1012,"图片格式必须为'jpg'、'png'、'jpge'、'gif'、'bmp'");

    private ExceptionMsg(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    private Integer code;
    private String msg;

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
