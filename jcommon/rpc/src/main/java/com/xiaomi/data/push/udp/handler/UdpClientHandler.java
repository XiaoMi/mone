package com.xiaomi.data.push.udp.handler;

import com.xiaomi.data.push.udp.po.UdpMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;


/**
 * @author goodjava@qq.com
 */
@Slf4j
public class UdpClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private Consumer<UdpMsg> consumer;

    public UdpClientHandler(Consumer<UdpMsg> consumer) {
        this.consumer = consumer;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
        final ByteBuf buf = msg.content();
        int readableBytes = buf.readableBytes();
        byte[] content = new byte[readableBytes];
        buf.readBytes(content);
        String res = new String(content);
        consumer.accept(new UdpMsg(res, msg.sender().getAddress().getHostAddress()));
    }
}
