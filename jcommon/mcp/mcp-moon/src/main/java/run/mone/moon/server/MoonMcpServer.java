package run.mone.moon.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpServer.ToolRegistration;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.moon.config.DubboConfiguration;
import run.mone.moon.function.MoonFunction;
import run.mone.moon.api.service.MoonTaskDubboService;

@Slf4j
@Component
@DependsOn("dubboConfiguration")
public class MoonMcpServer {

    private ServerMcpTransport transport;

    private McpSyncServer syncServer;

    @Resource
    private DubboConfiguration dubboConfiguration;

    public MoonMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
        log.info("MoonMcpServer initialized with transport: {}", transport);
    }

    public McpSyncServer start() {
        log.info("Starting MoonMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("moon_mcp_server", "1.0.0")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        // 注册moon_mcp_server工具
        log.info("Registering MoonMcpServer tool...");
        try {
            MoonFunction moonFunction = new MoonFunction(dubboConfiguration.getMoonTaskDubboService());
            var moonToolRegistration = new ToolRegistration(
                    new Tool(moonFunction.getName(), moonFunction.getDesc(), moonFunction.getTaskToolSchema()), moonFunction
            );
            syncServer.addTool(moonToolRegistration);

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