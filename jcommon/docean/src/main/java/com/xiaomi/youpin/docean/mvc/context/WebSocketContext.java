package com.xiaomi.youpin.docean.mvc.context;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author goodjava@qq.com
 * @date 2024/4/29 13:48
 */
@Slf4j
public class WebSocketContext {


    @Getter
    private ConcurrentMap<String, Channel> channelMap = new ConcurrentHashMap<>();

    public void put(String id, Channel channel) {
        this.channelMap.put(id, channel);
    }

    public void remove(String id) {
        this.channelMap.remove(id);
    }


    private static final class LazyHolder {
        private static final WebSocketContext ins = new WebSocketContext();
    }

    public static final WebSocketContext ins() {
        return LazyHolder.ins;
    }

    public void sendMessage(String id, String message) {
        Channel channel = channelMap.get(id);
        if (null != channel) {
            TextWebSocketFrame frame = new TextWebSocketFrame(message);
            channel.writeAndFlush(frame);
        }
    }

}
