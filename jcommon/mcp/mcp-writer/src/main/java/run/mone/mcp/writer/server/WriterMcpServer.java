
package run.mone.mcp.writer.server;

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
import run.mone.mcp.writer.function.WriterFunction;

@Slf4j
@Component
public class WriterMcpServer {

    private final ServerMcpTransport transport;

    private final WriterFunction writerFunction;

    private McpSyncServer syncServer;

    @Value("${mcp.transport.type:sse}")
    private String transportType;

    public WriterMcpServer(ServerMcpTransport transport, WriterFunction writerFunction) {
        this.transport = transport;
        this.writerFunction = writerFunction;
    }

    public void start() {
        log.info("transport type:{}", transportType);
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("writer_mcp", "0.0.2")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        var toolStreamRegistration = new ToolStreamRegistration(
                new Tool(writerFunction.getName(), writerFunction.getDesc(), writerFunction.getToolScheme()), writerFunction
        );
        syncServer.addStreamTool(toolStreamRegistration);
    }

    @PostConstruct
    public void init() {
        start();
    }

    @PreDestroy
    public void stop() {
    }
}
