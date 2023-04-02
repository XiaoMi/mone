package com.xiaomi.mone.hera.demo.client.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
public class RedisConfig {

    @Value("${spring.redis.cluster.nodes}")
    private String clusterNodes;
    @Value("${spring.redis.timeout.connection}")
    private int timeout;
    @Value("${spring.redis.jedis.pool.max-active}")
    private int maxActive;
    @Value("${spring.redis.pool.max-idle}")
    private int maxIdle;
    @Value("${spring.redis.password}")
    private String pwd;


    @Bean
    public JedisPool getJedisPooled() {
        String[] hp = clusterNodes.split(":");
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxTotal(maxActive);
        genericObjectPoolConfig.setMaxIdle(maxIdle);
        genericObjectPoolConfig.setTestWhileIdle(true);
        if(StringUtils.isEmpty(pwd)) {
            return new JedisPool(genericObjectPoolConfig, hp[0].trim(), Integer.valueOf(hp[1]), timeout);
        }else {
            return new JedisPool(genericObjectPoolConfig, hp[0].trim(), Integer.valueOf(hp[1]), timeout, pwd);
        }
    }

}