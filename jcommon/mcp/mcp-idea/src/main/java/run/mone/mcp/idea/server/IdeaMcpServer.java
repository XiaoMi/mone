package run.mone.mcp.idea.server;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpServer.ToolRegistration;
import run.mone.hive.mcp.server.McpServer.ToolStreamRegistration;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.idea.config.Const;
import run.mone.mcp.idea.function.CodeReviewFunction;
import run.mone.mcp.idea.function.CreateCommentFunction;
import run.mone.mcp.idea.function.CreateMethodFunction;
import run.mone.mcp.idea.function.GitPushFunction;
import run.mone.mcp.idea.function.IdeaFunctions;
import run.mone.mcp.idea.function.MethodRenameFunction;
import run.mone.mcp.idea.function.OpenClassFunction;

@Slf4j
@Component
public class IdeaMcpServer {

    private ServerMcpTransport transport;

    private CodeReviewFunction codeReviewFunction;

    private CreateCommentFunction createCommentFunction;

    private GitPushFunction gitPushFunction;

    private MethodRenameFunction methodRenameFunction;

    private CreateMethodFunction createMethodFunction;

    private McpSyncServer syncServer;

    public IdeaMcpServer(ServerMcpTransport transport,
                         CodeReviewFunction codeReviewFunction,
                         CreateCommentFunction createCommentFunction,
                         GitPushFunction gitPushFunction,
                         MethodRenameFunction methodRenameFunction,
                         CreateMethodFunction createMethodFunction) {
        this.transport = transport;
        this.codeReviewFunction = codeReviewFunction;
        this.createCommentFunction = createCommentFunction;
        this.gitPushFunction = gitPushFunction;
        this.methodRenameFunction = methodRenameFunction;
        this.createMethodFunction = createMethodFunction;
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
        var toolRegistrationGitPush = new ToolRegistration(
                new Tool(gitPushFunction.getName(), gitPushFunction.getDesc(), gitPushFunction.getToolScheme()), gitPushFunction
        );
        var toolRegistrationOpenClass = new ToolRegistration(new Tool(openClassFunc.getName(), openClassFunc.getDesc(), openClassFunc.getToolScheme()), openClassFunc);

        syncServer.addTool(toolRegistration);
        syncServer.addTool(toolRegistrationCreateUnitTest);
        syncServer.addTool(toolRegistrationGitPush);
        syncServer.addTool(toolRegistrationOpenClass);


        //代码review
        var toolStreamRegistration = new ToolStreamRegistration(
                new Tool(codeReviewFunction.getName(), codeReviewFunction.getDesc(), codeReviewFunction.getToolScheme()), codeReviewFunction
        );
        syncServer.addStreamTool(toolStreamRegistration);

        syncServer.addTool(new ToolRegistration(
                new Tool(methodRenameFunction.getName(), methodRenameFunction.getDesc(), methodRenameFunction.getToolScheme())
                , methodRenameFunction
        ));

        //创建注释
        var toolRegistrationCreateComment = new ToolStreamRegistration(
                new Tool(createCommentFunction.getName(), createCommentFunction.getDesc(), createCommentFunction.getToolScheme()), createCommentFunction
        );
        syncServer.addStreamTool(toolRegistrationCreateComment);

        //创建方法
        var toolRegistrationCreateMethod = new ToolStreamRegistration(
                new Tool(createMethodFunction.getName(), createMethodFunction.getDesc(), createMethodFunction.getToolScheme()), createMethodFunction
        );
        syncServer.addStreamTool(toolRegistrationCreateMethod);

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
