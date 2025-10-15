package run.mone.mcp.dayu.servicequery.function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ThinkingLimitFlowRouterFunctionTest {

    @Mock
    private DayuServiceLimitFlowFunction limitFlowFunction;

    private ThinkingLimitFlowRouterFunction routerFunction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        routerFunction = new ThinkingLimitFlowRouterFunction(limitFlowFunction);
    }

    @Test
    void testEmptyInput() {
        Map<String, Object> args = new HashMap<>();
        args.put("text", "");

        Flux<McpSchema.CallToolResult> resultFlux = routerFunction.apply(args);
        McpSchema.CallToolResult result = resultFlux.blockFirst();
        
        assertNotNull(result);
        assertFalse(result.isError());
        assertNotNull(result.content());
        assertEquals(1, result.content().size());
        String content = ((McpSchema.TextContent) result.content().get(0)).text();
        assertTrue(content.contains("思考过程"));
        assertTrue(content.contains("追问"));
    }

    @Test
    void testNonLimitFlowInput() {
        Map<String, Object> args = new HashMap<>();
        args.put("text", "查询天气信息");

        Flux<McpSchema.CallToolResult> resultFlux = routerFunction.apply(args);
        // 应该返回空，让其他路由器处理
        assertTrue(resultFlux.count().block() == 0);
    }

    @Test
    void testLimitFlowQueryWithThinking() {
        Map<String, Object> args = new HashMap<>();
        args.put("text", "查询应用 myapp 的限流规则");

        // Mock 限流功能的响应
        McpSchema.CallToolResult mockResult = new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("查询结果：应用 myapp 的限流规则")),
                false
        );
        when(limitFlowFunction.apply(any())).thenReturn(Flux.just(mockResult));

        Flux<McpSchema.CallToolResult> resultFlux = routerFunction.apply(args);
        List<McpSchema.CallToolResult> results = resultFlux.collectList().block();
        
        assertNotNull(results);
        assertEquals(2, results.size());
        
        // 第一个结果：思考过程
        McpSchema.CallToolResult thinkingResult = results.get(0);
        assertNotNull(thinkingResult);
        assertFalse(thinkingResult.isError());
        String thinkingContent = ((McpSchema.TextContent) thinkingResult.content().get(0)).text();
        assertTrue(thinkingContent.contains("思考过程"));
        assertTrue(thinkingContent.contains("分析用户输入"));
        assertTrue(thinkingContent.contains("检查限流关键词"));
        assertTrue(thinkingContent.contains("解析用户意图"));
        
        // 第二个结果：实际的限流功能响应
        McpSchema.CallToolResult actionResult = results.get(1);
        assertNotNull(actionResult);
        String actionContent = ((McpSchema.TextContent) actionResult.content().get(0)).text();
        assertTrue(actionContent.contains("查询结果"));
    }

    @Test
    void testCreateLimitFlowWithThinking() {
        Map<String, Object> args = new HashMap<>();
        args.put("text", "为服务 myservice 创建限流规则，QPS 100");

        // Mock 限流功能的响应
        McpSchema.CallToolResult mockResult = new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("创建成功：为服务 myservice 创建了QPS 100的限流规则")),
                false
        );
        when(limitFlowFunction.apply(any())).thenReturn(Flux.just(mockResult));

        Flux<McpSchema.CallToolResult> resultFlux = routerFunction.apply(args);
        List<McpSchema.CallToolResult> results = resultFlux.collectList().block();
        
        assertNotNull(results);
        // 由于参数不完整（缺少应用名称），只会返回思考过程，不会调用实际的限流功能
        assertEquals(1, results.size());
        
        // 第一个结果：思考过程
        McpSchema.CallToolResult thinkingResult = results.get(0);
        String thinkingContent = ((McpSchema.TextContent) thinkingResult.content().get(0)).text();
        assertTrue(thinkingContent.contains("思考过程"));
        assertTrue(thinkingContent.contains("识别操作类型：create"));
        assertTrue(thinkingContent.contains("发现缺失参数"));
    }

    @Test
    void testIncompleteParameters() {
        Map<String, Object> args = new HashMap<>();
        args.put("text", "创建限流规则"); // 缺少应用名称和服务名称

        Flux<McpSchema.CallToolResult> resultFlux = routerFunction.apply(args);
        McpSchema.CallToolResult result = resultFlux.blockFirst();
        
        String content = ((McpSchema.TextContent) result.content().get(0)).text();
        assertTrue(content.contains("思考过程"));
        assertTrue(content.contains("发现缺失参数"));
        assertTrue(content.contains("追问"));
    }

    @Test
    void testUpdateLimitFlowWithThinking() {
        Map<String, Object> args = new HashMap<>();
        args.put("text", "更新限流规则 ID 123，QPS 200");

        // Mock 限流功能的响应
        McpSchema.CallToolResult mockResult = new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("更新成功：限流规则 ID 123 已更新为QPS 200")),
                false
        );
        when(limitFlowFunction.apply(any())).thenReturn(Flux.just(mockResult));

        Flux<McpSchema.CallToolResult> resultFlux = routerFunction.apply(args);
        List<McpSchema.CallToolResult> results = resultFlux.collectList().block();
        
        assertNotNull(results);
        assertEquals(2, results.size());
        
        // 第一个结果：思考过程
        McpSchema.CallToolResult thinkingResult = results.get(0);
        String thinkingContent = ((McpSchema.TextContent) thinkingResult.content().get(0)).text();
        assertTrue(thinkingContent.contains("思考过程"));
        assertTrue(thinkingContent.contains("识别操作类型：update"));
        
        // 第二个结果：实际的限流功能响应
        McpSchema.CallToolResult actionResult = results.get(1);
        String actionContent = ((McpSchema.TextContent) actionResult.content().get(0)).text();
        assertTrue(actionContent.contains("更新成功"));
    }

    @Test
    void testDeleteLimitFlowWithThinking() {
        Map<String, Object> args = new HashMap<>();
        args.put("text", "删除限流规则 ID 456");

        // Mock 限流功能的响应
        McpSchema.CallToolResult mockResult = new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("删除成功：限流规则 ID 456 已删除")),
                false
        );
        when(limitFlowFunction.apply(any())).thenReturn(Flux.just(mockResult));

        Flux<McpSchema.CallToolResult> resultFlux = routerFunction.apply(args);
        List<McpSchema.CallToolResult> results = resultFlux.collectList().block();
        
        assertNotNull(results);
        assertEquals(2, results.size());
        
        // 第一个结果：思考过程
        McpSchema.CallToolResult thinkingResult = results.get(0);
        String thinkingContent = ((McpSchema.TextContent) thinkingResult.content().get(0)).text();
        assertTrue(thinkingContent.contains("思考过程"));
        assertTrue(thinkingContent.contains("识别操作类型：delete"));
        
        // 第二个结果：实际的限流功能响应
        McpSchema.CallToolResult actionResult = results.get(1);
        String actionContent = ((McpSchema.TextContent) actionResult.content().get(0)).text();
        assertTrue(actionContent.contains("删除成功"));
    }

    @Test
    void testExtractTextFromMessages() {
        Map<String, Object> args = new HashMap<>();
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", "查询应用 testapp 的限流规则");
        
        args.put("messages", List.of(message));

        // Mock 限流功能的响应
        McpSchema.CallToolResult mockResult = new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("查询结果")),
                false
        );
        when(limitFlowFunction.apply(any())).thenReturn(Flux.just(mockResult));

        Flux<McpSchema.CallToolResult> resultFlux = routerFunction.apply(args);
        McpSchema.CallToolResult result = resultFlux.blockFirst();
        
        String content = ((McpSchema.TextContent) result.content().get(0)).text();
        assertTrue(content.contains("思考过程"));
        assertTrue(content.contains("分析用户输入：\"查询应用 testapp 的限流规则\""));
    }
}