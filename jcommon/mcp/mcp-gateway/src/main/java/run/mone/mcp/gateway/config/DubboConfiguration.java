package run.mone.mcp.gateway.config;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.apache.dubbo.common.constants.CommonConstants.REMOTE_METADATA_STORAGE_TYPE;

@Configuration
@PropertySource("classpath:/spring/dubbo-provider.properties")
public class DubboConfiguration {

    @Value("${dubbo.protocol.port}")
    private int port;

    @Value("${server.port:8081}")
    private String httpGateWayPort;

    @Value("${dubbo.registry.address}")
    private String regAddress;

    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setQosEnable(false);
        applicationConfig.setSerializeCheckStatus("WARN");
        applicationConfig.setName("mcp-gateway-st-mason");
        applicationConfig.setRegisterMode(CommonConstants.INTERFACE_REGISTER_MODE);
        applicationConfig.setEnableFileCache(false);
        applicationConfig.setMetadataType(REMOTE_METADATA_STORAGE_TYPE);
        Map<String, String> m = new HashMap<>();
        m.put("nacos.subscribe.legacy-name", "true");
        applicationConfig.setParameters(m);
        return applicationConfig;
    }

    @Bean
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(regAddress);
        return registryConfig;
    }
}
