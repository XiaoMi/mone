package com.xiaomi.mock.config;
 
import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import com.xiaomi.data.push.nacos.NacosNaming;
import com.xiaomi.youpin.docean.anno.Bean;
import com.xiaomi.youpin.docean.anno.Configuration;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;

@Configuration
@EnableNacosConfig(globalProperties = @NacosProperties(serverAddr = "${nacos.config.addrs}"))
@NacosPropertySource(dataId = "mock_server_config", autoRefreshed = true)
public class NacosConfiguration {

    @Value("${dubbo.registry.addrs}")
    private String nacosAddress = "nacos://127.0.0.1:80";
 
    @Bean
    public NacosNaming nacosNaming() {
        NacosNaming nacosNaming = new NacosNaming();
        String address = nacosAddress.split("//")[1];
        nacosNaming.setServerAddr(address);
        nacosNaming.init();
        return nacosNaming;
    }
}