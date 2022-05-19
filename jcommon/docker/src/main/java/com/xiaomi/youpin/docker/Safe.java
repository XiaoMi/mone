package com.xiaomi.youpin.docker;

import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * @date 2020/7/22
 */
@Slf4j
public abstract class Safe {

    public static void run(Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable ex) {
            log.error(ex.getMessage());
        }
    }

}
