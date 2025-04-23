package run.mone.mcp.mysql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.mysql")
public class DatabaseMcpBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(DatabaseMcpBootstrap.class, args);
    }
}
