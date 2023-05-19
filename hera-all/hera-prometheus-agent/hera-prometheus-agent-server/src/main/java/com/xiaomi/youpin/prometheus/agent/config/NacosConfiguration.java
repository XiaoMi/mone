package com.xiaomi.youpin.prometheus.agent.config;

import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import com.xiaomi.data.push.nacos.NacosNaming;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableNacosConfig(globalProperties = @NacosProperties(serverAddr = "${nacos.config.addrs}"))
@NacosPropertySource(dataId = "prometheus_agent_open_config", autoRefreshed = true)
@ConditionalOnProperty(name = "service.selector.property", havingValue = "outer")
public class NacosConfiguration {

    @Value("${nacos.config.addrs}")
    private String nacosAddress;
    @NacosValue("${nacos.username}")
    private String username;
    @NacosValue("${nacos.password}")
    private String password;

    @Bean
    public NacosNaming nacosNaming() {
        NacosNaming nacosNaming = new NacosNaming();
        nacosNaming.setServerAddr(nacosAddress);
        nacosNaming.setUsername(username);
        nacosNaming.setPassword(password);
        nacosNaming.init();
        return nacosNaming;
    }
}