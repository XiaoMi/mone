package run.mone.mcp.vuetemp.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.vuetemp.function.VueTemplateFunction;
import run.mone.mcp.vuetemp.function.NaturalLangTemplateFunction;

public class VueTemplateMcpServer {

    // 避免编译时 Lombok 未处理导致的 log 不可用
    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(VueTemplateMcpServer.class);

    private final ServerMcpTransport transport;
    private McpSyncServer syncServer;

    public VueTemplateMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
    }

    public McpSyncServer start() {
        McpSyncServer server = McpServer.using(transport)
                .serverInfo("vue_template_mcp", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        // 注册结构化参数版本
        VueTemplateFunction function = new VueTemplateFunction();
        var registration = new McpServer.ToolStreamRegistration(
                new McpSchema.Tool(function.getName(), function.getDesc(), function.getToolScheme()), function);
        server.addStreamTool(registration);
        logger.info("VueTemplateMcpServer registered tool: {}", function.getName());

        // 注册自然语言版本
        NaturalLangTemplateFunction nlFunction = new NaturalLangTemplateFunction();
        var nlRegistration = new McpServer.ToolStreamRegistration(
                new McpSchema.Tool(nlFunction.getName(), nlFunction.getDesc(), nlFunction.getToolScheme()), nlFunction);
        server.addStreamTool(nlRegistration);
        logger.info("VueTemplateMcpServer registered tool: {}", nlFunction.getName());
        return server;
    }

    @PostConstruct
    public void init() {
        this.syncServer = start();
    }

    @PreDestroy
    public void stop() {
        if (this.syncServer != null) {
            this.syncServer.closeGracefully();
        }
    }
}


