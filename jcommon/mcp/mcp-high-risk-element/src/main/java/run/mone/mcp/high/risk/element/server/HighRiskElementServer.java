package run.mone.mcp.high.risk.element.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.high.risk.element.function.HighRiskElementFunction;
import run.mone.mcp.high.risk.element.function.HighRiskElementMockFunction;

@Slf4j
@Component
public class HighRiskElementServer {
    private ServerMcpTransport transport;

    private McpSyncServer syncServer;

    public HighRiskElementServer(ServerMcpTransport transport) {
        this.transport = transport;
        log.info("HighRiskElementServer initialized with transport: {}", transport);
    }

    public McpSyncServer start() {
        log.info("Starting HighRiskElementServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("high_risk_element_mcp", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        try {
            String ideaPort = System.getenv().getOrDefault("IDEA_PORT", "30000");
            HighRiskElementMockFunction function = new HighRiskElementMockFunction(ideaPort);
            var toolRegistration = new McpServer.ToolRegistration(
                    new McpSchema.Tool(function.getName(), function.getDesc(), function.getHighRiskElementToolSchema()), function
            );
            syncServer.addTool(toolRegistration);

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
            log.info("Stopping HighRiskElementServer...");
            this.syncServer.closeGracefully();
        }
    }
}
