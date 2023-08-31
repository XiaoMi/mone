package com.xiaomi.mone.tpc.cache;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.mone.tpc.cache.enums.CacheTypeEnum;
import com.xiaomi.mone.tpc.cache.key.Key;
import com.xiaomi.mone.tpc.common.util.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.util.*;
import java.util.stream.Collectors;

/**
 * redis缓存实现
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:57
 */
@Slf4j
@Component
public class RedisCacheService extends CacheService {

    @NacosValue("${redis.cluster:no}")
    private String cluster;
    @NacosValue("${redis.address:null}")
    private String address;
    @NacosValue("${redis.pwd:null}")
    private String password;
    @NacosValue("${redis.max-active:24}")
    private int maxActive;
    @NacosValue("${redis.max-wait:500}")
    private int maxWait;
    @NacosValue("${redis.max-idle:8}")
    private int maxIdle;
    @NacosValue("${redis.min-idle:0}")
    private int minIdle;
    @NacosValue("${redis.timeout:1000}")
    private int timeout;

    private JedisPool pool = null;
    private JedisCluster jedisCluster;

    public RedisCacheService() {
        super(CacheTypeEnum.REDIS);
    }

    @Override
    public void realInit() {
        if (StringUtils.isEmpty(address)) {
            throw new IllegalArgumentException("redis address is null");
        }
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setMaxTotal(maxActive);
        config.setMaxWaitMillis(maxWait);
        String[] addrArr = address.split(",");
        if (!"yes".equalsIgnoreCase(cluster)) {
            if (addrArr.length != 1) {
                throw new IllegalArgumentException("redis non-cluster mode, address error");
            }
            String[] infos = addrArr[0].split(":");
            if (infos.length != 2) {
                throw new IllegalArgumentException("redis address is error");
            }
            if (StringUtils.isEmpty(password)) {
                pool = new JedisPool(config, infos[0], Integer.parseInt(infos[1]), timeout);
            } else {
                pool = new JedisPool(config, infos[0], Integer.parseInt(infos[1]), timeout, password);
            }
            return;
        }
        Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
        Arrays.stream(addrArr).forEach(addr -> {
            String[] infos = addr.split(":");
            if (infos.length != 2) {
                throw new IllegalArgumentException("redis address is error");
            }
            jedisClusterNodes.add(new HostAndPort(infos[0], Integer.parseInt(infos[1])));
        });
        if (StringUtils.isEmpty(password)) {
            jedisCluster = new JedisCluster(jedisClusterNodes,3000, timeout, 3, config);
        } else {
            jedisCluster = new JedisCluster(jedisClusterNodes,3000, timeout, 3, password, config);
        }
    }

    @Override
    public boolean set0(Key key, Object value) {
        if (pool != null) {
            Jedis jedis = pool.getResource();
            try {
                jedis.setex(key.toString(), (int)key.getUnit().toSeconds(key.getTime()), GsonUtil.gsonString(value));
            } finally {
                jedis.close();
            }
            return true;
        }
        if (jedisCluster != null) {
            jedisCluster.setex(key.toString(), (int)key.getUnit().toSeconds(key.getTime()), GsonUtil.gsonString(value));
            return true;
        }
        throw new RuntimeException("redis no oper");
    }

    @Override
    public boolean set0(Key bigKey, Key key, Object value) {
        if (pool != null) {
            Jedis jedis = pool.getResource();
            try {
                jedis.hset(bigKey.toString(), key.toString(), GsonUtil.gsonString(value));
                jedis.expire(bigKey.toString(), (int)bigKey.getUnit().toSeconds(bigKey.getTime()));
            } finally {
                jedis.close();
            }
            return true;
        }
        if (jedisCluster != null) {
            jedisCluster.hset(bigKey.toString(), key.toString(), GsonUtil.gsonString(value));
            jedisCluster.expire(bigKey.toString(), (int)bigKey.getUnit().toSeconds(bigKey.getTime()));
            return true;
        }
        throw new RuntimeException("redis no oper");
    }

    @Override
    public <T> T get0(Key key, Class<T> clazz) {
        if (pool != null) {
            Jedis jedis = pool.getResource();
            try {
                String value = jedis.get(key.toString());
                if (StringUtils.isEmpty(value)) {
                    return null;
                }
                return GsonUtil.gsonToBean(value, clazz);
            } finally {
                jedis.close();
            }
        }
        if (jedisCluster != null) {
            String value = jedisCluster.get(key.toString());
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            return GsonUtil.gsonToBean(value, clazz);
        }
        throw new RuntimeException("redis no oper");
    }

