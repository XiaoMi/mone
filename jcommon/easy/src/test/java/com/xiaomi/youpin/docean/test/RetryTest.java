package com.xiaomi.youpin.docean.test;

import com.xiaomi.youpin.docean.retry.Retry;
import lombok.extern.java.Log;
import org.junit.Test;

/**
 * @author goodjava@qq.com
 * @date 2023/2/19 13:36
 */
@Log
public class RetryTest {


    @Test
    public void testRetry() {
        Retry<Boolean> retry = new Retry<>(3, (r) -> {
            System.out.println("res:" + r);
            return r;
        });

        boolean b = retry.execute(() -> {
            return false;
        }).isPresent();
        System.out.println(b);

    }

    @Test
    public void testRetry2() {
        Retry<Boolean> retry = new Retry<>(3, (r) -> {
            System.out.println("res:" + r);
            return r;
        });

        retry.execute(() -> {
            if (true) {
                throw new RuntimeException("aaa");
            }
            return true;
        });

    }
}
