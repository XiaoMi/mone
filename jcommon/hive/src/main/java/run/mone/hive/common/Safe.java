package run.mone.hive.common;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * @author goodjava@qq.com
 * @date 2025/3/16 13:50
 */
@Slf4j
public class Safe {

    public static void run(Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public static <T> T call(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
        return null;
    }

}
