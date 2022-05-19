package com.xiaomi.youpin.docean.plugin.log;


import com.lmax.disruptor.EventHandler;

import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 * @date 2020/6/7
 */
public class LogEventHandler implements EventHandler<LogRecord> {

    private Consumer<LogRecord> consumer;

    public LogEventHandler(Consumer<LogRecord> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void onEvent(LogRecord logEvent, long l, boolean b) throws Exception {
        consumer.accept(logEvent);
    }
}
