package com.xiaomi.youpin.docean.plugin.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.*;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by goodjava@qq.com on 30/05/2018.
 * redis 操作类
 * 支持pool模式和集群模式
 */
@Component
public class Redis {

    private static final Logger logger = LoggerFactory.getLogger(Redis.class);

    private JedisPool pool;

    private JedisCluster cluster;

    private String redisHosts;

    private String serverType;


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

    @PostConstruct
    public void init() {
        boolean success = true;
        try {
            final GenericObjectPoolConfig config = new GenericObjectPoolConfig();
            int timeout = 2000;
            if (null == redisHosts || redisHosts.equals("")) {
                logger.info("[Redis not init because reidsHost is null]  hosts: {}", redisHosts);
                return;
            }
            String[] serverArray = redisHosts.split(",");

            config.setTestOnBorrow(true);
            config.setTimeBetweenEvictionRunsMillis(5000);
            config.setMinEvictableIdleTimeMillis(TimeUnit.MINUTES.toMillis(15));
            config.setTestWhileIdle(true);

            //开发环境
            if (serverType.equals("dev")) {
                String[] hostPort = serverArray[0].split(":");
                if (hostPort.length <= 1) {
                    logger.error("[Redis.init()] invalid hostPort info: {}", hostPort);
                    return;
                }
                pool = new JedisPool(config, hostPort[0].trim(), Integer.valueOf(hostPort[1]), timeout);
            } else {
                Set<HostAndPort> nodes = new HashSet<>();
                for (String ipPort : serverArray) {
                    String[] ipPortPair = ipPort.split(":");
                    nodes.add(new HostAndPort(ipPortPair[0].trim(), Integer.valueOf(ipPortPair[1].trim())));
                }
                cluster = new JedisCluster(nodes, timeout, config);
            }
        } catch (Exception e) {
            success = false;
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
        } finally {
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
        } finally {
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
        } finally {
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
        } finally {
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
        } finally {
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
        } finally {
        }
    }

    public String setXx(String key, String value) {
        boolean success = true;
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
        } finally {
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
        } finally {
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
        } finally {
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
        } finally {
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
        } finally {
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
        } finally {
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
        } finally {
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
        } finally {
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
        }
    }


    public String set(String key, String value, String NXXX, int time) {
        boolean success = true;
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
        }
    }

    public String set(String key, byte[] value, int time) {
        boolean success = true;
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
        } finally {
        }
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
        } finally {
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
        } finally {
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
        } finally {
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
        } finally {
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
        } finally {
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
        } finally {
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
        } finally {
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
        } finally {
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
        } finally {
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
        } finally {
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
        } finally {
        }
    }


}
