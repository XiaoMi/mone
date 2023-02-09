package run.mone.raft.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author goodjava@qq.com
 * @date 2022/5/9 14:28
 */
@EnableAutoConfiguration
@ComponentScan(basePackages = {"run.mone"})
@Slf4j
public class Bootstrap2 {

    public static void main(String[] args) {
        try {
            System.setProperty("server.port", "8081");
            SpringApplication.run(Bootstrap2.class, args);
            log.info("start finish bootstrap2" + " 0.0.1");
        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
            System.exit(-1);
        }

    }
}
