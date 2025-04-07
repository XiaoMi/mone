package run.mone.mcp.knowledge.base.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.hive.mcp.server.McpServer.ToolRegistration;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.mcp.knowledge.base.function.KnowledgeBaseQueryFunction;


@Slf4j
@Component
public class KnowledgeBaseQueryMcpServer {
    private ServerMcpTransport transport;

    private McpSyncServer syncServer;

    public KnowledgeBaseQueryMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
        log.info("KnowledgeBaseQueryMcpServer initialized with transport: {}", transport);
        log.info("KnowledgeBaseQueryMcpServer initialized with API_HOST: {}",
                System.getenv().getOrDefault("API_HOST", "http://127.0.0.1:8083"));
    }

    public McpSyncServer start() {
        log.info("Starting KnowledgeBaseQueryMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("knowledge_base_query_mcp", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        try {
            KnowledgeBaseQueryFunction function = new KnowledgeBaseQueryFunction();
            var toolRegistration = new McpServer.ToolStreamRegistration(
                    new McpSchema.Tool(function.getName(), function.getDesc(), function.getToolScheme()), function
            );
            syncServer.addStreamTool(toolRegistration);

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
