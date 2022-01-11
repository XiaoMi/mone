/*
 *  Copyright 2020 Xiaomi
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

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
