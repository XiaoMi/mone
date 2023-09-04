package com.xiaomi.youpin.prometheus.agent.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author dingtao
 */
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.xiaomi.youpin.prometheus.agent", "com.xiaomi.youpin"})
@DubboComponentScan(basePackages = "com.xiaomi.youpin.prometheus.agent")
@Slf4j
public class PrometheusAgentBootstrap {
    private static final Logger logger = LoggerFactory.getLogger(PrometheusAgentBootstrap.class);

    public static void main(String... args) {
        try {
            SpringApplication.run(PrometheusAgentBootstrap.class, args);
        } catch (Throwable throwable) {
            logger.error(throwable.getMessage(), throwable);
            System.exit(-1);
        }
    }
}