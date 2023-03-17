package com.xiaomi.hera.trace.etl.es.util.redis;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.commands.JedisCommands;

import javax.annotation.PostConstruct;
import java.time.Duration;

@Component
@Slf4j
public class RedisClientUtil {

    @Value("${server.type}")
    private String env;
    @NacosValue("${spring.redis.cluster.nodes}")
    private String clusterNodes;
    @Value("${spring.redis.timeout.connection}")
    private int timeout;
    @Value("${spring.redis.jedis.pool.max-active}")
    private int maxActive;
    @Value("${spring.redis.pool.max-idle}")
    private int maxIdle;
    @Value("${spring.redis.pool.max-wait}")
    private long maxWaitMillis;
    @NacosValue("${spring.redis.password}")
    private String pwd;
    @Value("${spring.redis.max-attempts}")
    private int maxAttempts;

    private JedisCommands jedis;
    private JedisPooled jedisPooled;

    @PostConstruct
    public void init() {
        // 线上使用Redis集群，st使用单机版
        String[] hp = clusterNodes.split(":");
        if (StringUtils.isEmpty(pwd)) {
            jedisPooled = new JedisPooled(getGenericObjectPoolConfig(), hp[0].trim(), Integer.valueOf(hp[1]), timeout);
        } else {
            jedisPooled = new JedisPooled(getGenericObjectPoolConfig(), hp[0].trim(), Integer.valueOf(hp[1]), timeout, pwd);
        }
        jedis = jedisPooled;
    }

    private GenericObjectPoolConfig getGenericObjectPoolConfig() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxTotal(maxActive);
        genericObjectPoolConfig.setMaxIdle(maxIdle);
        genericObjectPoolConfig.setMaxWait(Duration.ofMillis(maxWaitMillis));
        genericObjectPoolConfig.setTimeBetweenEvictionRuns(Duration.ofMillis(5000L));
        genericObjectPoolConfig.setMinEvictableIdleTime(Duration.ofMinutes(15L));
        genericObjectPoolConfig.setTestWhileIdle(true);
        return genericObjectPoolConfig;
    }

    public Boolean sismember(String key, String member) {
        try {
            return jedis.sismember(key, member);
        } catch (Exception e) {
            log.error("redis sismember error key:" + key + " member:" + member, e);
        }
        return null;
    }

    public Long sadd(String key, String... members) {
        try {
            return jedis.sadd(key, members);
        } catch (Exception e) {
            log.error("redis sadd error key:" + key, e);
        }
        return 0L;
    }

    public Long setNx(String key, String value) {
        try {
            return jedis.setnx(key, value);
        } catch (Exception e) {
            log.error("redis sadd error key:" + key, e);
        }
        return 0L;
    }

    public String get(String key) {
        try {
            return jedis.get(key);
        } catch (Exception e) {
            log.error("redis sadd error key:" + key, e);
        }
        return null;
    }

    public String set(String key, String value) {
        try {
            return jedis.set(key, value);
        } catch (Exception e) {
            log.error("redis set error key:" + key, e);
        }
        return null;
    }

    public String set(String key, String value, long ttl) {
        try {
            String set = jedis.set(key, value);
            if ("OK".equals(set)) {
                jedis.expire(key, ttl);
            }
            return set;
        } catch (Exception e) {
            log.error("redis set error key:" + key, e);
        }
        return null;
    }

    public Long del(String key) {
        try {
            return jedis.del(key);
        } catch (Exception e) {
            log.error("redis del error key:" + key, e);
        }
        return null;
    }

}
