package com.xiaomi.data.push.rpc.netty;

import io.netty.channel.Channel;


public interface ChannelEventListener {
    void onChannelConnect(final String remoteAddr, final Channel channel);


    void onChannelClose(final String remoteAddr, final Channel channel);


    void onChannelException(final String remoteAddr, final Channel channel);


    void onChannelIdle(final String remoteAddr, final Channel channel);

    Channel channel(String remoteAddr);
}
