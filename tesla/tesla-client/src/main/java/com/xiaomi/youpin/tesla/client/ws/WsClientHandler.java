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

package com.xiaomi.youpin.tesla.client.ws;

import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


/**
 * @author goodjava@qq.com
 */
public class WsClientHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = LoggerFactory.getLogger(WsClientHandler.class);

    private final WebSocketClientHandshaker handshaker;

    private ChannelPromise handshakeFuture;

    private Consumer<String> consumer;

    private final WsClient client;

    public WsClientHandler(WebSocketClientHandshaker webSocketClientHandshaker, Consumer<String> consumer, WsClient client) {
        this.handshaker = webSocketClientHandshaker;
        this.consumer = consumer;
        this.client = client;
    }

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) {
        logger.info("channelActive");
        Channel channel = channelHandlerContext.channel();
        handshaker.handshake(channel);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        Channel ch = ctx.channel();
        FullHttpResponse response;
        if (!this.handshaker.isHandshakeComplete()) {
            try {
                response = (FullHttpResponse) msg;
                this.handshaker.finishHandshake(ch, response);
                this.handshakeFuture.setSuccess();
                logger.info("WebSocket Client connected! response headers[sec-websocket-extensions]:{}" + response.headers());
            } catch (WebSocketHandshakeException var7) {
                FullHttpResponse res = (FullHttpResponse) msg;
                String errorMsg = String.format("WebSocket Client failed to connect,status:%s,reason:%s", res.status(), res.content().toString(CharsetUtil.UTF_8));
                this.handshakeFuture.setFailure(new Exception(errorMsg));
            }
        } else if (msg instanceof FullHttpResponse) {
            response = (FullHttpResponse) msg;
            throw new IllegalStateException("Unexpected FullHttpResponse (getStatus=" + response.status() + ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        } else {
            WebSocketFrame frame = (WebSocketFrame) msg;
            if (frame instanceof TextWebSocketFrame) {
                TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
                consumer.accept(textFrame.text());
            } else if (frame instanceof BinaryWebSocketFrame) {
                BinaryWebSocketFrame binFrame = (BinaryWebSocketFrame) frame;
                System.out.println("BinaryWebSocketFrame");
            } else if (frame instanceof PongWebSocketFrame) {
                System.out.println("WebSocket Client received pong");
                consumer.accept("pong");
            } else if (frame instanceof CloseWebSocketFrame) {
                consumer.accept("close");
                ch.close();
            }

        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.info("ex:{}", cause.getMessage());
        ctx.close();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        logger.info("handlerRemoved");
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext channelHandlerContext) {
        channelHandlerContext.flush();
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.warn("client offline");
        client.connected.set(false);
        //使用过程中断线重连
        final EventLoop eventLoop = ctx.channel().eventLoop();
        eventLoop.schedule(() -> client.connect(), 1L, TimeUnit.SECONDS);
        super.channelInactive(ctx);
    }

}
