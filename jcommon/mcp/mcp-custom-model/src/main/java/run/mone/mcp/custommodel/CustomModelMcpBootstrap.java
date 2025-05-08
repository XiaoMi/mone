package run.mone.mcp.custommodel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.custommodel")
public class CustomModelMcpBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(CustomModelMcpBootstrap.class, args);
    }
} 