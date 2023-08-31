package com.xiaomi.mone.tpc.bootstrap;

import com.xiaomi.mone.dubbo.docs.EnableDubboApiDocs;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author zgf
 */
@Slf4j
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.xiaomi.mone.tpc"})
@DubboComponentScan(basePackages = "com.xiaomi.mone.tpc")
@EnableDubboApiDocs
@ServletComponentScan("com.xiaomi.mone.tpc.filter")
public class MiTpcBootstrap {

    public static void main(String... args) {
        try {
            SpringApplication.run(MiTpcBootstrap.class, args);
        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
            System.exit(-1);
        }
    }

}