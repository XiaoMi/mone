package com.xiaomi.data.push.rpc.netty;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 */
public class ConnectionListener implements ChannelFutureListener {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionListener.class);

    private NettyRemotingClient client;

    public ConnectionListener(NettyRemotingClient client) {
        this.client = client;
    }


    @Override
    public void operationComplete(ChannelFuture channelFuture) {
        if (!channelFuture.isSuccess()) {
            logger.info("Connection Reconnect");
            final EventLoop loop = channelFuture.channel().eventLoop();
            loop.schedule(() -> {
                try {
                    client.createChannel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 1L, TimeUnit.SECONDS);
        } else {
            logger.info("Connect server success");
        }

    }
}
