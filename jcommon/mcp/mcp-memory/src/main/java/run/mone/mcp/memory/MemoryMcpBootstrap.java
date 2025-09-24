package run.mone.mcp.memory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.memory")
public class MemoryMcpBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(MemoryMcpBootstrap.class, args);
    }
}