package com.xiaomi.data.push.uds.context;

import io.netty.channel.Channel;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author goodjava@qq.com
 */
public class UdsClientContext {

    @Setter
    private NetListener listener = new NetListener() {
        @Override
        public void handle(NetEvent event) {

        }
    };

    public AtomicReference<Channel> channel = new AtomicReference<>();

    private UdsClientContext() {
    }

    private static final class LazyHolder {
        private static final UdsClientContext ins = new UdsClientContext();
    }

    public static final UdsClientContext ins() {
        return LazyHolder.ins;
    }

    public void exceptionCaught(Throwable cause) {
        listener.handle(new NetEvent(NetType.exception));
    }

    public void channelInactive() {
        listener.handle(new NetEvent(NetType.inactive));
    }


}
