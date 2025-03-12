
package run.mone.mcp.novel.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.novel.function.NovelFunction;

@Component
public class NovelMcpServer {

    private final ServerMcpTransport transport;
    private final NovelFunction novelFunction;
    private McpSyncServer syncServer;

    public NovelMcpServer(ServerMcpTransport transport, NovelFunction novelFunction) {
        this.transport = transport;
        this.novelFunction = novelFunction;
    }

    public McpSyncServer start() {
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("novel_mcp", "0.0.1")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        var toolRegistration = new McpServer.ToolRegistration(
                new Tool(novelFunction.getName(), novelFunction.getDesc(), novelFunction.getToolScheme()),
                novelFunction
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
