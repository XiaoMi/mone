package com.xiaomi.miapi.config;
 
import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import com.xiaomi.data.push.nacos.NacosNaming;
import com.xiaomi.miapi.common.bo.NacosInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableNacosConfig(globalProperties = @NacosProperties(serverAddr = "${nacos.config.addrs}"))
@NacosPropertySource(dataId = "${nacos.config.data.id}", autoRefreshed = true)
public class NacosConfiguration {

    @NacosValue("${dubbo.registry.address.st}")
    private String nacosAddressSt;

    @NacosValue(value = "${nacos.usernameSt}",autoRefreshed = true)
    private String nacosUsernameCnSt;

    @NacosValue(value = "${nacos.passwordSt}",autoRefreshed = true)
    private String nacosPasswordCnSt;

    @NacosValue("${dubbo.registry.address.ol}")
    private String nacosAddressOl;

    @NacosValue(value = "${nacos.usernameOl}",autoRefreshed = true)
    private String nacosUsernameCnOl;

    @NacosValue(value = "${nacos.passwordOl}",autoRefreshed = true)
    private String nacosPasswordCnOl;

    @Bean
    public NacosInfo nacosInfo(){
        return new NacosInfo(nacosUsernameCnSt,nacosPasswordCnSt,nacosUsernameCnOl,nacosPasswordCnOl);
    }

    @Bean
    public NacosNaming nacosNamingSt() {
        NacosNaming nacosNaming = new NacosNaming();
        String[] address = nacosAddressSt.split("//");
        nacosNaming.setServerAddr(address[1]);
        nacosNaming.init();
        return nacosNaming;
    }

    @Bean
    public NacosNaming nacosNamingOl() {
        NacosNaming nacosNaming = new NacosNaming();
        String[] address = nacosAddressOl.split("//");
        nacosNaming.setServerAddr(address[1]);
        nacosNaming.init();
        return nacosNaming;
    }

}