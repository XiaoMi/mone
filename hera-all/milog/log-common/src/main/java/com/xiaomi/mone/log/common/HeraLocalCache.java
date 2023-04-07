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

package com.xiaomi.mone.log.common;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author shanwb
 * @date 2022-12-29
 */
@Slf4j
public class HeraLocalCache {
    private Cache<String, Object> localCache = null;

    public static HeraLocalCache instance() {
        return Inner.heraLocalCache;
    }
    private static class Inner {
        private static final HeraLocalCache heraLocalCache = new HeraLocalCache();
    }

    private HeraLocalCache() {
        localCache = CacheBuilder.newBuilder()
                .recordStats()
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .maximumSize(5000)
                .build();

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                log.info("HeraLocalCache status:{}", localCache.stats().toString());
            } catch (Exception ex) {
                //ignore
            }
        }, 0, 30, TimeUnit.SECONDS);
    }

    public void put(String key, Object value) {
        this.localCache.put(key, value);
    }

    public Object get(String key) {
        return this.localCache.getIfPresent(key);
    }

    public <T> T getObj(String key, Class<T> c) {
        return (T) this.localCache.getIfPresent(key);
    }

    public Cache<String, Object> getCache() {
        return this.localCache;
    }

}
