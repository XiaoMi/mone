package run.mone.mcp.time.server;

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
import run.mone.mcp.time.function.TimeFunction;

@Slf4j
@Component
public class TimeMcpServer {

    private final ServerMcpTransport transport;

    private McpSyncServer syncServer;

    public TimeMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
        log.info("TimeMcpServer initialized with transport: {}", transport);
    }

    public McpSyncServer start() {
        log.info("Starting TimeMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("time_mcp_server", "1.0.0")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        log.info("Registering time tool...");

        try {
            TimeFunction timeFunction = new TimeFunction(new ObjectMapper());
            var timeToolRegistration = new ToolRegistration(
                    new Tool(timeFunction.getName(), timeFunction.getDesc(), timeFunction.getTimeToolSchema()),
                    timeFunction
            );
            syncServer.addTool(timeToolRegistration);
            log.info("Time tool registered successfully");
            this.syncServer = syncServer;
            return syncServer;
        } catch (Exception e) {
            log.error("Failed to register time tool", e);
            throw new RuntimeException("Failed to register time tool", e);
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