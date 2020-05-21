package cn.zhaoxi.zxyx.data.dto;

import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FeedDto implements Serializable {
    private Long feedId;
    private String feedTitle;
    private String feedDescription;
    private UserDto postUser;
    private List<PhotoDto> photos;
    private Long createTime;
    private Long updateTime;
    private Integer feedType;
}
