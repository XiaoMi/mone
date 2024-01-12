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

package run.mone.mimeter.engine.service.test;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2022/6/5
 */
public class FutureTest {


    @Test
    public void testFuture() throws ExecutionException, InterruptedException {
        CompletableFuture<String> f = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("finish");
            return "ok";
        }).completeOnTimeout("timeout",1, TimeUnit.SECONDS).handle((v,e)->{
            System.out.println(v);
            return v;
        });
        System.out.println("aa");
        Thread.currentThread().join();
    }
}
