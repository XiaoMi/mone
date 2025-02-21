package run.mone.mcp.file.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.file.function.FileFormatFunction;


@Slf4j
@Component
public class FileFormatServer {

    // TODO:后续支持github
    @Autowired
    private ServerMcpTransport transport;

    private McpSyncServer syncServer;


    public FileFormatServer() {
        log.info("FileFormatServer initialized with transport: {}", transport);
    }

    public McpSyncServer start() {
        log.info("Starting FileFormatServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("file_format_mcp", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        try {
            FileFormatFunction fileFormatFunction = new FileFormatFunction();
            var sqlToolRegistration = new McpServer.ToolRegistration(
                    new McpSchema.Tool(fileFormatFunction.getName(), fileFormatFunction.getDesc(), fileFormatFunction.getFileFormatSchema()), fileFormatFunction
            );
            syncServer.addTool(sqlToolRegistration);

            log.info("Successfully registered file format tool");
        } catch (Exception e) {
            log.error("Failed to register file format tool", e);
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
            log.info("Stopping file format McpServer...");
            this.syncServer.closeGracefully();
        }
    }

}
