package run.mone.mcp.nacosservice.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.nacosservice.function.NacosServiceFunction;

import java.util.Properties;


@Slf4j
@Component
public class NacosServiceMcpServer {

    private ServerMcpTransport transport;

    private McpSyncServer syncServer;

    @Value("${nacos.username}")
    private String username;
    @Value("${nacos.password}")
    private String password;
    @Value("${nacos.serverAddr}")
    private String serverAddr;
    @Value("${nacos.namespace}")
    private String namespace;

    public NacosServiceMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
        log.info("nacosServiceMcpServer initialized with transport: {}", transport);
    }

    public McpSyncServer start() {
        log.info("Starting nacosServiceMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("nacosservice_mcp", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        try {
            Properties properties = new Properties();
            properties.put("namespace",namespace);
            properties.put("username",username);
            properties.put("password",password);
            properties.put("serverAddr",serverAddr);
            NacosServiceFunction nacosServiceFunction = new NacosServiceFunction(properties);
            var sqlToolRegistration = new McpServer.ToolRegistration(
                    new McpSchema.Tool(nacosServiceFunction.getName(), nacosServiceFunction.getDesc(), nacosServiceFunction.getConfigToolSchema()),nacosServiceFunction);
            syncServer.addTool(sqlToolRegistration);

            log.info("Successfully registered nacosservice tool");
        } catch (Exception e) {
            log.error("Failed to register nacosservice tool", e);
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
            log.info("Stopping nacosServiceMcpServer...");
            this.syncServer.closeGracefully();
        }
    }

}
