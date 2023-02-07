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

package com.xiaomi.youpin.docean.plugin.mvc;

import com.alibaba.nacos.api.exception.NacosException;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.common.NetUtils;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.config.Config;
import com.xiaomi.youpin.docean.plugin.nacos.NacosNaming;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * @author goodjava@qq.com
 */
@DOceanPlugin(order = 200)
@Slf4j
public class MvcPlugin implements IPlugin {

    private String serviceName;
    private String ip;
    private int port;

    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        log.info("mvc plugin init");
        Config config = ioc.getBean(Config.class);
        this.serviceName = config.get("app_name", "");

        String host = System.getenv("host.ip") == null ? NetUtils.getLocalHost() : System.getenv("host.ip");
        this.ip = host;

        this.port = Integer.parseInt(config.get("http_port", "80"));
        NacosNaming nn = ioc.getBean(NacosNaming.class.getName(), null);
        if (null != nn) {
            try {
                nn.registerInstance(this.serviceName, this.ip, this.port);
            } catch (NacosException e) {
                log.error(e.getMessage());
            }
        }
    }


    @Override
    public void destory(Ioc ioc) {
        NacosNaming nn = ioc.getBean(NacosNaming.class.getName(), null);
        if (null != nn) {
            try {
                nn.deregisterInstance(this.serviceName, this.ip, this.port);
            } catch (NacosException e) {
                log.error(e.getMessage());
            }
        }
    }
}
