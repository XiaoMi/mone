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

    private Map<String, String> attachments;

    private Map<String, String> headers;

    /**
     * 用户可以修改返回结果的headers
     */
    private Map<String, String> resHeaders = new HashMap<>();

    private JsonElement params;

    private boolean websocket;

    private HttpSession session;

    private FullHttpRequest request;

    private boolean sync = false;

    private Object response;

    /**
     * 是否允许跨域
     */
    private boolean allowCross;

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

}
