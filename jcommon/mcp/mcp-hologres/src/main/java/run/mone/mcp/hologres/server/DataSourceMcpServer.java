package run.mone.mcp.hologres.server;

import com.alibaba.druid.pool.DruidDataSource;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.hive.mcp.server.McpServer.ToolRegistration;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.mcp.hologres.function.HoloFunction;

import javax.sql.DataSource;

@Slf4j
@Component
public class DataSourceMcpServer {

    private ServerMcpTransport transport;

    private McpSyncServer syncServer;

    @Value("${hologres.url}")
    private String url;

    @Value("${hologres.userName}")
    private String userName;

    @Value("${hologres.password}")
    private String password;

    public DataSourceMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
        log.info("MysqlMcpServer initialized with transport: {}", transport);
    }

    public DataSource hologresCarDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUsername(userName);
        druidDataSource.setPassword(password);
        druidDataSource.setUrl("jdbc:postgresql://" + url);
        druidDataSource.setName("mpc-hologres");
        return druidDataSource;
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
            HoloFunction mysqlFunction = new HoloFunction(hologresCarDataSource());
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