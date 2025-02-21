package run.mone.mcp.docparsing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("run.mone.mcp.docparsing")
@Slf4j
public class McpDocParsingBootstrap {
    
    public static void main(String[] args) {
        log.info("mcp docparsing start");
        SpringApplication.run(McpDocParsingBootstrap.class, args);
    }
} 