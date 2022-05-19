package com.xiaomi.youpin.docean.common;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2/16/21
 */
public class DoceanWheelTimer {

    private HashedWheelTimer hashedWheelTimer;

    public void init() {
        hashedWheelTimer = new HashedWheelTimer(1000, TimeUnit.MILLISECONDS, 16);
    }


    public void newTimeout(TimerTask task, long delay, TimeUnit timeUnit) {
        hashedWheelTimer.newTimeout(task, delay, timeUnit);
    }

    public void newTimeout(Runnable runnable, long delay) {
        hashedWheelTimer.newTimeout(timeout -> runnable.run(), delay, TimeUnit.MILLISECONDS);
    }
}