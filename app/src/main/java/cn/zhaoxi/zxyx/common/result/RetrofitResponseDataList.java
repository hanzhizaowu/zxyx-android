package cn.zhaoxi.zxyx.common.result;

import java.util.List;

import lombok.Data;

@Data
public class RetrofitResponseDataList<T> {
    private Integer code;
    private String msg;
    private List<T> data;
}
