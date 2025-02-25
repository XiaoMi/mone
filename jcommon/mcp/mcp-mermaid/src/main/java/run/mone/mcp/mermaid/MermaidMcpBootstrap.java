package run.mone.mcp.mermaid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.mermaid")
@Slf4j
public class MermaidMcpBootstrap {
    public static void main(String[] args) {
        log.info("mcp mermaid start");
        SpringApplication.run(MermaidMcpBootstrap.class, args);
    }
}