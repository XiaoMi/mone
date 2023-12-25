package com.xiaomi.youpin.docean.test;

import com.xiaomi.youpin.docean.test.bo.M;
import lombok.SneakyThrows;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 2023/8/26 22:58
 */
public class ScopedTest {


    @SneakyThrows
    @Test
    public void test1() {
        ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();
        AtomicInteger i = new AtomicInteger();
        int num = 30;
        CountDownLatch latch = new CountDownLatch(num);

        ScopedValue<M> sv = ScopedValue.newInstance();

        IntStream.range(0, num).parallel().forEach(it -> pool.submit(() -> {
            M m = new M();
            m.setId(it);
            ScopedValue.where(sv, m).run(() -> {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                i.incrementAndGet();
                System.out.println(sv.get().getId());
                latch.countDown();
            });

        }));
        latch.await();
        ;
        System.out.println(i);

    }
}
