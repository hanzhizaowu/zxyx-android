package cn.zhaoxi.zxyx.data.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PhotoDto implements Serializable {
    private Long photoId;
    private String url;
    private Integer photoHeight;
    private Integer photoWidth;
}
