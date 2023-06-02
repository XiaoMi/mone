package com.xiaomi.data.push.common;


import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author goodjava@qq.com
 * @date 2022/12/4 16:44
 */
public class ExecutorServiceUtils {

    public static ExecutorService creatThreadPool(int size, ExecutorService defaultPool) {
        if (size <= 0) {
            return defaultPool;
        }
        return new ThreadPoolExecutor(size, size,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(100));
    }

    public static ExecutorService creatThreadPoolHasName(int size, String name, ExecutorService defaultPool) {
        if (size <= 0) {
            return defaultPool;
        }
        return new ThreadPoolExecutor(size, size,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(100),
                new ThreadFactory() {
                    private final AtomicInteger id = new AtomicInteger(0);

                    public Thread newThread(Runnable r) {
                        String threadName = name + this.id.getAndIncrement();
                        Thread thread = new Thread(r, threadName);
                        thread.setDaemon(true);
                        return thread;
                    }
                });
    }


}
