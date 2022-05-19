package com.xiaomi.youpin.docean.mvc;

import com.xiaomi.youpin.docean.Mvc;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * 支持长连接
 */
@Slf4j
public class TextWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            //移除http的操作
            ctx.pipeline().remove(HttpHandler.class);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }


    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        MvcContext context = new MvcContext();
        context.setWebsocket(true);
        MvcRequest req = new MvcRequest();
        req.setBody(msg.text().getBytes());
        MvcResponse response = new MvcResponse();
        response.setCtx(ctx);
        Mvc.ins().dispatcher(context, req, response);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }


}
