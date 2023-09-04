/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.agent;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.RateLimiter;
import com.google.gson.Gson;
import com.xiaomi.mone.log.agent.channel.comparator.OutputSimilarComparator;
import com.xiaomi.mone.log.agent.output.RmqOutput;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.Serializable;
import java.util.concurrent.*;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/8/29 19:52
 */
@Slf4j
public class Comparator {

    private Gson gson = new Gson();

    @Test
    public void testCompare() {
        String msg1 = "{\"clusterInfo\":\"http://127.0.0.1\",\"producerGroup\":\"subGroup_tags_1_4_620\",\"ak\":\"\",\"sk\":\"\",\"topic\":\"mone_hera_staging_trace_etl_server\",\"type\":\"talos\",\"tag\":\"tags_1_4_620\"}";
        RmqOutput outputOld = gson.fromJson(msg1, RmqOutput.class);
        OutputSimilarComparator outputSimilarComparator = new OutputSimilarComparator(outputOld);
        String msg2 = "{\"clusterInfo\":\"http://127.0.0.1\",\"producerGroup\":\"subGroup_tags_1_4_620\",\"ak\":\"\",\"sk\":\"\",\"topic\":\"mione_staging_jaeger_etl_sidecar_first\",\"type\":\"talos\",\"tag\":\"tags_1_4_620\"}";
        RmqOutput outputNew = gson.fromJson(msg2, RmqOutput.class);
        log.info("result:{}", outputSimilarComparator.compare(outputNew));
    }

    @Test
    public void test() {
        ImmutableList<? extends Serializable> list = ImmutableList.of("1", "2", 3);
        log.info("result:{}", list);
    }

    @Test
    public void testCache() throws ExecutionException {
        LoadingCache<String, String> graphs = CacheBuilder.newBuilder().maximumSize(1000)
                .build(new CacheLoader<String, String>() {
                    public String load(String key) {
                        // 这里是key根据实际去取值的方法，例如根据这个key去数据库或者通过复杂耗时的计算得出
                        System.out.println("no cache,load from db");
                        return "123";
                    }
                });
        String val1 = graphs.get("key");
        System.out.println("1 value is: " + val1);
        String val2 = graphs.get("key");
        System.out.println("2 value is: " + val2);
        Cache<String, String> cache = CacheBuilder.newBuilder()
                .maximumSize(1000).build();
        String val = cache.get("key", new Callable<String>() {
            public String call() {
                // 这里是key根据实际去取值的方法，例如根据这个key去数据库或者通过复杂耗时的计算得出
                System.out.println("val call method is invoked");
                return "123";
            }
        });
        System.out.println("1 value is: " + val1);
        val = cache.get("key", new Callable<String>() {
            public String call() {
                // 这里是key根据实际去取值的方法，例如根据这个key去数据库或者通过复杂耗时的计算得出
                System.out.println("val call method is invoked");
                return "123";
            }
        });
        System.out.println("1 value is: " + val1);

        val = cache.get("testKey", new Callable<String>() {
            public String call() {
                // 这里是key根据实际去取值的方法，例如根据这个key去数据库或者通过复杂耗时的计算得出
                System.out.println("testKey val call method is invoked");
                return "456";
            }
        });
        System.out.println("2 value is: " + val);

    }


    @Test
    public void testRateLimiter() throws InterruptedException {
        // qps设置为5，代表一秒钟只允许处理五个并发请求
        RateLimiter rateLimiter = RateLimiter.create(5);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        int nTasks = 10;
        CountDownLatch countDownLatch = new CountDownLatch(nTasks);
        long start = System.currentTimeMillis();
        for (int i = 0; i < nTasks; i++) {
            final int j = i;
            executorService.submit(() -> {
                rateLimiter.acquire();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                log.info(Thread.currentThread().getName() + " gets job " + j + " done");
                countDownLatch.countDown();
            });
        }
        executorService.shutdown();
        countDownLatch.await();
        long end = System.currentTimeMillis();
        log.info("10 jobs gets done by 5 threads concurrently in " + (end - start) + " milliseconds");
    }
}
