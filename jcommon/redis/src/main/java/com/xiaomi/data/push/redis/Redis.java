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

import com.xiaomi.youpin.cat.CatPlugin;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.*;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangzhiyong on 30/05/2018.
 * redis 操作类
 * 支持pool模式和集群模式
 */
@Slf4j
@Component
public class Redis {

    private static final String ACTION_GET = "get";
    private static final String ACTION_SET = "set";
    private static final String ACTION_ZREM = "zrem";

    private static final Logger logger = LoggerFactory.getLogger(Redis.class);

    private JedisPool pool;

    private JedisCluster cluster;

    @Value("${spring.redis}")
    private String redisHosts;

    @Value("${server.type}")
    private String serverType;


    @Value("${redis.cluster:true}")
    private boolean redisCluster = true;

    @Setter
    @Value("${redis.cluster.pwd:}")
    private String redisPwd = null;


    @Value("${spring.redis.cat.enabled}")
    private boolean catEnabled;


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

    @PostConstruct
    public void init() {
        CatPlugin cat = new CatPlugin("init", catEnabled);
        boolean success = true;
        cat.before(null);
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
                cluster = new JedisCluster(nodes, timeout, 2000, 5, redisPwd, config);
            }
        } catch (Exception e) {
            success = false;
            log.info("Redis.init error", e);
            throw e;
        } finally {
            cat.after(success);
        }
    }

    /**
     * 关闭redis
     *
     * @return
     */
    public boolean close() {
        CatPlugin cat = new CatPlugin("close", catEnabled);
        boolean success = true;
        cat.before(null);
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
            cat.after(success);
        }
    }


    public String get(String key) {
        CatPlugin cat = new CatPlugin(ACTION_GET, catEnabled);
        boolean success = true;
        cat.before(key);
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
        } finally {
            cat.after(success);
        }
    }

    public byte[] getBytes(String key) {
        CatPlugin cat = new CatPlugin("getBytes", catEnabled);
        boolean success = true;
        cat.before(key);
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
        } finally {
            cat.after(success);
        }
    }


    public boolean exists(String key) {
        CatPlugin cat = new CatPlugin("exists", catEnabled);
        boolean success = true;
        cat.before(key);
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
        } finally {
            cat.after(success);
        }
    }

    public void set(String key, String value) {
        CatPlugin cat = new CatPlugin(ACTION_SET, catEnabled);
        boolean success = true;
        cat.before(key);
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
        } finally {
            cat.after(success);
        }
    }

    public Long setNx(String key, String value) {
        CatPlugin cat = new CatPlugin("setNx", catEnabled);
        boolean success = true;
        cat.before(key);
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
        } finally {
            cat.after(success);
        }
    }

    public void del(String key) {
        CatPlugin cat = new CatPlugin("del", catEnabled);
        boolean success = true;
        cat.before(key);
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
        } finally {
            cat.after(success);
        }
    }

    public String setXx(String key, String value) {
        CatPlugin cat = new CatPlugin("setXx", catEnabled);
        boolean success = true;
        cat.before(key);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.set(key, value, "XX");
                }
            } else {
                return cluster.set(key, value, "XX");
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            cat.after(success);
        }
    }

    public long incr(String key) {
        CatPlugin cat = new CatPlugin("incr", catEnabled);
        boolean success = true;
        cat.before(key);
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
        } finally {
            cat.after(success);
        }
    }


    public void expire(String key, int time) {
        CatPlugin cat = new CatPlugin("expire", catEnabled);
        boolean success = true;
        cat.before(key);
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
        } finally {
            cat.after(success);
        }
    }

    public List<String> lrange(String key) {
        CatPlugin cat = new CatPlugin("lrange", catEnabled);
        boolean success = true;
        cat.before(key);
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
        } finally {
            cat.after(success);
        }
    }


    public Long lpush(String key, String value) {
        CatPlugin cat = new CatPlugin("lpush", catEnabled);
        boolean success = true;
        cat.before(key);
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
        } finally {
            cat.after(success);
        }
    }

    public Long zadd(String key, double score, String member) {
        CatPlugin cat = new CatPlugin("zadd", catEnabled);
        boolean success = true;
        cat.before(key);
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
        } finally {
            cat.after(success);
        }
    }

    public Long zadd(String key, Map<String, Double> scoreMembers) {
        CatPlugin cat = new CatPlugin("zadd", catEnabled);
        boolean success = true;
        cat.before(key);
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
        } finally {
            cat.after(success);
        }
    }

    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
        CatPlugin cat = new CatPlugin("zrevrangeByScoreWithScores", catEnabled);
        boolean success = true;
        cat.before(key);
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
        } finally {
            cat.after(success);
        }
    }

    public Long zrem(String key, String... members) {
        CatPlugin cat = new CatPlugin(ACTION_ZREM, catEnabled);
        boolean success = true;
        cat.before(key);
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
        } finally {
            cat.after(success);
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
        CatPlugin cat = new CatPlugin(ACTION_SET, catEnabled);
        boolean success = true;
        cat.before(key);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.set(key, value, "NX", "PX", (long) time);
                }
            } else {
                return cluster.set(key, value, "NX", "PX", (long) time);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            cat.after(success);
        }
    }


    public String set(String key, String value, String NXXX, int time) {
        CatPlugin cat = new CatPlugin(ACTION_SET, catEnabled);
        boolean success = true;
        cat.before(key);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.set(key, value, NXXX, "PX", (long) time);
                }
            } else {
                return cluster.set(key, value, NXXX, "PX", (long) time);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            cat.after(success);
        }
    }

    public String set(String key, byte[] value, int time) {
        CatPlugin cat = new CatPlugin(ACTION_SET, catEnabled);
        boolean success = true;
        cat.before(key);
        try {
            if (serverType.equals("dev")) {
                try (Jedis jedis = pool.getResource()) {
                    return jedis.set(key.getBytes(), value, "NX".getBytes(), "PX".getBytes(), (long) time);
                }
            } else {
                return cluster.set(key.getBytes(), value, "NX".getBytes(), "PX".getBytes(), (long) time);
            }
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            cat.after(success);
        }
    }

    public String set(String key, byte[] value) {
        CatPlugin cat = new CatPlugin(ACTION_SET, catEnabled);
        boolean success = true;
        cat.before(key);
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
        } finally {
            cat.after(success);
        }
    }


    public Set<String> zrange(String key, long start, long end) {
        CatPlugin cat = new CatPlugin("zrange", catEnabled);
        boolean success = true;
        cat.before(key);
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
        } finally {
            cat.after(success);
        }
    }

    public Set<String> zrevrange(String key, long start, long end) {
        CatPlugin cat = new CatPlugin("zrevrange", catEnabled);
        boolean success = true;
        cat.before(key);
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
        } finally {
            cat.after(success);
        }
    }

    public Long zrank(String key, String member) {
        CatPlugin cat = new CatPlugin("zrank", catEnabled);
        boolean success = true;
        cat.before(key);
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
        } finally {
            cat.after(success);
        }
    }

    public Double zincrby(String key, double score, String member) {
        CatPlugin cat = new CatPlugin("zincrby", catEnabled);
        boolean success = true;
        cat.before(key);
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
        } finally {
            cat.after(success);
        }
    }

    public Long sadd(String key, String... members) {
        CatPlugin cat = new CatPlugin("sadd", catEnabled);
        boolean success = true;
        cat.before(key);
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
        } finally {
            cat.after(success);
        }
    }

    public Long srem(String key, String... members) {
        CatPlugin cat = new CatPlugin("srem", catEnabled);
        boolean success = true;
        cat.before(key);
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
        } finally {
            cat.after(success);
        }
    }

    public Set<String> smembers(String key) {
        CatPlugin cat = new CatPlugin("smembers", catEnabled);
        boolean success = true;
        cat.before(key);
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
        } finally {
            cat.after(success);
        }
    }

    public Set<String> spop(String key, long count) {
        CatPlugin cat = new CatPlugin("spop", catEnabled);
        boolean success = true;
        cat.before(key);
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
        } finally {
            cat.after(success);
        }
    }

    public String spop(String key) {
        CatPlugin cat = new CatPlugin("spop", catEnabled);
        boolean success = true;
        cat.before(key);
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
        } finally {
            cat.after(success);
        }
    }


    public Long scard(String key) {
        CatPlugin cat = new CatPlugin("scard", catEnabled);
        boolean success = true;
        cat.before(key);
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
        } finally {
            cat.after(success);
        }
    }

    public Boolean sismember(String key, String member) {
        CatPlugin cat = new CatPlugin("sismember", catEnabled);
        boolean success = true;
        cat.before(key);
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
        } finally {
            cat.after(success);
        }
    }


}
