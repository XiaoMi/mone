package run.mone.mcp.pty4j.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpServer.ToolRegistration;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.pty4j.function.Pty4jFunction;

@Component
public class Pty4jMcpServer {

    private ServerMcpTransport transport;
    private McpSyncServer syncServer;

    public Pty4jMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
    }

    public McpSyncServer start() {
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("pty4j_mcp", "0.0.1")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        Pty4jFunction function = new Pty4jFunction();
        var toolRegistration = new ToolRegistration(
                new Tool(function.getName(), function.getDesc(), function.getToolScheme()), function
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
