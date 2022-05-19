package com.xiaomi.youpin.feishu.bo;

import lombok.Data;

@Data
public class Result<T> {
    /**
     * 返回码，非 0 表示失败
     */
    private int code;

    /**
     * 返回码描述
     */
    private String msg;

    private T data;
}
