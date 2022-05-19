package com.xiaomi.mione.mquic.demo.common;

import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * @date 9/5/21
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
