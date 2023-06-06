package com.xiaomi.mone.app.redis;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.stereotype.Component;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.bloom.BFInsertParams;
import redis.clients.jedis.bloom.BFReserveParams;
import redis.clients.jedis.commands.JedisCommands;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.params.SetParams;
import redis.clients.jedis.resps.ScanResult;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class RedisService {

    @NacosValue("${spring.redis.cluster}")
    private String cluster;
    @NacosValue("${spring.redis.cluster.nodes}")
    private String clusterNodes;
    @NacosValue("${spring.redis.timeout.connection}")
    private int timeout;
    @NacosValue("${spring.redis.jedis.pool.max-active}")
    private int maxActive;
    @NacosValue("${spring.redis.pool.max-idle}")
    private int maxIdle;
    @NacosValue("${spring.redis.pool.max-wait}")
    private long maxWaitMillis;
    @NacosValue("${spring.redis.password}")
    private String pwd;
    @NacosValue("${spring.redis.max-attempts}")
    private int maxAttempts;

    private BFInsertParams param = new BFInsertParams();
    private long ttlSeconds = 5;
    private long waitTimeOut = 30000;
    private static final int SCAN_PARAM_BATCH = 100;
    private static final ScanParams SCAN_PARAMS = new ScanParams().count(SCAN_PARAM_BATCH);
    private SetParams disLockParam = SetParams.setParams().ex(ttlSeconds).nx();

    private JedisCommands jedis;
    private JedisPooled jedisPooled;
    private JedisCluster jedisCluster;

    private static final BFReserveParams NON_SCALING = BFReserveParams.reserveParams().nonScaling();

    @PostConstruct
    public void init() {
        // 如果redis bloomfilter不存在插入的key，则不会自动创建
        param.noCreate();
        // 线上使用Redis集群，st使用单机版
        if ("true".equals(cluster)) {
            String[] serverArray = clusterNodes.split(",");
            Set<HostAndPort> nodes = new HashSet<>();
            for (String ipPort : serverArray) {
                String[] ipPortPair = ipPort.split(":");
                nodes.add(new HostAndPort(ipPortPair[0].trim(), Integer.valueOf(ipPortPair[1].trim())));
            }
            if (StringUtils.isEmpty(pwd)) {
                jedisCluster = new JedisCluster(nodes, timeout, timeout, maxAttempts, getGenericObjectPoolConfig());
            } else {
                jedisCluster = new JedisCluster(nodes, timeout, timeout, maxAttempts, pwd, getGenericObjectPoolConfig());
            }
            jedis = jedisCluster;
        } else {
            String[] hp = clusterNodes.split(":");
            if (StringUtils.isEmpty(pwd)) {
                jedisPooled = new JedisPooled(getGenericObjectPoolConfig(), hp[0].trim(), Integer.valueOf(hp[1]), timeout);
            } else {
                jedisPooled = new JedisPooled(getGenericObjectPoolConfig(), hp[0].trim(), Integer.valueOf(hp[1]), timeout, pwd);
            }
            jedis = jedisPooled;
        }
    }

    private GenericObjectPoolConfig getGenericObjectPoolConfig() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxTotal(maxActive);
        genericObjectPoolConfig.setMaxIdle(maxIdle);
        genericObjectPoolConfig.setMaxWaitMillis(maxWaitMillis);
        genericObjectPoolConfig.setTimeBetweenEvictionRunsMillis(5000L);
        genericObjectPoolConfig.setMinEvictableIdleTimeMillis(15L);
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

    public boolean getDisLock(String key) {
        long startTime = System.currentTimeMillis();
        try {
            while (true) {
                if ("OK".equals(jedis.set(key, "1", disLockParam))) {
                    return true;
                } else {
                    Thread.sleep(10);
                }
                if (System.currentTimeMillis() - startTime > waitTimeOut) {
                    log.warn("等待分布式锁超过30秒...");
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("redis getDisLock error key:" + key, e);
        }
        return false;
    }

    public Boolean setbit(String key, long offset, boolean value) {
        try {
            return jedis.setbit(key, offset, value);

        } catch (Exception e) {
            log.error("redis setbit error key:" + key, e);
        }
        return null;
    }

    public Boolean getbit(String key, long offset) {
        try {
            return jedis.getbit(key, offset);
        } catch (Exception e) {
            log.error("redis getbit error key:" + key, e);
        }
        return null;
    }

    public Boolean exists(String key) {
        try {
            return jedis.exists(key);
        } catch (Exception e) {
            log.error("redis exists error key:" + key, e);
        }
        return null;
    }

    public long expire(String key, int seconds) {
        try {
            return jedis.expire(key, seconds);
        } catch (Exception e) {
            log.error("redis expire error key:" + key, e);
        }
        return 0L;
    }

    public long ttl(String key) {
        try {
            return jedis.ttl(key);
        } catch (Exception e) {
            log.error("redis expire error key:" + key, e);
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

    public String hget(String key, String field) {
        try {
            return jedis.hget(key, field);
        } catch (Exception e) {
            log.error("redis hget error key: " + key + " field: " + field, e);
        }
        return null;
    }

    public Long hset(String key, String field, String value) {
        try {
            return jedis.hset(key, field, value);
        } catch (Exception e) {
            log.error("redis hset error key: " + key + " field: " + field + " value: " + value, e);
        }
        return null;
    }

    public Map<String,String> hgetAll(String key){
        try {
            Map<String,String> result = new HashMap<>();
            String cursor = "0";
            // 使用hscan迭代哈希表
            ScanResult<Map.Entry<String, String>> scanResult;
            do {
                scanResult = jedis.hscan(key, cursor, SCAN_PARAMS);
                List<Map.Entry<String, String>> entries = scanResult.getResult();
                for (Map.Entry<String, String> entry : entries) {
                    result.put(entry.getKey(), entry.getValue());
                }
                cursor = scanResult.getCursor();
            } while (!"0".equals(cursor));
            return result;
        } catch (Exception e) {
            log.error("redis hgetAll error key: " + key, e);
            return null;
        }
    }

    public List<Boolean> bfMAdd(String key, String... items) {
        try {
            if ("true".equals(cluster)) {
                return jedisCluster.bfMAdd(key, items);
            } else {
                return jedisPooled.bfMAdd(key, items);
            }
        } catch (Exception e) {
            log.error("redis bfMAdd error key:" + key, e);
        }
        return null;
    }

    public Boolean bfExist(String key, String item) {
        try {
            if ("true".equals(cluster)) {
                return jedisCluster.bfExists(key, item);
            } else {
                return jedisPooled.bfExists(key, item);
            }
        } catch (Exception e) {
            log.error("redis bfExists error key:" + key, e);
        }
        return null;
    }

    public List<Boolean> bfMExist(String key, String... item) {
        try {
            if ("true".equals(cluster)) {
                return jedisCluster.bfMExists(key, item);
            } else {
                return jedisPooled.bfMExists(key, item);
            }
        } catch (Exception e) {
            log.error("redis bfExists error key:" + key, e);
        }
        return null;
    }

    public String bfReserve(String key, double errorRate, long capacity, boolean scaling) {
        try {
            if ("true".equals(cluster)) {
                if (scaling) {
                    return jedisCluster.bfReserve(key, errorRate, capacity);
                } else {
                    return jedisCluster.bfReserve(key, errorRate, capacity, NON_SCALING);
                }
            } else {
                if (scaling) {
                    return jedisPooled.bfReserve(key, errorRate, capacity);
                } else {
                    return jedisPooled.bfReserve(key, errorRate, capacity, NON_SCALING);
                }
            }
        } catch (Exception e) {
            log.error("redis bfReserve error key:" + key, e);
        }
        return null;
    }

    public List<Boolean> bfInsert(String key, String... items) {
        try {
            if ("true".equals(cluster)) {
                return jedisCluster.bfInsert(key, param, items);
            } else {
                return jedisPooled.bfInsert(key, param, items);
            }
        } catch (Exception e) {
            log.error("redis bfReserve error key:" + key, e);
        }
        return null;
    }

    public Map<String, Object> bfInfo(String key) {
        try {
            if ("true".equals(cluster)) {
                return jedisCluster.bfInfo(key);
            } else {
                return jedisPooled.bfInfo(key);
            }
        } catch (Exception e) {
            log.error("redis bfReserve error key:" + key, e);
        }
        return null;
    }

}
