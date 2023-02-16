package com.xiaomi.youpin.prometheus.agent.config;

import com.xiaomi.data.push.nacos.NacosNaming;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NacosConfiguration {

    @Value("${nacos.config.addrs}")
    private String nacosAddress;
    @Value("${nacos.username}")
    private String username;
    @Value("${nacos.password}")
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