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

package com.xiaomi.youpin.docean.plugin.nacos;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.config.Config;

import java.util.Set;

/**
 * @author goodjava@qq.com
 * @date 2020/6/27
 */
@DOceanPlugin(order = 1)
public class NacosPlugin implements IPlugin {

    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        Config config = ioc.getBean(Config.class);
        if (config.get("close_nacos_plugin", "false").equals("true")) {
            return;
        }
        NacosConfig nacosConfig = new NacosConfig();
        nacosConfig.setDataId(config.get("nacos_config_dataid", ""));
        nacosConfig.setGroup(config.get("nacos_config_group", ""));
        nacosConfig.setServerAddr(config.get("nacos_config_server_addr", ""));
        nacosConfig.init();
        ioc.putBean(nacosConfig);
        //会覆盖基础配置(配置文件)
        nacosConfig.forEach((k, v) -> {
            ioc.putBean("$" + k, v);
            config.put(k, v);
        });

        NacosNaming nacosNaming = new NacosNaming();
        nacosNaming.setServerAddr(config.get("nacos_config_server_addr", ""));
        nacosNaming.init();

        ioc.putBean(nacosNaming);
    }
}
