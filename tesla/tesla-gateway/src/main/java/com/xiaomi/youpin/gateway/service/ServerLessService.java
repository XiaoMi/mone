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

package com.xiaomi.youpin.gateway.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.xiaomi.data.push.common.SnowFlake;
import com.xiaomi.mione.serverless.SLService;
import com.xiaomi.youpin.gateway.function.imp.GwSnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Author goodjava@qq.com
 * @Date 2021/4/16 11:40
 */
@Service
@Slf4j
public class ServerLessService implements SLService {

    private ExecutorService pool = Executors.newFixedThreadPool(200);


    private Cache<String, Object> cache;

    @Autowired
    private GwSnowFlake gwSnowFlake;


    @PostConstruct
    public void init() {
        log.info("init");
        cache = CacheBuilder.newBuilder()
                .maximumSize(5000)
                .expireAfterWrite(50, TimeUnit.SECONDS)
                .build();
    }


    @Override
    public Future<Object> submit(Callable<Object> callable) {
        return pool.submit(callable);
    }


    @Override
    public List<Future<Object>> invokeAll(List<Callable<Object>> callableList) throws InterruptedException {
        return pool.invokeAll(callableList);
    }

    @Override
    public void putCache(String key, Object value) {
        cache.put(key, value);
    }

    @Override
    public <T> T getFromCache(String key, Callable<T> callable) throws ExecutionException {
        return (T) cache.get(key, callable);
    }

    @Override
    public <T> T getCacheIfPresent(String key, Class<T> tClass) {
        Object o = cache.getIfPresent(key);
        if (null == o) {
            return null;
        }

        if (o.getClass().equals(tClass)) {
            return (T) o;
        } else {
            throw new IllegalStateException("can not cast value to class:"+tClass);
        }
    }

    @Override
    public long nextUniqueId() {
        return gwSnowFlake.nextId();
    }
}
