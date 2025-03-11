
package run.mone.mcp.gateway.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.gateway.function.ApiFunction;

@Component
public class GatewayMcpServer {

    private final ServerMcpTransport transport;
    private final ApiFunction apiFunction;
    private McpSyncServer syncServer;

    public GatewayMcpServer(ServerMcpTransport transport, ApiFunction apiFunction) {
        this.transport = transport;
        this.apiFunction = apiFunction;
    }

    public McpSyncServer start() {
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("gateway_mcp", "0.0.1")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        var toolRegistration = new McpServer.ToolRegistration(
                new Tool(apiFunction.getName(), apiFunction.getDesc(), apiFunction.getToolScheme()),
                apiFunction
        );

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
