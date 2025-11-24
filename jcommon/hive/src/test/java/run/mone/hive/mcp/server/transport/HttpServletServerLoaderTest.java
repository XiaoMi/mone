package run.mone.hive.mcp.server.transport;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.mone.hive.mcp.server.McpAsyncServer;
import run.mone.hive.mcp.server.transport.streamable.HttpServletServerLoader;
import run.mone.hive.mcp.server.transport.streamable.HttpServletStreamableServerTransport;
import run.mone.hive.mcp.spec.McpSchema;

import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HttpServletServerLoader 的测试类
 */
public class HttpServletServerLoaderTest {
    
    private HttpServletServerLoader loader;
    private static final int TEST_PORT = 18080; // 使用不同的端口避免冲突
    
    @BeforeEach
    public void setUp() {
        loader = HttpServletServerLoader.builder()
                .port(TEST_PORT)
                .mcpEndpoint("/test-mcp")
                .keepAliveInterval(Duration.ofSeconds(30))
                .disallowDelete(false)
                .objectMapper(new ObjectMapper())
                .authFunction(clientId -> "valid_client".equals(clientId))
                .build();
    }
    
    @AfterEach
    public void tearDown() {
        if (loader != null && loader.isRunning()) {
            loader.stop();
        }
    }
    
    @Test
    public void testBuilderCreation() {
        assertNotNull(loader, "Loader should be created successfully");
        assertEquals("http://localhost:" + TEST_PORT + "/test-mcp", loader.getServerUrl());
    }
    
    @Test
    public void testTransportCreation() {
        HttpServletStreamableServerTransport transport = loader.getTransport();
        assertNotNull(transport, "Transport should be created");
        assertTrue(transport instanceof HttpServletStreamableServerTransport);
    }
    
    @Test
    public void testAsyncServerCreation() {
        // 创建服务器构建器
        var serverBuilder = loader.createServerBuilder();
        assertNotNull(serverBuilder, "Server builder should be created");
        
        // 配置并构建异步服务器
        McpAsyncServer server = serverBuilder
                .serverInfo("test-server", "1.0.0", new java.util.HashMap<>())
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .tools(true)
                        .resources(false, true)
                        .build())
                .tool(createTestTool(), this::handleTestTool)
                .async();
        
        assertNotNull(server, "Async server should be built successfully");
    }
    
    @Test
    public void testSyncServerCreation() {
        // 创建服务器构建器
        var serverBuilder = loader.createServerBuilder();
        assertNotNull(serverBuilder, "Server builder should be created");
        
        // 配置并构建同步服务器
        var server = serverBuilder
                .serverInfo("test-server", "1.0.0", new java.util.HashMap<>())
                .capabilities(McpSchema.ServerCapabilities.builder()
                        .tools(true)
                        .resources(false, true)
                        .build())
                .tool(createTestTool(), this::handleTestToolSync)
                .sync();
        
        assertNotNull(server, "Sync server should be built successfully");
    }
    
    @Test
    public void testServerLifecycle() {
        // 初始状态
        assertFalse(loader.isRunning(), "Server should not be running initially");
        
        // 配置服务器
        loader.createServerBuilder()
                .serverInfo("lifecycle-test", "1.0.0", new java.util.HashMap<>())
                .tool(createTestTool(), this::handleTestTool)
                .async();
        
        // 启动服务器
        loader.start();
        assertTrue(loader.isRunning(), "Server should be running after start");
        
        // 停止服务器
        loader.stop();
        assertFalse(loader.isRunning(), "Server should not be running after stop");
    }
    
    @Test
    public void testMinimalConfiguration() {
        // 测试最小配置
        HttpServletServerLoader minimalLoader = HttpServletServerLoader.builder()
                .build();
        
        assertNotNull(minimalLoader, "Minimal loader should be created");
        assertEquals("http://localhost:8080/mcp", minimalLoader.getServerUrl());
        
        // 清理
        if (minimalLoader.isRunning()) {
            minimalLoader.stop();
        }
    }
    
    private McpSchema.Tool createTestTool() {
        String schemaJson = """
                {
                    "type": "object",
                    "properties": {
                        "message": {
                            "type": "string"
                        }
                    },
                    "required": ["message"]
                }
                """;
        return new McpSchema.Tool("test-tool", "A test tool for demonstration", schemaJson);
    }
    
    private reactor.core.publisher.Flux<McpSchema.CallToolResult> handleTestTool(Map<String, Object> args) {
        String message = (String) args.get("message");
        McpSchema.CallToolResult result = new McpSchema.CallToolResult(
                java.util.List.of(new McpSchema.TextContent("Echo: " + message)),
                false
        );
        return reactor.core.publisher.Flux.just(result);
    }
    
    private reactor.core.publisher.Flux<McpSchema.CallToolResult> handleTestToolSync(Map<String, Object> args) {
        return handleTestTool(args);
    }
}
