package run.mone.mcp.song.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.song.function.SongFunction;

@Slf4j
@Component
public class SongMcpServer {

    private final ServerMcpTransport transport;
    private McpSyncServer syncServer;

    public SongMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
    }

    public McpSyncServer start() {
        log.info("Starting SongMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("song_mcp", "0.0.1")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        // Register song tool
        log.info("Registering song tool...");
        try {
            SongFunction function = new SongFunction();
            var toolRegistration = new McpServer.ToolRegistration(
                    new Tool(function.getName(), function.getDesc(), function.getToolScheme()), 
                    function
            );
            syncServer.addTool(toolRegistration);
            log.info("Successfully registered song tool");
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