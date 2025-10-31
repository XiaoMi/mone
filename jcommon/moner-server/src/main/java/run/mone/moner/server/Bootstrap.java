
package run.mone.moner.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"run.mone.moner.server", "run.mone.hive"})
@Slf4j
public class Bootstrap {
    public static void main(String[] args) {
        log.info("moner server start");
        SpringApplication.run(Bootstrap.class, args);
    }
}
