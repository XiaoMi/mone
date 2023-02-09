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

package com.xiaomi.youpin.gwdash.common;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 */
@Component
@Slf4j
public class GwCache {

    private Cache<String, Object> caches[];

    public static final int TEN_MINUTE = 1;

    public static final int HOUR = 2;

    /**
     * 1分钟 0
     * 10分钟 1
     * 60分钟 2
     */
    private long times[] = {60, 600, 3600};

    @PostConstruct
    public void init() {
        caches = IntStream.range(0, times.length).mapToObj(i -> {
            Cache<String, Object> c = CacheBuilder.newBuilder()
                    //这里的时间不能随便改变,很多地方都用到了
                    .expireAfterWrite(times[i], TimeUnit.SECONDS)
                    .build();
            return c;
        }).toArray(Cache[]::new);
    }

    public <T> T get(String key, Callable<T> callable) {
        try {
            return (T) caches[0].get(key, callable);
        } catch (Throwable ex) {
            log.error(ex.getMessage());
            return null;
        }
    }

    public void put(int index, String key, Object value) {
        this.caches[index].put(key, value);
    }

    public Object get(int index, String key) {
        return caches[index].getIfPresent(key);
    }


    public static final String expansionKey(long envId) {
        return "expansion_" + envId;
    }

}
