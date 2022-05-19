package com.xiaomi.mione.mquic.demo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ChannelInputShutdownReadComplete;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.incubator.codec.quic.QuicChannel;
import io.netty.incubator.codec.quic.QuicClientCodecBuilder;
import io.netty.incubator.codec.quic.QuicSslContext;
import io.netty.incubator.codec.quic.QuicSslContextBuilder;
import io.netty.incubator.codec.quic.QuicStreamChannel;
import io.netty.incubator.codec.quic.QuicStreamType;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author goodjava@qq.com
 * @Date 2021/6/14 20:38
 */
@Slf4j
public class QuicClient {

    /**
     * 0 string 1 byte[]
     */
    private static final int type = 1;


    public static void main(String... args) throws ExecutionException, InterruptedException {
        QuicClient client = new QuicClient();
        client.run(args);
    }


    private void connect(Channel channel) throws InterruptedException, ExecutionException, TimeoutException {
        QuicChannel quicChannel = QuicChannel.newBootstrap(channel)
                .streamHandler(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelActive(ChannelHandlerContext ctx) {
                        ctx.close();
                    }
                })
                .remoteAddress(new InetSocketAddress(NetUtil.LOCALHOST4, 9999))
                .connect()
                .get(5, TimeUnit.SECONDS);

        QuicStreamChannel streamChannel = quicChannel.createStream(QuicStreamType.BIDIRECTIONAL,

                new ChannelInitializer<QuicStreamChannel>() {

                    @Override
                    protected void initChannel(QuicStreamChannel quicStreamChannel) throws Exception {
                        quicStreamChannel.pipeline().addLast(
                                new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) {
                                        ByteBuf byteBuf = (ByteBuf) msg;
                                        System.out.println(byteBuf.toString(CharsetUtil.UTF_8));
                                        byteBuf.release();
                                    }

                                    @Override
                                    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
                                        if (evt == ChannelInputShutdownReadComplete.INSTANCE) {
                                            ((QuicChannel) ctx.channel().parent()).close(true, 0,
                                                    ctx.alloc().directBuffer(16)
                                                            .writeBytes(new byte[]{'k', 't', 'h', 'x', 'b', 'y', 'e'}));
                                        }
                                    }

                                    @Override
                                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                        log.error("---->" + cause.getMessage(), cause);
                                        super.exceptionCaught(ctx, cause);
                                    }
                                }
                        ).addLast(new LengthFieldPrepender(4) {
                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                super.exceptionCaught(ctx, cause);
                                log.error("*****:" + cause.getMessage());
                            }
                        })
                        ;
                    }
                }

        ).sync().getNow();

        while (true) {
            AtomicBoolean ab = new AtomicBoolean(false);
            if (type == 0) {
                streamChannel.writeAndFlush(Unpooled.copiedBuffer("time\r\n", CharsetUtil.UTF_8));
            }
            if (type == 1) {
                byte[] data = "time".getBytes();
                ByteBuf buf = Unpooled.wrappedBuffer(data);
                ChannelFuture future = streamChannel.writeAndFlush(buf);
                future.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        DefaultChannelPromise dcp = (DefaultChannelPromise) channelFuture;
                        if (!dcp.isSuccess()) {
//                                Throwable ex = dcp.cause();
//                                log.error(ex.getMessage(), ex);
                            ab.set(true);


                        }
                        log.info("complete:" + dcp.isSuccess());
                    }
                });

                future.get();


                if (ab.get()) {
                    break;
                }

            }
//                        .addListener(QuicStreamChannel.SHUTDOWN_OUTPUT);

            TimeUnit.SECONDS.sleep(1);
        }

        System.out.println("exit begin");
        streamChannel.close();

        // Wait for the stream channel and quic channel to be closed (this will happen after we received the FIN).
        // After this is done we will close the underlying datagram channel.
        streamChannel.closeFuture().sync();
        quicChannel.closeFuture().sync();
        System.out.println("exit channel");
    }


    public void run(String[] args) throws InterruptedException, ExecutionException {
        QuicSslContext context = QuicSslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).
                applicationProtocols("http/0.9").build();
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        try {
            ChannelHandler codec = new QuicClientCodecBuilder()
                    .sslContext(context)
                    .maxIdleTimeout(5000, TimeUnit.MILLISECONDS)
                    .initialMaxData(10000000)
                    // As we don't want to support remote initiated streams just setup the limit for local initiated
                    // streams in this example.
                    .initialMaxStreamDataBidirectionalLocal(1000000)
                    .build();

            Bootstrap bs = new Bootstrap();
            Channel channel = bs.group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(codec)
                    .bind(0).sync().channel();


            while (true) {
                log.info("connect");
                try {
                    connect(channel);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                TimeUnit.SECONDS.sleep(5);
            }

        } finally {
            group.shutdownGracefully();
        }
    }

}
