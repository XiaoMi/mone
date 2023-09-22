package com.xiaomi.hera.trace.etl.config;

import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableNacosConfig(globalProperties = @NacosProperties(serverAddr = "${nacos.address}"))
@NacosPropertySource(dataId = "hera_trace_config", autoRefreshed = true)
public class NacosConfiguration {
}