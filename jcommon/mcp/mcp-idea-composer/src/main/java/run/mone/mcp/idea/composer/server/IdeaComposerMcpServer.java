
package run.mone.mcp.idea.composer.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.function.ChatFunction;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpServer.ToolStreamRegistration;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.idea.composer.function.*;
import run.mone.mcp.idea.composer.service.IdeaService;


@Slf4j
@Component
public class IdeaComposerMcpServer {

    private ServerMcpTransport transport;

    private McpSyncServer syncServer;

    private IdeaService ideaService;

    private RoleService roleService;

    public IdeaComposerMcpServer(ServerMcpTransport transport,
                                 IdeaService ideaService,
                                 RoleService roleService) {
        this.transport = transport;
        this.ideaService = ideaService;
        this.roleService = roleService;
    }

    public McpSyncServer start() {
        String ideaPort = System.getenv().getOrDefault("IDEA_PORT", "30000");
        log.info(ideaPort);
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("idea_composer_mcp", "0.0.2")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        ComposerFunction generateBizCodeFunc = new ComposerFunction(ideaPort);
        GitPushFunction gitPushFunction = new GitPushFunction(ideaService);
        ChatFunction chatFunction = new ChatFunction(roleService);


        var toolStreamRegistrationForComposer = new ToolStreamRegistration(new Tool(generateBizCodeFunc.getName(), generateBizCodeFunc.getDesc(), generateBizCodeFunc.getToolScheme()), generateBizCodeFunc);
        var toolRegistrationForGitPush = new McpServer.ToolRegistration(new Tool(gitPushFunction.getName(), gitPushFunction.getDesc(), gitPushFunction.getToolScheme()), gitPushFunction);
        var toolStreamRegistration = new ToolStreamRegistration(
                new Tool(chatFunction.getName(), chatFunction.getDesc("minzai"), chatFunction.getToolScheme()), chatFunction
        );

        syncServer.addStreamTool(toolStreamRegistrationForComposer);
        syncServer.addTool(toolRegistrationForGitPush);
        syncServer.addStreamTool(toolStreamRegistration);

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
