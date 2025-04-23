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
import com.xiaomi.youpin.docean.common.*;
import com.xiaomi.youpin.docean.config.HttpServerConfig;
import com.xiaomi.youpin.docean.exception.DoceanException;
import com.xiaomi.youpin.docean.mvc.http2.Http2ServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http2.Http2SecurityUtil;
import io.netty.handler.ssl.*;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

/**
 * @author goodjava@qq.com
 * @date 2020/6/21
 * <p>
 * rate limited or exceeded quota
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

        if (this.config.isSsl() && this.config.getHttpVersion().equals("http1")) {
            Safe.runAndLog(() -> {
                String domain = Ioc.ins().getBean("$ssl_domain");
                boolean test = Boolean.valueOf(Ioc.ins().getBean("$ssl_self_sign", "true"));
                if (test) {
                    SelfSignedCertificate certificate = new SelfSignedCertificate(domain);
                    sslContext = SslContextBuilder.forServer(certificate.certificate(), certificate.privateKey()).build();
                } else {
                    String certificate = Ioc.ins().getBean("$ssl_certificate");
                    String privateKey = Ioc.ins().getBean("$ssl_cprivateKey");
                    if (StringUtils.isEmpty(certificate) || StringUtils.isEmpty(privateKey)) {
                        String message = "Please provide the file addresses of the public key and private key.";
                        log.error(message);
                        throw new RuntimeException(message);
                    }

                    File certChainFile = new File(certificate);
                    File privateKeyFile = new File(privateKey);
                    sslContext = SslContextBuilder.forServer(certChainFile, privateKeyFile).build();
                }
            });
        }
    }

    @SneakyThrows
    public void start() throws InterruptedException {
        log.info("docean http server start:{}", new DoceanVersion());
        boolean useEpoll = NetUtils.useEpoll();
        EventLoopGroup eventLoopGroupBoss = null;
        EventLoopGroup eventLoopGroupWorker = null;
        //Whether to use the native epoll.
        int nThreads = Runtime.getRuntime().availableProcessors() * 2 + 1;
        if (useEpoll) {
            log.info("use epollEventLoopGroup nThreads:{}", nThreads);
            eventLoopGroupBoss = new EpollEventLoopGroup(1, new NamedThreadFactory("EpollNettyServerBoss_", false));
            eventLoopGroupWorker = new EpollEventLoopGroup(nThreads, new NamedThreadFactory("NettyEpollServerWorker_", false));
        } else {
            eventLoopGroupBoss = new NioEventLoopGroup(1, new NamedThreadFactory("NettyServerBoss_", false));
            eventLoopGroupWorker = new NioEventLoopGroup(nThreads, new NamedThreadFactory("NettyServerWorker_", false));
        }
        ServerBootstrap serverBootstrap =
                new ServerBootstrap().group(eventLoopGroupBoss, eventLoopGroupWorker)
                        .channel(useEpoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 1024)
                        .option(ChannelOption.SO_REUSEADDR, true)
                        .option(ChannelOption.SO_KEEPALIVE, false)
//                        .option(ChannelOption.SO_LINGER, 3)
                        .childOption(ChannelOption.TCP_NODELAY, true);


        ChannelInitializer initializer = null;

        if (config.getHttpVersion().equals(HttpServerConfig.HttpVersion.http1)) {
            HttpHandler httpHandler = new HttpHandler(config);
            initializer = new ChannelInitializer<Channel>() {
                @SneakyThrows
                @Override
                protected void initChannel(Channel ch) {
                    if (config.isSsl()) {
                        //同时支持http 和 https
                        ch.pipeline().addLast(new OptionalSslHandler(sslContext));
                    }
                    ch.pipeline().addLast(new HttpServerCodec());
                    ch.pipeline().addLast(new HttpObjectAggregator(1 * 1024 * 1024));
                    ch.pipeline().addLast(new ChunkedWriteHandler());
                    ch.pipeline().addLast(new IdleStateHandler(15, 15, 15));
                    ch.pipeline().addLast(httpHandler);

                    if (config.isWebsocket()) {
                        //目前只支持一个ws,写死的
                        ch.pipeline().addLast(new WebSocketServerProtocolHandler(Cons.WebSocketPath));
                        ch.pipeline().addLast(new TextWebSocketHandler());
                    }
                }
            };
        } else if (config.getHttpVersion().equals(HttpServerConfig.HttpVersion.http2)) {
            SslContext sslCtx;
            if (config.isSsl()) {
                SslProvider provider = OpenSsl.isAlpnSupported() ? SslProvider.OPENSSL : SslProvider.JDK;
                String certificate = Ioc.ins().getBean("$ssl_certificate");
                String privateKey = Ioc.ins().getBean("$ssl_cprivateKey");
                if (StringUtils.isEmpty(certificate) || StringUtils.isEmpty(privateKey)) {
                    throw new RuntimeException("certificate or privateKey is null");
                }
                sslCtx = SslContextBuilder.forServer(new File(certificate), new File(privateKey))
                        .sslProvider(provider)
                        .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
                        .applicationProtocolConfig(new ApplicationProtocolConfig(
                                ApplicationProtocolConfig.Protocol.ALPN,
                                ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE,
                                ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT,
                                ApplicationProtocolNames.HTTP_2,
                                ApplicationProtocolNames.HTTP_1_1))
                        .build();
            } else {
                sslCtx = null;
            }
            initializer = new Http2ServerInitializer(sslCtx, this.config);
        }
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
