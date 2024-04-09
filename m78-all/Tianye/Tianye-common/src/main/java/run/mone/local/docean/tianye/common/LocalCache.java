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

package run.mone.local.docean.tianye.common;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @author shanwb
 */
public class LocalCache {
    private final Cache<Object, Object> cache;

    private LocalCache() {
        cache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(17, TimeUnit.MINUTES)
                .build();
    }

    // 静态内部类，用于实现延迟加载
    private static class SingletonHolder {
        // 创建LocalCache的单例实例
        private static final LocalCache INSTANCE = new LocalCache();
    }

    // 提供一个公共的静态方法，用于获取单例实例
    public static LocalCache getInstance() {
        return SingletonHolder.INSTANCE;
    }

    // 向缓存中添加一个键值对
    public void put(Object key, Object value) {
        cache.put(key, value);
    }

    // 从缓存中获取一个对象
    public <V> V get(Object key) {
        return (V) cache.getIfPresent(key);
    }

    // 从缓存中移除一个对象
    public void invalidate(Object key) {
        cache.invalidate(key);
    }

    // 清空缓存
    public long invalidateAll() {
        long total = cache.size();
        cache.invalidateAll();

        return total;
    }
}

