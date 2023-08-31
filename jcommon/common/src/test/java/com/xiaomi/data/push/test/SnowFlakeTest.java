package com.xiaomi.data.push.test;

import com.google.common.base.Stopwatch;
import com.xiaomi.data.push.common.SnowFlake;
import org.junit.Test;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SnowFlakeTest {

    @Test
    public void testConflict() {
        Set<Long> idSet = new CopyOnWriteArraySet<>();

        for (int i = 0; i < 10; i++) {
            new Thread() {{
                    int conflict = 0;
                    SnowFlake worker = new SnowFlake();
                    Stopwatch stopwatch = Stopwatch.createStarted();
                    for (int j = 0; j < 1 * 10000; j++) {
                        long id = worker.nextId();
                        if (idSet.contains(id)) {
                            System.out.println("xxxxxxxxx conflict id:" + id);
                            conflict ++;
                        }
                        idSet.add(id);
                    }
                    stopwatch.stop();
                    System.out.println("end, 耗时：" + stopwatch.toString() + ", 冲突数:" + conflict);
                }
            }.start();
        }

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCost() throws InterruptedException {
        SnowFlake worker = new SnowFlake();

        int threadCnt = 100;
        final CountDownLatch cdl = new CountDownLatch(threadCnt);

        for (int i = 0; i < threadCnt; i++) {
            new Thread(){
                {
                    try {
                        Stopwatch stopwatch = Stopwatch.createStarted();
                        for (int j = 0; j < 10 * 10000; j++) {
                            long id = worker.nextId();
                        }
                        stopwatch.stop();
                        System.out.println("end, 耗时：" + stopwatch.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        cdl.countDown();
                    }
                }
            }.start();
        }

        cdl.await();
        System.out.println("SnowFlake end");
    }

    @Test
    public void testInit() {
        SnowFlake worker = new SnowFlake(3, 256, 0);
    }
}
