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
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.xiaomi.youpin.gateway.common.TeslaSafeRun;
import com.xiaomi.youpin.gateway.dispatch.strategy.impl.NormalStrategy;
import com.xiaomi.youpin.gateway.netty.transmit.connection.HttpHandler;
import com.xiaomi.youpin.gateway.protocol.http.HttpClient;
import com.xiaomi.youpin.gateway.service.ConfigService;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.NamedThreadFactory;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author goodjava@qq.com
 */
@Component
@Slf4j
public class Dispatcher {

    @Setter
    @NacosValue(value = "${invokePoolSize:50}")
    private int invokePoolSize = 50;


    @Setter
    @NacosValue(value = "${invokeQueueSize:5000}")
    private int queueSize = 5000;


    @Autowired
    private ConfigService configService;

    private NormalStrategy strategy = new NormalStrategy();


    private ThreadPoolExecutor executor = null;

    public Dispatcher() {

    }


    @PostConstruct
    public void init() {
        //这里必须用lined 的queue 不然被拒绝掉了,req 的内存资源不能得到清理(堆外内存泄漏)
        executor = new ThreadPoolExecutor(invokePoolSize, invokePoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(), new NamedThreadFactory("Dispatcher_Executor"));
        strategy.setQueueSize(queueSize);
        strategy.initPool(invokePoolSize);

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> TeslaSafeRun.run(() ->
                        log.info("Dispatcher_Executor {} {} {} future num:{} client num:{}",
                                executor.getActiveCount(),
                                executor.getQueue().size(),
                                executor.getActiveCount(),
                                HttpHandler.futureNum(),
                                HttpClient.clientSize()
                        ))
                , 5, 10, TimeUnit.SECONDS);
    }

    public Future dispatcher(Function<String, Object> function, Consumer<Object> resultConsumer, ApiInfo apiInfo, Consumer<ListenableFuture> futureConsumer) {
        final int timeOut = getTimeout(apiInfo);
        ListenableFuture<?> l = strategy.pool(0).submit(() -> {
            try {
                return strategy.callWithTimeout(() -> {
                    Object res = function.apply("");
                    return res;
                }, timeOut, TimeUnit.MILLISECONDS);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });

        if (null != futureConsumer) {
            futureConsumer.accept(l);
        }


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
        }, executor);

        return l;
    }


    public Future dispatcher(Function<String, Object> function, Consumer<Object> resultConsumer, ApiInfo apiInfo) {
        return dispatcher(function, resultConsumer, apiInfo, null);
    }


    private int getTimeout(ApiInfo apiInfo) {
        if (null == apiInfo) {
            return 1000;
        }
        int timeout = apiInfo.getTimeout();
        if (timeout <= 0) {
            return 1000;
        }
        int maxTimeout = configService.getTeslaTimeout();
        return Math.min(maxTimeout, timeout);
    }

}
