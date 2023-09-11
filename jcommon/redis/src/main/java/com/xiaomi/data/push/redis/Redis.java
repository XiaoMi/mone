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

package com.xiaomi.data.push.redis;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.data.push.redis.ext.PipelineCluster;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.*;
import redis.clients.jedis.params.SetParams;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zhangzhiyong on 30/05/2018.
 * redis 操作类
 * 支持pool模式和集群模式
 */
@Slf4j
@Component
public class Redis {

    private static final String ACTION_GET = "get";
    private static final String ACTION_MGET = "mget";
    private static final String ACTION_SET = "set";
    private static final String ACTION_ZREM = "zrem";

    private static final Logger logger = LoggerFactory.getLogger(Redis.class);

    private static Gson gson = new Gson();

    private JedisPool pool;

    private PipelineCluster cluster;

    @Value("${spring.redis}")
    private String redisHosts;

    @Value("${server.type}")
    private String serverType;


    @Value("${redis.cluster:true}")
    private boolean redisCluster = true;

    /**
     * 是否开启
     */
    @Value("${redis.enable:true}")
    private boolean enable;

    @Setter
    @Value("${redis.cluster.pwd:}")
    private String redisPwd = null;


    @Value("${spring.redis.cat.enabled:false}")
    private boolean catEnabled;

    @Value("${spring.redis.prometheus.enabled:false}")
    private boolean prometheusEnabled;

    public String getRedisHosts() {
        return redisHosts;
    }

    public void setRedisHosts(String redisHosts) {
        this.redisHosts = redisHosts;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public boolean isCatEnabled() {
        return catEnabled;
    }

    public void setCatEnabled(boolean catEnabled) {
        this.catEnabled = catEnabled;
    }

    private AtomicBoolean init = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        if (!enable) {
            log.info("redis is not enable");
            return;
        }

        if (!init.compareAndSet(false, true)) {
            return;
        }

        boolean success = true;
        try {
            final GenericObjectPoolConfig config = new GenericObjectPoolConfig();
            int timeout = 2000;
            if (null == redisHosts || redisHosts.equals("")) {
                logger.error("[Redis.init()] invalid redisHosts info: {}", redisHosts);
                return;
            }
            String[] serverArray = redisHosts.split(",");

            config.setTestOnBorrow(true);
            config.setTimeBetweenEvictionRunsMillis(5000);
            config.setMinEvictableIdleTimeMillis(TimeUnit.MINUTES.toMillis(15));
            config.setTestWhileIdle(true);

            //下边的判断是按照空来判断的
            if (null == redisPwd || redisPwd.equals("")) {
                redisPwd = null;
            }

            //开发环境
            if (serverType.equals("dev") || !redisCluster) {
                this.serverType = "dev";
                String[] hostPort = serverArray[0].split(":");
                if (hostPort.length <= 1) {
                    logger.error("[Redis.init()] invalid hostPort info: {}", hostPort);
                    return;
                }
                log.info("dev:{} {}", hostPort[0], hostPort[1]);
                pool = new JedisPool(config, hostPort[0].trim(), Integer.valueOf(hostPort[1]), timeout, redisPwd);
            } else {
                Set<HostAndPort> nodes = new HashSet<>();
                for (String ipPort : serverArray) {
                    String[] ipPortPair = ipPort.split(":");
                    nodes.add(new HostAndPort(ipPortPair[0].trim(), Integer.valueOf(ipPortPair[1].trim())));
                }
                cluster = new PipelineCluster(nodes, timeout, 2000, 5, redisPwd, config);
            }
        } catch (Exception e) {
            success = false;
            log.info("Redis.init error", e);
            throw e;
        }
    }

    /**
     * 关闭redis
     *
     * @return
     */
    public boolean close() {

        boolean success = true;
        try {
            logger.info("redis client close");
            if (serverType.equals("dev")) {
                pool.close();
            } else {
                cluster.close();
            }
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            success = false;
            return false;
        }
    }


