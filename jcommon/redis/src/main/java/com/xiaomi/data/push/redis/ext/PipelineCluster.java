package com.xiaomi.data.push.redis.ext;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.Pipeline;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author shanwenbang@xiaomi.com
 * @date 2021/4/22
 */
@Slf4j
public class PipelineCluster extends JedisCluster {
    public PipelineCluster(HostAndPort node) {
        super(node);
    }

    public PipelineCluster(Set<HostAndPort> jedisClusterNode, int connectionTimeout, int soTimeout, int maxAttempts, String password, GenericObjectPoolConfig poolConfig) {
        super(jedisClusterNode, connectionTimeout, soTimeout, maxAttempts, password, poolConfig);
    }

    public Map<String, String> mget(final List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return new HashMap<>();
        }
        return new PipelineClusterCommand<Map<String, String>>(this, connectionHandler) {
            @Override
            public void pipelineCommand(Pipeline pipeline, List<String> pipelineKeys) {
                for (String key : pipelineKeys) {
                    pipeline.get(key);
                }
            }

            @Override
            public Map<String, String> getResult(Map<String, Object> resultMap) {
                Map<String, String> result = new HashMap<String, String>();
                if (resultMap == null || resultMap.isEmpty()) {
                    return result;
                }
                for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
                    String key = entry.getKey();
                    Object object = entry.getValue();
                    if (object == null) {
                        //todo @shanwenbang null值,过滤掉 是否合理
                        //result.put(key, null);
                        continue;
                    }
                    if (checkRedirectException(object)) {
                        try {
                            String value = pipelineCluster.get(key);
                            if (value != null) {
                                result.put(key, value);
                            }
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    } else {
                        result.put(key, object.toString());
                    }
                }
                return result;
            }
        }.run(keys);
    }
}
