package com.xiaomi.youpin.tesla.rcurve.proxy;

import io.netty.handler.codec.http.FullHttpResponse;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 1/2/21
 */
@Data
public class ProxyResponse implements Serializable {

    private int code;

    private String message;

    private String data;

    private FullHttpResponse fullHttpResponse;

    public ProxyResponse() {
    }


    public ProxyResponse(String data) {
        this.data = data;
    }

    public ProxyResponse(FullHttpResponse r) {
        this.fullHttpResponse = r;
    }
}
