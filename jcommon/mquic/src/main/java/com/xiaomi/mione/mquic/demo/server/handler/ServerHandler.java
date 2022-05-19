package com.xiaomi.mione.mquic.demo.server.handler;

import com.xiaomi.mione.mquic.demo.server.dispatcher.Dispatcher;
import com.xiaomi.mione.mquic.demo.server.manager.ChannelManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @author goodjava@qq.com
 * @date 9/5/21
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private ChannelManager channelManager;

    private Dispatcher dispatcher;

    public ServerHandler(ChannelManager channelManager, Dispatcher dispatcher) {
        this.channelManager = channelManager;
        this.dispatcher = dispatcher;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        channelManager.addChannel(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        channelManager.removeChannel(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        String message = byteBuf.toString(CharsetUtil.UTF_8).trim();
        try {
            dispatcher.execute(message, ctx.channel());
        } finally {
            byteBuf.release();
        }
    }


}
