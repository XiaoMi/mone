package run.mone.mcp.mysql.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.mysql.function.MysqlFunction;
import run.mone.hive.mcp.server.McpServer.ToolRegistration;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;

@Slf4j
@Component
public class MysqlMcpServer {
    private ServerMcpTransport transport;
    private McpSyncServer syncServer;

    public MysqlMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
        log.info("MysqlMcpServer initialized with transport: {}", transport);
    }

    public McpSyncServer start() {
        log.info("Starting MysqlMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("mysql_mcp_serverV2", "1.0.0")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        // 注册execute_sql工具
        log.info("Registering execute_sql tool...");
        try {
            MysqlFunction mysqlFunction = new MysqlFunction();
            var sqlToolRegistration = new ToolRegistration(
                    new Tool(mysqlFunction.getName(), mysqlFunction.getDesc(), mysqlFunction.getSqlToolSchema()), mysqlFunction
            );

            syncServer.addTool(sqlToolRegistration);
            log.info("Successfully registered execute_sql tool");
        } catch (Exception e) {
            log.error("Failed to register execute_sql tool", e);
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
            log.info("Stopping MysqlMcpServer...");
            this.syncServer.closeGracefully();
        }
    }
}