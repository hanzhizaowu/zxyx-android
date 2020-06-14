package cn.zhaoxi.zxyx.data.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class VideoDto implements Serializable {
    private Long videoId;
    private String videoUrl;
    private Integer videoTime;
    private String videoCover;
    private Integer coverWidth;
    private Integer coverHeight;
}
