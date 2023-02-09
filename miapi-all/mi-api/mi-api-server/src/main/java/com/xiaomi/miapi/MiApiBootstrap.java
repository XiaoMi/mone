package com.xiaomi.miapi;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.xiaomi.miapi"})
@DubboComponentScan(basePackages = "com.xiaomi.miapi")
@MapperScan("com.xiaomi.miapi.mapper")
@ServletComponentScan
public class MiApiBootstrap {
    private static final Logger logger = LoggerFactory.getLogger(MiApiBootstrap.class);

    public static void main(String... args) {
        try {
            SpringApplication.run(MiApiBootstrap.class, args);
        } catch (Throwable throwable) {
            logger.error(throwable.getMessage(), throwable);
            System.exit(-1);
        }
    }
}