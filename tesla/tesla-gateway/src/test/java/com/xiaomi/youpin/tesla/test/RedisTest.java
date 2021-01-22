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

package com.xiaomi.youpin.tesla.test;

import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.youpin.gateway.common.Keys;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class RedisTest {


    private Redis redis() {
        Redis redis = new Redis();
        redis.setServerType("staging");
        redis.setRedisHosts("redis-mijia-spam-mijia-id.marathon.mesos-staging:11858");
        redis.init();
        return redis;
    }


    @Test
    public void testSet() throws IOException {


//        redis().lpush(Keys.systemFilterListKey(),"tesla-filter-demo-1.0.0-SNAPSHOT.jar");
        redis().set(Keys.systemFilterKey("tesla-filter-demo-1.0.0-SNAPSHOT.jar"), Files.readAllBytes(Paths.get("/tmp/filter/tesla-filter-demo-1.0.0-SNAPSHOT.jar")),2000000);
    }


    @Test
    public void testGet() {
        Redis redis = redis();
        List<String> list = redis.lrange(Keys.systemFilterSetKey());
        System.out.println(list);
        list.stream().forEach(it->{
            byte[] data = redis.getBytes(Keys.systemFilterKey(it));
            System.out.println(Arrays.toString(data));
        });
    }
}
