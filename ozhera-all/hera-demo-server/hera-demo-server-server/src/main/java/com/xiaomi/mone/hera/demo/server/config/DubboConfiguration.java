package com.xiaomi.mone.hera.demo.server.config;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Dubbo 配置
 */
@Configuration
public class DubboConfiguration {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${dubbo.registry.address}")
    private String dubboRegistryAddress;

    @Value("${dubbo.group}")
    private String dubboProviderGroup;

    /**
     * Dubbo应用配置
     */
    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(applicationName);
        applicationConfig.setQosEnable(false);
        return applicationConfig;
    }

    /**
     * 配置注册中心
     */
    @Bean
    public RegistryConfig registryConfig() {

        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(dubboRegistryAddress);
        return registryConfig;
    }

    /**
     * 配置注册中心
     */
    @Bean
    public ProviderConfig providerConfig() {

        ProviderConfig providerConfig = new ProviderConfig();
        providerConfig.setGroup(dubboProviderGroup);
        providerConfig.setVersion("1.0");
        providerConfig.setTimeout(1000);
        return providerConfig;
    }

    /**
     * 配置协议
     */
    @Bean
    public ProtocolConfig protocolConfig() {
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setName("dubbo");
        //自动尝试
        protocolConfig.setPort(-1);
        protocolConfig.setTransporter("netty4");
        protocolConfig.setThreadpool("fixed");
        protocolConfig.setThreads(800);
        return protocolConfig;
    }

}