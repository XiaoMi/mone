
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
import run.mone.mcp.idea.function.IdeaFunctions;

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
                .serverInfo("idea_mcp", "0.0.1")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        IdeaFunctions.IdeaOperationFunction function = new IdeaFunctions.IdeaOperationFunction(ideaPort);
        IdeaFunctions.TestGenerationFunction function2 = new IdeaFunctions.TestGenerationFunction(ideaPort);
        var toolRegistration = new ToolRegistration(
                new Tool(function.getName(), function.getDesc(), function.getToolScheme()), function
        );
        var toolRegistration2 = new ToolRegistration(
                new Tool(function2.getName(), function2.getDesc(), function2.getToolScheme()), function2
        );

        syncServer.addTool(toolRegistration);
        syncServer.addTool(toolRegistration2);

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
