package run.mone.mimeter.dashboard.config;

import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import com.xiaomi.data.push.nacos.NacosNaming;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import run.mone.mimeter.dashboard.bo.NacosInfo;

@Configuration
@EnableNacosConfig(globalProperties = @NacosProperties(serverAddr = "${nacos.config.addrs}"))
@NacosPropertySource(dataId = "${nacos.config.data.id}", autoRefreshed = true)
public class NacosConfiguration {
    /**
     * 中国区st nacos
     */
    @NacosValue("${dubbo.registry.address.st}")
    private String nacosAddressSt;

    @NacosValue(value = "${nacos.usernameSt}")
    private String nacosUsernameCnSt;

    @NacosValue(value = "${nacos.passwordSt}")
    private String nacosPasswordCnSt;

    /**
     * 中国区线上 nacos
     */
    @NacosValue("${dubbo.registry.address.ol}")
    private String nacosAddressOl;

    @NacosValue(value = "${nacos.usernameOl}")
    private String nacosUsernameCnOl;

    @NacosValue(value = "${nacos.passwordOl}")
    private String nacosPasswordCnOl;

    @Bean
    public NacosInfo nacosInfo(){
        return new NacosInfo(nacosUsernameCnSt,nacosPasswordCnSt,nacosUsernameCnOl,nacosPasswordCnOl);
    }

    /**
     * 中国区st nacos
     * @return
     */
    @Bean
    @Primary
    public NacosNaming nacosNamingSt() {
        NacosNaming nacosNaming = new NacosNaming();
        String[] address = nacosAddressSt.split("//");
        nacosNaming.setServerAddr(address[1]);
        nacosNaming.init();
        return nacosNaming;
    }

    /**
     * 中国区线上nacos
     * @return
     */
    @Bean
    public NacosNaming nacosNamingOl() {
        NacosNaming nacosNaming = new NacosNaming();
        String[] address = nacosAddressOl.split("//");
        nacosNaming.setServerAddr(address[1]);
        nacosNaming.init();
        return nacosNaming;
    }
}