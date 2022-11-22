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

package com.xiaomi.youpin.gateway.dispatch.strategy.impl;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.xiaomi.youpin.gateway.common.TeslaSafeRun;
import com.xiaomi.youpin.gateway.dispatch.strategy.DispatcherStrategy;
import io.netty.util.internal.PlatformDependent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.NamedThreadFactory;

import java.util.concurrent.*;

/**
 * Created by zhangzhiyong1 on 2016/9/27.
 */
@Slf4j
public class NormalStrategy extends DispatcherStrategy {

    @Setter
    private int queueSize = 5000;

    private ArrayBlockingQueue queue = null;

    private ArrayBlockingQueue timeLimiterQueue = null;

    private SimpleTimeLimiter timeLimiter = null;

    private ThreadPoolExecutor pool = null;

    private ListeningExecutorService listeningExecutor = null;


    @Override
    public void initPool(int poolSize) {
        log.info("init NormalStrategy poolSize:{} queueSize:{}", poolSize, queueSize);
        queue = new ArrayBlockingQueue<>(queueSize);
        timeLimiterQueue = new ArrayBlockingQueue<>(queueSize);

        ThreadPoolExecutor timeLimiterPool = new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, timeLimiterQueue, new NamedThreadFactory("MethodInvoker_Limiter"), (r, executor) -> log.warn("SimpleTimeLimiter invoke rejected"));
        timeLimiter = SimpleTimeLimiter.create(timeLimiterPool);

        pool = new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, queue, new NamedThreadFactory("MethodInvoker"), (r, executor) -> {
            try {
                boolean cancel = false;
                if (r instanceof Future) {
                    Future ft = (Future) r;
                    ft.cancel(true);
                    cancel = true;
                }
                log.warn("tesla method invoke rejected  future cacnel:{}", cancel);
            } catch (Throwable ex) {
                //ignore
            }
        });

        listeningExecutor = MoreExecutors.listeningDecorator(pool);

        //线程的监控+堆外内存的监控
        new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(() -> TeslaSafeRun.run(() -> {

            log.info("task queue size:{} active:{} complected:{}", pool.getQueue().size(), pool.getActiveCount(), pool.getCompletedTaskCount());

            log.info("limit queue size:{} active:{} complected:{}", timeLimiterPool.getQueue().size(), timeLimiterPool.getActiveCount(), timeLimiterPool.getCompletedTaskCount());

            log.info("direct buffer info:max:{}:used:{}", PlatformDependent.maxDirectMemory(), PlatformDependent.usedDirectMemory());

        }), 0, 30, TimeUnit.SECONDS);
    }

    @Override
    public int poolId() {
        return 0;
    }


    public Object callWithTimeout(Callable callable, long timeoutDuration, TimeUnit timeoutUnit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.timeLimiter.callWithTimeout(callable, timeoutDuration, timeoutUnit);
    }

    @Override
    public ListeningExecutorService pool(int index) {
        return this.listeningExecutor;
    }

}
