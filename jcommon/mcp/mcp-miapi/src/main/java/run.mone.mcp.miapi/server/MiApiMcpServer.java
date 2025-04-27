
package run.mone.mcp.miapi.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpServer.ToolRegistration;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.miapi.function.MiApiFunction;

@Slf4j
@Component
public class MiApiMcpServer {

    private final ServerMcpTransport transport;

    private final ChatFunction chatFunction;
    private McpSyncServer syncServer;

    public MiApiMcpServer(ServerMcpTransport transport, ChatFunction chatFunction) {
        this.transport = transport;
        this.chatFunction = chatFunction;
    }

    public McpSyncServer start() {
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("miapi_mcp", "0.0.2")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        var toolStreamRegistration = new McpServer.ToolStreamRegistration(
                new Tool(chatFunction.getName(), chatFunction.getDesc("miapi_agent"), chatFunction.getToolScheme()), chatFunction
        );

        syncServer.addStreamTool(toolStreamRegistration);

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
