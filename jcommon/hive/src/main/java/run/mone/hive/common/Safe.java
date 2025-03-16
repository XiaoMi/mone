package run.mone.hive.common;

import lombok.extern.slf4j.Slf4j;

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

}
