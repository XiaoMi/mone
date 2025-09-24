
package run.mone.moner.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.moner.server")
@Slf4j
public class Bootstrap {
    public static void main(String[] args) {
        log.info("moner server start");
        SpringApplication.run(Bootstrap.class, args);
    }
}
