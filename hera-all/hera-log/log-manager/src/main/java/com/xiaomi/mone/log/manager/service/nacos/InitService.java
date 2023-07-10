/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.service.nacos;

import com.xiaomi.data.push.nacos.NacosNaming;
import com.xiaomi.mone.log.common.NetUtils;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
@Slf4j
public class InitService {

    @Resource
    private NacosNaming nacosNaming;

    @Value(value = "$serverNameHttp", defaultValue = "")
    private String serverNameHttp;

    @Value(value = "$serverPort", defaultValue = "")
    private String httpPort;


    @Value(value = "$dubbo.group", defaultValue = "")
    private String group;

    @PostConstruct
    public void init() {
        String host = System.getenv("host.ip") == null ? NetUtils.getLocalHost() : System.getenv("host.ip");
        final String port = httpPort;
        final String appName = serverNameHttp;
        try {
            nacosNaming.registerInstance(appName, host, Integer.valueOf(port), group);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    System.out.println("stop");
                    nacosNaming.deregisterInstance(appName, host, Integer.valueOf(port), group);
                } catch (Exception e) {
                    log.error("init service err:{}", e.getMessage(), e);
                }
            }));
        } catch (Exception e) {
            log.error("init service err:{}", e.getMessage(), e);
        }
    }
}
