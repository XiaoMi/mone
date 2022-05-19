package com.xiaomi.youpin.docean.plugin.sentinel;

import com.alibaba.csp.sentinel.util.StringUtil;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResourceMetadataRegistry {
    private static final Map<String, MethodWrapper> FALLBACK_MAP = new ConcurrentHashMap();
    private static final Map<String, MethodWrapper> DEFAULT_FALLBACK_MAP = new ConcurrentHashMap();
    private static final Map<String, MethodWrapper> BLOCK_HANDLER_MAP = new ConcurrentHashMap();

    ResourceMetadataRegistry() {
    }

    static MethodWrapper lookupFallback(Class<?> clazz, String name) {
        return (MethodWrapper)FALLBACK_MAP.get(getKey(clazz, name));
    }

    static MethodWrapper lookupDefaultFallback(Class<?> clazz, String name) {
        return (MethodWrapper)DEFAULT_FALLBACK_MAP.get(getKey(clazz, name));
    }

    static MethodWrapper lookupBlockHandler(Class<?> clazz, String name) {
        return (MethodWrapper)BLOCK_HANDLER_MAP.get(getKey(clazz, name));
    }

    static void updateFallbackFor(Class<?> clazz, String name, Method method) {
        if (clazz != null && !StringUtil.isBlank(name)) {
            FALLBACK_MAP.put(getKey(clazz, name), MethodWrapper.wrap(method));
        } else {
            throw new IllegalArgumentException("Bad argument");
        }
    }

    static void updateDefaultFallbackFor(Class<?> clazz, String name, Method method) {
        if (clazz != null && !StringUtil.isBlank(name)) {
            DEFAULT_FALLBACK_MAP.put(getKey(clazz, name), MethodWrapper.wrap(method));
        } else {
            throw new IllegalArgumentException("Bad argument");
        }
    }

    static void updateBlockHandlerFor(Class<?> clazz, String name, Method method) {
        if (clazz != null && !StringUtil.isBlank(name)) {
            BLOCK_HANDLER_MAP.put(getKey(clazz, name), MethodWrapper.wrap(method));
        } else {
            throw new IllegalArgumentException("Bad argument");
        }
    }

    private static String getKey(Class<?> clazz, String name) {
        return String.format("%s:%s", clazz.getCanonicalName(), name);
    }

    static void clearFallbackMap() {
        FALLBACK_MAP.clear();
    }

    static void clearBlockHandlerMap() {
        BLOCK_HANDLER_MAP.clear();
    }
}
