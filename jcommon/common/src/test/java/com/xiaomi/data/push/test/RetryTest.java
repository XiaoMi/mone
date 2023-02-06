package com.xiaomi.data.push.test;

import com.github.rholder.retry.*;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

/**
 * @author goodjava@qq.com
 * @date 2022/12/2 16:24
 */
public class RetryTest {


    @Test
    public void testRetryer() throws ExecutionException, RetryException {
        Retryer<Integer> retryer = RetryerBuilder.<Integer>newBuilder()
                .retryIfRuntimeException()
                .retryIfResult(result -> result % 2 == 0)
                .withWaitStrategy(failedAttempt -> 1000L)
                .withStopStrategy(StopStrategies.stopAfterAttempt(3)).build();

        retryer.call(() -> {
            System.out.println("call");
            return 2;
        });
        System.out.println("finish");
    }

    @Test
    public void testRetryer2() throws ExecutionException, RetryException {
        Retryer<Integer> retryer = RetryerBuilder.<Integer>newBuilder()
                .retryIfRuntimeException()
                .retryIfException()
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        System.out.println(attempt.getAttemptNumber());
                        if (attempt.hasException()) {
                            System.out.println("retry:" + attempt.getExceptionCause());
                        }
                    }
                })
                .withWaitStrategy(failedAttempt -> 1000L)
                .withStopStrategy(StopStrategies.stopAfterAttempt(3)).build();

        try {
            Integer v = retryer.call(() -> {
                if (false) {
                    throw new RuntimeException("error");
                }
                System.out.println("call");
                return 2;
            });
            System.out.println("finish:" + v);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }


}
