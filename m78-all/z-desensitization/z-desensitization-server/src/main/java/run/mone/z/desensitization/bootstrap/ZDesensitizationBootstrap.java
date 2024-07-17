package run.mone.z.desensitization.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.SpringVersion;
import run.mone.sautumnn.springboot.starter.anno.DubboComponentScan;
import run.mone.sautumnn.springboot.starter.miapi.dubbo.annotation.EnableDubboApiDocs;
import run.mone.sautumnn.springboot.starter.miapi.http.annotation.EnableHttpApiDocs;


/**
 * @author wm
 */
@EnableAutoConfiguration
@ComponentScan(basePackages = {"run.mone.z.desensitization", "com.xiaomi.youpin"})
@DubboComponentScan(basePackages = {"run.mone.z.desensitization.service", "run.mone.z.desensitization"})
@EnableHttpApiDocs
@EnableDubboApiDocs
@Slf4j
public class ZDesensitizationBootstrap {


    public static void main(String... args) {
        try {
            log.info("springboot version:{} spring version:{}", SpringBootVersion.getVersion(), SpringVersion.getVersion());
            SpringApplication.run(ZDesensitizationBootstrap.class, args);
        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
            System.exit(-1);
        }
    }
}