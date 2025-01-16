package run.mone.hive.mcp.demo;

import java.time.Duration;
import java.util.Map;

import run.mone.hive.mcp.client.McpClient;
import run.mone.hive.mcp.client.McpSyncClient;
import run.mone.hive.mcp.client.transport.HttpClientSseClientTransport;
import run.mone.hive.mcp.client.transport.ServerParameters;
import run.mone.hive.mcp.client.transport.StdioClientTransport;
import run.mone.hive.mcp.spec.ClientMcpTransport;
import run.mone.hive.mcp.spec.McpSchema.CallToolRequest;
import run.mone.hive.mcp.spec.McpSchema.CallToolResult;
import run.mone.hive.mcp.spec.McpSchema.ClientCapabilities;
import run.mone.hive.mcp.spec.McpSchema.ListToolsResult;
import run.mone.hive.mcp.spec.McpTransport;

public class SimpleMcpClient {
    
    public static void main(String[] args) {
        // Create a sync client with custom configuration, using sse transport
        ClientMcpTransport transport = new HttpClientSseClientTransport("http://localhost:8080");

        // Create a sync client with custom configuration, using stdio transport
//        ServerParameters params = ServerParameters.builder("docker")
//            .args("run", "-i", "--rm", "mcp/fetch", "--ignore-robots-txt")
//            .build();
            
//        ClientMcpTransport transport = new StdioClientTransport(params);

        McpSyncClient client = McpClient.using(transport)
            .requestTimeout(Duration.ofSeconds(10))
            .capabilities(ClientCapabilities.builder()
                .roots(true)      // Enable roots capability
                .build())
            .sync();

        client.initialize();

        ListToolsResult listTools = client.listTools();
        System.out.println("listTools: " + listTools);
        // Call a tool
         CallToolResult result = client.callTool(
             new CallToolRequest("calculator",
                 Map.of("operation", "add", "a", 2, "b", 3))
         );
    }
}
