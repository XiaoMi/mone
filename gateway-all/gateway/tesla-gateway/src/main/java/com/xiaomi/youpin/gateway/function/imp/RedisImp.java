package com.xiaomi.youpin.gateway.function.imp;

import com.xiaomi.youpin.gateway.redis.Redis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class RedisImp implements Redis {

    @Autowired
    private com.xiaomi.data.push.redis.Redis redis;

    @Override
    public String get(String key) {
        return redis.get(key);
    }

    @Override
    public Map<String, String> mget(List<String> keys) {
        return redis.mget(keys);
    }

    @Override
    public byte[] getBytes(String key) {
        return redis.getBytes(key);
    }

    @Override
    public boolean exists(String key) {
        return redis.exists(key);
    }

    @Override
    public void set(String key, String value) {
        redis.set(key, value);
    }

    @Override
    public Long setNx(String key, String value) {
        return redis.setNx(key, value);
    }

    @Override
    public void del(String key) {
        redis.del(key);
    }

    @Override
    public long incr(String key) {
        return redis.incr(key);
    }

    @Override
    public void expire(String key, int time) {
        redis.expire(key, time);
    }


}
