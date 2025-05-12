package run.mone.agentx.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;
import run.mone.agentx.annotation.Cache;
import run.mone.agentx.annotation.RemovalStrategy;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class CacheService {
    
    private final LoadingCache<String, Object> cache;

    public CacheService() {
        this.cache = CacheBuilder.newBuilder()
                .maximumSize(1000)  // 最大缓存条目数
                .expireAfterWrite(1, TimeUnit.HOURS)  // 默认过期时间
                .build(new CacheLoader<String, Object>() {
                    @Override
                    public Object load(String key) {
                        return null;
                    }
                });
    }

    /**
     * 获取缓存值
     */
    public Object get(String key) {
        try {
            return cache.get(key);
        } catch (ExecutionException e) {
            return null;
        }
    }

    /**
     * 设置缓存值
     */
    public void put(String key, Object value, Cache cacheAnnotation) {
        if (cacheAnnotation.strategy() == RemovalStrategy.TIME) {
            // 使用注解中指定的过期时间
            CacheBuilder.newBuilder()
                    .expireAfterWrite(cacheAnnotation.expire(), TimeUnit.SECONDS)
                    .build()
                    .put(key, value);
        } else {
            // 手动移除策略，不设置过期时间
            cache.put(key, value);
        }
    }

    /**
     * 手动移除缓存
     */
    public void remove(String key) {
        cache.invalidate(key);
    }

    /**
     * 清除所有缓存
     */
    public void clear() {
        cache.invalidateAll();
    }
} 