package cn.zhaoxi.zxyx.entity;

import java.io.Serializable;

import lombok.Data;

// 点赞
@Data
public class Like implements Serializable {

    private String userId;
    private String userName;
}
