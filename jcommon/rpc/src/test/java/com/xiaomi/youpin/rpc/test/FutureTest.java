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
