package com.xiaomi.mone.tpc.config;

import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import com.xiaomi.data.push.nacos.NacosNaming;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableNacosConfig(globalProperties = @NacosProperties(serverAddr = "${nacos.config.addrs}"))
@NacosPropertySource(dataId = "mi-tpc-login", autoRefreshed = true)
public class NacosConfiguration {

    @Value("${nacos.config.addrs}")
    private String nacosAddress;

    @Bean
    public NacosNaming nacosNaming() {
        NacosNaming nacosNaming = new NacosNaming();
        nacosNaming.setServerAddr(nacosAddress);
        nacosNaming.init();
        return nacosNaming;
    }

}