package run.mone.moon.config;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.MetadataReportConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.spring.ConfigCenterBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import static org.apache.dubbo.common.constants.CommonConstants.REMOTE_METADATA_STORAGE_TYPE;

/**
 * description DubboConfiguration
 *
 * @author lizhao
 * @date 2021/6/2 11:59
 */
@Slf4j
@Order(-1)
@Configuration
public class DubboConfiguration {
    
    @Value("${dubbo.protocol.port}")
    private int port;
    
    @Value("${server.port}")
    private String httpGateWayPort;
    
    @Value("${dubbo.registry.address}")
    private String regAddress;

    @Value("${dubbo.metadata-report.address}")
    private String metadataReportAddress;

    @Value("${app.name}")
    private String appName;

    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setQosEnable(false);
        //老业务为了防止兼容风险，建议设置；新项目不建议设置
        applicationConfig.setSerializeCheckStatus("WARN");
        applicationConfig.setName(this.appName);
        applicationConfig.setRegisterMode(CommonConstants.DEFAULT_REGISTER_MODE);
        //不使用文件缓存,meta的信息也不缓存了
        applicationConfig.setEnableFileCache(false);
        applicationConfig.setMetadataType(REMOTE_METADATA_STORAGE_TYPE);

        applicationConfig.setParameters(Maps.newHashMap());
        applicationConfig.getParameters().put("http_gateway_port", this.httpGateWayPort);
        //必需设置兼容老版本订阅
        applicationConfig.getParameters().put("nacos.subscribe.legacy-name","true");
        applicationConfig.getParameters().put("prefer.serialization", "hessian2");
        return applicationConfig;
    }
    
    @Bean
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(regAddress);
        return registryConfig;
    }
    
    @Bean
    public ProtocolConfig protocolConfig() {
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setPort(port);
        protocolConfig.setTransporter("netty4");
        protocolConfig.setThreadpool("fixed");
        protocolConfig.setThreads(800);
        return protocolConfig;
    }

    @Bean
    public MetadataReportConfig metadataReportConfig(){
        MetadataReportConfig metadataConfig = new MetadataReportConfig();
        metadataConfig.setAddress(metadataReportAddress);
        return metadataConfig;
    }

    @Bean
    public ConfigCenterBean configCenterConfig() {
        ConfigCenterBean config = new ConfigCenterBean();
        config.setAddress(metadataReportAddress);
        return config;
    }
}
