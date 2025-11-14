package run.mone.mcp.git;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Git MCP Server Application
 *
 * 提供Git操作相关的MCP工具服务
 *
 * @author generated
 * @date 2025-11-13
 */
@SpringBootApplication
@Slf4j
public class GitApplication {

    public static void main(String[] args) {
        log.info("Starting Git MCP Server...");
        SpringApplication.run(GitApplication.class, args);
        log.info("Git MCP Server started successfully");
    }
}
