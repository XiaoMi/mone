
package run.mone.mcp.chat.server;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpServer.ToolStreamRegistration;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.chat.function.ChatFunction;
@Component
public class ChatMcpServer {

    private final ServerMcpTransport transport;
    private final ChatFunction chatFunction;
    private McpSyncServer syncServer;

    public ChatMcpServer(ServerMcpTransport transport, ChatFunction chatFunction) {
        this.transport = transport;
        this.chatFunction = chatFunction;
    }

    public McpSyncServer start() {
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("chat_mcp", "0.0.2")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        var toolStreamRegistration = new ToolStreamRegistration(
                new Tool(chatFunction.getName(), chatFunction.getDesc(), chatFunction.getToolScheme()), chatFunction
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
