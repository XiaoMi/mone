package run.mone.mcp.log.serever;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.log.function.HeraLogFunction;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2025/2/21 9:59
 */
@Slf4j
@Component
public class LogMcpServer {

    // TODO:后续支持github

    private ServerMcpTransport transport;

    private McpSyncServer syncServer;

    public LogMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
        log.info("GitMcpServer initialized with transport: {}", transport);
    }

    public McpSyncServer start() {
        log.info("Starting GitMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("git_mcp", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        try {
            HeraLogFunction heraLogFunction = new HeraLogFunction();
            var logToolRegistration = new McpServer.ToolRegistration(
                    new McpSchema.Tool(heraLogFunction.getName(), heraLogFunction.getDesc(), heraLogFunction.getGithubToolSchema()), heraLogFunction
            );
            syncServer.addTool(logToolRegistration);

            log.info("Successfully registered git tool");
        } catch (Exception e) {
            log.error("Failed to register git tool", e);
            throw e;
        }

        return syncServer;
    }

    @PostConstruct
    public void init() {
        this.syncServer = start();
    }

    @PreDestroy
    public void stop() {
        if (this.syncServer != null) {
            log.info("Stopping gitMcpServer...");
            this.syncServer.closeGracefully();
        }
    }
}
