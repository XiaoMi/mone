package com.xiaomi.miapi.util;

import com.xiaomi.miapi.common.Consts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

@Component
public class RedisUtil {

    @Autowired
    private JedisPool jedisPool;

    /**
     * 向Redis中存值，永久有效
     */
    public String set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.set(key, value);
        } catch (Exception e) {
            return "0";
        } finally {
            jedis.close();
        }
    }

    public void saveEpKey(String key, String value, Integer expireSecond) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.setex(key, expireSecond,value);
        } catch (Exception ignored) {
        } finally {
            jedis.close();
        }
    }

    /**
     * 根据传入Key获取指定Value
     */
    public String get(String key) {
        Jedis jedis = null;
        String value;
        try {
            jedis = jedisPool.getResource();
            value = jedis.get(key);
        } catch (Exception e) {
            return "0";
        } finally {
            jedis.close();
        }
        return value;
    }

    /**
     * 校验Key值是否存在
     */
    public Boolean exists(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.exists(key);
        } catch (Exception e) {
            return false;
        } finally {
            jedis.close();
        }
    }

    /**
     * 删除指定Key-Value
     */
    public Long del(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.del(key);
        } catch (Exception e) {
            return 0L;
        } finally {
            jedis.close();
        }
    }

    public List<String> lRange(String key,int start,int end) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.lrange(key,start,end);
        } catch (Exception e) {
            return null;
        } finally {
            jedis.close();
        }
    }

    public boolean recordRecently10Projects(Integer userId,Integer projectId){
        if (jedisPool == null || userId == null || projectId == null){
            return false;
        }
        String key = Consts.genRecentlyProjectsKey(userId);
        Jedis jedis = jedisPool.getResource();
//        Transaction tx = jedis.multi();
        try {
            List<String> projectIDs = jedis.lrange(key,0,7);
            if (projectIDs.contains(projectId.toString())){
                return true;
            }else {
                jedis.lpush(key, projectId.toString());
                jedis.ltrim(key, 0, 7);
            }
        } catch (Exception e) {
            return false;
        }finally {
            jedis.close();
        }
//        tx.exec();
        return true;
    }

    public boolean recordRecently10Apis(Integer userId,Integer apiId){
        if (jedisPool == null || userId == null || apiId == null){
            return false;
        }
        String key = Consts.genRecentlyApisKey(userId);
        Jedis jedis = jedisPool.getResource();
        try {
            List<String> apiIDs = jedis.lrange(key,0,7);

            if (apiIDs.contains(apiId.toString())){
                return true;
            }else {
                jedis.lpush(key, apiId.toString());
                jedis.ltrim(key, 0, 7);
            }
        } catch (Exception e) {
            return false;
        }finally {
            jedis.close();
        }
        return true;
    }
}