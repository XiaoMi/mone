package com.xiaomi.miapi.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {
    @NacosValue("${redis.host}")
    private String host;

    @NacosValue("${redis.port}")
    private int port;

    @NacosValue("${redis.password}")
    private String pwd;

    @Value("${redis.timeout}")
    private int timeout;

    @Value("${redis.maxIdle}")
    private int maxIdle;

    @Value("${redis.maxWaitMillis}")
    private int maxWaitMillis;

    @Value("${redis.blockWhenExhausted}")
    private Boolean blockWhenExhausted;

    @Value("${redis.JmxEnabled}")
    private Boolean JmxEnabled;

    @Bean
    public JedisPool jedisPoolFactory() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        jedisPoolConfig.setBlockWhenExhausted(blockWhenExhausted);
        jedisPoolConfig.setJmxEnabled(JmxEnabled);
        return new JedisPool(jedisPoolConfig, host, port,timeout,pwd);
    }
}