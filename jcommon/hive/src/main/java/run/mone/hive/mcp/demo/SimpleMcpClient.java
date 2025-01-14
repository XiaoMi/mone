package run.mone.hive.mcp.demo;

import java.time.Duration;
import java.util.Map;

import run.mone.hive.mcp.client.McpClient;
import run.mone.hive.mcp.client.McpSyncClient;
import run.mone.hive.mcp.client.transport.HttpClientSseClientTransport;
import run.mone.hive.mcp.spec.ClientMcpTransport;
import run.mone.hive.mcp.spec.McpSchema.CallToolRequest;
import run.mone.hive.mcp.spec.McpSchema.CallToolResult;
import run.mone.hive.mcp.spec.McpSchema.ClientCapabilities;

public class SimpleMcpClient {
    
    public static void main(String[] args) {
        // Create a sync client with custom configuration
        ClientMcpTransport transport = new HttpClientSseClientTransport("http://localhost:8080");

        McpSyncClient client = McpClient.using(transport)
            .requestTimeout(Duration.ofSeconds(10))
            .capabilities(ClientCapabilities.builder()
                .roots(true)      // Enable roots capability
                .build())
            .sync();

        // Call a tool
        CallToolResult result = client.callTool(
            new CallToolRequest("calculator",
                Map.of("operation", "add", "a", 2, "b", 3))
        );
    }
}
