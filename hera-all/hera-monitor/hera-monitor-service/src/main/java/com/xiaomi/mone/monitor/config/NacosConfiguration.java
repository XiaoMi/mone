package com.xiaomi.mone.monitor.config;

import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * @author gaoxihui
 * @date 2021/7/6 2:13 下午
 */

@Configuration
@EnableNacosConfig(globalProperties = @NacosProperties(serverAddr = "${nacos.config.addrs}"))
@NacosPropertySource(dataId = "mimonitor_open_config", autoRefreshed = true)
@ConditionalOnProperty(name = "service.selector.property", havingValue = "outer")
public class NacosConfiguration {
}
