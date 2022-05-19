package com.xiaomi.mone.docean.plugin.akka;

import com.xiaomi.youpin.docean.common.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * @author goodjava@qq.com
 * @date 2/12/21
 */
@Slf4j
public class ExecutorsManager {

    private ConcurrentHashMap<String, ExecutorService> map = new ConcurrentHashMap<>();

    public void createPool(String appName, int num) {
        log.info("create pool:{} {}", appName, num);
        ExecutorService pool = new ThreadPoolExecutor(num, num,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(20000), new NamedThreadFactory("appName_"));
        this.map.put(appName, pool);
    }


    public void destoryPool(String appName) {
        log.info("destory pool:{}", appName);
        ExecutorService pool = this.map.get(appName);
        pool.shutdownNow();
        this.map.remove(appName);
    }

    public void destory() {
        this.map.entrySet().forEach(e -> {
            e.getValue().shutdownNow();
        });
    }

    public CompletableFuture supplyAsync(String appName, Supplier supplier) {
        ExecutorService pool = this.map.get(appName);
        return CompletableFuture.supplyAsync(supplier, pool);
    }

    public Future submit(String appName, Supplier supplier) {
        ExecutorService pool = this.map.get(appName);
        return pool.submit(() -> supplier.get());
    }

}
