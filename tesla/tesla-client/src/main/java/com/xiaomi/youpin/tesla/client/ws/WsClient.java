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


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * @author goodjava@qq.com
 */
public class WsClient {

    private static final Logger logger = LoggerFactory.getLogger(WsClient.class);

    private URI uri;
    private Consumer<String> consumer;
    private Channel ch;
    private static final EventLoopGroup group = new NioEventLoopGroup();
    public AtomicBoolean connected = new AtomicBoolean(false);

    public WsClient() {
    }

    public void init(URI uri, Consumer<String> consumer, Supplier<String> pingSupplier) {
        this.consumer = consumer;
        this.uri = uri;


        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                if (connected.get()) {
                    ch.writeAndFlush(new TextWebSocketFrame(pingSupplier.get()));
                }
            } catch (Exception ex) {
                //ignore
            }


        }, 0, 5, TimeUnit.SECONDS);

    }

    public void connect() {
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000);

            WsClientHandler handler =
                    new WsClientHandler(
                            WebSocketClientHandshakerFactory.newHandshaker(
                                    uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()), consumer, this);

            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("http-codec", new HttpClientCodec());
                            pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
                            pipeline.addLast("ws-handler", handler);
                        }
                    });

            ch = bootstrap.connect(uri.getHost(), uri.getPort()).addListener(new ConnectionListener(this)).sync().channel();
            handler.handshakeFuture().sync();
            connected.set(true);
            logger.info("client connect");
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            System.out.println(ex.getMessage());
            connected.set(false);
        }

    }


    public void close() throws InterruptedException {
        ch.writeAndFlush(new CloseWebSocketFrame());
        ch.closeFuture().sync();
    }

    public void send(final String msg) {
        if (connected.get()) {
            logger.info("send message:{}", msg);
            ch.writeAndFlush(new TextWebSocketFrame(msg));
        }
    }

}
