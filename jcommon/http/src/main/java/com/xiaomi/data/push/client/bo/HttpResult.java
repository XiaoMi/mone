package com.xiaomi.data.push.client.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2022/11/24 14:10
 */
@Data
public class HttpResult implements Serializable {

    public int code;
    public String content;
    public byte[] data;
    private Map<String, String> respHeaders;

    public HttpResult(int code, String content, Map<String, String> respHeaders) {
        this.code = code;
        this.content = content;
        this.respHeaders = respHeaders;
    }

    public HttpResult() {
    }
}
