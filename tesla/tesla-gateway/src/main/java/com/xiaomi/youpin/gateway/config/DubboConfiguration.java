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

package com.xiaomi.youpin.gateway.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Maps;
import com.xiaomi.youpin.gateway.protocol.dubbo.DubboClient;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author goodjava@qq.com
 */
@Configuration
public class DubboConfiguration {

    @Value("${dubbo.protocol.port}")
    private int port;


    @Value("${server.port}")
    private String httpGateWayPort;


    @Value("${dubbo.registry.address}")
    private String regAddress;

    @Value("${server.port}")
    private int httpPort;

    @NacosValue(value = "${dubboPoolSize:50}", autoRefreshed = true)
    private int dubboPoolSize = 50;


    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("tesla-gateway");
        applicationConfig.setParameters(Maps.newHashMap());
        applicationConfig.setQosEnable(false);
        return applicationConfig;
    }

    @Bean
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(regAddress);
        return registryConfig;
    }

    @Bean
    public ProtocolConfig protocolConfig() {
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setPort(port);
        protocolConfig.setTransporter("netty4");
        protocolConfig.setThreadpool("fixed");
        protocolConfig.setThreads(dubboPoolSize);
        return protocolConfig;
    }

    @Bean
    protected DubboClient dubboClient(ApplicationConfig applicationConfig,
                                      RegistryConfig registryConfig) {
        return new DubboClient(applicationConfig, registryConfig);
    }


    public int getHttpPort() {
        return httpPort;
    }
}
