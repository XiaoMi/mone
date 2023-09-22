package com.xiaomi.hera.trace.etl.manager.bootstrap;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author dingtao
 */
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.xiaomi.hera.trace.etl"})
@DubboComponentScan(basePackages = "com.xiaomi.hera.trace.etl")
public class TraceEtlManagerBootstrap {

    public static void main(String... args) {
        try {
            SpringApplication.run(TraceEtlManagerBootstrap.class, args);
        } catch (Throwable throwable) {
            System.exit(-1);
        }
    }

}