package run.mone.mcp.mysql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.mysql")
public class DbaAgentBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(DbaAgentBootstrap.class, args);
    }
}
