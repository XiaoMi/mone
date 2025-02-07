package run.mone.mcp.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"run.mone.mcp.excel", "run.mone.mcp.excel.server"})
@Slf4j
public class ExcelMcpBootstrap {

    public static void main(String[] args) {
        log.info("Starting Excel MCP Server...");
        SpringApplication.run(ExcelMcpBootstrap.class, args);
    }
}
