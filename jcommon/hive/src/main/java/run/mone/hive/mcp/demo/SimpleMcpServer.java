package run.mone.hive.mcp.demo;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.demo.function.CalculatorFunction;
import run.mone.hive.mcp.demo.function.FileOperationFunction;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpServer.ToolRegistration;
import run.mone.hive.mcp.server.McpServer.ToolStreamRegistration;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;

@Component
public class SimpleMcpServer {

    private ServerMcpTransport transport;
    private McpSyncServer syncServer;

    public SimpleMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
    }

    public McpSyncServer start() {
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("my-server", "1.0.0")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        CalculatorFunction function = new CalculatorFunction();
        // FileOperationFunction function = new FileOperationFunction();
        // var toolRegistration = new ToolRegistration(
        //         new Tool(function.getName(), function.getDesc(), function.getToolScheme()), function
        // );

        // syncServer.addTool(toolRegistration);

        var toolStreamRegistration = new ToolStreamRegistration(
                new Tool(function.getName(), function.getDesc(), function.getToolScheme()), function
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
