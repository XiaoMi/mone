package run.mone.mimeter.engine.test;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CountdownLatchTest {


    @Test
    public void testLatch() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        System.out.println(latch.getCount());
        latch.await(2,TimeUnit.SECONDS);
        System.out.println(latch.getCount());
    }

    @Test
    public void testCountdownLatch() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(()->{
            try {
                TimeUnit.SECONDS.sleep(4);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            latch.countDown();
        }).start();
        latch.await(10, TimeUnit.SECONDS);
        System.out.println("finish");
    }
}
