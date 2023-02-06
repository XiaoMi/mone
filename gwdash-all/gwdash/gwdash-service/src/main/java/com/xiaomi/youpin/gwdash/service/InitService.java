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

package com.xiaomi.youpin.gwdash.service;


import com.xiaomi.data.push.nacos.NacosNaming;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.NetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author goodjava@qq.com
 */
@Service
@Slf4j
public class InitService {

    @Autowired
    private NacosNaming nacosNaming;

    @Value("${server.port}")
    private String httpPort;

    @Value("${dubbo.group}")
    private String group;


    @PostConstruct
    public void init() {
        try {
            String host = System.getenv("host.ip") == null ? NetUtils.getLocalHost() : System.getenv("host.ip");
            log.info("InitService.host:{}", host);
            final String port = httpPort;
            final String appName = "mione_api";
            nacosNaming.registerInstance(appName, host, Integer.valueOf(port), group);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    log.info("shutdown hook init service");
                    nacosNaming.deregisterInstance(appName, host, Integer.valueOf(port), group);
                } catch (Exception e) {
                    //ignore
                }
            }));
        } catch (Exception e) {
            log.warn("error:{}", e.getMessage());
        }
    }

}
