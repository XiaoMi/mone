package com.xiaomi.youpin.prometheus.agent.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.common.collect.Maps;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DubboConfiguration {

    @Value("${dubbo.protocol.port}")
    private int port;

    @Value("${server.port}")
    private String httpGateWayPort;

    @NacosValue("${dubbo.registry.address}")
    private String regAddress;

    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName("Prometheus-agent");
        applicationConfig.setParameters(Maps.newHashMap());
        applicationConfig.getParameters().put("http_gateway_port", httpGateWayPort);
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
        protocolConfig.setThreads(800);
        return protocolConfig;
    }

}
