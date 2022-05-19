package com.xiaomi.data.push.udp;

import com.xiaomi.data.push.udp.handler.UdpClientHandler;
import com.xiaomi.data.push.udp.po.UdpMsg;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 */
public class UdpClient {

    private Bootstrap bootstrap;

    private Channel channel;

    private int port;

    private Consumer<UdpMsg> consumer;

    public UdpClient(int port, Consumer<UdpMsg> consumer) {
        this.port = port;
        this.consumer = consumer;
    }

    public void start() throws InterruptedException {
        bootstrap = new Bootstrap();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        bootstrap.group(workerGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChannelInitializer<NioDatagramChannel>() {
                    @Override
                    protected void initChannel(NioDatagramChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new UdpClientHandler(consumer));
                    }
                });

        channel = bootstrap.bind(this.port).sync().channel();
    }


    public void sendMessage(final String msg, final InetSocketAddress inetSocketAddress) {
        ByteBuf dataBuf = Unpooled.copiedBuffer(msg, Charset.forName("UTF-8"));
        DatagramPacket datagramPacket = new DatagramPacket(dataBuf, inetSocketAddress);
        channel.writeAndFlush(datagramPacket);
    }

}
