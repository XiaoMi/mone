package com.xiaomi.youpin.gateway.sidecar;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2022/6/20 16:37
 */
@Data
public class FilterRequest implements Serializable {

    private Map<String, String> headers = new HashMap<>();

    private String method;

    private String queryString;

    private byte[] body;

    public String method() {
        return method;
    }

    public Map<String, String> headers() {
        return headers;
    }
}
