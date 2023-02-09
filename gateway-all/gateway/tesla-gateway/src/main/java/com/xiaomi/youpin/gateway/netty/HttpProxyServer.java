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

package com.xiaomi.youpin.gateway.netty;

import com.xiaomi.youpin.gateway.TeslaConstants;
import com.xiaomi.youpin.gateway.cache.ApiRouteCache;
import com.xiaomi.youpin.gateway.common.Utils;
import com.xiaomi.youpin.gateway.dispatch.Dispatcher;
import com.xiaomi.youpin.gateway.exception.GatewayException;
import com.xiaomi.youpin.gateway.netty.filter.RequestFilterChain;
import com.xiaomi.youpin.gateway.netty.transmit.connection.HttpHandler;
import com.xiaomi.youpin.gateway.netty.transmit.connection.TextWebSocketFrameHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.NamedThreadFactory;
import org.springframework.beans.factory.annotation.Value;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * proxy 服务器
 *
 * @author goodjava@qq.com
 */
@Slf4j
public class HttpProxyServer {

    private final AtomicBoolean stopped = new AtomicBoolean(false);
    private final ChannelGroup allChannels = new DefaultChannelGroup("tesla-server", GlobalEventExecutor.INSTANCE);
    private final Dispatcher dispatcher;
    private final RequestFilterChain filterChain;
    private final ApiRouteCache apiRouteCache;

    private final Thread shutdownHook = new Thread(() -> abort(), "tesla-shutdown-hook");

    private volatile int port;
    private volatile int maxContentLength;
    private volatile InetSocketAddress boundAddress;

    public static HttpProxyServerBootstrap bootstrap(Dispatcher dispatcher, RequestFilterChain filterChain, ApiRouteCache apiRouteCache) {
        return new HttpProxyServerBootstrap(dispatcher, filterChain, apiRouteCache);
    }


    public HttpProxyServer(int port,
                           Dispatcher dispatcher, RequestFilterChain filterChain, ApiRouteCache apiRouteCache) {
        this.dispatcher = dispatcher;
        this.filterChain = filterChain;
        this.port = port;
        this.apiRouteCache = apiRouteCache;
        this.maxContentLength = apiRouteCache.getMaxContentLength();
    }


    public void abort() {
        doStop(false);
    }


    protected void doStop(boolean graceful) {
        if (stopped.compareAndSet(false, true)) {
            log.info("shutting down proxy server  begin gracefully:{}", graceful);
            closeAllChannels(graceful);
            try {
                Runtime.getRuntime().removeShutdownHook(shutdownHook);
            } catch (IllegalStateException e) {
            }
            log.info("shutting down proxy server finish");
        }
    }


    public void registerChannel(Channel channel) {
        allChannels.add(channel);
    }


    protected void closeAllChannels(boolean graceful) {
        log.info("Closing all channels " + (graceful ? "(graceful)" : "(non-graceful)"));

        ChannelGroupFuture future = allChannels.close();
        if (graceful) {
            try {
                future.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

                log.warn("Interrupted while waiting for channels to shut down gracefully.");
            }

            if (!future.isSuccess()) {
                for (ChannelFuture cf : future) {
                    if (!cf.isSuccess()) {
                        log.info("Unable to close channel.  Cause of failure for {} is {}", cf.channel(),
                                cf.cause());
                    }
                }
            }
        }
    }

    public HttpProxyServer start() {
        doStart();
        return this;
    }

    /**
     * 启动netty服务器
     */
    private void doStart() {
        boolean useEpoll = Utils.useEpoll();
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
                        .option(ChannelOption.SO_BACKLOG, 5000)
                        .option(ChannelOption.SO_REUSEADDR, true)
                        .option(ChannelOption.SO_KEEPALIVE, false)
                        .option(ChannelOption.SO_LINGER, 3)
                        .option(ChannelOption.SO_SNDBUF, 65535)
                        .option(ChannelOption.SO_RCVBUF, 65535)
                        .childOption(ChannelOption.TCP_NODELAY, true);
        ChannelInitializer<Channel> initializer = new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) {
                ch.pipeline().addLast(new HttpServerCodec());
                ch.pipeline().addLast(new HttpObjectAggregator(maxContentLength * 1024 * 1024));
                ch.pipeline().addLast(new IdleStateHandler(15, 15, 15));
                ch.pipeline().addLast(new HttpHandler(dispatcher, filterChain, apiRouteCache));
                ch.pipeline().addLast(new WebSocketServerProtocolHandler(TeslaConstants.WebSocketPath));
                ch.pipeline().addLast(new TextWebSocketFrameHandler(dispatcher, filterChain, apiRouteCache));
            }
        };

        serverBootstrap.childHandler(initializer);
        ChannelFuture future =
                serverBootstrap.bind("0.0.0.0", this.port).addListener((ChannelFutureListener) future1 -> {
                    if (future1.isSuccess()) {
                        registerChannel(future1.channel());
                    }
                }).awaitUninterruptibly();

        Throwable cause = future.cause();
        if (cause != null) {
            throw new GatewayException(cause);
        }

        this.boundAddress = ((InetSocketAddress) future.channel().localAddress());
        log.info("tesla started at address:{}", this.boundAddress);
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }


}
