
package run.mone.mcp.idea.composer.server;

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
import run.mone.mcp.idea.composer.config.Const;
import run.mone.mcp.idea.composer.function.ComposerFunction;

@Slf4j
@Component
public class IdeaComposerMcpServer {

    private ServerMcpTransport transport;


    private McpSyncServer syncServer;

    public IdeaComposerMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
    }

    public McpSyncServer start() {
        String ideaPort = System.getenv().getOrDefault("IDEA_PORT", "30000");
        log.info(ideaPort);
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("idea_mcp", Const.VERSION)
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        ComposerFunction generateBizCodeFunc = new ComposerFunction(ideaPort);

        var toolRegistrationGenerateBizCode = new ToolRegistration(new Tool(generateBizCodeFunc.getName(), generateBizCodeFunc.getDesc(), generateBizCodeFunc.getToolScheme()), generateBizCodeFunc);

        syncServer.addTool(toolRegistrationGenerateBizCode);

        return syncServer;
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
