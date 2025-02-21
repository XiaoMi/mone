package run.mone.mcp.chrome.server;

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
import run.mone.mcp.chrome.function.ClickAfterRefreshFunction;
import run.mone.mcp.chrome.function.CodeFunction;
import run.mone.mcp.chrome.function.FullPageFunction;
import run.mone.mcp.chrome.function.GetContentFunction;
import run.mone.mcp.chrome.function.OpenTabFunction;
import run.mone.mcp.chrome.function.OperationFunction;
import run.mone.mcp.chrome.function.ScrollFunction;

@Slf4j
@Component
public class ChromeMcpServer {
    private final ServerMcpTransport transport;
    private final ObjectMapper objectMapper;
    private McpSyncServer syncServer;

    public ChromeMcpServer(ServerMcpTransport transport, ObjectMapper objectMapper) {
        this.transport = transport;
        this.objectMapper = objectMapper;
    }

    public McpSyncServer start() {
        log.info("Starting ChromeMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("chrome_mcp_server", "1.0.0")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        log.info("Registering chrome tools...");
        try {
            // 注册 ClickAfterRefresh 功能
            ClickAfterRefreshFunction clickAfterRefreshFunction = new ClickAfterRefreshFunction(objectMapper);
            var clickAfterRefreshRegistration = new ToolRegistration(
                    new Tool(clickAfterRefreshFunction.getName(), 
                           clickAfterRefreshFunction.getDesc(), 
                           clickAfterRefreshFunction.getToolScheme()), 
                    clickAfterRefreshFunction
            );
            syncServer.addTool(clickAfterRefreshRegistration);
            log.info("Successfully registered ClickAfterRefresh tool");

            // 注册 CodeAction 功能
            CodeFunction codeActionFunction = new CodeFunction(objectMapper);
            var codeActionRegistration = new ToolRegistration(
                    new Tool(codeActionFunction.getName(),
                            codeActionFunction.getDesc(),
                            codeActionFunction.getToolScheme()),
                    codeActionFunction
            );
            syncServer.addTool(codeActionRegistration);
            log.info("成功注册 CodeAction 工具");

            // 注册 FullPageAction 功能
            FullPageFunction fullPageActionFunction = new FullPageFunction(objectMapper);
            var fullPageActionRegistration = new ToolRegistration(
                    new Tool(fullPageActionFunction.getName(),
                            fullPageActionFunction.getDesc(),
                            fullPageActionFunction.getToolScheme()),
                    fullPageActionFunction
            );
            syncServer.addTool(fullPageActionRegistration);
            log.info("Successfully registered FullPageAction tool");

            // 注册 GetContentAction 功能
            GetContentFunction getContentActionFunction = new GetContentFunction(objectMapper);
            var getContentActionRegistration = new ToolRegistration(
                    new Tool(getContentActionFunction.getName(),
                            getContentActionFunction.getDesc(),
                            getContentActionFunction.getToolScheme()),
                    getContentActionFunction
            );
            syncServer.addTool(getContentActionRegistration);
            log.info("成功注册 GetContentAction 工具");

            // 注册 OpenTabAction 功能
            OpenTabFunction openTabActionFunction = new OpenTabFunction(objectMapper);
            var openTabActionRegistration = new ToolRegistration(
                    new Tool(openTabActionFunction.getName(),
                            openTabActionFunction.getDesc(),
                            openTabActionFunction.getToolScheme()),
                    openTabActionFunction
            );
            syncServer.addTool(openTabActionRegistration);
            log.info("成功注册 OpenTabAction 工具");

            // 注册 OperationAction 功能
            OperationFunction operationActionFunction = new OperationFunction(objectMapper);
            var operationActionRegistration = new ToolRegistration(
                    new Tool(operationActionFunction.getName(),
                            operationActionFunction.getDesc(),
                            operationActionFunction.getToolScheme()),
                    operationActionFunction
            );
            syncServer.addTool(operationActionRegistration);
            log.info("成功注册 OperationAction 工具");

            // 注册 ScrollAction 功能
            ScrollFunction scrollActionFunction = new ScrollFunction(objectMapper);
            var scrollActionRegistration = new ToolRegistration(
                    new Tool(scrollActionFunction.getName(),
                            scrollActionFunction.getDesc(),
                            scrollActionFunction.getToolScheme()),
                    scrollActionFunction
            );
            syncServer.addTool(scrollActionRegistration);
            log.info("成功注册 ScrollAction 工具");

        } catch (Exception e) {
            log.error("Failed to register chrome tools", e);
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
            log.info("Stopping ChromeMcpServer...");
            this.syncServer.closeGracefully();
        }
    }
} 