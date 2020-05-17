package cn.zhaoxi.zxyx.common.result;

import lombok.Data;

@Data
public class RetrofitResponseData<T> {
    private Integer code;
    private String msg;
    private T data;
}
