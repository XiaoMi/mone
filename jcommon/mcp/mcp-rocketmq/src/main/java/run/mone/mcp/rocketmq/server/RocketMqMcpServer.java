package run.mone.mcp.rocketmq.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.rocketmq.function.RocketMqFunction;

@Slf4j
@Component
public class RocketMqMcpServer {

    private ServerMcpTransport transport;

    private McpSyncServer syncServer;

    private RocketMqFunction rocketMqFunction;

    public RocketMqMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
        log.info("RocketMqMcpServer initialized with transport: {}", transport);
    }

    @SneakyThrows
    public McpSyncServer start() {
        log.info("Starting RocketMqMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("rocket_mcp", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        log.info("Registering stream_rocketmq_sender tool...");

        try {
            rocketMqFunction = new RocketMqFunction();
            var rocketMqToolRegistration = new McpServer.ToolStreamRegistration(
                    new McpSchema.Tool(rocketMqFunction.getName(), rocketMqFunction.getDesc(), rocketMqFunction.getSqlToolSchema()), rocketMqFunction
            );
            syncServer.addStreamTool(rocketMqToolRegistration);
            log.info("Successfully registered stream_rocketmq_sender tool");
        } catch (Exception e) {
            log.error("Failed to register tool", e);
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
            log.info("Stopping RocketMqMcpServer...");
            this.syncServer.closeGracefully();
        }

    }
}
