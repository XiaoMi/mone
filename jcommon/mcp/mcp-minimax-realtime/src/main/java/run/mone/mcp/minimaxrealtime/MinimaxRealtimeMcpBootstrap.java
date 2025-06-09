package run.mone.mcp.minimaxrealtime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.minimaxrealtime")
public class MinimaxRealtimeMcpBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(MinimaxRealtimeMcpBootstrap.class, args);
    }
} 