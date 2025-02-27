package run.mone.mcp.xmind.server;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpServer.ToolRegistration;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.xmind.function.XmindFunction;

@Slf4j
@Component
public class XmindMcpServer {

    private final ServerMcpTransport transport;

    private McpSyncServer syncServer;

    public XmindMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
        log.info("XmindMcpServer initialized with transport: {}", transport);
    }

    public McpSyncServer start() {
        log.info("Starting XmindMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("xmind_mcp_server", "1.0.0")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        log.info("Registering xmind tool...");

        try {
            XmindFunction xmindFunction = new XmindFunction(new ObjectMapper());
            var xmindToolRegistration = new ToolRegistration(
                    new Tool(xmindFunction.getName(), xmindFunction.getDesc(), xmindFunction.getXmindToolSchema()),
                    xmindFunction
            );
            syncServer.addTool(xmindToolRegistration);
            log.info("Xmind tool registered successfully");
            this.syncServer = syncServer;
            return syncServer;
        } catch (Exception e) {
            log.error("Failed to register xmind tool", e);
            throw new RuntimeException("Failed to register xmind tool", e);
        }
    }

    @PostConstruct
    public void init() {
        start();
    }

    @PreDestroy
    public void destroy() {
        if (syncServer != null) {
            syncServer.close();
        }
    }
}