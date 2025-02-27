
package run.mone.mcp.idea.server;

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
import run.mone.mcp.idea.config.Const;
import run.mone.mcp.idea.function.CreateCommentFunction;
import run.mone.mcp.idea.function.GitPushFunction;
import run.mone.mcp.idea.function.IdeaFunctions;
import run.mone.mcp.idea.function.*;

@Slf4j
@Component
public class IdeaMcpServer {

    private ServerMcpTransport transport;

    private CodeReviewFunction codeReviewFunction;

    private CreateCommentFunction createCommentFunction;

    private GitPushFunction gitPushFunction;

    private MethodRenameFunction methodRenameFunction;

    private McpSyncServer syncServer;

    public IdeaMcpServer(ServerMcpTransport transport,
                         CodeReviewFunction codeReviewFunction,
                         CreateCommentFunction createCommentFunction,
                         GitPushFunction gitPushFunction,
                         MethodRenameFunction methodRenameFunction) {
        this.transport = transport;
        this.codeReviewFunction = codeReviewFunction;
        this.createCommentFunction = createCommentFunction;
        this.gitPushFunction = gitPushFunction;
        this.methodRenameFunction = methodRenameFunction;
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

        IdeaFunctions.IdeaOperationFunction function = new IdeaFunctions.IdeaOperationFunction(ideaPort);
        IdeaFunctions.TestGenerationFunction createUnitTestFunc = new IdeaFunctions.TestGenerationFunction(ideaPort);
        OpenClassFunction openClassFunc = new OpenClassFunction(ideaPort);

        var toolRegistration = new ToolRegistration(
                new Tool(function.getName(), function.getDesc(), function.getToolScheme()), function
        );
        var toolRegistrationCreateUnitTest = new ToolRegistration(
                new Tool(createUnitTestFunc.getName(), createUnitTestFunc.getDesc(), createUnitTestFunc.getToolScheme()), createUnitTestFunc
        );
        var toolRegistrationCreateComment = new ToolRegistration(
                new Tool(createCommentFunction.getName(), createCommentFunction.getDesc(), createCommentFunction.getToolScheme()), createCommentFunction
        );
        var toolRegistrationGitPush = new ToolRegistration(
                new Tool(gitPushFunction.getName(), gitPushFunction.getDesc(), gitPushFunction.getToolScheme()), gitPushFunction
        );
        var toolRegistrationOpenClass = new ToolRegistration(new Tool(openClassFunc.getName(), openClassFunc.getDesc(), openClassFunc.getToolScheme()), openClassFunc);

        syncServer.addTool(toolRegistration);
        syncServer.addTool(toolRegistrationCreateUnitTest);
        syncServer.addTool(toolRegistrationCreateComment);
        syncServer.addTool(toolRegistrationGitPush);
        syncServer.addTool(toolRegistrationOpenClass);

        syncServer.addTool(new ToolRegistration(
                new Tool(codeReviewFunction.getName(), codeReviewFunction.getDesc(), codeReviewFunction.getToolScheme())
                , codeReviewFunction
        ));
        syncServer.addTool(new ToolRegistration(
                new Tool(methodRenameFunction.getName(), methodRenameFunction.getDesc(), methodRenameFunction.getToolScheme())
                , methodRenameFunction
        ));

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
