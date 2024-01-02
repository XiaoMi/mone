package run.mone.mimeter.engine.service.test;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2022/9/17 10:17
 */
public class WaitTest {

    @Test
    public void testCountdownLatch() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
//        latch.await();
        latch.await(2, TimeUnit.SECONDS);
        System.out.println("finish");
    }
}
