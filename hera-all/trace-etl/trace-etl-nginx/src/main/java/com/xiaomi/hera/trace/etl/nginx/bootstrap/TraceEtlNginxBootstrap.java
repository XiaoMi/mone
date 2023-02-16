package com.xiaomi.hera.trace.etl.nginx.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author dingtao
 */
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = {"com.xiaomi.hera.trace.etl.nginx"})
@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
@Slf4j
public class TraceEtlNginxBootstrap {

    public static void main(String... args) {
        try {
            SpringApplication.run(TraceEtlNginxBootstrap.class, args);
        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
            System.exit(-1);
        }
    }
}