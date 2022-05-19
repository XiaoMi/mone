package com.xiaomi.mione.prometheus.redis;

import com.xiaomi.mione.prometheus.redis.monitor.AttachInfo;
import com.xiaomi.mione.prometheus.redis.monitor.MetricTypes;
import com.xiaomi.mione.prometheus.redis.monitor.RedisMonitor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author goodjava@qq.com
 * @Date 2021/6/28 14:30
 */

@Slf4j
@Aspect
@Component
@ConditionalOnClass(RedisTemplate.class)
public class RedisAdvice {


    @Value("${redis.slow.query.time:100}")
    private Long redisSlowQueryTime;

    @Value("${spring.redis.prometheus.enabled:true}")
    private boolean prometheusEnabled;

    @Value("${spring.redis.cat.enabled:true}")
    private boolean catEnabled;


    private RedisMonitor redisMonitor;

    public RedisAdvice(){
        this.redisMonitor = new RedisMonitor(redisSlowQueryTime);
    }

    class RAZSetOperations implements  ZSetOperations{
        private ZSetOperations zSetOperations;
        private AttachInfo attachInfo;

        public RAZSetOperations(ZSetOperations zSetOperations,AttachInfo attachInfo){
            this.zSetOperations = zSetOperations;
            this.attachInfo = attachInfo;
        }

