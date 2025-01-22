
package run.mone.mcp.filesystem.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.filesystem.function.FilesystemFunction;
import run.mone.hive.mcp.server.McpServer.ToolRegistration;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;

@Slf4j
@Component
public class FilesystemMcpServer {

    private ServerMcpTransport transport;

    private McpSyncServer syncServer;

    @Value("${filesystem.root}")
    private String filesystemRoot;

    public FilesystemMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
        log.info("FilesystemMcpServer initialized with transport: {}", transport);
    }

    public McpSyncServer start() {
        log.info("Starting FilesystemMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("filesystem_mcp_server", "1.0.0")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        // 注册filesystem工具
        log.info("Registering filesystem tool...");
        try {
            FilesystemFunction filesystemFunction = new FilesystemFunction(Lists.newArrayList("/"),new ObjectMapper());
            var filesystemToolRegistration = new ToolRegistration(
                    new Tool(filesystemFunction.getName(), filesystemFunction.getDesc(), filesystemFunction.getFilesystemToolSchema()), filesystemFunction
            );
            syncServer.addTool(filesystemToolRegistration);

            log.info("Successfully registered filesystem tool");
        } catch (Exception e) {
            log.error("Failed to register filesystem tool", e);
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
            log.info("Stopping FilesystemMcpServer...");
            this.syncServer.closeGracefully();
        }
    }
}
