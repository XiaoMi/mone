package com.xiaomi.data.push.udp.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;


/**
 * @author goodjava@qq.com
 */
@Slf4j
public class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private Function<String, String> function;

    public UdpServerHandler(Function<String, String> function) {
        this.function = function;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
        final ByteBuf buf = msg.content();
        int readableBytes = buf.readableBytes();
        byte[] content = new byte[readableBytes];
        buf.readBytes(content);
        String str = new String(content);
        String res = function.apply(str);
        ctx.writeAndFlush(new DatagramPacket(Unpooled.wrappedBuffer(res.getBytes()), msg.sender()));
    }
}
