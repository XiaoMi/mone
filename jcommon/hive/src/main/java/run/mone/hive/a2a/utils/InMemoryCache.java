package com.google.a2a.common.utils;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * 简单的内存缓存实现，支持缓存过期和自动清理
 * @param <K> 键类型
 * @param <V> 值类型
 */
public class InMemoryCache<K, V> {
    
    private static class CacheEntry<V> {
        private final V value;
        private final Instant expiryTime;
        
        public CacheEntry(V value, Duration ttl) {
            this.value = value;
            this.expiryTime = ttl != null ? Instant.now().plus(ttl) : null;
        }
        
        public boolean isExpired() {
            return expiryTime != null && Instant.now().isAfter(expiryTime);
        }
        
        public V getValue() {
            return value;
        }
    }
    
    private final Map<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final Duration defaultTtl;
    private final ScheduledExecutorService scheduler;
    
    /**
     * 创建一个内存缓存，无默认过期时间
     */
    public InMemoryCache() {
        this(null);
    }
    
    /**
     * 创建一个内存缓存，指定默认过期时间
     * @param defaultTtl 默认过期时间
     */
    public InMemoryCache(Duration defaultTtl) {
        this.defaultTtl = defaultTtl;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "cache-cleanup-thread");
            thread.setDaemon(true);
            return thread;
        });
        
        // 定期清理过期条目
        this.scheduler.scheduleAtFixedRate(
                this::removeExpiredEntries,
                1, 1, TimeUnit.MINUTES);
    }
    
    /**
     * 获取缓存条目，如果不存在或已过期则通过提供的函数获取
     * @param key 缓存键
     * @param supplier 值提供函数
     * @return 缓存值
     */
    public V get(K key, Function<K, V> supplier) {
        return get(key, supplier, defaultTtl);
    }
    
    /**
     * 获取缓存条目，如果不存在或已过期则通过提供的函数获取，并指定过期时间
     * @param key 缓存键
     * @param supplier 值提供函数
     * @param ttl 过期时间
     * @return 缓存值
     */
    public V get(K key, Function<K, V> supplier, Duration ttl) {
        // 尝试从缓存获取
        Optional<V> cachedValue = getIfPresent(key);
        
        // 如果存在有效值，则返回
        if (cachedValue.isPresent()) {
            return cachedValue.get();
        }
        
        // 否则，通过supplier获取并缓存
        V value = supplier.apply(key);
        put(key, value, ttl);
        return value;
    }
    
    /**
     * 尝试从缓存获取值，如果不存在或已过期则返回空
     * @param key 缓存键
     * @return 可选的缓存值
     */
    public Optional<V> getIfPresent(K key) {
        CacheEntry<V> entry = cache.get(key);
        
        if (entry == null || entry.isExpired()) {
            if (entry != null && entry.isExpired()) {
                cache.remove(key);
            }
            return Optional.empty();
        }
        
        return Optional.of(entry.getValue());
    }
    
    /**
     * 将值添加到缓存
     * @param key 缓存键
     * @param value 缓存值
     */
    public void put(K key, V value) {
        put(key, value, defaultTtl);
    }
    
    /**
     * 将值添加到缓存，并指定过期时间
     * @param key 缓存键
     * @param value 缓存值
     * @param ttl 过期时间
     */
    public void put(K key, V value, Duration ttl) {
        cache.put(key, new CacheEntry<>(value, ttl));
    }
    
    /**
     * 从缓存中移除指定键
     * @param key 要移除的键
     * @return 如果键存在且被移除则返回true
     */
    public boolean remove(K key) {
        return cache.remove(key) != null;
    }
    
    /**
     * 清空缓存
     */
    public void clear() {
        cache.clear();
    }
    
    /**
     * 获取缓存大小
     * @return 缓存条目数量
     */
    public int size() {
        removeExpiredEntries();
        return cache.size();
    }
    
    /**
     * 移除所有过期条目
     */
    private void removeExpiredEntries() {
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
    /**
     * 关闭缓存，停止定期清理任务
     */
    public void shutdown() {
        scheduler.shutdown();
    }
} 