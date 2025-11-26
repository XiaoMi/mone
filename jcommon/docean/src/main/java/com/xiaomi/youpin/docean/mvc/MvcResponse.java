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

import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.mvc.session.HttpSessionManager;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.Getter;
import lombok.Setter;

/**
 * @author goodjava@qq.com
 * @date 2020/6/21
 */
public class MvcResponse {

    @Getter
    @Setter
    private ChannelHandlerContext ctx;

    public void writeAndFlush(MvcContext context, HttpResponseStatus status, String message) {
        //websocket发送
        if (context.isWebsocket()) {
            TextWebSocketFrame frame = new TextWebSocketFrame(message);
            ctx.writeAndFlush(frame);
        } else {
            FullHttpResponse response = HttpResponseUtils.create(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.wrappedBuffer(message.getBytes())));
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, message.getBytes().length);
            if (StringUtils.isNotEmpty(context.getContentType())) {
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, context.getContentType());
            }
            context.getResHeaders().forEach((k, v) -> response.headers().set(k, v));
            if (context.isAllowCross()) {
                response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
                response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "*");
                response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT,DELETE");
                response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            }
            if (context.isCookie()) {
                HttpSessionManager.setSessionId(context, HttpSessionManager.isHasSessionId(context.getHeaders()), response);
            }
            if (StringUtils.isNotEmpty(context.getRequest().headers().get(HttpHeaderNames.CONNECTION))) {
                response.headers().add(HttpHeaderNames.CONNECTION, context.getRequest().headers().get(HttpHeaderNames.CONNECTION));
            }
            ctx.writeAndFlush(response);
        }
    }


    public void writeAndFlush(MvcContext context, String message) {
        writeAndFlush(context, message, HttpResponseStatus.OK.code());
    }

    public void writeAndFlush(MvcContext context, String message, int code) {
        HttpResponseStatus responseStatus = HttpResponseStatus.valueOf(code);
        String status = context.getResHeaders().get("x-status");
        if (null != status) {
            responseStatus = HttpResponseStatus.valueOf(Integer.valueOf(status));
        }
        writeAndFlush(context, responseStatus, message);
    }

    /**
     * Initialize Server-Sent Events (SSE) connection
     *
     * @param context The MVC context
     */
    public void initSSE(MvcContext context) {
        if (context.isWebsocket()) {
            // SSE not supported for WebSocket
            return;
        }

        // Create HTTP response with headers but no content yet
        DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/event-stream");
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, "no-cache");
        response.headers().set(HttpHeaderNames.CONNECTION, "keep-alive");
        response.headers().set("X-Accel-Buffering", "no"); // Disable nginx buffering if used

        // Add client-specified response headers 
        context.getResHeaders().forEach((k, v) -> response.headers().set(k, v));

        // Add CORS headers if needed
        if (context.isAllowCross()) {
            response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "*");
            response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE");
            response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        }

        // Add session cookie if needed
        if (context.isCookie()) {
            HttpSessionManager.setSessionId(context, HttpSessionManager.isHasSessionId(context.getHeaders()), response);
        }

        // Send the response headers
        ctx.writeAndFlush(response);

        // Send SSE initial connection message
        sendSSEEvent(context, "", "connection-setup", "");
    }

    /**
     * Send an SSE event with data
     *
     * @param context The MVC context
     * @param data The data to send
     */
    public void sendSSEEvent(MvcContext context, String data) {
        sendSSEEvent(context, data, null, null);
    }

    /**
     * Send an SSE event with data, event type, and ID
     *
     * @param context The MVC context
     * @param data The data to send
     * @param event The event type
     * @param id The event ID
     */
    public void sendSSEEvent(MvcContext context, String data, String event, String id) {
        if (context.isWebsocket()) {
            // SSE not supported for WebSocket
            return;
        }

        StringBuilder builder = new StringBuilder();
        
        // Add event ID if provided
        if (StringUtils.isNotEmpty(id)) {
            builder.append("id: ").append(id).append("\n");
        }
        
        // Add event type if provided
        if (StringUtils.isNotEmpty(event)) {
            builder.append("event: ").append(event).append("\n");
        }
        
        // Add data (split multi-line data)
        if (data != null) {
            for (String line : data.split("\n")) {
                builder.append("data: ").append(line).append("\n");
            }
        }
        
        // End the event with a blank line
        builder.append("\n");
        
        // Create and send the event
        DefaultHttpContent content = new DefaultHttpContent(
            Unpooled.wrappedBuffer(builder.toString().getBytes())
        );
        ctx.writeAndFlush(content);
    }

    /**
     * Close the SSE connection
     *
     * @param context The MVC context
     */
    public void closeSSE(MvcContext context) {
        if (context.isWebsocket()) {
            // SSE not supported for WebSocket
            return;
        }

        // Send a final message indicating the stream is closing
        sendSSEEvent(context, "Stream closed", "close", null);
        
        // End the response
        ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
    }

    public void clear() {
        this.ctx = null;
    }
}
