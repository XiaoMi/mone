package com.xiaomi.youpin.jcommon.log;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 * @date 2020/6/7
 */
public class LogDisruptor {

    private Disruptor<LogEvent> disruptor;

    public boolean start(Consumer<LogRecord> consumer) {
        try {
            EventFactory<LogEvent> eventFactory = () -> new LogEvent();
            ExecutorService executor = Executors.newCachedThreadPool();
            int ringBufferSize = 1024 * 1024;
            disruptor = new Disruptor<LogEvent>(eventFactory,
                    ringBufferSize, executor, ProducerType.MULTI,
                    new YieldingWaitStrategy());
            disruptor.handleEventsWith(new LogEventHandler(consumer));
            disruptor.start();
            return true;
        } catch (Throwable ex) {
            System.err.println(ex.getMessage());
            return false;
        }
    }


    public void publishEvent(Consumer<LogRecord> consumer) {
        try {
            this.disruptor.publishEvent((logEvent, sequence) -> {
                consumer.accept(logEvent.getLogRecord());
            });
        } catch (Throwable ex) {
            System.err.println(ex.getMessage());
        }
    }


}
