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
        HttpResponseStatus responseStatus = HttpResponseStatus.OK;
        String status = context.getResHeaders().get("x-status");
        if (null != status) {
            responseStatus = HttpResponseStatus.valueOf(Integer.valueOf(status));
        }
        writeAndFlush(context, responseStatus, message);
    }

    public void clear() {
        this.ctx = null;
    }
}
