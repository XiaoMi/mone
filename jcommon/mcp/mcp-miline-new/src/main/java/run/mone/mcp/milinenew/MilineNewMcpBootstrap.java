package run.mone.mcp.milinenew;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"run.mone.mcp.milinenew", "run.mone.hive.mcp.service", "run.mone.mcp.git"})
public class MilineNewMcpBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(MilineNewMcpBootstrap.class, args);
    }
}

