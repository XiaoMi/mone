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

package com.xiaomi.youpin.gateway.dispatch;


import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.*;
import com.xiaomi.youpin.gateway.common.GatewayWheelTimer;
import com.xiaomi.youpin.gateway.common.TeslaSafeRun;
import com.xiaomi.youpin.gateway.netty.transmit.connection.HttpHandler;
import com.xiaomi.youpin.gateway.protocol.http.HttpClient;
import com.xiaomi.youpin.gateway.service.ConfigService;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import io.netty.util.internal.PlatformDependent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.NamedThreadFactory;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author goodjava@qq.com
 */
@Component
@Slf4j
public class Dispatcher {

    @Value("${server.type}")
    private String serverType;

    @Setter
    @NacosValue(value = "${invokePoolSize:200}")
    private int invokePoolSize = 200;

    @Autowired
    private ConfigService configService;

    @Autowired
    private GatewayWheelTimer wheelTimer;

    private ThreadPoolExecutor executor = null;
    private ListeningExecutorService listeningExecutor = null;

    @Getter
    private ConcurrentHashMap<String, ListeningExecutorService> groupMap = new ConcurrentHashMap<>();


    public Dispatcher() {
    }


    @PostConstruct
    public void init() {
        executor = new ThreadPoolExecutor(1, 100, TimeUnit.SECONDS.toMillis(60), TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(100), new NamedThreadFactory("default_dispatcher_executor"));
        listeningExecutor = MoreExecutors.listeningDecorator(executor);
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> TeslaSafeRun.run(() -> {
                    log.info("execute pool num {} future num:{} client num:{}",
                            groupMap.size(),
                            HttpHandler.futureNum(),
                            HttpClient.clientSize()
                    );
                    //打印堆外内存信息(方便排查问题)
                    log.info("direct buffer info:max:{}:used:{}", PlatformDependent.maxDirectMemory(), PlatformDependent.usedDirectMemory());
                })
                , 5, 10, TimeUnit.SECONDS);
    }

    private ListeningExecutorService getListeningExecutor() {
        return this.listeningExecutor;
    }

    public ListeningExecutorService getPool(String group) {
        ListeningExecutorService pool = this.groupMap.get(group);
        if (null == pool) {
            return this.listeningExecutor;
        }
        return pool;
    }

    /**
     * 每个组使用不同的threadpool了,防止互相影响
     * @param group
     * @return
     */
    public ListeningExecutorService createExecutorService(String group, int poolSize) {
        log.info("create thread pool group:{}", group);
        Stopwatch sw = Stopwatch.createStarted();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(5, poolSize, TimeUnit.SECONDS.toMillis(60), TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(100), new NamedThreadFactory("dispatcher_executor_" + group));
        ListeningExecutorService listeningExecutor = MoreExecutors.listeningDecorator(executor);
        log.info("create pool use group:{} time:{}", group, sw.elapsed(TimeUnit.MILLISECONDS));
        return listeningExecutor;
    }


    private ThreadPoolExecutor getExecutor() {
        return this.executor;
    }

    public Future dispatcher(Function<String, Object> function, Consumer<Object> resultConsumer, ApiInfo apiInfo, Consumer<ListenableFuture> futureConsumer, Consumer<ApiInfo> cancel) {
        String g = "";
        try {
            final int timeOut = getTimeout(apiInfo);
            g = getGroup(apiInfo);
            //这里提交的任务可能会被拒绝掉,现在的队列是有限长度
            ListenableFuture<?> l = getPool(g).submit(() -> function.apply(""));
            if (null != futureConsumer) {
                futureConsumer.accept(l);
            }
            this.wheelTimer.newTimeout(timeOut, () -> {
                if (!l.isDone()) {
                    log.info("{} timeout cancel", Optional.ofNullable(apiInfo).isPresent() ? apiInfo.getUrl() : "null");
                    l.cancel(true);
                    if (null != cancel) {
                        cancel.accept(apiInfo);
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("{} is done", Optional.ofNullable(apiInfo).isPresent() ? apiInfo.getUrl() : "null");
                    }
                }
            }, timeOut + 10);
            Futures.addCallback(l, new FutureCallback<Object>() {
                @Override
                public void onSuccess(@Nullable Object result) {
                    resultConsumer.accept(result);
                }

                @Override
                public void onFailure(Throwable t) {
                    log.warn("dispatcher error:{}, apiinfo: {}", t.getMessage(), apiInfo);
                    resultConsumer.accept(t.getMessage());
                }
            }, getExecutor());

            return l;
        } catch (Throwable ex) {
            log.error("dispatcher error group:{} {}", g, ex.getMessage());
            resultConsumer.accept(ex);
            return CompletableFuture.completedFuture(ex);
        }
    }

    public String getGroup(ApiInfo apiInfo) {
        if (null == apiInfo) {
            return "";
        }
        String[] array = apiInfo.getUrl().split("/");
        if (array.length >= 3) {
            return array[2];
        }
        return "";
    }


    public Future dispatcher(Function<String, Object> function, Consumer<Object> resultConsumer, ApiInfo apiInfo) {
        return dispatcher(function, resultConsumer, apiInfo, null, null);
    }


    public int getTimeout(ApiInfo apiInfo) {
        if (null == apiInfo) {
            return 300;
        }
        int timeout = apiInfo.getTimeout();
        if (timeout <= 0) {
            return 300;
        }
        if ("file".equals(serverType)) {
            return configService.getTeslaFileTimeout();
        }
        int maxTimeout = configService.getTeslaTimeout();
        return Math.min(maxTimeout, timeout);
    }

}
