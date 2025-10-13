package run.mone.hive.spring.starter;

import lombok.RequiredArgsConstructor;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.server.McpServer.ToolStreamRegistration;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class McpServer {

    private final ServerMcpTransport transport;

    private McpSyncServer syncServer;

    private final List<McpFunction> functionList;

    private final Map<String, String> meta;

    public McpSyncServer start() {
        McpSyncServer syncServer = run.mone.hive.mcp.server.McpServer.using(transport)
                .serverInfo(meta.getOrDefault(Const.AGENT_SERVER_NAME, "ai_agent_server"), meta.getOrDefault(Const.AGENT_SERVER_VERSION, "0.0.1"), meta)
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        functionList.forEach(function -> {
            if (function.getName().startsWith("stream_")) {
                var toolStreamRegistration = new ToolStreamRegistration(
                        new Tool(function.getName(), function.getDesc(), function.getToolScheme()), function
                );
                syncServer.addStreamTool(toolStreamRegistration);
            } else {
                syncServer.addTool(new run.mone.hive.mcp.server.McpServer.ToolRegistration(new Tool(function.getName(), function.getDesc(), function.getToolScheme()), function));
            }
        });

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
