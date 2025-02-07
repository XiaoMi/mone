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

package com.xiaomi.youpin.docean.mvc;

import com.google.gson.JsonElement;
import com.xiaomi.youpin.docean.mvc.session.HttpSession;
import com.xiaomi.youpin.docean.mvc.session.HttpSessionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2020/6/21
 */
@Data
public class MvcContext {

    private String traceId;

    /**
     * rate limited or exceeded quota
     */
    private boolean virtualThread;

    private Map<String, String> attachments;

    private Map<String, String> headers;

    /**
     * Users can modify the headers of the returned results.
     */
    private Map<String, String> resHeaders = new HashMap<>();

    private JsonElement params;

    private boolean websocket;

    private boolean cookie;

    private HttpSession session;

    private FullHttpRequest request;

    private boolean sync = false;

    private Object response;

    private String contentType = "application/json; charset=utf-8";

    /**
     * rate limited or exceeded quota
     */
    private boolean allowCross;

    /**
     * cluster session
     */
    private boolean clusterSession;

    public HttpSession session() {
        if (null == session) {
            this.session = HttpSessionManager.getSession(this);
        }
        return this.session;
    }

    private String sessionId = "";

    private String method;

    private ChannelHandlerContext handlerContext;

    private String path;

    public void clear() {
        this.traceId = null;
        if (null != this.attachments) {
            this.attachments.clear();
        }
        if (null != this.headers) {
            this.headers.clear();
        }
        if (null != this.resHeaders) {
            this.resHeaders.clear();
        }
    }

}
