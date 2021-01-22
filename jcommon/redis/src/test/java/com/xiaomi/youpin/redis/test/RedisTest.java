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

package com.xiaomi.youpin.redis.test;

import com.xiaomi.data.push.redis.Redis;
import org.junit.Test;

public class RedisTest {


    @Test
    public void testNx() {
        Redis redis = new Redis();
        redis.setServerType("dev");
        redis.setRedisHosts("127.0.0.1:6379");
        redis.init();
        Long v = redis.setNx("name1", "zzy");
        System.out.println(v);
    }

    @Test
    public void testSet() {
        Redis redis = new Redis();
        redis.setServerType("dev");
        redis.setRedisHosts("127.0.0.1:6379");
        redis.init();
        //单位毫秒
        String v = redis.set("name11", "zzy", 15000);
        System.out.println(v);
    }


    @Test
    public void testSetXX() {
        Redis redis = new Redis();
        redis.setServerType("dev");
        redis.setRedisHosts("127.0.0.1:6379");
        redis.init();
        //单位毫秒
        String v = redis.set("name11", "zzy", "XX", 15000);
        System.out.println(v);
    }
}
