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

package com.xiaomi.mione.prometheus.redis;


import com.xiaomi.mione.prometheus.redis.monitor.AttachInfo;
import com.xiaomi.mione.prometheus.redis.monitor.MetricTypes;
import com.xiaomi.mione.prometheus.redis.monitor.RedisMonitor;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by gaoxihui on 2021-06-21
 * redis 操作类
 * 支持pool模式和集群模式
 */
@Slf4j
@Data
public class Redis {

    private static final String ACTION_GET = "get";
    private static final String ACTION_MGET = "mget";
    private static final String ACTION_SET = "set";
    private static final String ACTION_ZREM = "zrem";

    private static final Logger logger = LoggerFactory.getLogger(Redis.class);

    private JedisPool pool;

    private PipelineCluster cluster;

    private String redisHosts;

    private String serverType;

    private boolean redisCluster = true;

    private String redisPwd = null;

    private boolean catEnabled;

    private boolean prometheusEnabled;

    private RedisMonitor redisMonitor;

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

    private AtomicBoolean init = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        if (!init.compareAndSet(false, true)) {
            return;
        }

        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_INIT", MetricTypes.Gauge, "init", null, startTime, success,attachInfo);
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
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_INIT", MetricTypes.Gauge, "init", null, startTime, success,attachInfo);
        }
    }

    /**
     * 关闭redis
     *
     * @return
     */
    public boolean close() {

        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_CLOSE", MetricTypes.Gauge, "close", null, startTime, success,attachInfo);
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
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_CLOSE", MetricTypes.Gauge, "close", null, startTime, success,attachInfo);
        }
    }

    public String get(String key) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;

        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, "get", key, startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    return jedis.get(key);
                }
            } else {
                try {
                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }

                return cluster.get(key);

            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, "get", key, startTime, success,attachInfo);
        }
    }

    public Map<String, String> mget(final List<String> keys) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, ACTION_MGET, keys.toString(), startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                Map<String, String> result = new HashMap<>();
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    String[] keyArr = keys.toArray(new String[keys.size()]);
                    List<String> mgetList = jedis.mget(keyArr);
                    for (int i = 0; i < keys.size(); i++) {
                        result.put(keys.get(i), i <= mgetList.size() ? mgetList.get(i) : null);
                    }
                    return result;
                }
            } else {
//                try {
//                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
//                } catch (Exception e) {
//                    log.error(e.getMessage(),e);
//                }
                return cluster.mget(keys);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, ACTION_MGET, keys.toString(), startTime, success,attachInfo);
        }
    }


    public byte[] getBytes(String key) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, "getBytes", key, startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    return jedis.get(key.getBytes());
                }
            } else {
                try {
                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
                return cluster.get(key.getBytes());
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, "getBytes", key, startTime, success,attachInfo);
        }
    }


    public boolean exists(String key) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, "exists", key, startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    return jedis.exists(key);
                }
            } else {
                try {
                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
                return cluster.exists(key);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
//            cat.after(success,attachInfo);
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, "exists", key, startTime, success,attachInfo);
        }
    }


    public void set(String key, String value) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, ACTION_SET, key, startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    jedis.set(key, value);
                }
            } else {
                try {
                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
                cluster.set(key, value);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, ACTION_SET, key, startTime, success,attachInfo);
        }
    }


    public Long setNx(String key, String value) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, "setNx", key, startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    return jedis.setnx(key, value);
                }
            } else {
                try {
                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
                return cluster.setnx(key, value);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, "setNx", key, startTime, success,attachInfo);
        }
    }


    public void del(String key) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, "del", key, startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    jedis.del(key);
                }
            } else {
                try {
                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
                cluster.del(key);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, "del", key, startTime, success,attachInfo);
        }
    }


    public String setXx(String key, String value) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, "setXx", key, startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    return jedis.set(key, value, "XX");
                }
            } else {
                try {
                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
                return cluster.set(key, value, "XX");
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, "setXx", key, startTime, success,attachInfo);
        }
    }


    public long incr(String key) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, "incr", key, startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    return jedis.incr(key);
                }
            } else {
                try {
                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
                return cluster.incr(key);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, "incr", key, startTime, success,attachInfo);
        }
    }


    public void expire(String key, int time) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, "expire", key, startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    jedis.expire(key, time);
                }
            } else {
                try {
                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
                cluster.expire(key, time);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, "expire", key, startTime, success,attachInfo);
        }
    }


    public List<String> lrange(String key) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, "lrange", key, startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    return jedis.lrange(key, 0, -1);
                }
            } else {
                try {
                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
                return cluster.lrange(key, 0, -1);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, "lrange", key, startTime, success,attachInfo);
        }
    }


    public Long lpush(String key, String value) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, "lpush", key, startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    return jedis.lpush(key, value);
                }
            } else {
                try {
                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
                return cluster.lpush(key, value);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, "lpush", key, startTime, success,attachInfo);
        }
    }


    public Long zadd(String key, double score, String member) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, "zadd", key, startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    return jedis.zadd(key, score, member);
                }
            } else {
                try {
                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
                return cluster.zadd(key, score, member);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, "zadd", key, startTime, success,attachInfo);
        }
    }


    public Long zadd(String key, Map<String, Double> scoreMembers) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, "zadd", key, startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    return jedis.zadd(key, scoreMembers);
                }
            } else {
                try {
                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
                return cluster.zadd(key, scoreMembers);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, "zadd", key, startTime, success,attachInfo);
        }
    }


    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, "zrevrangeByScoreWithScores", key, startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    return jedis.zrevrangeByScoreWithScores(key, max, min);
                }
            } else {
                try {
                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
                return cluster.zrevrangeByScoreWithScores(key, max, min);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, "zrevrangeByScoreWithScores", key, startTime, success,attachInfo);
        }
    }


    public Long zrem(String key, String... members) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, ACTION_ZREM, key, startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    return jedis.zrem(key, members);
                }
            } else {
                try {
                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
                return cluster.zrem(key, members);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, ACTION_ZREM, key, startTime, success,attachInfo);
        }
    }

    /**
     * NX是不存在时才set， XX是存在时才set， EX是秒，PX是毫秒
     *
     * @param key
     * @param value
     * @param time
     * @return
     */

    public String set(String key, String value, int time) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, ACTION_SET, key, startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    return jedis.set(key, value, "NX", "PX", (long) time);
                }
            } else {
                try {
                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
                return cluster.set(key, value, "NX", "PX", (long) time);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, ACTION_SET, key, startTime, success,attachInfo);
        }
    }


    public String set(String key, String value, String NXXX, int time) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, ACTION_SET, key, startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    return jedis.set(key, value, NXXX, "PX", (long) time);
                }
            } else {
                try {
                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
                return cluster.set(key, value, NXXX, "PX", (long) time);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, ACTION_SET, key, startTime, success,attachInfo);
        }
    }


    public String set(String key, byte[] value, int time) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, ACTION_SET, key, startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    return jedis.set(key.getBytes(), value, "NX".getBytes(), "PX".getBytes(), (long) time);
                }
            } else {
                try {
                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
                return cluster.set(key.getBytes(), value, "NX".getBytes(), "PX".getBytes(), (long) time);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, ACTION_SET, key, startTime, success,attachInfo);
        }
    }


    public String set(String key, byte[] value) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, ACTION_SET, key, startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    return jedis.set(key.getBytes(), value);
                }
            } else {
                try {
                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
                return cluster.set(key.getBytes(), value);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, ACTION_SET, key, startTime, success,attachInfo);
        }
    }


    public Set<String> zrange(String key, long start, long end) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, "zrange", key, startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    return jedis.zrange(key, start, end);
                }
            } else {
                try {
                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
                return cluster.zrange(key, start, end);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, "zrange", key, startTime, success,attachInfo);
        }
    }


    public Set<String> zrevrange(String key, long start, long end) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, "zrevrange", key, startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    return jedis.zrevrange(key, start, end);
                }
            } else {
                try {
                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
                return cluster.zrevrange(key, start, end);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, "zrevrange", key, startTime, success,attachInfo);
        }
    }


    public Long zrank(String key, String member) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, "zrank", key, startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    return jedis.zrank(key, member);
                }
            } else {
                try {
                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
                return cluster.zrank(key, member);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, "zrank", key, startTime, success,attachInfo);
        }
    }


    public Double zincrby(String key, double score, String member) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, "zincrby", key, startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    return jedis.zincrby(key, score, member);
                }
            } else {
                try {
                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
                return cluster.zincrby(key, score, member);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, "zincrby", key, startTime, success,attachInfo);
        }
    }


    public Long sadd(String key, String... members) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, "sadd", key, startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    return jedis.sadd(key, members);
                }
            } else {
                try {
                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
                return cluster.sadd(key, members);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, "sadd", key, startTime, success,attachInfo);
        }
    }


    public Long srem(String key, String... members) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, "srem", key, startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    return jedis.srem(key, members);
                }
            } else {
                try {
                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
                return cluster.srem(key, members);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, "srem", key, startTime, success,attachInfo);
        }
    }


    public Set<String> smembers(String key) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, "smembers", key, startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    return jedis.smembers(key);
                }
            } else {
                try {
                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
                return cluster.smembers(key);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, "smembers", key, startTime, success,attachInfo);
        }
    }


    public Set<String> spop(String key, long count) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, "spop", key, startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    return jedis.spop(key, count);
                }
            } else {
                try {
                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
                return cluster.spop(key, count);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, "spop", key, startTime, success,attachInfo);
        }
    }


    public String spop(String key) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, "spop", key, startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    return jedis.spop(key);
                }
            } else {
                try {
                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
                return cluster.spop(key);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, "spop", key, startTime, success,attachInfo);
        }
    }


    public Long scard(String key) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, "scard", key, startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    return jedis.scard(key);
                }
            } else {
                try {
                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
                return cluster.scard(key);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, "scard", key, startTime, success,attachInfo);
        }
    }


    public Boolean sismember(String key, String member) {
        boolean success = true;
        Long startTime = System.currentTimeMillis();
        AttachInfo attachInfo = null;
        redisMonitor.recordMonitorInfo(catEnabled, false, true, "REDIS_DATA_ACCESS", MetricTypes.Counter, "sismember", key, startTime, success,attachInfo);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    attachInfo = new AttachInfo(jedis);
                    return jedis.sismember(key, member);
                }
            } else {
                try {
                    attachInfo = new AttachInfo(cluster.getClusterNodes().get(key).getResource());
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
                return cluster.sismember(key, member);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "REDIS_DATA_ACCESS", MetricTypes.Counter, "sismember", key, startTime, success,attachInfo);
        }
    }


}