    @Override
    public <T> T get0(Key bigKey, Key key, Class<T> clazz) {
        if (pool != null) {
            Jedis jedis = pool.getResource();
            try {
                String value = jedis.hget(bigKey.toString(), key.toString());
                if (StringUtils.isEmpty(value)) {
                    return null;
                }
                return GsonUtil.gsonToBean(value, clazz);
            } finally {
                jedis.close();
            }
        }
        if (jedisCluster != null) {
            String value = jedisCluster.hget(bigKey.toString(), key.toString());
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            return GsonUtil.gsonToBean(value, clazz);
        }
        throw new RuntimeException("redis no oper");
    }

    @Override
    public <T> List<T> gets0(Key key, Class<T> clazz) {
        if (pool != null) {
            Jedis jedis = pool.getResource();
            try {
                String value = jedis.get(key.toString());
                if (StringUtils.isEmpty(value)) {
                    return null;
                }
                return GsonUtil.gsonToBean(value, new TypeToken<List<T>>(){});
            } finally {
                jedis.close();
            }
        }
        if (jedisCluster != null) {
            String value = jedisCluster.get(key.toString());
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            return GsonUtil.gsonToBean(value, new TypeToken<List<T>>(){});
        }
        throw new RuntimeException("redis no oper");
    }

    @Override
    public <T> List<T> gets0(Key bigKey, Key key, Class<T> clazz) {
        if (pool != null) {
            Jedis jedis = pool.getResource();
            try {
                String value = jedis.hget(bigKey.toString(), key.toString());
                if (StringUtils.isEmpty(value)) {
                    return null;
                }
                return GsonUtil.gsonToBean(value, new TypeToken<List<T>>(){});
            } finally {
                jedis.close();
            }
        }
        if (jedisCluster != null) {
            String value = jedisCluster.hget(bigKey.toString(), key.toString());
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            return GsonUtil.gsonToBean(value, new TypeToken<List<T>>(){});
        }
        throw new RuntimeException("redis no oper");
    }

    @Override
    public boolean delete0(Key key) {
        if (pool != null) {
            Jedis jedis = pool.getResource();
            try {
                jedis.del(key.toString());
                return true;
            } finally {
                jedis.close();
            }
        }
        if (jedisCluster != null) {
            jedisCluster.del(key.toString());
            return true;
        }
        throw new RuntimeException("redis no oper");
    }

    @Override
    public boolean delete0(Key bigKey, Key key) {
        if (pool != null) {
            Jedis jedis = pool.getResource();
            try {
                jedis.hdel(bigKey.toString(), key.toString());
                return true;
            } finally {
                jedis.close();
            }
        }
        if (jedisCluster != null) {
            jedisCluster.hdel(bigKey.toString(), key.toString());
            return true;
        }
        throw new RuntimeException("redis no oper");
    }

    @Override
    public boolean delete0(Collection<Key> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return true;
        }
        List<String> strKeys = keys.stream().map(Key::toString).collect(Collectors.toList());
        String[] strKeyArr = new String[strKeys.size()];
        strKeys.toArray(strKeyArr);
        if (pool != null) {
            Jedis jedis = pool.getResource();
            try {
                jedis.del(strKeyArr);
                return true;
            } finally {
                jedis.close();
            }
        }
        if (jedisCluster != null) {
            jedisCluster.del(strKeyArr);
            return true;
        }
        throw new RuntimeException("redis no oper");
    }

    @Override
    public boolean lock0(Key key) {
        if (pool != null) {
            Jedis jedis = pool.getResource();
            try {
                boolean r = jedis.setnx(key.toString(), "1") == 1;
                if (!r) {
                    return false;
                }
                jedis.expire(key.toString(), (int)key.getUnit().toSeconds(key.getTime()));
                return true;
            } finally {
                jedis.close();
            }
        }
        if (jedisCluster != null) {
            boolean r = jedisCluster.setnx(key.toString(), "1") == 1;
            if (!r) {
                return false;
            }
            jedisCluster.expire(key.toString(), (int)key.getUnit().toSeconds(key.getTime()));
            return true;
        }
        throw new RuntimeException("redis no oper");
    }

    @Override
    public boolean unlock0(Key key) {
        if (pool != null) {
            Jedis jedis = pool.getResource();
            try {
                jedis.del(key.toString());
                return true;
            } finally {
                jedis.close();
            }
        }
        if (jedisCluster != null) {
            jedisCluster.del(key.toString());
            return true;
        }
        throw new RuntimeException("redis no oper");
    }
}
