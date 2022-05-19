package com.xiaomi.youpin.docean.plugin.test.log;

import com.xiaomi.youpin.docean.plugin.log.Log;
import org.junit.Test;

/**
 * @author goodjava@qq.com
 * @date 2020/7/4
 */
public class LogTest {


    @Test
    public void testLog() throws InterruptedException {
        Log log = new Log();
        log.init();

        log.info("", "abc");

        Thread.currentThread().join();
    }
}
