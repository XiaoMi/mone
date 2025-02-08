package run.mone.m78.service.common;

import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * @date 2023/4/19 14:30
 */
@Slf4j
public class SafeRun {

    public static void run(Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
    }

}
