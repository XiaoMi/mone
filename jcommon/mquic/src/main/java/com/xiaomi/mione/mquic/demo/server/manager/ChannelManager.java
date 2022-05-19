package com.xiaomi.mione.mquic.demo.server.manager;

import com.xiaomi.mione.mquic.demo.common.Safe;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author goodjava@qq.com
 * @date 9/5/21
 */
public class ChannelManager {

    private ConcurrentHashMap<String, Channel> channelMap = new ConcurrentHashMap<>();

    public void addChannel(Channel channel) {
        channelMap.put(channel.id().asLongText(), channel);
    }


    public void removeChannel(Channel channel) {
        channelMap.remove(channel.id().asLongText());
    }

    public void sendMessage(final String message) {
        channelMap.values().forEach(c -> Safe.run(() -> c.writeAndFlush(Unpooled.wrappedBuffer(message.getBytes()))));
    }


}
