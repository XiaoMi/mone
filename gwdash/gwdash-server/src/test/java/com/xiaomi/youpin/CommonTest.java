/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.xiaomi.youpin.gwdash.common.LockUtils;
import lombok.Data;
import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.Test;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CommonTest {

    public static class C {
        public Integer i;
    }

    class A {
        public C b;
    }




    @Test
    public void testOptional() {
        C b = new C();
        b.i = 123;
        A a = new A();
        a.b = b;
        int i = Optional.ofNullable(a).map(it -> it.b).map(it -> it.i).orElse(0);
        System.out.println(i);

    }


    @Test
    public void testLimit() {
        System.out.println(Stream.of(1, 2, 34).limit(1).collect(Collectors.toList()));
    }


    @Test
    public void testMap() {
        ConcurrentHashMap<String, Boolean> m = new ConcurrentHashMap<>();
        System.out.println(m.putIfAbsent("a", true));
        System.out.println(m.putIfAbsent("a", true));
    }


    @Test
    public void testString() {
        String tml = "'$.%s'";
        String str = String.format(tml, "key");
        System.out.println(str);
    }


    @Data
    class B {
        Integer i;

        public B(Integer i) {
            this.i = i;
        }
    }


    @Test
    public void testSort() {
        System.out.println(Stream.of(new B(1), new B(2), new B(3)).sorted((a, b) -> b.i.compareTo(a.i)).collect(Collectors.toList()));

    }


    @Test
    public void testUrl() {
        String url = "http://%s/health";
        System.out.println(String.format(url, "127.0.0.1:8080"));


        String url2 = "dubbo://%s/com.xiaomi.youpin.Test/staging/health";
        System.out.println(String.format(url2, "127.0.0.1"));

    }

    @Test
    public void testUrl2() {

    }


    @Test
    public void testCache() throws ExecutionException, InterruptedException {
        Cache<String,String> cache= CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.SECONDS).build();
        String s = cache.get("name",() -> {
            System.out.println("load");
            return "abc";
        });
        System.out.println(s);

        System.out.println(cache.getIfPresent("name"));
        TimeUnit.SECONDS.sleep(4);
        System.out.println(cache.getIfPresent("name"));

    }
}
