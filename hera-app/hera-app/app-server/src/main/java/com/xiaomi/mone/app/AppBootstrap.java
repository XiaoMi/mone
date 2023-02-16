package com.xiaomi.mone.app;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.TimeUnit;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/10/29 10:26
 */
@Slf4j
@SpringBootApplication
@DubboComponentScan(basePackages = "com.xiaomi.mone.app")
@MapperScan("com.xiaomi.mone.app.dao")
@EnableScheduling
public class AppBootstrap {

    public static void main(String[] args) {

        try {
            Stopwatch stopwatch = Stopwatch.createStarted();
            SpringApplication.run(AppBootstrap.class, args);

            log.info("AppBootstrap MAX_MEMORY: {}MB", (Runtime.getRuntime().maxMemory()/1024/1024));
            log.info("AppBootstrap TOTAL_MEMORY: {}MB", (Runtime.getRuntime().totalMemory()/1024/1024));
            log.info("AppBootstrap Start used: {}s", stopwatch.elapsed(TimeUnit.SECONDS));
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            System.exit(-1);
        }
    }
}
