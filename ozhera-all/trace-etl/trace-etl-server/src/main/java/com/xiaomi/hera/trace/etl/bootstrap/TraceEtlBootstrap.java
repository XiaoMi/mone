package com.xiaomi.hera.trace.etl.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author dingtao
 */
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.xiaomi.hera.trace.etl", "com.xiaomi.data.push.redis"})
@DubboComponentScan(basePackages = "com.xiaomi.hera.trace.etl.dubbo")
@Slf4j
public class TraceEtlBootstrap {

    public static void main(String... args) {
        try {
            SpringApplication.run(TraceEtlBootstrap.class, args);
        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
            System.exit(-1);
        }
    }

}