package run.mone.mcp.hera.analysis.config;

import com.google.common.collect.Maps;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import static org.apache.dubbo.common.constants.CommonConstants.REMOTE_METADATA_STORAGE_TYPE;

import java.util.HashMap;
import java.util.Map;

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
        applicationConfig.setSerializeCheckStatus("WARN");//老业务为了防止兼容风险，建议设置；新项目不建议设置
        applicationConfig.setName("mcp-hera-analysis");
        //注册方式，默认：接口和应用维度
        //dubbo.application.register-mode 为 instance（只注册应用级）、all（接口级+应用级均注册）开启全局的注册开关、interface（只注册接口级）
        applicationConfig.setRegisterMode(CommonConstants.INTERFACE_REGISTER_MODE);
        //不使用文件缓存,meta的信息也不缓存了
        applicationConfig.setEnableFileCache(false);
        applicationConfig.setMetadataType(REMOTE_METADATA_STORAGE_TYPE);
        Map<String, String> m = new HashMap<>();
        //必需设置兼容老版本订阅，订阅服务名的拼接规则需要兼容
        m.put("nacos.subscribe.legacy-name","true");
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
