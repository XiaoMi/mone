package run.mone.mcp.linuxagent;

import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpServer.ToolRegistration;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.mcp.linuxagent.tool.*;
import run.mone.mcp.linuxagent.McpTool;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class LinuxAgentMcpServer {

    private ServerMcpTransport transport;
    private McpSyncServer syncServer;

    public LinuxAgentMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
    }

    public McpSyncServer start() {
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("linux-agent", "0.1.0")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        List<ToolRegistration> toolRegistrations = Stream.of(
                new CaptureScreen(),
                new MouseClick(),
                new MouseLeftClick(),
                new MouseDoubleClick(),
                new KeyboardInputKey(),
                new KeyboardInputHotkey(),
                new KeyboardInputString(),
                new ExecuteCommand(),
                new ExecuteCommandNonBlocking(),
                new Wait()
        ).map(it -> new ToolRegistration(
                new Tool(it.getName(), it.getDesc(), it.getToolScheme()),
                it
        )).collect(Collectors.toList());

        toolRegistrations.forEach(syncServer::addTool);
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
