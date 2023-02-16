package com.xiaomi.hera.trace.etl.es.bootstrap;

import com.xiaomi.hera.trace.etl.es.consumer.TraceSpanConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author dingtao
 */
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.xiaomi.hera.trace.etl", "com.xiaomi.data.push.redis"})
@DubboComponentScan(basePackages = "com.xiaomi.hera.trace.etl.es.dubbo")
@Slf4j
public class TraceEtlEsBootstrap {

    public static void main(String... args) {
        try {
            SpringApplication.run(TraceEtlEsBootstrap.class, args);
        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
            System.exit(-1);
        }
    }

}