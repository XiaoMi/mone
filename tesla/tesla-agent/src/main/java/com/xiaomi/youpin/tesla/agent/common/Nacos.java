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

package com.xiaomi.youpin.tesla.agent.common;

import com.xiaomi.data.push.nacos.NacosConfig;

import java.util.Optional;

/**
 * @author goodjava@qq.com
 */
public class Nacos {

    private NacosConfig config;

    private Nacos() {
        config = new NacosConfig();
        config.setServerAddr(Config.ins().get("nacos_addrs", ""));
        config.setDataId("tesla-agent");
        config.setGroup("DEFAULT_GROUP");
        config.init();
    }


    private static final class NacosLazyHolder {
        private static final Nacos ins = new Nacos();
    }


    public static Nacos ins() {
        return NacosLazyHolder.ins;
    }


    public String get(String key, String defaultValue) {
        String value = config.getConfig(key);
        return Optional.ofNullable(value).orElse(defaultValue);
    }

}
