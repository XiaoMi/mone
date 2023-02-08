package com.xiaomi.mone.grpc;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 1/2/21
 */
public class CommonTest {

    @Test
    public void testMap() {
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        IntStream.range(0,100000).parallel().forEach(i->{
            map.compute("name", (k, v) -> {
                System.out.println(v);
                if (null == v) {
                    return 1;
                }
                return v+1;
            });
        });
        System.out.println(map);
    }

    @Test
    public void testClassLoader() throws ClassNotFoundException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        System.out.println(cl.loadClass("a.b.c.A"));
    }


    @Test
    public void testSet() {
        LinkedHashSet<String> l = new LinkedHashSet<>();
        l.add("b");
        l.add("a");
        System.out.println(l);
    }
}
