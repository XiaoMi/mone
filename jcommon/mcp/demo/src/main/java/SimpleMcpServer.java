import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.ServerMcpTransport;

import java.util.List;
import java.util.Map;

@Component
public class SimpleMcpServer {
    private ServerMcpTransport transport;
    private McpSyncServer syncServer;

    public SimpleMcpServer(ServerMcpTransport transport) {
        this.transport = transport;
    }

    public McpSyncServer start() {
        McpSyncServer syncServer = McpServer.using(transport)
                .serverInfo("my-server", "1.0.0")
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        String toolSchema = """
            {
                "type": "object",
                "properties": {
                "operation": {
                    "type": "string"
                },
                "a": {
                    "type": "number"
                },
                "b": {
                    "type": "number"
                }
                },
                "required": ["operation", "a", "b"]
            }
            """;
        var toolRegistration = new McpServer.ToolRegistration(
                new McpSchema.Tool("calculator", "Basic calculator", toolSchema),
                arguments -> {
                    // Tool implementation
                    Map<String, Object> args = arguments;
                    String op = (String) args.get("operation");
                    double a = ((Number) args.get("a")).doubleValue();
                    double b = ((Number) args.get("b")).doubleValue();

                    double result = switch(op) {
                        case "add" -> a + b;
                        case "subtract" -> a - b;
                        case "multiply" -> a * b;
                        case "divide" -> a / b;
                        default -> throw new IllegalArgumentException("Unknown operation: " + op);
                    };

                    return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(String.valueOf(result))), false);
                }
        );

        syncServer.addTool(toolRegistration);

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
