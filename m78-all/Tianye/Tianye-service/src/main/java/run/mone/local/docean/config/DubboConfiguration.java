package run.mone.local.docean.config;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.RegistryConfig;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import com.xiaomi.youpin.docean.anno.Bean;
import com.xiaomi.youpin.docean.anno.Configuration;

@Configuration
public class DubboConfiguration {

    @Value("${dubbo_app_name}")
    private String appName;

    @Value("${dubbo_reg_address}")
    private String regAddress;

    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(appName);
        applicationConfig.setParameters(Maps.<String, String>newHashMap());
        String prometheusPort = System.getenv("PROMETHEUS_PORT");
        if (StringUtils.isEmpty(prometheusPort)) {
            prometheusPort = "4444";
        }
        applicationConfig.getParameters().put("prometheus_port", prometheusPort);
        applicationConfig.setQosEnable(false);
        return applicationConfig;
    }

    @Bean
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(regAddress);
        return registryConfig;
    }


}
