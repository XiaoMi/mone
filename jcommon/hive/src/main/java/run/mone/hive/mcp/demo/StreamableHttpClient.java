package run.mone.hive.mcp.demo;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import run.mone.hive.mcp.core.client.transport.HttpClientStreamableHttpTransport;
import run.mone.hive.mcp.jsonjackson2.jackson.JacksonMcpJsonMapper;
import run.mone.hive.mcp.core.spec.McpClientTransport;
import run.mone.hive.mcp.core.spec.McpSchema.ClientCapabilities;
import run.mone.hive.mcp.core.spec.McpSchema.ListToolsResult;
import run.mone.hive.mcp.core.spec.McpSchema.Root;
import run.mone.hive.mcp.core.spec.McpTransport;
import run.mone.hive.mcp.core.spec.McpSchema.CallToolRequest;
import run.mone.hive.mcp.core.spec.McpSchema.CallToolResult;
import run.mone.hive.mcp.core.client.McpClient;
import run.mone.hive.mcp.core.client.McpSyncClient;
import run.mone.hive.mcp.core.client.McpAsyncClient;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.RequestBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class StreamableHttpClient {

    /**
     * 测试使用了formulahendry/mcp-server-code-runner这个mcp server
     * 本地启动其streamable http 模式执行下面两条命令:
     * npm install -g mcp-server-code-runner@latest
     * mcp-server-code-runner --transport http
     *
     * @param args
     * @throws InterruptedException
     */

    @SneakyThrows
    public static void main(String[] args) throws InterruptedException {

        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        McpClientTransport transport = HttpClientStreamableHttpTransport
//                            .builder("http://localhost:3088")
                .builder("https://mcp.alphavantage.co")
                .jsonMapper(new JacksonMcpJsonMapper(objectMapper))
                .endpoint("https://mcp.alphavantage.co/mcp?apikey=123456&categories=core_stock_apis,options_data_apis,alpha_intelligence")
                .build();

        McpSyncClient client = McpClient.sync(transport)
                .requestTimeout(Duration.ofSeconds(10))
                .capabilities(ClientCapabilities.builder()
                        .roots(true)      // Enable roots capability
                        .build())
                .build();

        // Initialize connection
        client.initialize();

        // List available tools
        ListToolsResult tools = client.listTools();

        System.out.println("Tools: " + tools);

        // Call a tool
        CallToolResult result = client.callTool(
                new CallToolRequest(
                        "run-code",
                        Map.of(
                                "code", "print(2 + 3)",
                                "languageId", "python"
                        )
                )
        );
        System.out.println("Tool result: " + result);
    }


    public static void testAsyncClient() throws InterruptedException {
        McpClientTransport transport = HttpClientStreamableHttpTransport
                .builder("http://localhost:3088")
                .build();

        // Create an async client with custom configuration
        McpAsyncClient client = McpClient.async(transport)
                .requestTimeout(Duration.ofSeconds(10))
                .capabilities(ClientCapabilities.builder()
                        .roots(true)      // Enable roots capability
                        .build())
                .build();

        // Initialize connection and use features
        client.initialize()
                .doFinally(signalType -> {
                    client.closeGracefully().subscribe();
                })
                .subscribe();

        // List available tools asynchronously
        client.listTools()
                .doOnNext(tools -> System.out.println("Tools: " + tools))
                .subscribe();

        // Call a tool asynchronously
        // CallToolRequest callToolRequest = new CallToolRequest("chat/completions", Map.of("model", "gpt-3.5-turbo"));
        // client.callTool(callToolRequest)
        //     .doOnNext(result -> System.out.println("Tool result: " + result))
        //     .subscribe();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        Thread.sleep(10000);
        countDownLatch.countDown();
        countDownLatch.await();
    }
}
