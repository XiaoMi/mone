package com.xiaomi.data.push.uds.schedule;

import com.xiaomi.data.push.common.SafeRun;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 1/8/21
 */
public class ScheduleManager {

    public static void scheduleAtFixedRate(Runnable runnable, int period) {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            SafeRun.run(() -> {
                runnable.run();
            });
        }, 0, period, TimeUnit.SECONDS);
    }

}
