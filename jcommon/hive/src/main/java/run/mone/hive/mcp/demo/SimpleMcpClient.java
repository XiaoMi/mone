package run.mone.hive.mcp.demo;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import reactor.core.publisher.Flux;
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

public class SimpleMcpClient {
    
    public static void main(String[] args) {
        // new SimpleMcpClient().simpleClientViaStdio();
        try {
            new SimpleMcpClient().simpleClientViaSSE();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void simpleClientViaSSE() throws InterruptedException {
        // Create a sync client with custom configuration, using sse transport
        ClientMcpTransport transport = new HttpClientSseClientTransport("http://localhost:8080");
        try (McpSyncClient client = McpClient.using(transport)
                .requestTimeout(Duration.ofSeconds(10))
                .capabilities(ClientCapabilities.builder()
                        .roots(true)      // Enable roots capability
                        .build())
                .sync()) {
            client.initialize();
            ListToolsResult listTools = client.listTools();
            System.out.println("listTools: " + listTools);
            // Call a tool
            Flux<CallToolResult> result = client.callToolStream(
                    new CallToolRequest("calculator",
                            Map.of("operation", "add", "a", 2, "b", 3))
            );
            // TODO
            // result.subscribe(System.out::println);
            result.subscribe(new Subscriber<CallToolResult>() {
                @Override
                public void onSubscribe(Subscription s) {
                    s.request(Long.MAX_VALUE);
                }


                @Override
                public void onNext(CallToolResult t) {
                    System.out.println("onNext: " + t);
                }

                @Override
                public void onError(Throwable t) {
                    System.out.println("onError: " + t);
                }

                @Override
                public void onComplete() {
                    System.out.println("onComplete");
                }
            });

            TimeUnit.SECONDS.sleep(30);
        }
    }

    public void simpleClientViaStdio() {
        // Create a sync client with custom configuration, using stdio transport
        ServerParameters params = ServerParameters.builder("docker")
            .args("run", "-i", "--rm", "mcp/fetch", "--ignore-robots-txt")
            .build();
        ClientMcpTransport transport = new StdioClientTransport(params);
        ListToolsResult listTools;
        try (McpSyncClient client = McpClient.using(transport)
                .requestTimeout(Duration.ofSeconds(10))
                .capabilities(ClientCapabilities.builder()
                        .roots(true)      // Enable roots capability
                        .build())
                .sync()) {
            client.initialize();
            listTools = client.listTools();
        }
        System.out.println("listTools: " + listTools);
    }
}
