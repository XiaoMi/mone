package run.mone.mcp.chaos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.chaos")
public class ChaosMcpBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(ChaosMcpBootstrap.class, args);
    }
}
