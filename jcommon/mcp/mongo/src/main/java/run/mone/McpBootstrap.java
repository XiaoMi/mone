package run.mone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mongo")
public class McpBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(McpBootstrap.class, args);
    }
}