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

package com.xiaomi.youpin.docean.plugin.redis;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.config.Config;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @author goodjava@qq.com
 * @date 2020/6/27
 */
@DOceanPlugin
@Slf4j
public class RedisPlugin implements IPlugin {

    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        Redis redis = new Redis();
        Config config = ioc.getBean(Config.class);
        redis.setRedisHosts(config.get("redis_hosts", ""));
        redis.setServerType(config.get("redis_type", ""));
        redis.init();
        ioc.putBean(redis);
    }

    public void add(RedisDsConfig config) {
        Redis redis = new Redis();
        redis.setRedisHosts(config.getHosts());
        redis.setServerType(config.getType());
        redis.init();
        Ioc.ins().putBean("redis:" + config.getName(), redis);
    }


    public void remove(RedisDsConfig config) {
        Redis redis = Ioc.ins().getBean("redis:" + config.getName());
        redis.close();
        Ioc.ins().remove("redis:" + config.getName());
    }

}