        @Override
        public Boolean add(Object o, Object o2, double v1) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.add(o,o2,v1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "add", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long remove(Object o, Object... objects) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.remove(o,objects);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "remove", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Double incrementScore(Object o, Object o2, double v1) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.incrementScore(o,o2,v1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "incrementScore", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long rank(Object o, Object o2) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.rank(o,o2);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "rank", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long reverseRank(Object o, Object o2) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.reverseRank(o,o2);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "reverseRank", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Set range(Object o, long l, long l1) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.range(o,l,l1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "range", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Set<TypedTuple> rangeWithScores(Object o, long l, long l1) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.rangeWithScores(o,l,l1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "rangeWithScores", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Set rangeByScore(Object o, double v, double v1) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.rangeByScore(o,v,v1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "rangeByScore", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Set<TypedTuple> rangeByScoreWithScores(Object o, double v, double v1) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.rangeByScoreWithScores(o,v,v1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "rangeByScoreWithScores", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Set rangeByScore(Object o, double v, double v1, long l, long l1) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.rangeByScore(o,v,v1,l,l1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "rangeByScore", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Set<TypedTuple> rangeByScoreWithScores(Object o, double v, double v1, long l, long l1) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.rangeByScoreWithScores(o,v,v1,l,l1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "rangeByScoreWithScores", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Set reverseRange(Object o, long l, long l1) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.reverseRange(o,l,l1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "reverseRange", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Set<TypedTuple> reverseRangeWithScores(Object o, long l, long l1) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.reverseRangeWithScores(o,l,l1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "reverseRangeWithScores", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Set reverseRangeByScore(Object o, double v, double v1) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.reverseRangeByScore(o,v,v1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "reverseRangeByScore", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Set<TypedTuple> reverseRangeByScoreWithScores(Object o, double v, double v1) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.reverseRangeByScoreWithScores(o,v,v1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "reverseRangeByScoreWithScores", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Set reverseRangeByScore(Object o, double v, double v1, long l, long l1) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.reverseRangeByScore(o,v,v1,l,l1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "reverseRangeByScore", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Set<TypedTuple> reverseRangeByScoreWithScores(Object o, double v, double v1, long l, long l1) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.reverseRangeByScoreWithScores(o,v,v1,l,l1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "reverseRangeByScoreWithScores", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long count(Object o, double v, double v1) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.count(o,v,v1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "count", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long size(Object o) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.size(o);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "size", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long zCard(Object o) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.zCard(o);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "zCard", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Double score(Object o, Object o2) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.score(o,o2);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "score", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long removeRange(Object o, long l, long l1) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.removeRange(o,l,l1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "removeRange", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long removeRangeByScore(Object o, double v, double v1) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.removeRangeByScore(o,v,v1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "removeRangeByScore", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long unionAndStore(Object o, Object k1, Object k2) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.unionAndStore(o,k1,k2);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "unionAndStore", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long unionAndStore(Object o, Collection collection, Object k1) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.unionAndStore(o,collection,k1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "unionAndStore", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long unionAndStore(Object o, Collection collection, Object k1, RedisZSetCommands.Aggregate aggregate, RedisZSetCommands.Weights weights) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.unionAndStore(o,collection,k1,aggregate,weights);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "unionAndStore", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long intersectAndStore(Object o, Object k1, Object k2) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.intersectAndStore(o,k1,k2);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "intersectAndStore", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long intersectAndStore(Object o, Collection collection, Object k1) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.intersectAndStore(o,collection,k1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "intersectAndStore", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long intersectAndStore(Object o, Collection collection, Object k1, RedisZSetCommands.Aggregate aggregate, RedisZSetCommands.Weights weights) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.intersectAndStore(o,collection,k1,aggregate,weights);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "intersectAndStore", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Cursor<TypedTuple> scan(Object o, ScanOptions scanOptions) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.scan(o,scanOptions);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "scan", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Set rangeByLex(Object o, RedisZSetCommands.Range range) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.rangeByLex(o,range);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "rangeByLex", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Set rangeByLex(Object o, RedisZSetCommands.Range range, RedisZSetCommands.Limit limit) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.rangeByLex(o,range,limit);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "rangeByLex", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public RedisOperations getOperations() {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.getOperations();
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "getOperations", "", startTime, success,attachInfo);
            }
        }

        @Override
        public Long add(Object o, Set set) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return zSetOperations.add(o,set);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "add", o.toString(), startTime, success,attachInfo);
            }
        }
    }

    class RASetOperations implements SetOperations{

        private SetOperations setOperations;
        private AttachInfo attachInfo;

        public RASetOperations(SetOperations setOperations,AttachInfo attachInfo){
            this.setOperations = setOperations;
            this.attachInfo = attachInfo;
        }

        @Override
        public Long add(Object o, Object[] objects) {

            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return setOperations.add(o,objects);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "add", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long remove(Object o, Object... objects) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return setOperations.remove(o,objects);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "remove", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Object pop(Object o) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return setOperations.pop(o);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "pop", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public List pop(Object o, long l) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return setOperations.pop(o,l);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "pop", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Boolean move(Object o, Object o2, Object k1) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return setOperations.move(o,o2,k1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "move", o.toString() + "," + o2.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long size(Object o) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return setOperations.size(o);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "size", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Boolean isMember(Object o, Object o2) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return setOperations.isMember(o,o2);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "isMember", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Set intersect(Object o, Object k1) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return setOperations.intersect(o,k1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "intersect", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Set intersect(Object o, Collection collection) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return setOperations.intersect(o,collection);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "intersect", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long intersectAndStore(Object o, Object k1, Object k2) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return setOperations.intersectAndStore(o,k1,k2);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "intersectAndStore", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long intersectAndStore(Object o, Collection collection, Object k1) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return setOperations.intersectAndStore(o,collection,k1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "intersectAndStore", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Set union(Object o, Object k1) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return setOperations.union(o,k1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "union", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Set union(Object o, Collection collection) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return setOperations.union(o,collection);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "union", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long unionAndStore(Object o, Object k1, Object k2) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return setOperations.unionAndStore(o,k1,k2);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "unionAndStore", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long unionAndStore(Object o, Collection collection, Object k1) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return setOperations.unionAndStore(o,collection,k1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "unionAndStore", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Set difference(Object o, Object k1) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return setOperations.difference(o,k1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "difference", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Set difference(Object o, Collection collection) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return setOperations.difference(o,collection);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "difference", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long differenceAndStore(Object o, Object k1, Object k2) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return setOperations.differenceAndStore(o,k1,k2);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "differenceAndStore", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long differenceAndStore(Object o, Collection collection, Object k1) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return setOperations.differenceAndStore(o,collection,k1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "differenceAndStore", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Set members(Object o) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return setOperations.members(o);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "members", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Object randomMember(Object o) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return setOperations.randomMember(o);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "randomMember", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Set distinctRandomMembers(Object o, long l) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return setOperations.distinctRandomMembers(o,l);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "distinctRandomMembers", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public List randomMembers(Object o, long l) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return setOperations.randomMembers(o,l);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "randomMembers", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Cursor scan(Object o, ScanOptions scanOptions) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return setOperations.scan(o,scanOptions);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "scan", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public RedisOperations getOperations() {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return setOperations.getOperations();
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "getOperations", "", startTime, success,attachInfo);
            }
        }
    }

    class RAListOperations  implements ListOperations{

        private ListOperations listOperations;
        private AttachInfo attachInfo;

        public RAListOperations(ListOperations listOperations,AttachInfo attachInfo){
            this.listOperations = listOperations;
            this.attachInfo = attachInfo;
        }

        @Override
        public List range(Object o, long l, long l1) {

            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return listOperations.range(o,l,l1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "range", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public void trim(Object o, long l, long l1) {

            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                listOperations.trim(o,l,l1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "trim", o.toString(), startTime, success,attachInfo);
            }

        }

        @Override
        public Long size(Object o) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return listOperations.size(o);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "size", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long leftPush(Object o, Object o2) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return listOperations.leftPush(o,o2);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "leftPush", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long leftPushAll(Object o, Object[] objects) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return listOperations.leftPushAll(o,objects);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "leftPushAll", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long leftPushAll(Object o, Collection collection) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return listOperations.leftPushAll(o,collection);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "leftPushAll", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long leftPushIfPresent(Object o, Object o2) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return listOperations.leftPushIfPresent(o,o2);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "leftPushIfPresent", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long leftPush(Object o, Object o2, Object v1) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return listOperations.leftPush(o,o2,v1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "leftPush", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long rightPush(Object o, Object o2) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return listOperations.rightPush(o,o2);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "rightPush", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long rightPushAll(Object o, Object[] objects) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return listOperations.rightPushAll(o,objects);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "rightPushAll", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long rightPushAll(Object o, Collection collection) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return listOperations.rightPushAll(o,collection);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "rightPushAll", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long rightPushIfPresent(Object o, Object o2) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return listOperations.rightPushIfPresent(o,o2);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "rightPushIfPresent", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long rightPush(Object o, Object o2, Object v1) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return listOperations.rightPush(o,o2,v1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "rightPush", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public void set(Object o, long l, Object o2) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                listOperations.set(o,l,o2);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "set", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long remove(Object o, long l, Object o2) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return listOperations.remove(o,l,o2);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "remove", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Object index(Object o, long l) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return listOperations.index(o,l);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "index", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Object leftPop(Object o) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return listOperations.leftPop(o);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "leftPop", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Object leftPop(Object o, long l, TimeUnit timeUnit) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return listOperations.leftPop(o,l,timeUnit);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "leftPop", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Object rightPop(Object o) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return listOperations.rightPop(o);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "rightPop", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Object rightPop(Object o, long l, TimeUnit timeUnit) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return listOperations.rightPop(o,l,timeUnit);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "rightPop", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Object rightPopAndLeftPush(Object o, Object k1) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return listOperations.rightPopAndLeftPush(o,k1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "rightPopAndLeftPush", o.toString() + "," +k1.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Object rightPopAndLeftPush(Object o, Object k1, long l, TimeUnit timeUnit) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return listOperations.rightPopAndLeftPush(o,k1,l,timeUnit);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "rightPopAndLeftPush", o.toString() + "," +k1.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public RedisOperations getOperations() {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return listOperations.getOperations();
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "getOperations", "", startTime, success,attachInfo);
            }
        }
    }

    class RAHashOperations  implements HashOperations {

        private HashOperations hashOperations;
        private AttachInfo attachInfo;

        public RAHashOperations(HashOperations hashOperations,AttachInfo attachInfo){
            this.hashOperations = hashOperations;
            this.attachInfo = attachInfo;
        }


        @Override
        public Long delete(Object o, Object... objects) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return hashOperations.delete(o,objects);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "delete", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Boolean hasKey(Object o, Object o2) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return hashOperations.hasKey(o,o2);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "hasKey", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Object get(Object o, Object o2) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return hashOperations.get(o,o2);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "get", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public List multiGet(Object o, Collection collection) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return hashOperations.multiGet(o,collection);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "multiGet", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long increment(Object o, Object o2, long l) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return hashOperations.increment(o,o2,l);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "increment", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Double increment(Object o, Object o2, double v) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return hashOperations.increment(o,o2,v);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "increment", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Set keys(Object o) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return hashOperations.keys(o);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "keys", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long lengthOfValue(Object o, Object o2) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return hashOperations.lengthOfValue(o,o2);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "lengthOfValue", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long size(Object o) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return hashOperations.size(o);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "size", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public void putAll(Object o, Map map) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                hashOperations.putAll(o,map);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "putAll", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public void put(Object o, Object o2, Object o3) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                hashOperations.put(o,o2,o3);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "put", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Boolean putIfAbsent(Object o, Object o2, Object o3) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return hashOperations.putIfAbsent(o,o2,o3);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "putIfAbsent", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public List values(Object o) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return hashOperations.values(o);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "values", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Map entries(Object o) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return hashOperations.entries(o);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "entries", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Cursor<Map.Entry> scan(Object o, ScanOptions scanOptions) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return hashOperations.scan(o,scanOptions);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "scan", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public RedisOperations getOperations() {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return hashOperations.getOperations();
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(catEnabled, prometheusEnabled, false, "", MetricTypes.Counter, "getOperations", "", startTime, success,attachInfo);
            }
        }
    }

    class RAValueOperations implements ValueOperations {

        private ValueOperations valueOperations;
        private AttachInfo attachInfo;

        public RAValueOperations() {
        }

        public RAValueOperations(ValueOperations valueOperations,AttachInfo attachInfo) {
            this.valueOperations = valueOperations;
            this.attachInfo = attachInfo;
        }

        @Override
        public void set(Object o, Object o2) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                valueOperations.set(o,o2);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(true, prometheusEnabled, false, "", MetricTypes.Counter, "set", o.toString(), startTime, success,attachInfo);
            }

        }

        @Override
        public void set(Object o, Object o2, long l, TimeUnit timeUnit) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                valueOperations.set(o,o2,l,timeUnit);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(true, prometheusEnabled, false, "", MetricTypes.Counter, "set", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Boolean setIfAbsent(Object o, Object o2) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return valueOperations.setIfAbsent(o,o2);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(true, prometheusEnabled, false, "", MetricTypes.Counter, "setIfAbsent", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Boolean setIfAbsent(Object o, Object o2, long l, TimeUnit timeUnit) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return valueOperations.setIfAbsent(o,o2,l,timeUnit);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(true, prometheusEnabled, false, "", MetricTypes.Counter, "setIfAbsent", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Boolean setIfPresent(Object o, Object o2) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return valueOperations.setIfPresent(o,o2);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(true, prometheusEnabled, false, "", MetricTypes.Counter, "setIfPresent", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Boolean setIfPresent(Object o, Object o2, long l, TimeUnit timeUnit) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return valueOperations.setIfPresent(o,o2,l,timeUnit);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(true, prometheusEnabled, false, "", MetricTypes.Counter, "setIfPresent", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public void multiSet(Map map) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                valueOperations.multiSet(map);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(true, prometheusEnabled, false, "", MetricTypes.Counter, "multiSet", StringUtils.collectionToCommaDelimitedString(map.keySet()), startTime, success,attachInfo);
            }
        }

        @Override
        public Boolean multiSetIfAbsent(Map map) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return valueOperations.multiSetIfAbsent(map);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(true, prometheusEnabled, false, "", MetricTypes.Counter, "multiSetIfAbsent", StringUtils.collectionToCommaDelimitedString(map.keySet()), startTime, success,attachInfo);
            }
        }

        @Override
        public Object get(Object o) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return valueOperations.get(o);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(true, prometheusEnabled, false, "", MetricTypes.Counter, "get", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Object getAndSet(Object o, Object o2) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return valueOperations.getAndSet(o,o2);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(true, prometheusEnabled, false, "", MetricTypes.Counter, "getAndSet", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public List multiGet(Collection collection) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return valueOperations.multiGet(collection);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(true, prometheusEnabled, false, "", MetricTypes.Counter, "multiGet", StringUtils.collectionToCommaDelimitedString(collection), startTime, success,attachInfo);
            }
        }

        @Override
        public Long increment(Object o) {

            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return valueOperations.increment(o);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(true, prometheusEnabled, false, "", MetricTypes.Counter, "increment", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long increment(Object o, long l) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return valueOperations.increment(o,l);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(true, prometheusEnabled, false, "", MetricTypes.Counter, "increment", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Double increment(Object o, double v) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return valueOperations.increment(o,v);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(true, prometheusEnabled, false, "", MetricTypes.Counter, "increment", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long decrement(Object o) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return valueOperations.decrement(o);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(true, prometheusEnabled, false, "", MetricTypes.Counter, "decrement", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Long decrement(Object o, long l) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return valueOperations.decrement(o,l);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(true, prometheusEnabled, false, "", MetricTypes.Counter, "decrement", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Integer append(Object o, String s) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return valueOperations.append(o,s);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(true, prometheusEnabled, false, "", MetricTypes.Counter, "append", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public String get(Object o, long l, long l1) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return valueOperations.get(o,l,l1);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(true, prometheusEnabled, false, "", MetricTypes.Counter, "get", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public void set(Object o, Object o2, long l) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                valueOperations.set(o,o2,l);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(true, prometheusEnabled, false, "", MetricTypes.Counter, "set", o.toString(), startTime, success,attachInfo);
            }

        }

        @Override
        public Long size(Object o) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return valueOperations.size(o);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(true, prometheusEnabled, false, "", MetricTypes.Counter, "size", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Boolean setBit(Object o, long l, boolean b) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return valueOperations.setBit(o,l,b);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(true, prometheusEnabled, false, "", MetricTypes.Counter, "setBit", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public Boolean getBit(Object o, long l) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return valueOperations.getBit(o,l);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(true, prometheusEnabled, false, "", MetricTypes.Counter, "getBit", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public List<Long> bitField(Object o, BitFieldSubCommands bitFieldSubCommands) {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return valueOperations.bitField(o,bitFieldSubCommands);
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(true, prometheusEnabled, false, "", MetricTypes.Counter, "bitField", o.toString(), startTime, success,attachInfo);
            }
        }

        @Override
        public RedisOperations getOperations() {
            boolean success = true;
            Long startTime = System.currentTimeMillis();
            try {
                return valueOperations.getOperations();
            } catch (Exception e) {
                success = false;
                throw e;
            } finally {
                redisMonitor.recordMonitorInfo(true, prometheusEnabled, false, "", MetricTypes.Counter, "getOperations", "", startTime, success,attachInfo);
            }
        }
    }



    @Around("execution(* org.springframework.data.redis.core.RedisTemplate.opsFor*())")
    public Object run(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Object result = joinPoint.proceed(args);

        AttachInfo attachInfo = new AttachInfo();
        try {
            RedisTemplate redisTemplate = (RedisTemplate) joinPoint.getTarget();
            JedisConnectionFactory jedisConnectionFactory = (JedisConnectionFactory) redisTemplate.getConnectionFactory();
            attachInfo.setHostName(jedisConnectionFactory.getHostName());
            attachInfo.setPort(jedisConnectionFactory.getPort());
            attachInfo.setDbIndex(jedisConnectionFactory.getDatabase());
        } catch (Exception e) {
            log.error("RedisAdvice cast redisTemplate.ConnectionFactory to JedisConnectionFactory error : {} ",e.getMessage(),e);
        }

//        log.info("redisServerInfo hostName : {} , port : {}, dataBase : {}",hostName,port,database);


        if (result instanceof ValueOperations) {
            return new RAValueOperations((ValueOperations) result,attachInfo);
        }

        if (result instanceof HashOperations) {
            return new RAHashOperations((HashOperations) result,attachInfo);
        }

        if (result instanceof ListOperations) {
            return new RAListOperations((ListOperations) result,attachInfo);
        }

        if (result instanceof SetOperations) {
            return new RASetOperations((SetOperations) result,attachInfo);
        }

        if (result instanceof ZSetOperations) {
            return new RAZSetOperations((ZSetOperations) result,attachInfo);
        }

        return result;
    }

}
