package com.xiaomi.mone.app.config;

import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import com.xiaomi.data.push.nacos.NacosConfig;
import com.xiaomi.data.push.nacos.NacosNaming;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/10/29 11:45
 */
@Configuration
@EnableNacosConfig(globalProperties = @NacosProperties(serverAddr = "${nacos.config.addrs}"))
@NacosPropertySource(dataId = "hera_app_config", autoRefreshed = true)
public class NacosConfiguration {

    @Value("${nacos.config.addrs}")
    private String nacosAddress;

    @Bean
    public NacosConfig getNacosConfig() {
        NacosConfig config = new NacosConfig();
        config.setServerAddr(nacosAddress);
        config.init();
        return config;
    }

    @Bean
    public NacosNaming getNacosNaming() {
        NacosNaming nacosNaming = new NacosNaming();
        nacosNaming.setServerAddr(nacosAddress);
        nacosNaming.init();
        return nacosNaming;
    }
}
