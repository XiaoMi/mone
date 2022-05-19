package com.xiaomi.youpin.docean.config;

import lombok.Builder;
import lombok.Data;

/**
 * @author goodjava@qq.com
 */
@Data
@Builder
public class HttpServerConfig {

    private boolean ssl;

    private boolean websocket;

    private int port;

    public static int HTTP_POOL_SIZE = 500;
    public static int HTTP_POOL_QUEUE_SIZE = 1000;

    public HttpServerConfig(boolean ssl, boolean websocket, int port) {
        this.ssl = ssl;
        this.websocket = websocket;
        this.port = port;
    }

    public HttpServerConfig() {
    }
}
