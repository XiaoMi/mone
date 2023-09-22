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
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    public static ExecutorService creatThreadPoolHasName(int size, String name, ExecutorService defaultPool) {
        if (size <= 0) {
            return defaultPool;
        }
        return Executors.newVirtualThreadPerTaskExecutor();
    }


}
