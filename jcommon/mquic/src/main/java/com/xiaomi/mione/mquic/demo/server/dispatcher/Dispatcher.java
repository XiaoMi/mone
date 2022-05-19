package com.xiaomi.mione.mquic.demo.server.dispatcher;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author goodjava@qq.com
 * @date 9/5/21
 */
@Slf4j
public class Dispatcher {

    private ExecutorService pool = Executors.newFixedThreadPool(200);

    public void execute(String message, Channel channel) {
        log.info("message:{}", message);
        pool.submit(() -> {
            byte[] res = ("ok:" + message).getBytes();
            channel.writeAndFlush(Unpooled.wrappedBuffer(res));
        });
    }

}
