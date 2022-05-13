package com.xiaomi.mone.grpc.common;

import com.xiaomi.data.push.nacos.NacosNaming;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NacosConfiguration {
    @Value("${dubbo.registry.addrs}")
    private static String nacosAddress;

    @Bean
    public static NacosNaming nacosNaming() {
        NacosNaming nacosNaming = new NacosNaming();
        String address = nacosAddress.split("//")[1];
        nacosNaming.setServerAddr(address);
        nacosNaming.init();
        return nacosNaming;
    }
}