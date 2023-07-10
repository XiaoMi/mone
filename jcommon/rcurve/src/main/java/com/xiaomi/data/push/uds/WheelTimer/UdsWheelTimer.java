package com.xiaomi.data.push.uds.WheelTimer;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;

import java.util.concurrent.TimeUnit;

/**
 * @author zhangping17
 * @date 2023-06-09
 */
public class UdsWheelTimer {

    private HashedWheelTimer hashedWheelTimer;

    public UdsWheelTimer() {
        init();
    }

    private void init() {
        hashedWheelTimer = new HashedWheelTimer(100, TimeUnit.MILLISECONDS, 512);
    }


    public Timeout newTimeout(Runnable runnable, long delay) {
        return hashedWheelTimer.newTimeout(timeout -> runnable.run(), delay, TimeUnit.MILLISECONDS);
    }
}
