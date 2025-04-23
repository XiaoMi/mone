package run.mone.hive.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * @date 2023/9/26 13:40
 */
@Slf4j
public class SafeRun {


    public static interface ExRunnable  {

        void run() throws Throwable;

    }


    public static void run(ExRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
    }

}
