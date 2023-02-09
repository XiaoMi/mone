package com.xiaomi.mone.tpc.cache;

import com.xiaomi.mone.tpc.cache.key.Key;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存接口定义
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@Slf4j
public abstract class CacheService {

    protected Integer cacheType;

    public CacheService(Integer cacheType) {
        this.cacheType = cacheType;
    }

    public boolean set(Key key, Object value) {
        try {
            return set0(key, value);
        } catch (Throwable e) {
            log.info("cache set exception key={}, value={}", key, value, e);
            return false;
        }
    }
    public abstract boolean set0(Key key, Object value);

    public boolean set(Key bigKey, Key key, Object value){
        try {
            return set0(bigKey, key, value);
        } catch (Throwable e) {
            log.error("cache set exception bigKey={}, key={}, value={}", bigKey, key, value, e);
            return false;
        }
    }
    public abstract boolean set0(Key bigKey, Key key, Object value);

    public <T> T get(Key key, Class<T> clazz) {
        try {
            return get0(key, clazz);
        } catch (Throwable e) {
            log.error("cache get exception key={}", key, e);
            return null;
        }
    }
    public abstract <T> T get0(Key key, Class<T> clazz);

    public <T> T get(Key bigKey, Key key, Class<T> clazz) {
        try {
            return get0(bigKey, key, clazz);
        } catch (Throwable e) {
            log.error("cache set exception bigKey={}, key={}", bigKey, key, e);
            return null;
        }
    }

    public abstract <T> T get0(Key bigKey, Key key, Class<T> clazz);

    public <T> List<T> gets(Key key, Class<T> clazz) {
        try {
            return gets0(key, clazz);
        } catch (Throwable e) {
            log.error("cache gets exception key={}", key, e);
            return null;
        }
    }

    public abstract <T> List<T> gets0(Key key, Class<T> clazz);

    public <T> List<T> gets(Key bigKey, Key key, Class<T> clazz){
        try {
            return gets0(bigKey, key, clazz);
        } catch (Throwable e) {
            log.error("cache gets exception bigKey={}, key={}", bigKey, key, e);
            return null;
        }
    }

    public abstract <T> List<T> gets0(Key bigKey, Key key, Class<T> clazz);

    public boolean delete(Key key) {
        try {
            return delete0(key);
        } catch (Throwable e) {
            log.error("cache delete delete key={}", key, e);
            return false;
        }
    }

    public abstract boolean delete0(Key key);

    public boolean delete(Key bigKey, Key key) {
        try {
            return delete0(bigKey, key);
        } catch (Throwable e) {
            log.error("cache delete bigKey={}, key={}", bigKey, key, e);
            return false;
        }
    }
    public abstract boolean delete0(Key bigKey, Key key);

    public boolean delete(Collection<Key> keys) {
        try {
            return delete0(keys);
        } catch (Throwable e) {
            log.error("cache delete delete keys={}", keys, e);
            return false;
        }
    }

    public abstract boolean delete0(Collection<Key> keys);

    public boolean lock(Key key) {
        try {
            return lock0(key);
        } catch (Throwable e) {
            log.error("cache lock key={}", key, e);
            return false;
        }
    }

    public boolean lock(Key key, int waitTime, TimeUnit unit) {
        try {
            return lock0(key);
        } catch (Throwable e) {
            log.error("cache lock key={}", key, e);
            return false;
        }
    }

    public abstract boolean lock0(Key key);

    public boolean unlock(Key key) {
        try {
            return unlock0(key);
        } catch (Throwable e) {
            log.error("cache lock key={}", key, e);
            return false;
        }
    }

    public abstract boolean unlock0(Key key);
}
