package com.xiaomi.youpin.docean.test.common;

import com.xiaomi.youpin.docean.common.DoceanWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2/16/21
 */
public class DoceanWheelTimerTest {

    @Test
    public void testnewTimeout() throws IOException {
        DoceanWheelTimer timer = new DoceanWheelTimer();
        timer.init();
        System.out.println(new Date());
        timer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                System.out.println(new Date());
            }
        }, 5, TimeUnit.SECONDS);

        System.in.read();
    }
}
