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
            HttpSessionManager.setSessionId(context,HttpSessionManager.isHasSessionId(context.getHeaders()),response);
            ctx.writeAndFlush(response);
        }
    }


    public void writeAndFlush(MvcContext context, String message) {
        writeAndFlush(context, HttpResponseStatus.OK, message);
    }
}
