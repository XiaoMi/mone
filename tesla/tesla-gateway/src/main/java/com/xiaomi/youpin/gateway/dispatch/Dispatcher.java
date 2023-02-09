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
import com.google.common.util.concurrent.*;
import com.xiaomi.youpin.gateway.common.Const;
import com.xiaomi.youpin.gateway.common.GatewayWheelTimer;
import com.xiaomi.youpin.gateway.common.TeslaSafeRun;
import com.xiaomi.youpin.gateway.netty.transmit.connection.HttpHandler;
import com.xiaomi.youpin.gateway.protocol.http.HttpClient;
import com.xiaomi.youpin.gateway.service.ConfigService;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
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

    @NacosValue(value = "${slowInvokePoolSize:200}")
    private int slowInvokePoolSize = 200;

    @Autowired
    private ConfigService configService;

    @Autowired
    private GatewayWheelTimer wheelTimer;

    private ThreadPoolExecutor executor = null;
    private ListeningExecutorService listeningExecutor = null;

    private ThreadPoolExecutor slowExecutor = null;
    private ListeningExecutorService slowListeningExecutor = null;

    public Dispatcher() {
    }


    @PostConstruct
    public void init() {
        //这里必须用lined 的queue 不然被拒绝掉了,req 的内存资源不能得到清理(堆外内存泄漏)
        executor = new ThreadPoolExecutor(invokePoolSize, invokePoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(), new NamedThreadFactory("Dispatcher_Executor"));
        listeningExecutor = MoreExecutors.listeningDecorator(executor);
        //专门处理慢的请求
        slowExecutor = new ThreadPoolExecutor(slowInvokePoolSize, slowInvokePoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(), new NamedThreadFactory("Dispatcher_Slow_Executor"));
        slowListeningExecutor = MoreExecutors.listeningDecorator(slowExecutor);

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> TeslaSafeRun.run(() ->
                        log.info("Dispatcher_Executor {} {} {} future num:{} client num:{} slow:{} {} {}",
                                executor.getActiveCount(),
                                executor.getQueue().size(),
                                executor.getActiveCount(),
                                HttpHandler.futureNum(),
                                HttpClient.clientSize(),
                                slowExecutor.getActiveCount(),
                                slowExecutor.getQueue().size(),
                                slowExecutor.getActiveCount()
                        ))
                , 5, 10, TimeUnit.SECONDS);
    }

    private ListeningExecutorService getListeningExecutor(int timeout) {
        return timeout < Const.SLOW_TIME? this.listeningExecutor : this.slowListeningExecutor;
    }

    private ThreadPoolExecutor getExecutor(int timeout) {
        return timeout < Const.SLOW_TIME ? this.executor : this.slowExecutor;
    }

    public Future dispatcher(Function<String, Object> function, Consumer<Object> resultConsumer, ApiInfo apiInfo, Consumer<ListenableFuture> futureConsumer, Consumer<ApiInfo> cancel) {
        final int timeOut = getTimeout(apiInfo);
        ListenableFuture<?> l = this.getListeningExecutor(timeOut).submit(() -> function.apply(""));

        if (null != futureConsumer) {
            futureConsumer.accept(l);
        }

        this.wheelTimer.newTimeout(timeOut,() -> {
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
        }, timeOut + 50);


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
        }, getExecutor(timeOut));

        return l;
    }


    public Future dispatcher(Function<String, Object> function, Consumer<Object> resultConsumer, ApiInfo apiInfo) {
        return dispatcher(function, resultConsumer, apiInfo, null, null);
    }


    private int getTimeout(ApiInfo apiInfo) {
        if (null == apiInfo) {
            return 1000;
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
