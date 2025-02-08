package run.mone.m78.server;

import com.xiaomi.mone.http.docs.EnableHttpApiDocs;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAutoConfiguration
@EnableConfigurationProperties
@ComponentScan(basePackages = {"run.mone.m78", "com.xiaomi.youpin", "com.xiaomi.data.push.redis"})
@DubboComponentScan(basePackages = "run.mone.m78")
@MapperScan("run.mone.m78.service.dao")
@EnableHttpApiDocs
@EnableScheduling
@EnableAspectJAutoProxy(exposeProxy = true)
@Slf4j
public class M78Bootstrap {

    public static void main(String... args) {
        try {
            SpringApplication.run(M78Bootstrap.class, args);
        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
            System.exit(-1);
        }
    }
}