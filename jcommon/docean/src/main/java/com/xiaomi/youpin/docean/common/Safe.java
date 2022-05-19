package com.xiaomi.youpin.docean.common;

import com.xiaomi.youpin.docean.exception.DoceanException;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

/**
 * @author goodjava@qq.com
 * @date 2020/6/21
 */
@Slf4j
public class Safe {


    public interface ExRunnable {
        void run() throws Throwable;
    }


    public static void run(ExRunnable runnable, Consumer<Throwable> consumer) {
        try {
            runnable.run();
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
            consumer.accept(ex);
        }
    }

    public static void run(ExRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
            throw new DoceanException(ex);
        }
    }

    public static void runAndLog(ExRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
    }

}
