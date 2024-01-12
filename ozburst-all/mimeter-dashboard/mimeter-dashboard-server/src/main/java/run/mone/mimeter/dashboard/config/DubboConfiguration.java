package run.mone.mimeter.dashboard.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DubboConfiguration {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${dubbo.protocol.port}")
    private int port;

    @Value("${server.port}")
    private String httpGateWayPort;

    @NacosValue("${dubbo.registry.address.st}")
    private String stDubboRegistryAddress;

    @NacosValue("${dubbo.registry.address.ol}")
    private String olDubboRegistryAddress;

    @Value("${is.online}")
    private boolean isOnline;

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
     * 配置st注册中心
     */
    @Bean
    @Primary
    public RegistryConfig stRegistry() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(stDubboRegistryAddress);
        registryConfig.setDefault(!isOnline);
        return registryConfig;
    }

    /**
     * 配置ol注册中心
     */
    @Bean
    public RegistryConfig olRegistry() {

        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(olDubboRegistryAddress);
        registryConfig.setDefault(isOnline);
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
