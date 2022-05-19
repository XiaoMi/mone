package com.xiaomi.data.push.common;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;


/**
 * @author goodjava@qq.com
 */
@Slf4j
public class SafeRun {


    public static boolean run(SafeSupplier<Boolean> supplier, String name, long sleepTime) {
        try {
            return supplier.get();
        } catch (Throwable ex) {
            log.error("safe run {} error:{} ", name, ex.getMessage());
            if (sleepTime > 0) {
                try {
                    TimeUnit.MILLISECONDS.sleep(sleepTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
            return false;
        }
    }

    public static void run(SafeRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable ex) {
            log.error("safe run  error:" + ex.getMessage(), ex);
        }
    }


    public interface SafeRunnable {
        void run() throws Throwable;
    }


    @FunctionalInterface
    public interface SafeSupplier<T> {

        /**
         * Gets a result.
         *
         * @return a result
         */
        T get() throws Exception;
    }
}
