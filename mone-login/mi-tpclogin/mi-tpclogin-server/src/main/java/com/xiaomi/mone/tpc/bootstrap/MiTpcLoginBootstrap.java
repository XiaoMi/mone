package com.xiaomi.mone.tpc.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author zgf
 */
@Slf4j
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.xiaomi.mone.tpc", "com.xiaomi.youpin"})
public class MiTpcLoginBootstrap {

    public static void main(String... args) {
        try {
            SpringApplication.run(MiTpcLoginBootstrap.class, args);
        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
            System.exit(-1);
        }
    }

}