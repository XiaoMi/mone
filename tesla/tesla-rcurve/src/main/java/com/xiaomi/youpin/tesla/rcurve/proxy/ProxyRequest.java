package com.xiaomi.youpin.tesla.rcurve.proxy;

import io.netty.handler.codec.http.FullHttpRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 1/2/21
 */
@Data
public class ProxyRequest implements Serializable {

    /**
     * 0 http 1 dubbo 2 grpc
     */
    private int type;

    private Map<String, String> headers;

    private FullHttpRequest request;

    public Map<String, String> headers() {
        return this.headers;
    }

    private String url;

    private String app;

    private String serviceName;

    private String methodName;

    private String[] paramTypes;

    private String[] params;

    private long timeout;


}