    public String get(String key) {
        boolean success = true;
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.get(key);
                }
            } else {
                return cluster.get(key);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }

    public Map<String, String> mget(final List<String> keys) {
        boolean success = true;
        try {
            if (serverType.equals("dev")) {
                Map<String, String> result = new HashMap<>();
                try (Jedis jedis = pool.getResource()) {
                    String[] keyArr = keys.toArray(new String[keys.size()]);
                    List<String> mgetList = jedis.mget(keyArr);
                    for (int i = 0; i < keys.size(); i++) {
                        result.put(keys.get(i), i <= mgetList.size() ? mgetList.get(i) : null);
                    }
                    return result;
                }
            } else {
                return cluster.mget(keys);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }

    public byte[] getBytes(String key) {
        boolean success = true;
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.get(key.getBytes());
                }
            } else {
                return cluster.get(key.getBytes());
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }


    public boolean exists(String key) {
        boolean success = true;
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.exists(key);
                }
            } else {
                return cluster.exists(key);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }

    public void set(String key, String value) {
        boolean success = true;
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    jedis.set(key, value);
                }
            } else {
                cluster.set(key, value);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }

    public Long setNx(String key, String value) {
        boolean success = true;
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.setnx(key, value);
                }
            } else {
                return cluster.setnx(key, value);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }

    public String setNx(String key, String value, int time) {
        boolean success = true;
        try {
            SetParams setParams = new SetParams();
            setParams.nx().px(time);

            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.set(key, value, setParams);
                }
            } else {
                return cluster.set(key, value, setParams);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }

    public void del(String key) {
        boolean success = true;
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    jedis.del(key);
                }
            } else {
                cluster.del(key);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }

    public String setXx(String key, String value) {
        boolean success = true;
        try {
            SetParams setParams = new SetParams();
            setParams.xx();
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.set(key, value, setParams);
                }
            } else {
                return cluster.set(key, value, setParams);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }


    public long incr(String key) {
        boolean success = true;
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.incr(key);
                }
            } else {
                return cluster.incr(key);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }


    public void expire(String key, int time) {
        boolean success = true;
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    jedis.expire(key, time);
                }
            } else {
                cluster.expire(key, time);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }

    public List<String> lrange(String key) {
        boolean success = true;
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.lrange(key, 0, -1);
                }
            } else {
                return cluster.lrange(key, 0, -1);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }


    public Long lpush(String key, String value) {
        boolean success = true;
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.lpush(key, value);
                }
            } else {
                return cluster.lpush(key, value);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }

    public Long rpush(String key, String value) {
        boolean success = true;
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.rpush(key, value);
                }
            } else {
                return cluster.rpush(key, value);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }

    public String rpop(String key) {
        boolean success = true;
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.rpop(key);
                }
            } else {
                return cluster.rpop(key);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }

    public Long llen(String key) {
        boolean success = true;
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.llen(key);
                }
            } else {
                return cluster.llen(key);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }

    public Long zadd(String key, double score, String member) {
        boolean success = true;
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.zadd(key, score, member);
                }
            } else {
                return cluster.zadd(key, score, member);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }

    public Long zadd(String key, Map<String, Double> scoreMembers) {
        boolean success = true;
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.zadd(key, scoreMembers);
                }
            } else {
                return cluster.zadd(key, scoreMembers);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
        boolean success = true;
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.zrevrangeByScoreWithScores(key, max, min);
                }
            } else {
                return cluster.zrevrangeByScoreWithScores(key, max, min);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }

    public Long zrem(String key, String... members) {
        boolean success = true;
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.zrem(key, members);
                }
            } else {
                return cluster.zrem(key, members);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }

//    /**
//     *
//     * 使用setNx
//     * @see Redis#setNx(String, String, int)
//     *
//     * 使用key的expire的原子操作
//     * @see Redis#setV2(String, String, int)
//     *
//     * NX是不存在时才set， XX是存在时才set， EX是秒，PX是毫秒
//     *
//     * @param key
//     * @param value
//     * @param time
//     * @return
//     */

