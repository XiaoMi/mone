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

package run.mone.mimeter.engine.test;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 2022/5/19
 */
public class VirturalThreadTest {

    static final Object obj = new Object();

    @Test
    public void test1() throws InterruptedException {
        System.out.println("123");
        new Thread(() -> {
            System.out.println("123");
        }).start();
        Thread.currentThread().join();
    }

    @Test
    public void test3() {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            System.out.println(random.nextInt(3));
        }
    }

    @Test
    public void test5() {
        AtomicInteger counter = new AtomicInteger(10);
        long before = System.currentTimeMillis();
//
        new Thread(() -> {
            try {
                synchronized (obj) {
                    TimeUnit.SECONDS.sleep(3000);
                    obj.notify();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }).start();

        System.out.println("123");
        synchronized (obj) {
            try {
                obj.wait(8000);
            } catch (InterruptedException e) {
                e.fillInStackTrace();
            }
        }

        long now = System.currentTimeMillis();
        System.out.println("time:" + (now - before));
        System.out.println("456");
    }

    @Test
    public void test4() {
        System.out.println(isInt(1.10));
    }


    @Test
    public void test6(){
        Pattern EL_PATTERN = Pattern.compile("\\$\\{([^}]*)}");

        String json = "{\"userInfo\":\"${username}\"}";
        Matcher m = EL_PATTERN.matcher(json);
        while (m.find()) {
            String expr = m.group(1);
//            body = Util.Parser.parse$(expr, body, getDataValue(expr, dataMap, lineFlag));
            System.out.println(expr);
        }
    }

    public static boolean isInt(double num) {
        return Math.abs(num - Math.round(num)) < Double.MIN_VALUE;
    }

    @Test
    public void test2() throws InterruptedException {

        LongAdder total = new LongAdder();

        total.add(1000*1000);

        LongAdder counter = new LongAdder();

        ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();
        IntStream.range(0, 1000).forEach(i -> {
            for (int j = 0; j < 1000; j++) {
                pool.submit(() -> {
                    counter.increment();
                    try {
                        Thread.sleep(1000);
                        total.decrement();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("fiber-" + i);
                });
            }
        });

        aWait(total);
        System.out.println("！！！！"+counter.longValue());

    }

    private void aWait(LongAdder counter) throws InterruptedException {
        long before = System.currentTimeMillis();
        //阻塞主线程直到所有调用结束
        while (counter.intValue() != 0) {
            TimeUnit.MILLISECONDS.sleep(500);
        }
    }
}
