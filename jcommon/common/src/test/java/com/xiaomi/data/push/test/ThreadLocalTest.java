/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
