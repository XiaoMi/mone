package run.mone.hive.common;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.utils.SafeRun;

import java.util.concurrent.Callable;

/**
 * @author goodjava@qq.com
 * @date 2025/3/16 13:50
 */
@Slf4j
public class Safe {


    public interface ExRunnable {
        void run() throws Throwable;
    }

    public static void run(ExRunnable runnable) {
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
