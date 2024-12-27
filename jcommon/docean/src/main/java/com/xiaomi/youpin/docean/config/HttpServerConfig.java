/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.docean.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author goodjava@qq.com
 */
@Data
@Builder
@AllArgsConstructor
public class HttpServerConfig {

    private boolean ssl;

    @Builder.Default
    private HttpVersion httpVersion = HttpVersion.http1;

    private boolean websocket;

    private int port;

    @Builder.Default
    private boolean cookie = true;

    /**
     * Allow file upload
     */
    private boolean upload;

    /**
     * Upload path
     */
    private String uploadDir;

    private boolean useWs;

    public static int HTTP_POOL_SIZE = 500;

    public static int HTTP_POOL_QUEUE_SIZE = 1000;

    public enum HttpVersion {
        http1, http2
    }

    public HttpServerConfig(boolean ssl, boolean websocket, int port) {
        this.ssl = ssl;
        this.websocket = websocket;
        this.port = port;
    }

    public HttpServerConfig() {
    }
}
