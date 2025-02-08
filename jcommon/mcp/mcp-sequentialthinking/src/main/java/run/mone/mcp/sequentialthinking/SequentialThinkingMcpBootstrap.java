package run.mone.mcp.sequentialthinking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.sequentialthinking")
public class SequentialThinkingMcpBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(SequentialThinkingMcpBootstrap.class, args);
    }
}
