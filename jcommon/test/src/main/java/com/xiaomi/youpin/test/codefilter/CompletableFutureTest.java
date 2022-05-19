package com.xiaomi.youpin.test.codefilter;

import com.google.common.util.concurrent.MoreExecutors;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author goodjava@qq.com
 * @date 2/12/21
 */
public class CompletableFutureTest {


    @Test
    public void testCompletableFuture() throws ExecutionException, InterruptedException, IOException {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("supply");
            return "abc";
        }, pool);

//        String str = future.handleAsync((v, e) -> {
//            return v;
//        }, pool).get();
//        System.out.println(str);

        System.in.read();
    }
}
