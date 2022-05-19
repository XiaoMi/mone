package com.xiaomi.youpin.jcommon.log;


import com.lmax.disruptor.EventHandler;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author goodjava@qq.com
 * @date 2020/6/7
 */
public class LogEventHandler implements EventHandler<LogEvent> {

    private Consumer<LogRecord> consumer;

    public LogEventHandler(Consumer<LogRecord> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void onEvent(LogEvent logEvent, long l, boolean b) throws Exception {
        consumer.accept(logEvent.getLogRecord());
    }
}
