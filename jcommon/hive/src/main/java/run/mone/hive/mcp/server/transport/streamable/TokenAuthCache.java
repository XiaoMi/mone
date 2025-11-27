/*
 * Copyright 2024-2024 the original author or authors.
 */

package run.mone.hive.mcp.server.transport.streamable;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Token authentication cache with expiration support using Guava Cache.
 * Caches bearer token validation results to avoid calling the validation service on every request.
 */
public class TokenAuthCache {

    private static final Logger logger = LoggerFactory.getLogger(TokenAuthCache.class);

    private final Cache<String, Boolean> cache;
    private final Duration defaultTtl;

    /**
     * Creates a new TokenAuthCache with the specified default TTL.
     * @param defaultTtl The default time-to-live for cache entries
     */
    public TokenAuthCache(Duration defaultTtl) {
        this.defaultTtl = defaultTtl;

        // Build Guava cache with expiration
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(defaultTtl.toMillis(), TimeUnit.MILLISECONDS)
                .maximumSize(10000) // Limit cache size to prevent memory issues
                .recordStats() // Enable statistics for monitoring
                .build();

        logger.info("Initialized token auth cache with TTL: {}", defaultTtl);
    }

    /**
     * Puts a validation result into the cache.
     * @param token The bearer token
     * @param isValid Whether the token is valid
     */
    public void put(String token, boolean isValid) {
        cache.put(token, isValid);
        logger.debug("Cached token validation result: valid={}, ttl={}", isValid, defaultTtl);
    }

    /**
     * Puts a validation result into the cache with a custom TTL.
     * Note: Guava Cache does not support per-entry TTL, so this method uses the default TTL.
     * If you need per-entry TTL, consider using a different cache implementation.
     * @param token The bearer token
     * @param isValid Whether the token is valid
     * @param ttl The time-to-live for this cache entry (ignored, uses default TTL)
     */
    public void put(String token, boolean isValid, Duration ttl) {
        // Guava Cache doesn't support per-entry TTL easily
        // We'll just use the default TTL and log a warning if different
        if (!ttl.equals(defaultTtl)) {
            logger.debug("Custom TTL {} requested but using default TTL {} (Guava Cache limitation)",
                        ttl, defaultTtl);
        }
        put(token, isValid);
    }

    /**
     * Gets a validation result from the cache.
     * @param token The bearer token
     * @return The cached validation result, or null if not found or expired
     */
    public Boolean get(String token) {
        Boolean result = cache.getIfPresent(token);
        if (result == null) {
            logger.debug("Token not found in cache or expired");
            return null;
        }

        logger.debug("Token cache hit: valid={}", result);
        return result;
    }

    /**
     * Invalidates a token in the cache.
     * @param token The bearer token to invalidate
     */
    public void invalidate(String token) {
        cache.invalidate(token);
        logger.debug("Invalidated token cache entry");
    }

    /**
     * Clears all entries from the cache.
     */
    public void clear() {
        cache.invalidateAll();
        logger.debug("Cleared token cache");
    }

    /**
     * Gets the current cache size.
     * @return The number of entries in the cache
     */
    public long size() {
        return cache.size();
    }

    /**
     * Gets cache statistics.
     * @return Cache statistics string
     */
    public String getStats() {
        return cache.stats().toString();
    }

    /**
     * Shuts down the cache.
     * Performs cleanup of cache resources.
     */
    public void shutdown() {
        cache.invalidateAll();
        cache.cleanUp();
        logger.info("Token cache shutdown complete. Final stats: {}", getStats());
    }
}
