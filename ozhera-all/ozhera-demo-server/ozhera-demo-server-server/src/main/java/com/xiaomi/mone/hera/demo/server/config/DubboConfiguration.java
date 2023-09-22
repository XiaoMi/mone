package com.xiaomi.mone.hera.demo.server.config;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Dubbo Configuration
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
     * Dubbo application configuration
     */
    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(applicationName);
        applicationConfig.setQosEnable(false);
        return applicationConfig;
    }

    /**
     * Configure registry center
     */
    @Bean
    public RegistryConfig registryConfig() {

        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(dubboRegistryAddress);
        return registryConfig;
    }

    /**
     * Configure registry center
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
     * Configuration protocol
     */
    @Bean
    public ProtocolConfig protocolConfig() {
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setName("dubbo");
        //Automatic attempt
        protocolConfig.setPort(-1);
        protocolConfig.setTransporter("netty4");
        protocolConfig.setThreadpool("fixed");
        protocolConfig.setThreads(800);
        return protocolConfig;
    }

}