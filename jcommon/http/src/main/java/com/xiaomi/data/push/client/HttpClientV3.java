package com.xiaomi.data.push.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.concurrent.DefaultPromise;

import java.net.URI;
import java.util.Map;

/**
 * @author goodjava@qq.com
 */
public class HttpClientV3 {



    public String get(String url, Map<String, String> headers) {
        try {
            URI uri = new URI(url);
            int port = uri.getPort();
            port = port == -1 ? 80 : port;
            DefaultPromise<String> promise = new DefaultPromise<>(new DefaultEventLoop());
            Bootstrap bootstrap = getBootstrap(promise);
            Channel channel = bootstrap.connect(uri.getHost(), port).sync().channel();

            DefaultFullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.getPath());

            httpRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderNames.CONNECTION);
            httpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpRequest.content().readableBytes());
            headers.entrySet().stream().forEach(it -> httpRequest.headers().set(it.getKey(), it.getValue()));

            channel.writeAndFlush(httpRequest);

            return promise.get();
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }


    public String post(String url, String body, Map<String, String> headers) {
        try {
            URI uri = new URI(url);
            int port = uri.getPort();
            port = port == -1 ? 80 : port;
            DefaultPromise<String> promise = new DefaultPromise<>(new DefaultEventLoop());
            Bootstrap bootstrap = getBootstrap(promise);

            Channel channel = bootstrap.connect(uri.getHost(), port).sync().channel();
            ByteBuf buffer = Unpooled.wrappedBuffer(body.getBytes());
            DefaultFullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getPath(), buffer);
            httpRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderNames.CONNECTION);
            httpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, body.length());
            headers.entrySet().stream().forEach(it -> httpRequest.headers().set(it.getKey(), it.getValue()));
            channel.writeAndFlush(httpRequest);
            return promise.get();
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }

    private Bootstrap getBootstrap(DefaultPromise<String> promise) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup(1))
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true).handler((new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) {
                socketChannel.pipeline().addLast(new HttpClientCodec());
                socketChannel.pipeline().addLast(new HttpContentDecompressor());
                socketChannel.pipeline().addLast(new HttpObjectAggregator(123433));
                socketChannel.pipeline().addLast(new HttpClientMsgHandler(promise));
            }
        }));
        return bootstrap;
    }


    private class HttpClientMsgHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

        private DefaultPromise<String> promise;

        public HttpClientMsgHandler(DefaultPromise<String> promise) {
            this.promise = promise;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
            if (msg.content() != null) {
                byte[] data = new byte[msg.content().readableBytes()];
                msg.content().getBytes(0, data);
                promise.setSuccess(new String(data));
            } else {
                promise.setSuccess("");
            }

        }
    }
}
