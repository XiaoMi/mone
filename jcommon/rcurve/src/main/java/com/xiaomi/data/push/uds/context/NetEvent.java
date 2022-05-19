package com.xiaomi.data.push.uds.context;

import io.netty.channel.Channel;
import lombok.Data;

/**
 * @author goodjava@qq.com
 */
@Data
public class NetEvent {

    private NetType type;

    private String app;

    private Channel channel;

    public NetEvent(NetType type, String app) {
        this.type = type;
        this.app = app;
    }

    public NetEvent() {
    }

    public NetEvent(NetType type) {
        this.type = type;
    }

    public NetEvent(NetType type, Channel channel) {
        this.type = type;
        this.channel = channel;
    }
}
