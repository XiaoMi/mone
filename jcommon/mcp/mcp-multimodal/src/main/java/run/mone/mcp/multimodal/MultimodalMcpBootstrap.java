package run.mone.mcp.multimodal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"run.mone.mcp.multimodal", "run.mone.hive.spring.starter"})
public class MultimodalMcpBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(MultimodalMcpBootstrap.class, args);
    }
} 