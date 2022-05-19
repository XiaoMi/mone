package com.xiaomi.data.push.test;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.TtlRunnable;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @Author goodjava@qq.com
 * @Date 2021/7/5 12:30
 */
public class ThreadLocalTest {

    @Test
    public void test1() throws InterruptedException {
        TransmittableThreadLocal<String> tl = new TransmittableThreadLocal<>();
        tl.set("abc");
        System.out.println(tl.get());

        new Thread(() -> {
            System.out.println(tl.get());
        }).start();

        pool.submit(TtlRunnable.get(new Runnable() {
            @Override
            public void run() {
                IntStream.range(0, 3).forEach(i -> {
                    System.out.println(tl.get());
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                });
            }
        }));
        tl.set("def");
        pool.submit(TtlRunnable.get(new Runnable() {
            @Override
            public void run() {
                IntStream.range(0, 1).forEach(i -> {
                    System.out.println("def:" + tl.get());
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                });
            }
        }));
        Thread.currentThread().join();
    }


    @Test
    public void test2() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        ThreadLocal<String> tl = new ThreadLocal<>();
        tl.set("abc");
        System.out.println(tl.get());

        new Thread(() -> {
            System.out.println(tl.get());
        }).start();

        pool.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println(tl.get());
            }
        });
        Thread.currentThread().join();
    }


    private static ExecutorService pool = Executors.newSingleThreadExecutor();

    @Test
    public void test3() throws InterruptedException {
        InheritableThreadLocal<String> tl = new InheritableThreadLocal<>();
        tl.set("abc");
        System.out.println(tl.get());

        new Thread(() -> {
            System.out.println(tl.get());
        }).start();

        pool.submit(new Runnable() {
            @Override
            public void run() {
                IntStream.range(0, 3).forEach(i -> {
                    System.out.println(tl.get());
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                });
            }
        });
        tl.set("def");
        pool.submit(new Runnable() {
            @Override
            public void run() {
                IntStream.range(0, 1).forEach(i -> {
                    System.out.println("def:" + tl.get());
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                });
            }
        });
        Thread.currentThread().join();
    }

}
