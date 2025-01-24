
package run.mone.mcp.writer.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.writer.function.WriterFunction;

@Component
public class WriterMcpServer {

    private final ServerMcpTransport transport;
    private final WriterFunction writerFunction;
    private McpSyncServer syncServer;

    public WriterMcpServer(ServerMcpTransport transport, WriterFunction writerFunction) {
        this.transport = transport;
        this.writerFunction = writerFunction;
    }

    public McpSyncServer start() {
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("writer_mcp", "0.0.1")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        var toolRegistration = new McpServer.ToolRegistration(
                new Tool(writerFunction.getName(), writerFunction.getDesc(), writerFunction.getToolScheme()),
                writerFunction
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
