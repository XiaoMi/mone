/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.docean.plugin.log;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.Setter;
import lombok.SneakyThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author goodjava@qq.com
 * @date 2020/7/4
 */
public class Log {

    private Disruptor<LogRecord> disruptor;

    @Setter
    private LogWriter logWriter = new LogWriter("") {
        @Override
        public void write(LocalDateTime time, String log) {
            System.out.print(log);
        }

        @Override
        public void force() {
        }
    };

    public void init() {
        try {
            EventFactory<LogRecord> eventFactory = () -> new LogRecord();
            ExecutorService executor = Executors.newFixedThreadPool(20);
            int ringBufferSize = 1024 * 1024;
            disruptor = new Disruptor<LogRecord>(eventFactory,
                    ringBufferSize, executor, ProducerType.MULTI,
                    new YieldingWaitStrategy());
            disruptor.handleEventsWith(new LogEventHandler(record -> {
                String log = Stream.of(record.getTime(),
                        record.getLevel(),
                        record.getThreadName(),
                        record.getClassName(),
                        record.getMethodName(),
                        record.getLine(),
                        record.getMessage()
                ).collect(Collectors.joining("|"));
                logWriter.write(record.getLtime(), log + System.lineSeparator());
            }));
            disruptor.start();
        } catch (Throwable ex) {
            System.err.println(ex.getMessage());
        } finally {
            System.out.println("init log finish");
        }
    }

    @SneakyThrows
    public void shutdown() {
        disruptor.shutdown(10, TimeUnit.SECONDS);
        this.logWriter.force();
    }

    private void pushEvent(final String message, String level, LocalDateTime time, String className, String methodName, int lineNumber) {
        disruptor.publishEvent((event, sequence) -> {
            event.setMessage(message);
            event.setLevel(level);
            event.setTime(getTime(time));
            event.setLtime(time);
            event.setThreadName(getThreadName());
            event.setClassName(className);
            event.setMethodName(methodName);
            event.setLine(String.valueOf(lineNumber));
        });
    }

    private String getTime(LocalDateTime rightNow) {
        String date = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(rightNow);
        return date;
    }

    private String getThreadName() {
        return Thread.currentThread().getName();
    }


    public void info(final String name, final String message) {
        log("info", name, message, "", 0);
    }

    public void log(String level, final String name, final String message, String methodName, int lineNumber) {
        String className = name;
        pushEvent(message, level, LocalDateTime.now(), className, methodName, lineNumber);
    }


}
