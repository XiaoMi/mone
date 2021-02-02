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

package com.xiaomi.youpin.gateway.service;

import com.xiaomi.data.push.nacos.NacosNaming;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;

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

    @Value("${init.group}")
    private String group;


    @PostConstruct
    public void init() {
        final String host = Optional.ofNullable(System.getenv("host.ip")).orElse(com.xiaomi.youpin.gateway.common.NetUtils.getLocalHost());
        final String port = httpPort;
        final String appName = "tesla";
        log.info("init service execute appName:{} host:{}", appName, host);
        try {
            nacosNaming.registerInstance(appName, host, Integer.valueOf(port), group);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> shtudownHook(appName, host, port)));
        } catch (Exception e) {
            log.warn("init service error:{}", e.getMessage());
        }
    }


    private void shtudownHook(String appName, String host, String port) {
        log.info("shutdown hook deregister instance");
        try {
            System.out.println("stop");
            nacosNaming.deregisterInstance(appName, host, Integer.valueOf(port), group);
        } catch (Exception e) {
            log.warn("shutdown hook error:{}", e.getMessage());
        }
    }


}
