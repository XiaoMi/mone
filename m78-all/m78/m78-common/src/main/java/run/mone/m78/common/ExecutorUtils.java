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
package run.mone.m78.common;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutorUtils {

    private static BlockingQueue<Runnable> flowRunnerQueue = new ArrayBlockingQueue<>(100);

    public static final ThreadPoolExecutor FLOW_STATUS_EXECUTOR;

    private static AtomicInteger threadNumber = new AtomicInteger(1);

    static{
        FLOW_STATUS_EXECUTOR = new ThreadPoolExecutor(10, 20,
                0L, TimeUnit.MILLISECONDS,
                flowRunnerQueue, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(false);
                thread.setName("flow-status-" + threadNumber.getAndIncrement());
                return thread;
            }
        });
    }
}
