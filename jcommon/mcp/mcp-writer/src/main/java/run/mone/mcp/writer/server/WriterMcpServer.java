
package run.mone.mcp.writer.server;

import com.google.common.collect.Maps;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.grpc.server.GrpcMcpServer;
import run.mone.hive.mcp.grpc.transport.GrpcServerTransport;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpServer.ToolStreamRegistration;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.writer.function.WriterFunction;

import java.util.concurrent.CopyOnWriteArrayList;

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
        if (transportType.equals("grpc")) {
            //session->connect 直接就会拉起来grpc
            McpSchema.ServerCapabilities serverCapabilities = new ServerCapabilities(Maps.newHashMap(), new ServerCapabilities.LoggingCapabilities(), new ServerCapabilities.PromptCapabilities(false), new ServerCapabilities.ResourceCapabilities(false, false), new ServerCapabilities.ToolCapabilities(true));
            GrpcMcpServer server = new GrpcMcpServer(transport, serverCapabilities, new CopyOnWriteArrayList<>(), new CopyOnWriteArrayList<>());
            if (this.transport instanceof GrpcServerTransport gst) {
                gst.setGrpcServer(server);
            }
            var toolStreamRegistration = new ToolStreamRegistration(
                    new Tool(writerFunction.getName(), writerFunction.getDesc(), writerFunction.getToolScheme()), writerFunction
            );
            server.addStreamTool(toolStreamRegistration);
        } else {
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
    }

    @PostConstruct
    public void init() {
        start();
    }

    @PreDestroy
    public void stop() {
    }
}
