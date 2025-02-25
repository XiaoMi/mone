package run.mone.mcp.nacosconfig.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.nacosconfig.function.NacosConfigFunction;

import java.util.Properties;

@Slf4j
@Component
public class NacosConfigMcpServer {

    private ServerMcpTransport transport;

    private McpSyncServer syncServer;

    @Value("${nacos.username}")
    private String username;
    @Value("${nacos.password}")
    private String password;
    @Value("${nacos.serverAddr}")
    private String serverAddr;

    public NacosConfigMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
        log.info("nacosConfigMcpServer initialized with transport: {}", transport);
    }

    public McpSyncServer start() {
        log.info("Starting nacosConfigMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("nacosconfig_mcp", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        try {
            Properties properties = new Properties();
            properties.put("namespace","");
            properties.put("username",username);
            properties.put("password",password);
            properties.put("serverAddr",serverAddr);
            NacosConfigFunction nacosConfigFunction = new NacosConfigFunction(properties);
            var sqlToolRegistration = new McpServer.ToolRegistration(
                    new McpSchema.Tool(nacosConfigFunction.getName(), nacosConfigFunction.getDesc(), nacosConfigFunction.getConfigToolSchema()),nacosConfigFunction);
            syncServer.addTool(sqlToolRegistration);

            log.info("Successfully registered nacosconfig tool");
        } catch (Exception e) {
            log.error("Failed to register nacosconfig tool", e);
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
            log.info("Stopping nacosConfigMcpServer...");
            this.syncServer.closeGracefully();
        }
    }

}
