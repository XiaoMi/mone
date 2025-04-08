
package run.mone.mcp.idea.composer.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpServer.ToolStreamRegistration;
import run.mone.hive.mcp.server.McpSyncServer;
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

    public IdeaComposerMcpServer(ServerMcpTransport transport,
                                 IdeaService ideaService) {
        this.transport = transport;
        this.ideaService = ideaService;
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
        CodeReviewFunction codeReviewFunction = new CodeReviewFunction(ideaService);
        CreateCommentFunction createCommentFunction = new CreateCommentFunction(ideaService);
        CreateMethodFunction createMethodFunction = new CreateMethodFunction(ideaService);

        var toolStreamRegistrationForComposer = new ToolStreamRegistration(new Tool(generateBizCodeFunc.getName(), generateBizCodeFunc.getDesc(), generateBizCodeFunc.getToolScheme()), generateBizCodeFunc);
        var toolStreamRegistrationForCodeReview = new ToolStreamRegistration(new Tool(codeReviewFunction.getName(), codeReviewFunction.getDesc(), codeReviewFunction.getToolScheme()), codeReviewFunction);
        var toolStreamRegistrationForCreateComment = new ToolStreamRegistration(new Tool(createCommentFunction.getName(), createCommentFunction.getDesc(), createCommentFunction.getToolScheme()), createCommentFunction);
        var toolStreamRegistrationForCreateMethod = new ToolStreamRegistration(new Tool(createMethodFunction.getName(), createMethodFunction.getDesc(), createMethodFunction.getToolScheme()), createMethodFunction);
        var toolRegistrationForGitPush = new McpServer.ToolRegistration(new Tool(gitPushFunction.getName(), gitPushFunction.getDesc(), gitPushFunction.getToolScheme()), gitPushFunction);

        syncServer.addStreamTool(toolStreamRegistrationForComposer);
        syncServer.addStreamTool(toolStreamRegistrationForCodeReview);
        syncServer.addStreamTool(toolStreamRegistrationForCreateComment);
        syncServer.addStreamTool(toolStreamRegistrationForCreateMethod);
        syncServer.addTool(toolRegistrationForGitPush);

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
