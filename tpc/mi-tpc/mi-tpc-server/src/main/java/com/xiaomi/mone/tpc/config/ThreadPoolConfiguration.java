package com.xiaomi.mone.tpc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池配置
 */
@EnableAsync//开启异步调用
@Configuration
public class ThreadPoolConfiguration {

    @Bean("notifyExecutor")
    public ThreadPoolExecutor notifyExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 10, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(50), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "notify-executor");
            }
        }, new ThreadPoolExecutor.CallerRunsPolicy());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> executor.shutdown()));
        return executor;
    }

    @Bean("nodeLinkExecutor")
    public ThreadPoolExecutor nodeLinkExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 10, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(50), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "nodelink-executor");
            }
        }, new ThreadPoolExecutor.CallerRunsPolicy());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> executor.shutdown()));
        return executor;
    }

    @Bean("nodeChangeExecutor")
    public ThreadPoolExecutor nodeChangeExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 10, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(50), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "nodeChange-executor");
            }
        }, new ThreadPoolExecutor.CallerRunsPolicy());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> executor.shutdown()));
        return executor;
    }
}
