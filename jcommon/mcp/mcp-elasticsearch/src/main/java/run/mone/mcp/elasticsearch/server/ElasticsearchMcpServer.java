package run.mone.mcp.elasticsearch.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpServer.ToolRegistration;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.mcp.elasticsearch.function.ElasticsearchFunction;

@Component
public class ElasticsearchMcpServer {
    private ServerMcpTransport transport;
    private McpSyncServer syncServer;

    public ElasticsearchMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
    }
    public McpSyncServer start() {
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("elasticsearch_mcp", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();
        ElasticsearchFunction function = new ElasticsearchFunction();
        var toolRegistration = new ToolRegistration(new Tool(function.getName(), function.getDesc(), function.getToolScheme()), function);
        syncServer.addTool(toolRegistration);
        return syncServer;
    }

    @PostConstruct
    public void init() {
        this.syncServer = start();
    }

    @PreDestroy
    public void stop() {
        if (this.syncServer != null) {
            this.syncServer.closeGracefully();
        }
    }
}
