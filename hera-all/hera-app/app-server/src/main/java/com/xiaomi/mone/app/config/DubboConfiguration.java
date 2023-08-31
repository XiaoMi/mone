package com.xiaomi.mone.app.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/10/29 15:07
 */
@Configuration
public class DubboConfiguration {

    @Value("${dubbo.protocol.port}")
    private int port;

    @NacosValue(value = "${dubbo.registry.address}", autoRefreshed = true)
    private String regAddress;

    @Value("${spring.application.name}")
    private String appName;

    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(appName);
        return applicationConfig;
    }

    @Bean
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(regAddress);
        registryConfig.setDefault(true);
        return registryConfig;
    }

    @Bean
    public ProtocolConfig protocolConfig() {
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setPort(port);
        protocolConfig.setTransporter("netty4");
        protocolConfig.setThreadpool("fixed");
        protocolConfig.setThreads(200);
        return protocolConfig;
    }
}
