package com.xiaomi.youpin.docean.plugin.http;


import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 */
@Data
public class Response implements Serializable {
    private int code;
    private byte[] data;

    public Response(int code, byte[] data) {
        this.code = code;
        this.data = data;
    }
}
