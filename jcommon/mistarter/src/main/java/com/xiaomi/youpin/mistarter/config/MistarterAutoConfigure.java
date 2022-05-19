package com.xiaomi.youpin.mistarter.config;

import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.xiaomi.data.push.nacos.NacosNaming;
import com.xiaomi.mone.mistarter.nacos.MoneConfig;
import com.xiaomi.youpin.health.HealthController;
import com.xiaomi.youpin.health.HealthServiceImpl;
import com.xiaomi.youpin.health.InitService;
import com.xiaomi.youpin.qps.QpsAop;
import com.xiaomi.youpin.service.HealthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.annotation.PostConstruct;

/**
 * @author goodjava@qq.com
 */
@Configuration
@Aspect
@ConditionalOnClass({QpsAop.class, InitService.class})
@EnableAspectJAutoProxy
@DubboComponentScan(basePackages = "com.xiaomi.youpin")
@EnableNacosConfig(globalProperties = @NacosProperties(serverAddr = "${nacos.config.addrs}"))
@Slf4j
public class MistarterAutoConfigure {

    @PostConstruct
    private void init() {
        log.info("mistarter init");
    }


    /**
     * http 健康监测
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = {"http_health"})
    public HealthController httpHealth() {
        log.info("init httpHealth");
        HealthController controller = new HealthController();
        return controller;
    }

    /**
     * dubbo 健康监测
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = {"dubbo_health", "health_dubbo_group"})
    public HealthService DubboHealth() {
        log.info("init dubboHealth");
        HealthService service = new HealthServiceImpl();
        return service;
    }

    /**
     * 动态配置
     * @return
     */
    @Bean
    public MoneConfig moneConfig() {
        return new MoneConfig();
    }


}
