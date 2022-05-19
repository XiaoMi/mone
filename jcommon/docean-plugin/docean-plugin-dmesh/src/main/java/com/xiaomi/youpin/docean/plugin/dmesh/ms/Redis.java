package com.xiaomi.youpin.docean.plugin.dmesh.ms;

import com.xiaomi.youpin.docean.plugin.dmesh.anno.MeshMsService;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author goodjava@qq.com
 * @author dingpei@xiaomi.com
 * @date 1/10/21
 * redis操作
 */
@MeshMsService(interfaceClass = Redis.class, name = "redis")
public interface Redis {

    String get(String key);

    void set(String key, String value);

    //NX是不存在时才set， XX是存在时才set， EX是秒，PX是毫秒
    Long setNx(String key, String value);

    String setXx(String key, String value);

    long incr(String key);

    void expire(String key, int time);

    boolean exists(String key);

    void del(String key);



    List<String> lrange(String key);

    Long lpush(String key, String value);

    Long zadd(String key, double score, String member);

    Long zaddMembers(String key, Map<String, Double> scoreMembers);

    Long zrem(String key, String[] members);

    Set<String> zrange(String key, long start, long end);

    Set<String> zrevrange(String key, long start, long end);

    Long zrank(String key, String member);

    Double zincrby(String key, double score, String member);

    Long sadd(String key, String[] members);

    Long srem(String key, String[] members);

    Set<String> smembers(String key);

    Set<String> spop(String key, long count);



}
