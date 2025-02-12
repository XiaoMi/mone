package run.mone.mcp.word.server;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.word.function.WordFunction;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.mcp.word.service.WordService;

@Slf4j
@Component
public class WordMcpServer {

    private final ServerMcpTransport transport;
    private final WordFunction wordFunction;
    private McpSyncServer syncServer;

    public WordMcpServer(ServerMcpTransport transport, WordFunction wordFunction) {
        this.transport = transport;
        this.wordFunction = wordFunction;
        log.info("WordMcpServer initialized with transport: {}", transport);
    }

    public McpSyncServer start() {
        log.info("Starting WordMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("word_mcp_server", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        // 注册word工具
        log.info("Registering word tool...");
        try {
            var wordToolRegistration = new McpServer.ToolRegistration(
                    new Tool(wordFunction.getName(), wordFunction.getDescription(), wordFunction.getToolScheme()),
                    wordFunction
            );
            syncServer.addTool(wordToolRegistration);

            log.info("Successfully registered word tool");
        } catch (Exception e) {
            log.error("Failed to register word tool", e);
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
            log.info("Stopping WordMcpServer...");
            this.syncServer.closeGracefully();
        }
    }
}
