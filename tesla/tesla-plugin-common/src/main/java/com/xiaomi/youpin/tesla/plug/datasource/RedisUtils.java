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

package com.xiaomi.youpin.tesla.plug.datasource;

import com.xiaomi.data.push.redis.Redis;

/**
 * @author goodjava@qq.com
 */
public abstract class RedisUtils {

    public static Redis createRedis(String redisHosts, String serverType) {
        Redis redis = new Redis();
        redis.setRedisHosts(redisHosts);
        redis.setServerType(serverType);
        redis.setCatEnabled(false);
        redis.setRedisPwd("");
        redis.init();
        return redis;
    }
}
