package com.xiaomi.youpin.docean.mvc;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.mvc.session.HttpSessionManager;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

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
            context.getResHeaders().forEach((k, v) -> response.headers().set(k, v));
            if (context.isAllowCross()) {
                response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
                response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "*");
                response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT,DELETE");
                response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            }
            HttpSessionManager.setSessionId(context, HttpSessionManager.isHasSessionId(context.getHeaders()), response);
            ctx.writeAndFlush(response);
        }
    }


    public void writeAndFlush(MvcContext context, String message) {
        writeAndFlush(context, HttpResponseStatus.OK, message);
    }
}
