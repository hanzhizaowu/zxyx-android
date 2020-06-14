package cn.zhaoxi.zxyx.data.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class LetterDto implements Serializable {
    private Long letterId;
    private UserDto postUser;
    private UserDto receiverUser;
    private String content;
    private Long createTime;
    private Integer letterState; //0:未读;1:已读
}
