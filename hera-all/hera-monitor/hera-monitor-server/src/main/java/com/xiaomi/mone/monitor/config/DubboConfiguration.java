package com.xiaomi.mone.monitor.config;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gaoxihui
 * @date 2021/7/6 2:19 下午
 */
@Configuration
@ConditionalOnProperty(name = "service.selector.property", havingValue = "outer")
public class DubboConfiguration {

        @Value("${dubbo.protocol.port}")
        private int port;

        @Value("${dubbo.registry.address}")
        private String regAddress;

        @Bean
        public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("mimonitor");
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
