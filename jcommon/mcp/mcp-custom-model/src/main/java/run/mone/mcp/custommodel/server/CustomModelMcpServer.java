package run.mone.mcp.custommodel.server;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.custommodel.function.CustomModelFunction;

@Slf4j
@Component
public class CustomModelMcpServer {

    private final ServerMcpTransport transport;
    private McpSyncServer syncServer;
    
    @Autowired
    private CustomModelFunction customModelFunction;

    public CustomModelMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
        log.info("CustomModelMcpServer initialized with transport: {}", transport);
    }

    public McpSyncServer start() {
        log.info("Starting CustomModelMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport).serverInfo("custom_model_mcp", "1.0.0")
            .capabilities(McpSchema.ServerCapabilities.builder().tools(true).logging().build()).sync();

        try {
            var toolRegistration =
                new McpServer.ToolStreamRegistration(new McpSchema.Tool(customModelFunction.getName(),
                    customModelFunction.getDesc(), McpSchema.parseSchema(customModelFunction.getToolSchema())), customModelFunction);
            syncServer.addStreamTool(toolRegistration);
            log.info("Successfully registered custom model tool");
        } catch (Exception e) {
            log.error("Failed to register custom model tool", e);
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
            log.info("Stopping CustomModelMcpServer...");
            this.syncServer.closeGracefully();
        }
    }
} 