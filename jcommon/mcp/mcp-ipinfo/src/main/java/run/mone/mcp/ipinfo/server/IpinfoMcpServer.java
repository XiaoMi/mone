package run.mone.mcp.ipinfo.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpServer.ToolRegistration;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.ipinfo.function.IpinfoFunction;

@Slf4j
@Component
public class IpinfoMcpServer {

    private final ServerMcpTransport transport;

    private McpSyncServer syncServer;

    public IpinfoMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
        log.info("IpinfoMcpServer initialized with transport: {}", transport);
    }

    public McpSyncServer start() {
        log.info("Starting IpinfoMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("ipinfo_mcp_server", "1.0.0")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        log.info("Registering ipinfo tool...");

        try {
            IpinfoFunction ipinfoFunction = new IpinfoFunction(new ObjectMapper());
            var ipinfoToolRegistration = new ToolRegistration(
                    new Tool(ipinfoFunction.getName(), ipinfoFunction.getDesc(), ipinfoFunction.getIpinfoToolSchema()),
                    ipinfoFunction
            );
            syncServer.addTool(ipinfoToolRegistration);
            log.info("Successfully registered ipinfo tool");
        } catch (Exception e) {
            log.error("Failed to register ipinfo tool", e);
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
            log.info("Stopping IpinfoMcpServer...");
            this.syncServer.closeGracefully();
        }
    }
}