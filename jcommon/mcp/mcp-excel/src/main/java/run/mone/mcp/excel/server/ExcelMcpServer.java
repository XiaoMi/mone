package run.mone.mcp.excel.server;

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
import run.mone.mcp.excel.function.ExcelFunction;
import run.mone.mcp.excel.service.ExcelService;

@Slf4j
@Component
public class ExcelMcpServer {

    private final ServerMcpTransport transport;
    private McpSyncServer syncServer;

    public ExcelMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
        log.info("ExcelMcpServer initialized with transport: {}", transport);
    }

    public McpSyncServer start() {
        log.info("Starting ExcelMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("excel_mcp_server", "1.0.0")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        // 注册excel工具
        log.info("Registering excel tool...");
        try {
            ExcelFunction excelFunction = new ExcelFunction(new ExcelService());
            var excelToolRegistration = new ToolRegistration(
                    new Tool(excelFunction.getName(), excelFunction.getDesc(), excelFunction.getToolScheme()),
                    excelFunction
            );
            syncServer.addTool(excelToolRegistration);

            log.info("Successfully registered excel tool");
        } catch (Exception e) {
            log.error("Failed to register excel tool", e);
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
            log.info("Stopping ExcelMcpServer...");
            this.syncServer.closeGracefully();
        }
    }
} 