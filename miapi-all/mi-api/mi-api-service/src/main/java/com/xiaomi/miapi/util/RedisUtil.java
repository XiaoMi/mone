package com.xiaomi.miapi.util;

import com.xiaomi.miapi.common.Consts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Component
public class RedisUtil {

    @Autowired
    private JedisPool jedisPool;

    /**
     * set op for redis
     */
    public String set(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key, value);
        } catch (Exception e) {
            return "0";
        }
    }

    /**
     * set with expire time
     */
    public void saveEpKey(String key, String value, Integer expireSecond) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key, expireSecond, value);
        } catch (Exception ignored) {
        }
    }

    public String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        } catch (Exception e) {
            return "0";
        }
    }

    /**
     * check if key exists
     */
    public Boolean exists(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(key);
        } catch (Exception e) {
            return false;
        }
    }

    public Long del(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.del(key);
        } catch (Exception e) {
            return 0L;
        }
    }

    public List<String> lRange(String key, int start, int end) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lrange(key, start, end);
        } catch (Exception e) {
            return null;
        }
    }

    public void recordRecently10Projects(String username, Integer projectId) {
        if (jedisPool == null || username == null || projectId == null) {
            return;
        }
        String key = Consts.genRecentlyProjectsKey(username);
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> projectIDs = jedis.lrange(key, 0, 7);
            if (projectIDs.contains(projectId.toString())) {
            } else {
                jedis.lpush(key, projectId.toString());
                jedis.ltrim(key, 0, 7);
            }
        } catch (Exception ignored) {
        }
    }

    public void recordRecently10Apis(String username, Integer apiId) {

        if (jedisPool == null || username == null || apiId == null){
            return;
        }
        String key = Consts.genRecentlyApisKey(username);
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> apiIDs = jedis.lrange(key, 0, 7);

            if (apiIDs.contains(apiId.toString())) {
            } else {
                jedis.lpush(key, apiId.toString());
                jedis.ltrim(key, 0, 7);
            }
        } catch (Exception ignored) {
        }
    }
}