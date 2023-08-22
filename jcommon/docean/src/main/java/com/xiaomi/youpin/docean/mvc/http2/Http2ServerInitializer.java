/*
 * Copyright 2016 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.xiaomi.youpin.docean.mvc.http2;

import com.xiaomi.youpin.docean.config.HttpServerConfig;
import com.xiaomi.youpin.docean.mvc.HttpHandlerRead;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http2.Http2FrameCodecBuilder;
import io.netty.handler.codec.http2.Http2MultiplexHandler;
import io.netty.handler.codec.http2.Http2StreamFrameToHttpObjectCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.util.ReferenceCountUtil;

import static io.netty.util.internal.ObjectUtil.checkPositiveOrZero;

/**
 * Sets up the Netty pipeline for the example server. Depending on the endpoint config, sets up the
 * pipeline for NPN or cleartext HTTP upgrade to HTTP/2.
 */
public class Http2ServerInitializer extends ChannelInitializer<SocketChannel> {


    private final SslContext sslCtx;
    private final int maxHttpContentLength;
    private HttpServerConfig config;

    public Http2ServerInitializer(SslContext sslCtx, HttpServerConfig config) {
        this(sslCtx, 16 * 1024);
        this.config = config;
    }

    public Http2ServerInitializer(SslContext sslCtx, int maxHttpContentLength) {
        this.sslCtx = sslCtx;
        this.maxHttpContentLength = checkPositiveOrZero(maxHttpContentLength, "maxHttpContentLength");
    }

    @Override
    public void initChannel(SocketChannel ch) {
        if (sslCtx != null) {
            configureSsl(ch);
        } else {
            configureClearText2(ch);
        }
    }

    /**
     * Configure the pipeline for TLS NPN negotiation to HTTP/2.
     */
    private void configureSsl(SocketChannel ch) {
        ch.pipeline().addLast(sslCtx.newHandler(ch.alloc()), new Http2OrHttpHandler(this.config));
    }

    /**
     * Configure the pipeline for a cleartext upgrade from HTTP to HTTP/2.0
     */
    private void configureClearText(SocketChannel ch) {
        final ChannelPipeline p = ch.pipeline();
        final HttpServerCodec sourceCodec = new HttpServerCodec();
        p.addLast(sourceCodec);
        p.addLast(new HttpServerUpgradeHandler(sourceCodec, new Http2UpgradeCodecFactory(this.config)));
        p.addLast(new SimpleChannelInboundHandler<HttpMessage>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, HttpMessage msg) throws Exception {
                // If this handler is hit then no upgrade has been attempted and the client is just talking HTTP.
                System.err.println("Directly talking: " + msg.protocolVersion() + " (no upgrade was attempted)");
                ChannelPipeline pipeline = ctx.pipeline();
//                pipeline.addAfter(ctx.name(), null, new HelloWorldHttp1Handler("Direct. No Upgrade Attempted."));
                pipeline.replace(this, null, new HttpObjectAggregator(maxHttpContentLength));
                ctx.fireChannelRead(ReferenceCountUtil.retain(msg));
            }
        });

        p.addLast(new UserEventLogger());
    }


    /**
     * No need to upgrade the HTTP protocol, use it directly.
     *
     * @param ch
     */
    private void configureClearText2(SocketChannel ch) {
        ch.pipeline().addLast(Http2FrameCodecBuilder.forServer().build());
        ch.pipeline().addLast(new Http2MultiplexHandler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) {
                ch.pipeline().addLast(new Http2StreamFrameToHttpObjectCodec(true));
                ch.pipeline().addLast(new SimpleChannelInboundHandler<HttpObject>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
                        HttpHandlerRead.read(ctx, msg, config);
                    }
                });
            }
        }));
        ch.pipeline().addLast(new UserEventLogger());
    }


    /**
     * Class that logs any User Events triggered on this channel.
     */
    private static class UserEventLogger extends ChannelInboundHandlerAdapter {
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
            System.out.println("User Event Triggered: " + evt);
            ctx.fireUserEventTriggered(evt);
        }
    }
}