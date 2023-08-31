package com.xiaomi.data.push.common;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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


}
