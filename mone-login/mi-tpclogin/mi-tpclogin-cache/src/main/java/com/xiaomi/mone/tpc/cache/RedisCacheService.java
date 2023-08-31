package com.xiaomi.mone.tpc.cache;

import com.google.gson.reflect.TypeToken;
import com.xiaomi.mone.tpc.cache.enums.CacheTypeEnum;
import com.xiaomi.mone.tpc.cache.key.Key;
import com.xiaomi.mone.tpc.common.util.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * redis缓存实现
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@Slf4j
@Service
public class RedisCacheService extends CacheService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public RedisCacheService() {
        super(CacheTypeEnum.REDIS.getCode());
    }

    @Override
    public boolean set0(Key key, Object value) {
        stringRedisTemplate.opsForValue().set(key.toString(), GsonUtil.gsonString(value), key.getTime(), key.getUnit());
        return true;
    }

    @Override
    public boolean set0(Key bigKey, Key key, Object value) {
        stringRedisTemplate.opsForHash().put(bigKey.toString(), key.toString(), GsonUtil.gsonString(value));
        stringRedisTemplate.expire(bigKey.toString(), key.getTime(), key.getUnit());
        return true;
    }

    @Override
    public <T> T get0(Key key, Class<T> clazz) {
        String value = stringRedisTemplate.opsForValue().get(key.toString());
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        return GsonUtil.gsonToBean(value, clazz);
    }

    @Override
    public <T> T get0(Key bigKey, Key key, Class<T> clazz) {
        String value = (String) stringRedisTemplate.opsForHash().get(bigKey.toString(), key.toString());
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        return GsonUtil.gsonToBean(value, clazz);
    }

    @Override
    public <T> List<T> gets0(Key key, Class<T> clazz) {
        String value = stringRedisTemplate.opsForValue().get(key.toString());
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        return GsonUtil.gsonToBean(value, new TypeToken<List<T>>(){});
    }

    @Override
    public <T> List<T> gets0(Key bigKey, Key key, Class<T> clazz) {
        String value = (String)stringRedisTemplate.opsForHash().get(bigKey.toString(), key.toString());
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        return GsonUtil.gsonToBean(value, new TypeToken<List<T>>(){});
    }

    @Override
    public boolean delete0(Key key) {
        stringRedisTemplate.delete(key.toString());
        return true;
    }

    @Override
    public boolean delete0(Key bigKey, Key key) {
        stringRedisTemplate.opsForHash().delete(bigKey.toString(), key.toString());
        return true;
    }

    @Override
    public boolean delete0(Collection<Key> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return true;
        }
        List<String> strKeys = keys.stream().map(Key::toString).collect(Collectors.toList());
        stringRedisTemplate.delete(strKeys);
        return true;
    }

    @Override
    public boolean lock0(Key key) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key.toString(), "1", key.getTime(), key.getUnit());
    }

    @Override
    public boolean unlock0(Key key) {
        return stringRedisTemplate.delete(key.toString());
    }
}
