package run.mone.mcp.mermaid.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpServer.ToolRegistration;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.mermaid.function.MermaidFunction;

@Slf4j
@Component
public class MermaidMcpServer {

    private final ServerMcpTransport transport;

    private McpSyncServer syncServer;

    public MermaidMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
        log.info("MermaidMcpServer initialized with transport: {}", transport);
    }

    public McpSyncServer start() {
        log.info("Starting MermaidMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("mermaid_mcp_server", "1.0.0")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        log.info("Registering mermaid tool...");

        try {
            MermaidFunction mermaidFunction = new MermaidFunction(new ObjectMapper());
            var mermaidToolRegistration = new ToolRegistration(
                    new Tool(mermaidFunction.getName(), mermaidFunction.getDesc(), mermaidFunction.getMermaidToolSchema()),
                    mermaidFunction
            );
            syncServer.addTool(mermaidToolRegistration);
            log.info("Successfully registered mermaid tool");
        } catch (Exception e) {
            log.error("Failed to register mermaid tool", e);
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
            log.info("Stopping MermaidMcpServer...");
            this.syncServer.closeGracefully();
        }
    }
}