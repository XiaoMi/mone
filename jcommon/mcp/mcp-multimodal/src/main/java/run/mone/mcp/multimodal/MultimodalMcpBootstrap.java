package run.mone.mcp.multimodal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.multimodal")
public class MultimodalMcpBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(MultimodalMcpBootstrap.class, args);
    }
} 