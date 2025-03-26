package run.mone.mcp.rocketmq.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.hive.mcp.server.McpServer.ToolRegistration;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.mcp.rocketmq.function.RocketMqFunction;

@Slf4j
@Component
public class RocketMqMcpServer {

    private ServerMcpTransport transport;

    private McpSyncServer syncServer;

    private RocketMqFunction rocketMqFunction;

//    @Value("${rocketmq.nameSrvAddress}")
//    private String nameSrvAddress;
//
//    @Value("${rocketmq.group}")
//    private String group;
//
//    @Value("${rocketmq.accessKey}")
//    private String accessKey;
//
//    @Value("${rocketmq.secureKey}")
//    private String secureKey;

    public RocketMqMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
        log.info("RocketMqMcpServer initialized with transport: {}", transport);
    }

    @SneakyThrows
    public McpSyncServer start() {
        log.info("Starting RocketMqMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("rocket_mcp", "1.0.0")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        log.info("Registering rocketmq-sender tool...");
        try {
            String nameSrvAddress = System.getenv().getOrDefault("namesrv_addr", "");
            String group = System.getenv().getOrDefault("group", "defaultGroup");
            String accessKey = System.getenv().getOrDefault("access_key", "");
            String secureKey = System.getenv().getOrDefault("secure_key", "");
            if (StringUtils.isEmpty(nameSrvAddress) || StringUtils.isEmpty(group) || StringUtils.isEmpty(accessKey) || StringUtils.isEmpty(secureKey)) {
                throw new Exception("mvp config invalid");
            }
            rocketMqFunction = new RocketMqFunction(nameSrvAddress, group, accessKey, secureKey);
            var rocketMqToolRegistration = new ToolRegistration(
                    new Tool(rocketMqFunction.getName(), rocketMqFunction.getDesc(), rocketMqFunction.getSqlToolSchema()), rocketMqFunction
            );
            syncServer.addTool(rocketMqToolRegistration);
            log.info("Successfully registered rocketmq-sender tool");
        } catch (Exception e) {
            log.error("Failed to register rocketmq-sender tool", e);
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
        if (rocketMqFunction != null) {
            log.info("rocketMqFunction is not null, {}", rocketMqFunction);
        }
        if (this.syncServer != null) {
            log.info("Stopping RocketMqMcpServer...");
            this.syncServer.closeGracefully();
        }

    }
}