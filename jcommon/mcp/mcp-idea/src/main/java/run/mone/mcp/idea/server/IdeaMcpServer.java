
package run.mone.mcp.idea.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpServer.ToolRegistration;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.idea.config.Const;
import run.mone.mcp.idea.function.CreateCommentFunction;
import run.mone.mcp.idea.function.GenerateBizCodeFunction;
import run.mone.mcp.idea.function.GitPushFunction;
import run.mone.mcp.idea.function.IdeaFunctions;
import run.mone.mcp.idea.function.*;

@Slf4j
@Component
public class IdeaMcpServer {

    private ServerMcpTransport transport;

    private McpSyncServer syncServer;

    //    @Value("${idea.port}")
//    private String ideaPort;

    public IdeaMcpServer(ServerMcpTransport transport) {
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

        IdeaFunctions.IdeaOperationFunction function = new IdeaFunctions.IdeaOperationFunction(ideaPort);
        IdeaFunctions.TestGenerationFunction createUnitTestFunc = new IdeaFunctions.TestGenerationFunction(ideaPort);
        CreateCommentFunction createCommentFunc = new CreateCommentFunction(ideaPort);
        GitPushFunction gitPushFunc = new GitPushFunction(ideaPort);
        GenerateBizCodeFunction generateBizCodeFunc = new GenerateBizCodeFunction(ideaPort);
        OpenClassFunction openClassFunc = new OpenClassFunction(ideaPort);

        var toolRegistration = new ToolRegistration(
                new Tool(function.getName(), function.getDesc(), function.getToolScheme()), function
        );
        var toolRegistrationCreateUnitTest = new ToolRegistration(
                new Tool(createUnitTestFunc.getName(), createUnitTestFunc.getDesc(), createUnitTestFunc.getToolScheme()), createUnitTestFunc
        );
        var toolRegistrationCreateComment = new ToolRegistration(
                new Tool(createCommentFunc.getName(), createCommentFunc.getDesc(), createCommentFunc.getToolScheme()), createCommentFunc
        );
        var toolRegistrationGitPush = new ToolRegistration(
                new Tool(gitPushFunc.getName(), gitPushFunc.getDesc(), gitPushFunc.getToolScheme()), gitPushFunc
        );
        var toolRegistrationGenerateBizCode = new ToolRegistration(new Tool(generateBizCodeFunc.getName(), generateBizCodeFunc.getDesc(), generateBizCodeFunc.getToolScheme()), generateBizCodeFunc);
        var toolRegistrationOpenClass = new ToolRegistration(new Tool(openClassFunc.getName(), openClassFunc.getDesc(), openClassFunc.getToolScheme()), openClassFunc);

        syncServer.addTool(toolRegistration);
        syncServer.addTool(toolRegistrationCreateUnitTest);
        syncServer.addTool(toolRegistrationCreateComment);
        syncServer.addTool(toolRegistrationGitPush);
        syncServer.addTool(toolRegistrationGenerateBizCode);
        syncServer.addTool(toolRegistrationOpenClass);
        syncServer.addTool(new ToolRegistration(
                new Tool(new CodeReviewFunction(ideaPort).getName(), new CodeReviewFunction(ideaPort).getDesc(), new CodeReviewFunction(ideaPort).getToolScheme())
                , new CodeReviewFunction(ideaPort)
        ));
        syncServer.addTool(new ToolRegistration(
                new Tool(new MethodRenameFunction(ideaPort).getName(), new MethodRenameFunction(ideaPort).getDesc(), new MethodRenameFunction(ideaPort).getToolScheme())
                , new MethodRenameFunction(ideaPort)
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
