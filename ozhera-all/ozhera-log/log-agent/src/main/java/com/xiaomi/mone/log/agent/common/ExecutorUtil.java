/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.agent.common;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author shanwb
 * @date 2021-08-05
 */
@Slf4j
public class ExecutorUtil {

    public static ScheduledThreadPoolExecutor STP_EXECUTOR = new ScheduledThreadPoolExecutor(15, new CustomThreadFactory("ExecutorUtil-STP-Thread"));

    public static ExecutorService TP_EXECUTOR = createPool();

    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
                                                         long initialDelay,
                                                         long period,
                                                         TimeUnit unit) {
        return STP_EXECUTOR.scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    public static ExecutorService createPool() {
        System.setProperty("jdk.virtualThreadScheduler.parallelism", String.valueOf(Runtime.getRuntime().availableProcessors() + 1));
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    public static Future<?> submit(Runnable task) {
        log.warn("TP_EXECUTOR submit task:{}", task.toString());
        return TP_EXECUTOR.submit(task);
    }

    public static class CustomThreadFactory implements ThreadFactory {

        private String threadNamePrefix;
        private AtomicInteger count = new AtomicInteger(0);

        public CustomThreadFactory(String threadNamePrefix) {
            this.threadNamePrefix = threadNamePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            String threadName = threadNamePrefix + count.addAndGet(1);
            t.setName(threadName);
            return t;
        }
    }

    static class CustomRejectedExecutionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            try {
                executor.getQueue().put(r);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static {
        //Regularly print thread pool information
        STP_EXECUTOR.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                log.warn("Executor statistic TP_EXECUTOR:{}", TP_EXECUTOR.toString());
                log.warn("Executor statistic STP_EXECUTOR:{}", STP_EXECUTOR.toString());
            }
        }, 17, 30, TimeUnit.SECONDS);
    }

}
