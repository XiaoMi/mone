package com.xiaomi.mone.monitor.config;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gaoxihui
 * @date 2021/7/6 2:19 下午
 */
@Configuration
public class DubboConfiguration {

        @Value("${dubbo.protocol.port}")
        private int port;

        @Value("${dubbo.registry.address}")
        private String regAddress;

        @Value("${dubbo.registry.address.youpin}")
        private String regAddressYoupin;

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
        public RegistryConfig registryConfigYoupin() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(regAddressYoupin);
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
