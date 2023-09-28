package com.xiaomi.youpin.docean.test.thread;

import com.google.common.util.concurrent.Striped;
import lombok.SneakyThrows;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 2023/9/27 17:05
 */
public class ThreadTest {


    private int num = 100;

    private Object[] objs = createObjects(num);


    private Object[] createObjects(int n) {
        Object[] objs = new Object[n];
        IntStream.range(0, n).forEach(i -> objs[i] = new Object());
        return objs;
    }


    @SneakyThrows
    @Test
    public void testVirtualThread() {
        Striped<Lock> striped = Striped.lock(num);
        ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();
        CountDownLatch latch = new CountDownLatch(num);
        IntStream.range(0, num).forEach(i -> {
            pool.submit(() -> {
                method2(i, striped.get(i));
                latch.countDown();
            });
        });

        IntStream.range(0, 100).forEach(i -> {
            pool.submit(() -> {
                System.out.println("=====>" + i);
            });
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        latch.await();
    }


    @SneakyThrows
    private void method1(int i, Lock lock) {
        lock.lock();
        try {
            System.out.println(i + ":" + Thread.currentThread());
            TimeUnit.SECONDS.sleep(10);
        } finally {
            lock.unlock();
        }
    }

    @SneakyThrows
    private void method2(int i, Lock lock) {
        synchronized (objs[i % num]) {
            System.out.println(i + ":" + Thread.currentThread());
            TimeUnit.SECONDS.sleep(10);
        }
    }
}
