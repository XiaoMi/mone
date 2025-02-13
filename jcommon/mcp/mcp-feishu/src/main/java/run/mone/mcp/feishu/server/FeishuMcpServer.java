package run.mone.mcp.feishu.server;

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
import run.mone.mcp.feishu.function.FeishuDocFunction;
import run.mone.mcp.feishu.service.FeishuDocService;

@Slf4j
@Component
public class FeishuMcpServer {

    private final ServerMcpTransport transport;
    private final FeishuDocService docService;
    private final ObjectMapper objectMapper;
    private McpSyncServer syncServer;

    public FeishuMcpServer(ServerMcpTransport transport, FeishuDocService docService, ObjectMapper objectMapper) {
        this.transport = transport;
        this.docService = docService;
        this.objectMapper = objectMapper;
        log.info("FeishuMcpServer initialized with transport: {}", transport);
    }

    public McpSyncServer start() {
        log.info("Starting FeishuMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("lark_doc_mcp", "1.0.0")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        // 注册飞书文档工具
        log.info("Registering feishu doc tool...");
        try {
            FeishuDocFunction function = new FeishuDocFunction(docService, objectMapper);
            var toolRegistration = new ToolRegistration(
                    new Tool(function.getName(), function.getDesc(), function.getToolScheme()), function
            );
            syncServer.addTool(toolRegistration);
            log.info("Successfully registered feishu doc tool");
        } catch (Exception e) {
            log.error("Failed to register feishu doc tool", e);
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
            log.info("Stopping FeishuMcpServer...");
            this.syncServer.closeGracefully();
        }
    }
} 