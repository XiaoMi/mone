package run.mone.mcp.chrome;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"run.mone.mcp.chrome"})
@Slf4j
public class ChromeMcpBootstrap {

    public static void main(String[] args) {
        log.info("Starting Chrome MCP Server...");
        SpringApplication.run(ChromeMcpBootstrap.class, args);
    }
}
