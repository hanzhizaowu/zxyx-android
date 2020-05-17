package cn.zhaoxi.zxyx.data.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import cn.zhaoxi.zxyx.data.dto.UserDto;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Entity(indices = {@Index(value = "userName", unique = true), @Index(value = "userId", unique = true)})
public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String userName;
    private Long userId;
    private String userToken;
    private String userAvatar;
    private String userPassword;

    public User() {}

    public User(UserDto userDto) {
        this.userName = userDto.getUserName();
        this.userId = userDto.getUserId();
        this.userToken = userDto.getUserToken();
        this.userPassword = userDto.getUserPassword();
        this.userAvatar = userDto.getUserAvatar();
    }
}
