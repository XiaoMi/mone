package run.mone.hive.mcp.demo;

import java.util.List;
import java.util.Map;

import run.mone.hive.mcp.server.McpServer;
import run.mone.hive.mcp.server.McpServer.ToolRegistration;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.spec.McpSchema.CallToolResult;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.hive.mcp.spec.McpSchema.TextContent;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

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
            .capabilities(ServerCapabilities.builder()
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
        var toolRegistration = new ToolRegistration(
            new Tool("calculator", "Basic calculator", toolSchema),
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
                
                return new CallToolResult(List.of(new TextContent(String.valueOf(result))), false);
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
