package run.mone.mcp.mione;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"run.mone.mcp.mione", "run.mone.hive.spring.starter"})
public class MioneMcpBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(MioneMcpBootstrap.class, args);
    }
}

