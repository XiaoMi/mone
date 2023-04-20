package com.xiaomi.mone.monitor.service.aop.context;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @project: mimonitor
 * @author: zgf1
 * @date: 2022/1/14 15:02
 */
public class HeraRequestMappingContext {

    private static final ThreadLocal<Map<String, Object>> local = new ThreadLocal<>();

    public static void putAll(Map<String, Object> map) {
        local.set(map);
    }

    public static Map<String, Object> getAll() {
        return local.get();
    }

    public static void clearAll() {
        local.remove();
    }

    public static void set(String key, Object value) {
       Map<String,Object> map = local.get();
       if (map == null) {
           map = new HashMap<>();
           local.set(map);
       }
       map.put(key, value);
    }

    public static <T> T get(String key) {
        Map<String,Object> map = local.get();
        if (map == null) {
            return null;
        }
        return (T)map.get(key);
    }

    public static void clear(String key) {
        Map<String, Object> map = local.get();
        if (map == null) {
            return;
        }
        map.remove(key);
    }
}
