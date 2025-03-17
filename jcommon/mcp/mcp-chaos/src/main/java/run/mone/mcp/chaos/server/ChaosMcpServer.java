package run.mone.mcp.chaos.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.chaos.function.ChaosFunction;
import run.mone.mcp.chaos.function.CreateChaosFunction;

@Slf4j
@Component
public class ChaosMcpServer {
    private ServerMcpTransport transport;

    private McpSyncServer syncServer;

    public ChaosMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
        log.info("ChaosMcpServer initialized with transport: {}", transport);
    }

    public McpSyncServer start() {
        log.info("Starting ChaosMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("chaos_mcp", "1.0.2")
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        try {
            ChaosFunction chaosFunction = new ChaosFunction();
            CreateChaosFunction createChaosFunction = new CreateChaosFunction();

            var toolRegistration = new McpServer.ToolStreamRegistration(
                    new McpSchema.Tool(chaosFunction.getName(), chaosFunction.getDesc(), chaosFunction.getChaosToolSchema()), chaosFunction
            );

            var createChaosRegistration = new McpServer.ToolStreamRegistration(
                    new McpSchema.Tool(createChaosFunction.getName(), createChaosFunction.getDesc(), createChaosFunction.getChaosToolSchema()), createChaosFunction
            );

            syncServer.addStreamTool(toolRegistration);
            syncServer.addStreamTool(createChaosRegistration);

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
