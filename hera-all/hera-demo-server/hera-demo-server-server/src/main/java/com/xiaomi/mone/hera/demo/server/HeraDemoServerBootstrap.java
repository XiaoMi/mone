package com.xiaomi.mone.hera.demo.server;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author zxw
 */
@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = {"com.xiaomi.mone.hera.demo.server"})
@DubboComponentScan(basePackages = {"com.xiaomi.mone.hera.demo.server"})
public class HeraDemoServerBootstrap {
    public static void main(String... args) {
        try {
            log.info("this is {}", "hera-demo-server");
            SpringApplication.run(HeraDemoServerBootstrap.class, args);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            log.error(throwable.getMessage(), throwable);
            System.exit(-1);
        }
    }
}