package run.mone.hive.mcp.demo;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import run.mone.hive.mcp.client.McpClient;
import run.mone.hive.mcp.client.McpSyncClient;
import run.mone.hive.mcp.client.transport.streamable.StreamableHttpClientTransport;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.McpSchema.CallToolResult;
import reactor.core.publisher.Mono;
import java.util.Map;
import java.util.HashMap;

import java.time.Duration;

/**
 * Example usage of StreamableHttpClientTransport.
 *
 * This example demonstrates how to create and use the Streamable HTTP transport
 * implementation for the custom MCP framework.
 */
public class StreamableHttpClientTransportExample {

    public static void main(String[] args) {
        // demo1(args);
       
        ObjectMapper customMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        StreamableHttpClientTransport customTransport = StreamableHttpClientTransport
                .builder("http://localhost:3088")
                .objectMapper(customMapper)
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        McpSyncClient client = McpClient.using(customTransport)
                .requestTimeout(Duration.ofSeconds(120))
                .msgConsumer(msg -> System.out.println("Handling message: " + msg))
                .capabilities(McpSchema.ClientCapabilities.builder()
                        .roots(true)
                        .build())
                .sync();

        client.initialize();

        System.out.println("======== Tools ======== : " + client.getTools());
        String toolName = "run-code";
        Map<String, Object> toolArguments = new HashMap<>();
        toolArguments.put("code", "print(2 + 3)");
        toolArguments.put("languageId", "python");
        McpSchema.CallToolRequest request = new McpSchema.CallToolRequest(toolName, toolArguments);
        System.out.println("======== Call Tool ========" + "Request: " + request + "\n\n Tool result: " + client.callTool(request));
        System.out.println("======== END ========");
    }

    public static void demo1(String[] args) {
        // Example 1: Basic usage with default configuration
        StreamableHttpClientTransport transport = StreamableHttpClientTransport
                .builder("http://localhost:3088")
                .build();

        // Example 2: Custom configuration with object mapper and timeout
        // ObjectMapper customMapper = new ObjectMapper();
        // Configure your ObjectMapper as needed
        // customMapper.configure(...);
        ObjectMapper customMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        StreamableHttpClientTransport customTransport = StreamableHttpClientTransport
                .builder("http://localhost:3088")
                .objectMapper(customMapper)
                .endpoint("/custom-mcp-endpoint")
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        // Example 3: With HTTP request customization
        StreamableHttpClientTransport transportWithAuth = StreamableHttpClientTransport
                .builder("http://localhost:8080")
                .httpRequestCustomizer((builder, method, uri, body, context) -> {
                    // Add custom headers, e.g., authentication
                    builder.header("Authorization", "Bearer your-token-here");
                    builder.header("X-Custom-Header", "custom-value");
                })
                .build();

        // Example 4: Connect and handle messages
        transport.connect(messageMono -> {
            // Handle incoming messages
            return messageMono.flatMap(message -> {
                System.out.println("Received message: " + message);
                // Process the message and return a response
                return Mono.just(message);
            });
        }).subscribe();

        // Example 5: Send a message
        McpSchema.JSONRPCRequest request = new McpSchema.JSONRPCRequest(
                "2.0",
                "method-name",
                new Object(),  // params
                "request-id"
        );

        transport.sendMessage(request)
                .doOnSuccess(response -> {
                    System.out.println("Message sent successfully, response: " + response);
                })
                .doOnError(error -> {
                    System.err.println("Error sending message: " + error.getMessage());
                })
                .subscribe();

        // Example 6: Close gracefully
        transport.closeGracefully()
                .doOnSuccess(v -> {
                    System.out.println("Transport closed gracefully");
                })
                .subscribe();
    }

    // Example 7: Using with async customizer
    public static StreamableHttpClientTransport createWithAsyncCustomizer() {
        return StreamableHttpClientTransport
                .builder("http://localhost:8080")
                .asyncHttpRequestCustomizer((builder, method, uri, body, context) -> {
                    // Async customization - can return Mono for async operations
                    return Mono.fromSupplier(() -> {
                        // You can perform async operations here
                        builder.header("X-Request-Time", String.valueOf(System.currentTimeMillis()));
                        return builder;
                    });
                })
                .build();
    }
}
