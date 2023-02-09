package com.xiaomi.youpin.gitlab.bo;

import lombok.Data;

@Data
public class BaseResponse {
    private int code;
    private String message;
    //private T data;

    public BaseResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
