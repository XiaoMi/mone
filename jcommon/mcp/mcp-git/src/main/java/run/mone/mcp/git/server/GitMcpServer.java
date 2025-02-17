package run.mone.mcp.git.server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.git.function.GitLabFunction;

@Slf4j
@Component
public class GitMcpServer {

    // TODO:后续支持github

    private ServerMcpTransport transport;

    private McpSyncServer syncServer;

    public GitMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
        log.info("GitMcpServer initialized with transport: {}", transport);
    }

    public McpSyncServer start() {
        log.info("Starting GitMcpServer...");
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("git_mcp", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        try {
            GitLabFunction gitLabFunction = new GitLabFunction();
            var sqlToolRegistration = new McpServer.ToolRegistration(
                    new McpSchema.Tool(gitLabFunction.getName(), gitLabFunction.getDesc(), gitLabFunction.getGitLabToolSchema()), gitLabFunction
            );
            syncServer.addTool(sqlToolRegistration);

            log.info("Successfully registered git tool");
        } catch (Exception e) {
            log.error("Failed to register git tool", e);
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
            log.info("Stopping gitMcpServer...");
            this.syncServer.closeGracefully();
        }
    }

}
