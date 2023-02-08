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
 * Dubbo 配置
 */
@Configuration
public class DubboConfiguration {

	@Value("${spring.application.name}")
	private String applicationName;

	@NacosValue("${dubbo.registry.address.st}")
	private String stDubboRegistryAddress;

	@NacosValue("${dubbo.registry.address.ol}")
	private String olDubboRegistryAddress;

	@NacosValue("${dubbo.registry.address.cloud.dev}")
	private String cloudDevRegistryAddress;

	// 通过使用分组区分不同测试场景
	@Value("${dubbo.provider.group}")
	private String dubboProviderGroup;

	@Value("${is.online}")
	private boolean isOnline;

	/**
	 * Dubbo应用配置
	 */
	@Bean
	public ApplicationConfig applicationConfig() {
		ApplicationConfig applicationConfig = new ApplicationConfig();
		applicationConfig.setName(applicationName);
		applicationConfig.setQosEnable(false);
		return applicationConfig;
	}

	/**
	 * 配置st注册中心
	 */
	@Bean
	public RegistryConfig stRegistry() {
		RegistryConfig registryConfig = new RegistryConfig();
		registryConfig.setAddress(stDubboRegistryAddress);
		registryConfig.setDefault(!isOnline);
		return registryConfig;
	}

	/**
	 * 配置注册中心
	 */
	@Bean
	public RegistryConfig olRegistry() {
		RegistryConfig registryConfig = new RegistryConfig();
		registryConfig.setAddress(olDubboRegistryAddress);
		registryConfig.setDefault(isOnline);
		return registryConfig;
	}

	/**
	 * 配置注册中心
	 */
	@Bean
	public ProviderConfig providerConfig() {
		ProviderConfig providerConfig = new ProviderConfig();
		providerConfig.setGroup(dubboProviderGroup);
		providerConfig.setVersion("1.0");
		providerConfig.setTimeout(1000);
		return providerConfig;
	}

	/**
	 * 配置协议
	 */
	@Bean
	public ProtocolConfig protocolConfig() {
		ProtocolConfig protocolConfig = new ProtocolConfig();
		protocolConfig.setName("dubbo");
		//自动尝试
		protocolConfig.setPort(-1);
		protocolConfig.setTransporter("netty4");
		protocolConfig.setThreadpool("fixed");
		protocolConfig.setThreads(200);
		return protocolConfig;
	}

}
