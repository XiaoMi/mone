
package run.mone.mcp.coder.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.coder.function.CoderFunction;

@Component
public class CoderMcpServer {

    private final ServerMcpTransport transport;
    private final CoderFunction coderFunction;
    private McpSyncServer syncServer;

    public CoderMcpServer(ServerMcpTransport transport, CoderFunction coderFunction) {
        this.transport = transport;
        this.coderFunction = coderFunction;
    }

    public McpSyncServer start() {
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("coder_mcp", "0.0.1")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        var toolRegistration = new McpServer.ToolRegistration(
                new Tool(coderFunction.getName(), coderFunction.getDesc(), coderFunction.getToolScheme()),
                coderFunction
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
