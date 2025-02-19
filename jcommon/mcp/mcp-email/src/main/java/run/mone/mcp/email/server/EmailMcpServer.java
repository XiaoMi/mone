package run.mone.mcp.email.server;

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
import run.mone.mcp.email.function.EmailFunction;

@Slf4j
@Component
public class EmailMcpServer {

    private final ServerMcpTransport transport;

    private McpSyncServer syncServer;

    public EmailMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
        log.info("EmailMcpServer initialized with transport: {}", transport);
    }

    public McpSyncServer start() {
        log.info("Starting EmailMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("email_mcp_server", "1.0.0")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        log.info("Registering email tool...");

        try {
            EmailFunction emailFunction = new EmailFunction(new ObjectMapper());
            var emailToolRegistration = new ToolRegistration(
                    new Tool(emailFunction.getName(), emailFunction.getDesc(), emailFunction.getEmailToolSchema()),
                    emailFunction
            );
            syncServer.addTool(emailToolRegistration);
            log.info("Successfully registered email tool");
        } catch (Exception e) {
            log.error("Failed to register email tool", e);
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
            log.info("Stopping EmailMcpServer...");
            this.syncServer.closeGracefully();
        }
    }
} 