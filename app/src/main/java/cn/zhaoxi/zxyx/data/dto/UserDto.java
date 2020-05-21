package cn.zhaoxi.zxyx.data.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserDto {
    private String userName;
    private Long userId;
    private String userToken;
    private String userAvatar;
    private String userPassword;
    private String userMobile;
    private String userEmail;
    private String userNewPassword;
    private Integer userSex;
    private String userSignature;
}
