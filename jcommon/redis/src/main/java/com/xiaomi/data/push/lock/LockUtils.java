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

package com.xiaomi.data.push.lock;

import com.xiaomi.data.push.redis.Redis;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Created by zhangzhiyong on 05/06/2018.
 */
@Component
public class LockUtils {

    private JedisCluster cluster;

    @Value("${spring.redis}")
    private String redisHosts;

    private static final Logger logger = LoggerFactory.getLogger(LockUtils.class);

    @Autowired
    private Redis redis;

    /**
     * 一段时间内只执行一次
     *
     * @param key
     * @param runnable
     */
    public void runOnce(String key, Runnable runnable) {
        if (redis.setNx(key, key) >= 1) {
            try {
                runnable.run();
            } catch (Exception ex) {
                logger.error(ex.getMessage());
            } finally {
                redis.expire(key, (int) TimeUnit.DAYS.toSeconds(2));
            }
        }
    }


    public void lockAndRun(String key, Function function) {
        if (redis.setNx(key, "") >= 1) {
            try {
                function.apply(null);
            } finally {
                redis.del(key);
            }
        }
    }

    @PostConstruct
    public void init() {
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

            Set<HostAndPort> nodes = new HashSet<>();
            for (String ipPort : serverArray) {
                String[] ipPortPair = ipPort.split(":");
                nodes.add(new HostAndPort(ipPortPair[0].trim(), Integer.valueOf(ipPortPair[1].trim())));
            }
            cluster = new JedisCluster(nodes, timeout, config);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }



    public boolean lock(String key, String value, long expireMilliSeconds, long retryMilliSeconds) {
        long start = System.currentTimeMillis();
        try {
            String result;
            result = cluster.set(key, value, "NX", "PX", expireMilliSeconds);
            if ("OK".equals(result)) {
                return true;
            }
            if (retryMilliSeconds <= 0) {
                return false;
            }
            while (System.currentTimeMillis() - start < retryMilliSeconds) {
                result = cluster.set(key, value, "NX", "PX", expireMilliSeconds);
                if ("OK".equals(result)) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public boolean unlock(String key, String value) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result;
        try {
            result = cluster.eval(script, Collections.singletonList(key),
                    Collections.singletonList(value));
        } catch (Exception e) {
            return false;
        }
        if (1L == (long) result) {
            return true;
        }
        return false;
    }

}
