package com.xiaomi.youpin.gateway.sidecar;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2022/6/20 16:36
 */
@Data
@AllArgsConstructor
public class FilterResponse implements Serializable {

    public static final int FAILURE = 9999;

    private Map<String,String> headers = new HashMap<>();

    private Map<String,String> attachments = new HashMap<>();

    private int code;

    private String message;

    private byte[] data;

    public FilterResponse(int code, String message, byte[] data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
