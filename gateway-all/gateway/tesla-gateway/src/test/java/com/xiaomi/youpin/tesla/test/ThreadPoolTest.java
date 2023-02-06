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

package com.xiaomi.youpin.tesla.test;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.xiaomi.youpin.gateway.common.TeslaSafeRun;
import org.apache.dubbo.common.utils.NamedThreadFactory;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 2022/4/16
 */
public class ThreadPoolTest {

    @Test
    public void testThreadPool() {
        String group = "group";
        int poolSize = 1;
        ThreadPoolExecutor pool = new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1), new NamedThreadFactory("dispatcher_executor_" + group));
        IntStream.range(0, 10).forEach(i -> {
            TeslaSafeRun.run(() -> {
                pool.submit(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            });
        });
    }


    @Test
    public void testThreadPool2() {
        String group = "group";
        int poolSize = 1;
        ThreadPoolExecutor pool = new ThreadPoolExecutor(poolSize, poolSize, 60000L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1), new NamedThreadFactory("dispatcher_executor_" + group), new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                System.out.println("===>rej:" + executor.getQueue().size() + "," + executor.getActiveCount());
            }
        }) {
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                System.out.println("===>" + this.getQueue().size());
            }
        };
        ListeningExecutorService listeningExecutor = MoreExecutors.listeningDecorator(pool);
        IntStream.range(0, 10).forEach(i -> {
            TeslaSafeRun.run(() -> {
                listeningExecutor.submit(() -> {
                    try {
                        TimeUnit.SECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("finish");
                });
            });
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}

