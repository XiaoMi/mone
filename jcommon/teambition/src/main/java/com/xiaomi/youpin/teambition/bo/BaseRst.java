package com.xiaomi.youpin.teambition.bo;

import lombok.Data;

@Data
public class BaseRst<T> {
    private int code;
    private String errorMessage;
    private int count;
    private String nextPageToken;
    private T result;
}
