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

package run.mone.docean.plugin.redissession;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.redis.Redis;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @author shanwb
 * @date 2024-09-03
 */
@DOceanPlugin(order = 101)
@Slf4j
public class RedisSessionPlugin implements IPlugin {


    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        Redis redis = ioc.getBean(Redis.class);
        if (null == redis) {
            log.error("redis can not be empty");
        }
        RedisSessionStore redisSessionStore = new RedisSessionStore(redis);
        ioc.putBean("ClusterSessionStore", redisSessionStore);
    }

}
