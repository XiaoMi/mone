package com.xiaomi.mione.prometheus.redis;

import com.google.common.util.concurrent.AtomicLongMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisAskDataException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.exceptions.JedisMovedDataException;
import redis.clients.jedis.exceptions.JedisRedirectionException;
import redis.clients.util.JedisClusterCRC16;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public abstract class PipelineClusterCommand<T> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final AtomicLongMap<String> NODE_JEDIS_EXCEPTION_MAP = AtomicLongMap.create();

    private JedisClusterConnectionHandler connectionHandler;

    protected final PipelineCluster pipelineCluster;

    public PipelineClusterCommand(PipelineCluster pipelineCluster, JedisClusterConnectionHandler connectionHandler) {
        this.pipelineCluster = pipelineCluster;
        this.connectionHandler = connectionHandler;
    }

    /**
     * 执行批处理命令
     *
     * @param pipeline
     */
    public abstract void pipelineCommand(Pipeline pipeline, List<String> pipelineKeys);

    public abstract T getResult(Map<String, Object> resultMap);

    public T run(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return null;
        }
        Map<JedisPool, List<String>> poolKeysMap = getPoolKeyMap(keys);
        Map<String, Object> resultMap = new HashMap<>();

        poolKeysMap.entrySet()
                //并行化
                .parallelStream()
                .forEach(entry -> {
                    JedisPool jedisPool = entry.getKey();
                    List<String> subKeys = entry.getValue();
                    if (subKeys == null || subKeys.isEmpty()) {
                        return;
                    }
                    //申请jedis对象
                    Jedis jedis = null;
                    Pipeline pipeline = null;
                    List<Object> subResultList = null;
                    try {
                        jedis = jedisPool.getResource();
                        jedis.getClient().connect();
                        pipeline = jedis.pipelined();
                        pipelineCommand(pipeline, subKeys);
                        subResultList = pipeline.syncAndReturnAll();
                    } catch (JedisException e) {
                        if (jedisPool != null) {
                            // add-code:记录节点错误，当超过5次时，更新对应slot-node关系
                            String node = jedis.getClient().getHost() + ":" + jedis.getClient().getPort();
                            long count = NODE_JEDIS_EXCEPTION_MAP.incrementAndGet(node);
                            // DEFAULT_MAX_ATTEMPTS = 5
                            if (count >= 5) {
                                // renewSlotCache
                                connectionHandler.renewSlotCache();
                                NODE_JEDIS_EXCEPTION_MAP.remove(node);
                            }
                            logger.error("RedisConnectionError-node:{},keys={}, {}", node, subKeys, e);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    } finally {
                        if (pipeline != null) {
                            try {
                                pipeline.close();
                            } catch (IOException e) {
                            }
                        }
                        //释放jedis对象
                        if (jedis != null) {
                            jedis.close();
                        }
                    }

                    if (subResultList == null) {
                        return;
                    }

                    if (subResultList.size() == subKeys.size()) {
                        for (int i = 0; i < subKeys.size(); i++) {
                            String key = subKeys.get(i);
                            Object result = subResultList.get(i);
                            resultMap.put(key, result);
                        }
                    } else {
                        logger.error("PipelineClusterCommand: subKeys={} subResultList={}", subKeys, subResultList);
                    }
                });

        return getResult(resultMap);
    }

    private Map<JedisPool, List<String>> getPoolKeyMap(List<String> keys) {
        Map<JedisPool, List<String>> poolKeysMap = new LinkedHashMap<JedisPool, List<String>>();
        try {
            Field field = JedisClusterConnectionHandler.class.getDeclaredField("cache");
            for (String key : keys) {
                JedisPool jedisPool;
                int slot = JedisClusterCRC16.getSlot(key);
                field.setAccessible(true);
                JedisClusterInfoCache jedisClusterInfoCache =  (JedisClusterInfoCache)field.get(connectionHandler);
                jedisPool = jedisClusterInfoCache.getSlotPool(slot);

                if (poolKeysMap.containsKey(jedisPool)) {
                    poolKeysMap.get(jedisPool).add(key);
                } else {
                    List<String> subKeyList = new ArrayList<String>();
                    subKeyList.add(key);
                    poolKeysMap.put(jedisPool, subKeyList);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return poolKeysMap;
    }

    protected boolean checkRedirectException(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof JedisRedirectionException) {
            //重定向slot 映射.
            if (obj instanceof JedisMovedDataException) {
                JedisMovedDataException movedException = (JedisMovedDataException) obj;
                logger.warn("JedisMovedDataException:slot={} node={} ",
                        movedException.getSlot(), movedException.getTargetNode().toString());
                // add-code: 检测到JedisMovedDataException 更新 renewSlotCache
                connectionHandler.renewSlotCache();
            } else if (obj instanceof JedisAskDataException) {
                JedisAskDataException askDataException = (JedisAskDataException) obj;
                logger.warn("JedisAskDataException:slot={} node={} ",
                        askDataException.getSlot(), askDataException.getTargetNode().toString());
            }
            return true;
        } else if (obj instanceof Exception) {
            Exception e = (Exception) obj;
            logger.error(e.getMessage(), e);
        }
        return false;
    }

}
