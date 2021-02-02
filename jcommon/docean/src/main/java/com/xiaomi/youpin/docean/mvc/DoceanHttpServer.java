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

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.common.Cons;
import com.xiaomi.youpin.docean.common.DoceanVersion;
import com.xiaomi.youpin.docean.common.NamedThreadFactory;
import com.xiaomi.youpin.docean.common.NetUtils;
import com.xiaomi.youpin.docean.config.HttpServerConfig;
import com.xiaomi.youpin.docean.exception.DoceanException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.OptionalSslHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

/**
 * @author goodjava@qq.com
 * @date 2020/6/21
 * <p>
 * 一个简单的http服务器,为了支撑mvc
 */
@Slf4j
public class DoceanHttpServer {

    private HttpServerConfig config;

    private SslContext sslContext;

    private CountDownLatch latch = new CountDownLatch(1);


    public DoceanHttpServer(HttpServerConfig config) {
        this.config = config;
        if (this.config.getPort() == 0) {
            String port = Ioc.ins().getBean("$http_port");
            this.config.setPort(Integer.valueOf(port));
        }

        if (this.config.isSsl()) {
            try {
                SelfSignedCertificate certificate = new SelfSignedCertificate("youpinfs.com");
                sslContext = SslContextBuilder.forServer(certificate.certificate(), certificate.privateKey()).build();
            } catch (Throwable ex) {
                log.warn("error:{}", ex.getMessage());
            }
        }
    }

    public void start() throws InterruptedException {
        log.info("docean http server start:{}", new DoceanVersion());
        boolean useEpoll = NetUtils.useEpoll();
        EventLoopGroup eventLoopGroupBoss = null;
        EventLoopGroup eventLoopGroupWorker = null;
        //是否使用原生的epoll
        if (useEpoll) {
            log.info("use epollEventLoopGroup");
            eventLoopGroupBoss = new EpollEventLoopGroup(1, new NamedThreadFactory("EpollNettyServerBoss_", false));
            eventLoopGroupWorker = new EpollEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2 + 1, new NamedThreadFactory("NettyEpollServerWorker_", false));
        } else {
            eventLoopGroupBoss = new NioEventLoopGroup(1, new NamedThreadFactory("NettyServerBoss_", false));
            eventLoopGroupWorker = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2 + 1, new NamedThreadFactory("NettyServerWorker_", false));
        }

        ServerBootstrap serverBootstrap =
                new ServerBootstrap().group(eventLoopGroupBoss, eventLoopGroupWorker)
                        .channel(useEpoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 1024)
                        .option(ChannelOption.SO_REUSEADDR, true)
                        .option(ChannelOption.SO_KEEPALIVE, false)
                        .option(ChannelOption.SO_LINGER, 3)
                        .option(ChannelOption.SO_SNDBUF, 65535)
                        .option(ChannelOption.SO_RCVBUF, 65535)
                        .childOption(ChannelOption.TCP_NODELAY, true);
        ChannelInitializer<Channel> initializer = new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) {

                if (config.isSsl()) {
                    //同时支持http 和 https
                    ch.pipeline().addLast(new OptionalSslHandler(sslContext));
                }

                ch.pipeline().addLast(new HttpServerCodec());
                ch.pipeline().addLast(new HttpObjectAggregator(1 * 1024 * 1024));
                ch.pipeline().addLast(new IdleStateHandler(15, 15, 15));
                ch.pipeline().addLast(new HttpHandler());

                if (config.isWebsocket()) {
                    ch.pipeline().addLast(new WebSocketServerProtocolHandler(Cons.WebSocketPath));
                    ch.pipeline().addLast(new TextWebSocketHandler());
                }
            }
        };

        serverBootstrap.childHandler(initializer);
        ChannelFuture future =
                serverBootstrap.bind("0.0.0.0", this.config.getPort()).addListener((ChannelFutureListener) future1 -> {
                    if (future1.isSuccess()) {
                        log.info("start finish");
                    }
                }).awaitUninterruptibly();

        Throwable cause = future.cause();
        if (cause != null) {
            throw new DoceanException(cause);
        }

        InetSocketAddress boundAddress = ((InetSocketAddress) future.channel().localAddress());
        log.info("tesla started at address:{}", boundAddress);
        latch.await();
    }

    public void stop() {
        log.info("stop server");
        this.latch.countDown();
    }

}
