package com.xiaomi.mone.hera.demo.client.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author dingtao
 */
@EnableAutoConfiguration(exclude= {DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = {"com.xiaomi.mone.hera.demo.client"})
@DubboComponentScan(basePackages = "com.xiaomi.mone.hera.demo.client")
@Slf4j
public class HeraDemoClientBootstrap {

    public static void main(String... args) {
        try {
            SpringApplication.run(HeraDemoClientBootstrap.class, args);
        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
            System.exit(-1);
        }
    }
}