package com.xiaomi.youpin.rpc.test;

import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author goodjava@qq.com
 */
public class FutureTest {

    @Test
    public void testFuture() throws ExecutionException, InterruptedException, TimeoutException, IOException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(()->{
            System.out.println("call");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "abc";
        });
        future.complete("def");
        System.out.println(future.get(4, TimeUnit.SECONDS));
        System.in.read();
    }

    @Test
    public void testFuture2() throws ExecutionException, InterruptedException, TimeoutException, IOException {
        CompletableFuture<String> future = new CompletableFuture<>();
        System.out.println(future.get(4, TimeUnit.SECONDS));
    }

}
