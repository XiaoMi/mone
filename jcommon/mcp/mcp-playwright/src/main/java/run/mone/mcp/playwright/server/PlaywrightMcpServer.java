
package run.mone.mcp.playwright.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.playwright.function.PlaywrightFunction;
import run.mone.hive.mcp.server.McpServer.ToolRegistration;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;

@Slf4j
@Component
public class PlaywrightMcpServer {
    private ServerMcpTransport transport;
    private McpSyncServer syncServer;
    private PlaywrightFunction playwrightFunction;

    public PlaywrightMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
        log.info("PlaywrightMcpServer initialized with transport: {}", transport);
    }

    public McpSyncServer start() {
        log.info("Starting PlaywrightMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("playwright_mcp_server", "1.0.0")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        log.info("Registering execute_playwright tool...");
        try {
            playwrightFunction = new PlaywrightFunction();
            var playwrightToolRegistration = new ToolRegistration(
                    new Tool(playwrightFunction.getName(), playwrightFunction.getDesc(), playwrightFunction.getPlaywrightToolSchema()), playwrightFunction
            );

            syncServer.addTool(playwrightToolRegistration);
            log.info("Successfully registered execute_playwright tool");
        } catch (Exception e) {
            log.error("Failed to register execute_playwright tool", e);
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
            log.info("Stopping PlaywrightMcpServer...");
            this.syncServer.closeGracefully();
        }
        if (this.playwrightFunction != null) {
            this.playwrightFunction.close();
        }
    }
}
