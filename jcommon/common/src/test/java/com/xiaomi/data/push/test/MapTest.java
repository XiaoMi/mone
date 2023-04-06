package com.xiaomi.data.push.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 2023/3/6 16:50
 */
public class MapTest {

    @Test
    public void testMap() {
        ConcurrentHashMap<String, Integer> m = new ConcurrentHashMap<>();
        IntStream.range(0, 100000).parallel().forEach(i -> {
            m.compute("a", (k, v) -> {
                if (v == null) {
                    return 1;
                }
                return v + 1;
            });
        });
        System.out.println(m);
        Assert.assertEquals(100000, (Object) m.get("a"));
    }
}
