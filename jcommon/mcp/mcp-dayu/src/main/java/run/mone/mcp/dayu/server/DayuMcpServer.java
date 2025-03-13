package run.mone.mcp.dayu.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.dayu.function.DayuFunction;

import java.util.Properties;




@Slf4j
@Component
public class DayuMcpServer {

    private ServerMcpTransport transport;

    private McpSyncServer syncServer;

    public DayuMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
        log.info("dayuMcpServer initialized with transport: {}", transport);
    }

    public McpSyncServer start() {
        log.info("Starting dayuMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("dayu_mcp", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        try {
            DayuFunction dayuFunction = new DayuFunction();
            var sqlToolRegistration = new McpServer.ToolRegistration(
                    new McpSchema.Tool(dayuFunction.getName(), dayuFunction.getDesc(), dayuFunction.getConfigToolSchema()),dayuFunction);
            syncServer.addTool(sqlToolRegistration);

            log.info("Successfully registered dayu tool");
        } catch (Exception e) {
            log.error("Failed to register dayu tool", e);
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
            log.info("Stopping dayuMcpServer...");
            this.syncServer.closeGracefully();
        }
    }

}
