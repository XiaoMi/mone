package com.xiaomi.mone.tpc.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.xiaomi.mone.tpc.cache.enums.CacheTypeEnum;
import com.xiaomi.mone.tpc.cache.key.Key;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * 简单的内存缓存实现
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@Slf4j
@Service
public class MemoryCacheService extends CacheService {

    private final Cache<String, Object> cache;

    public MemoryCacheService() {
        super(CacheTypeEnum.MEMORY.getCode());
        cache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.DAYS).build();
    }

    private Map<String,Object> getSubCache(Key bigKey) {
        String key = bigKey.toString();
        Map<String,Object> map = (Map<String,Object>)cache.getIfPresent(key);
        if (map == null) {
            synchronized (cache) {
                map = (Map<String,Object>)cache.getIfPresent(key);
                if (map == null) {
                    map = Maps.newConcurrentMap();
                    cache.put(key, map);
                }
            }
        }
        return map;
    }

    private ReentrantLock getReentrantLock(Key key) {
        String keystr = key.toString();
        ReentrantLock lock = (ReentrantLock)cache.getIfPresent(keystr);
        if (lock == null) {
            synchronized (cache) {
                lock = (ReentrantLock)cache.getIfPresent(keystr);
                if (lock == null) {
                    lock = new ReentrantLock();
                    cache.put(keystr, lock);
                }
            }
        }
        return lock;
    }


    @Override
    public boolean set0(Key key, Object value) {
        cache.put(key.toString(), value);
        return true;
    }

    @Override
    public boolean set0(Key bigKey, Key key, Object value) {
        Map<String, Object> subCache = getSubCache(bigKey);
        subCache.put(key.toString(), value);
        return true;
    }

    @Override
    public <T> T get0(Key key, Class<T> clazz) {
        return (T)cache.getIfPresent(key.toString());
    }

    @Override
    public <T> T get0(Key bigKey, Key key, Class<T> clazz) {
        Map<String, Object> subCache = getSubCache(bigKey);
        return (T)subCache.get(key.toString());
    }

    @Override
    public <T> List<T> gets0(Key key, Class<T> clazz) {
        return (List<T>)cache.getIfPresent(key.toString());
    }

    @Override
    public <T> List<T> gets0(Key bigKey, Key key, Class<T> clazz) {
        Map<String, Object> subCache = getSubCache(bigKey);
        return (List<T>)subCache.get(key.toString());
    }

    @Override
    public boolean delete0(Key key) {
        cache.invalidate(key.toString());
        return true;
    }

    @Override
    public boolean delete0(Key bigKey, Key key) {
        Map<String, Object> subCache = getSubCache(bigKey);
        subCache.remove(key.toString());
        return true;
    }

    @Override
    public boolean delete0(Collection<Key> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return true;
        }
        Set<String> keySet = keys.stream().map(Key::toString).collect(Collectors.toSet());
        cache.invalidate(keySet);
        return true;
    }

    @Override
    public boolean lock0(Key key) {
        ReentrantLock lock = getReentrantLock(key);
        return lock.tryLock();
    }

    @Override
    public boolean unlock0(Key key) {
        ReentrantLock lock = getReentrantLock(key);
        lock.unlock();
        return false;
    }
}
