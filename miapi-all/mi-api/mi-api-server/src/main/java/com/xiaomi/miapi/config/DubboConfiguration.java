package com.xiaomi.miapi.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ProviderConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Configuration
public class DubboConfiguration {

	@Value("${spring.application.name}")
	private String applicationName;

	/**
	 * staging dubbo address
	 */
	@NacosValue("${dubbo.registry.address.st}")
	private String stDubboRegistryAddress;

	/**
	 * online dubbo address
	 */
	@NacosValue("${dubbo.registry.address.ol}")
	private String olDubboRegistryAddress;

	/**
	 * the dubbo service group you provide
	 */
	@Value("${dubbo.provider.group}")
	private String dubboProviderGroup;

	@Value("${is.online}")
	private boolean isOnline;

	@Bean
	public ApplicationConfig applicationConfig() {
		ApplicationConfig applicationConfig = new ApplicationConfig();
		applicationConfig.setName(applicationName);
		applicationConfig.setQosEnable(false);
		return applicationConfig;
	}

	/**
	 * staging dubbo service registry
	 * we use the filed flag 'isOnline' to distinguish the
	 * default dubbo env we use
	 */
	@Bean
	public RegistryConfig stRegistry() {
		RegistryConfig registryConfig = new RegistryConfig();
		registryConfig.setAddress(stDubboRegistryAddress);
		registryConfig.setDefault(!isOnline);
		return registryConfig;
	}

	@Bean
	public RegistryConfig olRegistry() {

		RegistryConfig registryConfig = new RegistryConfig();
		registryConfig.setAddress(olDubboRegistryAddress);
		registryConfig.setDefault(isOnline);
		return registryConfig;
	}

	@Bean
	public ProviderConfig providerConfig() {
		ProviderConfig providerConfig = new ProviderConfig();
		providerConfig.setGroup(dubboProviderGroup);
		providerConfig.setVersion("1.0");
		providerConfig.setTimeout(1000);
		return providerConfig;
	}

	@Bean
	public ProtocolConfig protocolConfig() {
		ProtocolConfig protocolConfig = new ProtocolConfig();
		protocolConfig.setName("dubbo");
		protocolConfig.setPort(-1);
		protocolConfig.setTransporter("netty4");
		protocolConfig.setThreadpool("fixed");
		protocolConfig.setThreads(200);
		return protocolConfig;
	}

}
