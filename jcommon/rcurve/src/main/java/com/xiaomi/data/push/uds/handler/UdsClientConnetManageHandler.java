package com.xiaomi.data.push.uds.handler;

import com.xiaomi.data.push.uds.UdsClient;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class UdsClientConnetManageHandler extends ChannelDuplexHandler {

    private final boolean reconnection;

    private final UdsClient udsClient;

    private final String path;

    public UdsClientConnetManageHandler(boolean reconnection, UdsClient udsClient, String path) {
        this.reconnection = reconnection;
        this.udsClient = udsClient;
        this.path = path;
    }

    /**
     * 不活跃的时候可以设置重连策略
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("client channelInactive");
        final EventLoop eventLoop = ctx.channel().eventLoop();
        if (this.reconnection) {
            eventLoop.schedule(() -> {
                log.info("client reconnection:{}", path);
                udsClient.start(path);
            }, 1L, TimeUnit.SECONDS);
        }
        super.channelInactive(ctx);
    }


}
