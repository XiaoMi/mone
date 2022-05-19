package com.xiaomi.youpin.docean.plugin.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class Http {

    private static final String VERSION = "0.0.1:2020-06-13";

    public final AttributeKey<String> id = AttributeKey.newInstance("channel_id");

    private final ConcurrentHashMap<String, DefaultPromise<Response>> latchMap = new ConcurrentHashMap<>();

    private Bootstrap bootstrap;

    private Bootstrap httpsBootstrap;

    private SslContextBuilder sslContext;

    public Http() {
        log.info("version:{}", VERSION);
        sslContext = SslContextBuilder.forClient();
        bootstrap = getBootstrap(false);
        //https
        httpsBootstrap = getBootstrap(true);
    }

    public Response get(String url, Map<String, String> headers, long timeout) {
        String channelId = "";
        try {
            URI uri = new URI(url);
            int port = port(uri);
            boolean ishttps = ishttps(uri);
            Channel channel = (ishttps ? httpsBootstrap : bootstrap).connect(uri.getHost(), port).sync().channel();
            DefaultFullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getPath());
            httpRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderNames.CONNECTION);
            httpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpRequest.content().readableBytes());
            httpRequest.headers().set(HttpHeaderNames.HOST, uri.getHost());
            headers.entrySet().stream().forEach(it -> httpRequest.headers().set(it.getKey(), it.getValue()));
            channelId = channel.id().asLongText();
            channel.attr(id).set(channelId);
            DefaultPromise<Response> promise = new DefaultPromise<>(new DefaultEventLoop());
            latchMap.put(channelId, promise);
            channel.writeAndFlush(httpRequest);
            Response res = promise.get(timeout, TimeUnit.MILLISECONDS);
            channel.close();
            return res;
        } catch (Throwable ex) {
            return new Response(500, ex.toString().getBytes());
        } finally {
            latchMap.remove(channelId);
        }
    }

    private boolean ishttps(URI uri) {
        return uri.getScheme().toLowerCase().equals("https");
    }


    private int port(URI uri) {
        int port = uri.getPort();
        if (port == -1) {
            if (ishttps(uri)) {
                return 443;
            } else {
                return 80;
            }
        }
        return port;
    }


    private AttributeKey<Boolean> ssl_key = AttributeKey.newInstance("ssl");


    public Response post(String url, String body, Map<String, String> headers, long timeout) {
        String channelId = "";
        try {
            URI uri = new URI(url);
            int port = port(uri);
            Channel channel = (ishttps(uri) ? httpsBootstrap : bootstrap).connect(uri.getHost(), port).sync().channel();
            ByteBuf buffer = Unpooled.wrappedBuffer(body.getBytes());
            DefaultFullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getPath(), buffer);
            httpRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderNames.CONNECTION);
            httpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, body.length());
            httpRequest.headers().set(HttpHeaderNames.HOST, uri.getHost());
            headers.entrySet().stream().forEach(it -> httpRequest.headers().set(it.getKey(), it.getValue()));
            DefaultPromise<Response> promise = new DefaultPromise<>(new DefaultEventLoop());
            channelId = channel.id().asLongText();
            channel.attr(id).set(channelId);
            latchMap.put(channelId, promise);
            channel.writeAndFlush(httpRequest);
            Response res = promise.get(timeout, TimeUnit.MILLISECONDS);
            channel.close();
            return res;
        } catch (Throwable ex) {
            return new Response(500, ex.getMessage().getBytes());
        } finally {
            latchMap.remove(channelId);
        }
    }

    private Bootstrap getBootstrap(boolean ssl) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup(256))
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_SNDBUF, 65535)
                .option(ChannelOption.SO_RCVBUF, 65535)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler((new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws SSLException {
                        if (ssl) {
                            SSLEngine engine = sslContext.build().newEngine(socketChannel.alloc());
                            socketChannel.pipeline().addFirst("ssl", new SslHandler(engine));
                        }
                        socketChannel.pipeline().addLast(new HttpClientCodec());
                        socketChannel.pipeline().addLast(new HttpContentDecompressor());
                        socketChannel.pipeline().addLast(new HttpObjectAggregator(1024 * 1024 * 4));
                        socketChannel.pipeline().addLast(new HttpClientMsgHandler());
                    }
                }));
        return bootstrap;
    }


    private class HttpClientMsgHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse res) {
            Attribute<String> v = ctx.channel().attr(id);
            if (v.get() != null) {
                DefaultPromise<Response> p = latchMap.get(v.get());
                if (null != p) {
                    if (res.content() != null) {
                        byte[] data = new byte[res.content().readableBytes()];
                        res.content().getBytes(0, data);
                        p.setSuccess(new Response(res.status().code(), data));
                    } else {
                        int code = res.status().code();
                        Response httpRes = new Response(code, null);
                        p.setSuccess(httpRes);
                    }
                }
            }
        }
    }

}
