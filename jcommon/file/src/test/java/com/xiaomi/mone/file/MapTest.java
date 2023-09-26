package com.xiaomi.mone.file;

import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author goodjava@qq.com
 * @date 2023/9/26 10:00
 */
public class MapTest {


    @Test
    public void testMap() {
        ConcurrentHashMap<String, String> m = new ConcurrentHashMap<>();
        String v = m.computeIfAbsent("a", k -> "1");
        System.out.println(v);
        System.out.println(m.computeIfAbsent("a", k -> "2"));
    }

}