//    @Deprecated
//    public String set(String key, String value, int time) {
//        boolean success = true;
//        try {
//            SetParams setParams = new SetParams();
//            setParams.nx().px(time);
//
//            if (serverType.equals("dev")) {
//                try (Jedis jedis = pool.getResource()) {
//                    return jedis.set(key, value, setParams);
//                }
//            } else {
//                return cluster.set(key, value, setParams);
//            }
//        } catch (Exception e) {
//            success = false;
//            throw e;
//        }
//    }

    public String setV2(String key, String value, int time) {
        boolean success = true;
        try {
            String script = "local result = redis.call('set',KEYS[1],ARGV[1]);redis.call('pexpire',KEYS[1],ARGV[2]);return result";
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return (String) jedis.eval(script, Lists.newArrayList(key),Lists.newArrayList(value,String.valueOf(time)));
                }
            } else {
                return (String) cluster.eval(script,  Lists.newArrayList(key),Lists.newArrayList(value,String.valueOf(time)));
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }


    public String set(String key, String value, String NXXX, int time) {
        boolean success = true;
        try {
            SetParams setParams = new SetParams();
            if ("NX".equals(NXXX)) {
                setParams.nx();
            } else if ("XX".equals(NXXX)) {
                setParams.xx();
            }
            setParams.px(time);
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.set(key, value, setParams);
                }
            } else {
                return cluster.set(key, value, setParams);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }

    public String set(String key, byte[] value, int time) {
        boolean success = true;
        try {
            SetParams setParams = new SetParams();
            setParams.nx().px(time);
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.set(key.getBytes(), value, setParams);
                }
            } else {
                return cluster.set(key.getBytes(), value, setParams);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }


    public String set(String key, byte[] value) {
        boolean success = true;
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.set(key.getBytes(), value);
                }
            } else {
                return cluster.set(key.getBytes(), value);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }

    public <T> void set(String key, T value) {
        set(key, JSON.toJSONString(value));
    }

    public <T> void set(String key, T value, int time) {
        setV2(key, gson.toJson(value), time);
    }

    public <T> T get(String key, Class<T> clazz) {
        String value = get(key);
        return gson.fromJson(value, clazz);
    }


    public Set<String> zrange(String key, long start, long end) {
        boolean success = true;
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.zrange(key, start, end);
                }
            } else {
                return cluster.zrange(key, start, end);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }

    public Set<String> zrevrange(String key, long start, long end) {
        boolean success = true;
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.zrevrange(key, start, end);
                }
            } else {
                return cluster.zrevrange(key, start, end);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }

    public Long zrank(String key, String member) {
        boolean success = true;
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.zrank(key, member);
                }
            } else {
                return cluster.zrank(key, member);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }

    public Double zincrby(String key, double score, String member) {
        boolean success = true;
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.zincrby(key, score, member);
                }
            } else {
                return cluster.zincrby(key, score, member);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }

    public Long sadd(String key, String... members) {
        boolean success = true;
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.sadd(key, members);
                }
            } else {
                return cluster.sadd(key, members);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }

    public Long srem(String key, String... members) {
        boolean success = true;
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.srem(key, members);
                }
            } else {
                return cluster.srem(key, members);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }

    public Set<String> smembers(String key) {
        boolean success = true;
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.smembers(key);
                }
            } else {
                return cluster.smembers(key);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }

    public Set<String> spop(String key, long count) {
        boolean success = true;
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.spop(key, count);
                }
            } else {
                return cluster.spop(key, count);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }

    public String spop(String key) {
        boolean success = true;
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.spop(key);
                }
            } else {
                return cluster.spop(key);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }


    public Long scard(String key) {
        boolean success = true;
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.scard(key);
                }
            } else {
                return cluster.scard(key);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }

    public Boolean sismember(String key, String member) {
        boolean success = true;
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.sismember(key, member);
                }
            } else {
                return cluster.sismember(key, member);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }

    public Object eval(String script, List<String> keys, List<String> values) {
        boolean success = true;
        String joinKeys = StringUtils.join(keys);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.eval(script, keys, values);
                }
            } else {
                return cluster.eval(script, keys, values);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        }
    }

}
