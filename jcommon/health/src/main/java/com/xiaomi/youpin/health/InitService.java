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

package com.xiaomi.youpin.health;

import com.xiaomi.data.push.nacos.NacosNaming;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.Optional;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class InitService {

    @Autowired
    private NacosNaming nacosNaming;

    @Value("${server.port}")
    private String httpPort;

    @Value("${server.group}")
    private String group;

    @Value("${server.name}")
    private String appName;

    @PostConstruct
    public void init() {
        String host = Optional.ofNullable(System.getenv("host.ip")).orElse(HNetUtils.getLocalHost());
        log.info("InitService.host:{} {} {} {}", host, appName, group, httpPort);
        final String port = httpPort;
        try {
            nacosNaming.registerInstance(appName, host, Integer.valueOf(port), group);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    nacosNaming.deregisterInstance(appName, host, Integer.valueOf(port), group);
                } catch (Exception e) {
                    log.warn("error:{}", e.getMessage());
                }
            }));
        } catch (Exception e) {
            log.warn("error:{}", e.getMessage());
        }
    }

}
