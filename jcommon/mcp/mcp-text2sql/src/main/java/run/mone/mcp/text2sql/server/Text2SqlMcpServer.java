package run.mone.mcp.text2sql.server;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.text2sql.function.Text2SqlFunction;

@Slf4j
@Component
public class Text2SqlMcpServer {

    private ServerMcpTransport transport;

    private McpSyncServer syncServer;

    public Text2SqlMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
        log.info("Text2SqlMcpServer initialized with transport: {}", transport);
    }

    public McpSyncServer start() {
        log.info("Starting Text2SqlMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport).serverInfo("text2sql_mcp", "1.0.0")
            .capabilities(McpSchema.ServerCapabilities.builder().tools(true).logging().build()).sync();

        try {
            Text2SqlFunction text2SqlLabFunction = new Text2SqlFunction();
            var toolRegistration = new McpServer.ToolRegistration(new McpSchema.Tool(text2SqlLabFunction.getName(),
                text2SqlLabFunction.getDesc(), text2SqlLabFunction.getToolSchema()), text2SqlLabFunction);
            syncServer.addTool(toolRegistration);

            log.info("Successfully registered text2sql tool");
        } catch (Exception e) {
            log.error("Failed to register text2sql tool", e);
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
            log.info("Stopping Text2SqlMcpServer...");
            this.syncServer.closeGracefully();
        }
    }

}
