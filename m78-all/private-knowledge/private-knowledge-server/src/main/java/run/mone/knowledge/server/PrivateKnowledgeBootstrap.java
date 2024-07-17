package run.mone.knowledge.server;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author bot
 */
@EnableAutoConfiguration
@ComponentScan(basePackages = {"run.mone", "com.xiaomi.youpin", "com.xiaomi.data.push.redis"})
@DubboComponentScan(basePackages = "run.mone")
@MapperScan("run.mone.knowledge.service.dao")
public class PrivateKnowledgeBootstrap {
    private static final Logger logger = LoggerFactory.getLogger(PrivateKnowledgeBootstrap.class);

    public static void main(String... args) {
        try {
            SpringApplication.run(PrivateKnowledgeBootstrap.class, args);
        } catch (Throwable throwable) {
            logger.error(throwable.getMessage(), throwable);
            System.exit(-1);
        }
    }
}