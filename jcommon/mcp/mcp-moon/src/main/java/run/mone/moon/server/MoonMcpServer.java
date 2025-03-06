package run.mone.moon.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpServer.ToolRegistration;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.moon.function.MoonCreateFunction;
import run.mone.moon.function.MoonGetFunction;
import run.mone.moon.function.MoonQueryFunction;

@Slf4j
@Component
@DependsOn("dubboConfiguration")
public class MoonMcpServer {

    private ServerMcpTransport transport;

    private McpSyncServer syncServer;

    @Resource
    ApplicationConfig applicationConfig;
    @Resource
    RegistryConfig registryConfig;
    @Value("${moon.dubbo.group}")
    private String group;

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
            // 创建
            MoonCreateFunction createMoonFunction = new MoonCreateFunction(applicationConfig, registryConfig, group);
            var createMoonTool = new ToolRegistration(
                    new Tool(createMoonFunction.getName(), createMoonFunction.getDesc(), createMoonFunction.getTaskToolSchema()), createMoonFunction
            );

            // 查询
            MoonQueryFunction queryMoonFunction = new MoonQueryFunction(applicationConfig, registryConfig, group);
            var queryMoonTool = new ToolRegistration(
                    new Tool(queryMoonFunction.getName(), queryMoonFunction.getDesc(), queryMoonFunction.getTaskQuerySchema()), queryMoonFunction
            );

            // id查询
            MoonGetFunction queryIdFunction = new MoonGetFunction(applicationConfig, registryConfig, group);
            var queryIdMoonTool = new ToolRegistration(
                    new Tool(queryIdFunction.getName(), queryIdFunction.getDesc(), queryIdFunction.getTaskQuerySchema()), queryIdFunction
            );

            // 添加工具类
            syncServer.addTool(createMoonTool);
            syncServer.addTool(queryMoonTool);
            syncServer.addTool(queryIdMoonTool);

            log.info("Successfully registered moon tool");
        } catch (Exception e) {
            log.error("Failed to register moon tool", e);
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
            log.info("Stopping MoonMcpServer...");
            this.syncServer.closeGracefully();
        }
    }
}