package com.xiaomi.miapi.config;
 
import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import com.xiaomi.data.push.nacos.NacosNaming;
import com.xiaomi.miapi.bo.NacosInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 * we use two nacos env
 */
@Configuration
@EnableNacosConfig(globalProperties = @NacosProperties(serverAddr = "${nacos.config.addrs}"))
@NacosPropertySource(dataId = "${nacos.config.data.id}", autoRefreshed = true)
public class NacosConfiguration {

    @NacosValue("${dubbo.registry.address.st}")
    private String nacosAddressSt;

    @NacosValue(value = "${nacos.usernameSt}",autoRefreshed = true)
    private String nacosUsernameSt;

    @NacosValue(value = "${nacos.passwordSt}",autoRefreshed = true)
    private String nacosPasswordSt;

    @NacosValue("${dubbo.registry.address.ol}")
    private String nacosAddressOl;

    @NacosValue(value = "${nacos.usernameOl}",autoRefreshed = true)
    private String nacosUsernameOl;

    @NacosValue(value = "${nacos.passwordOl}",autoRefreshed = true)
    private String nacosPasswordOl;


    @Bean
    public NacosInfo nacosInfo(){
        return new NacosInfo(nacosUsernameSt,nacosPasswordSt,nacosUsernameOl,nacosPasswordOl);
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