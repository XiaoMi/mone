package run.mone.mcp.hammerspoon.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.hammerspoon.function.ChatToFunction;
import run.mone.mcp.hammerspoon.function.*;

@Slf4j
@Component
public class HammerspoonMcpServer {

    private final ServerMcpTransport transport;
    private McpSyncServer syncServer;

    public HammerspoonMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
    }

    public McpSyncServer start() {
        log.info("Starting SongMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("hammerspoon_mcp", "0.0.1")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        // Register song tool
        log.info("Registering song tool...");
        try {
            ScreenFunction function = new ScreenFunction();
            var toolRegistration = new McpServer.ToolRegistration(
                    new Tool(function.getName(), function.getDesc(), function.getToolScheme()), 
                    function
            );
            syncServer.addTool(toolRegistration);

            ChatToFunction function1 = new ChatToFunction();
            var toolRegistration1 = new McpServer.ToolRegistration(
                    new Tool(function1.getName(), function1.getDesc(), function1.getToolScheme()),
                    function1
            );
            syncServer.addTool(toolRegistration1);

            ChatViewFunction function2 = new ChatViewFunction();
            var toolRegistration2 = new McpServer.ToolRegistration(
                    new Tool(function2.getName(), function2.getDesc(), function2.getToolScheme()),
                    function2
            );
            syncServer.addTool(toolRegistration2);

            NextUnreadMessageFunction function3 = new NextUnreadMessageFunction();
            var toolRegistration3 = new McpServer.ToolRegistration(
                    new Tool(function3.getName(), function3.getDesc(), function3.getToolScheme()),
                    function3
            );
            syncServer.addTool(toolRegistration3);

            log.info("Successfully registered tools");
        } catch (Exception e) {
            log.error("Failed to register song tool", e);
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
            this.syncServer.closeGracefully();
        }
    }
}
