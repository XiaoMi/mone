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

class RealThinkingRouterFunctionTest {

    @Mock
    private DayuServiceQueryFunction dayuServiceQueryFunction;

    private RealThinkingRouterFunction routerFunction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        routerFunction = new RealThinkingRouterFunction(dayuServiceQueryFunction);
    }

    @Test
    void testEmptyInput() {
        Map<String, Object> args = new HashMap<>();
        args.put("text", "");

        Flux<McpSchema.CallToolResult> resultFlux = routerFunction.apply(args);
        McpSchema.CallToolResult result = resultFlux.blockFirst();
        
        assertNotNull(result);
        assertFalse(result.isError());
        String content = ((McpSchema.TextContent) result.content().get(0)).text();
        assertTrue(content.contains("思考过程"));
        assertTrue(content.contains("追问"));
    }

    @Test
    void testNonQueryInput() {
        Map<String, Object> args = new HashMap<>();
        args.put("text", "今天天气怎么样");

        Flux<McpSchema.CallToolResult> resultFlux = routerFunction.apply(args);
        McpSchema.CallToolResult result = resultFlux.blockFirst();
        
        String content = ((McpSchema.TextContent) result.content().get(0)).text();
        assertTrue(content.contains("思考过程"));
        assertTrue(content.contains("未发现查询相关词汇"));
        assertTrue(content.contains("追问"));
    }

    @Test
    void testServiceQueryWithRealThinking() {
        Map<String, Object> args = new HashMap<>();
        args.put("text", "查询服务 myapp");

        // Mock 查询功能的响应
        McpSchema.CallToolResult mockResult = new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("查询结果：服务 myapp 的信息")),
                false
        );
        when(dayuServiceQueryFunction.apply(any())).thenReturn(Flux.just(mockResult));

        Flux<McpSchema.CallToolResult> resultFlux = routerFunction.apply(args);
        List<McpSchema.CallToolResult> results = resultFlux.collectList().block();
        
        assertNotNull(results);
        assertEquals(2, results.size());
        
        // 第一个结果：真正的思考过程
        McpSchema.CallToolResult thinkingResult = results.get(0);
        String thinkingContent = ((McpSchema.TextContent) thinkingResult.content().get(0)).text();
        assertTrue(thinkingContent.contains("思考过程"));
        assertTrue(thinkingContent.contains("分析用户输入"));
        assertTrue(thinkingContent.contains("检查查询关键词"));
        assertTrue(thinkingContent.contains("识别查询类型"));
        assertTrue(thinkingContent.contains("提取查询值"));
        assertTrue(thinkingContent.contains("构建查询参数"));
        assertTrue(thinkingContent.contains("所有参数验证通过"));
        
        // 第二个结果：实际的查询功能响应
        McpSchema.CallToolResult actionResult = results.get(1);
        String actionContent = ((McpSchema.TextContent) actionResult.content().get(0)).text();
        assertTrue(actionContent.contains("查询结果"));
    }

    @Test
    void testApplicationQueryWithRealThinking() {
        Map<String, Object> args = new HashMap<>();
        args.put("text", "查询应用 myapp");

        // Mock 查询功能的响应
        McpSchema.CallToolResult mockResult = new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("查询结果：应用 myapp 的信息")),
                false
        );
        when(dayuServiceQueryFunction.apply(any())).thenReturn(Flux.just(mockResult));

        Flux<McpSchema.CallToolResult> resultFlux = routerFunction.apply(args);
        List<McpSchema.CallToolResult> results = resultFlux.collectList().block();
        
        assertNotNull(results);
        assertEquals(2, results.size());
        
        // 验证思考过程
        McpSchema.CallToolResult thinkingResult = results.get(0);
        String thinkingContent = ((McpSchema.TextContent) thinkingResult.content().get(0)).text();
        assertTrue(thinkingContent.contains("思考过程"));
        assertTrue(thinkingContent.contains("识别查询类型：application"));
        assertTrue(thinkingContent.contains("提取查询值：myapp"));
    }

    @Test
    void testIpQueryWithRealThinking() {
        Map<String, Object> args = new HashMap<>();
        args.put("text", "查询包含IP 192.168.1.1");

        // Mock 查询功能的响应
        McpSchema.CallToolResult mockResult = new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("查询结果：IP 192.168.1.1 的信息")),
                false
        );
        when(dayuServiceQueryFunction.apply(any())).thenReturn(Flux.just(mockResult));

        Flux<McpSchema.CallToolResult> resultFlux = routerFunction.apply(args);
        List<McpSchema.CallToolResult> results = resultFlux.collectList().block();
        
        assertNotNull(results);
        assertEquals(2, results.size());
        
        // 验证思考过程
        McpSchema.CallToolResult thinkingResult = results.get(0);
        String thinkingContent = ((McpSchema.TextContent) thinkingResult.content().get(0)).text();
        assertTrue(thinkingContent.contains("思考过程"));
        assertTrue(thinkingContent.contains("识别查询类型：ip"));
        assertTrue(thinkingContent.contains("提取查询值：192.168.1.1"));
    }

    @Test
    void testIncompleteQuery() {
        Map<String, Object> args = new HashMap<>();
        args.put("text", "查询服务"); // 缺少服务名

        Flux<McpSchema.CallToolResult> resultFlux = routerFunction.apply(args);
        McpSchema.CallToolResult result = resultFlux.blockFirst();
        
        String content = ((McpSchema.TextContent) result.content().get(0)).text();
        assertTrue(content.contains("思考过程"));
        assertTrue(content.contains("未找到有效查询值"));
        assertTrue(content.contains("追问"));
    }

    @Test
    void testSessionContext() {
        Map<String, Object> args1 = new HashMap<>();
        args1.put("text", "查询服务 myapp");
        args1.put("session_id", "session1");

        Map<String, Object> args2 = new HashMap<>();
        args2.put("text", "查询应用 myapp");
        args2.put("session_id", "session1");

        // 第一个请求
        Flux<McpSchema.CallToolResult> resultFlux1 = routerFunction.apply(args1);
        McpSchema.CallToolResult result1 = resultFlux1.blockFirst();
        
        // 第二个请求（相同session）
        Flux<McpSchema.CallToolResult> resultFlux2 = routerFunction.apply(args2);
        McpSchema.CallToolResult result2 = resultFlux2.blockFirst();
        
        // 验证两个请求都能正常处理
        assertNotNull(result1);
        assertNotNull(result2);
        
        String content1 = ((McpSchema.TextContent) result1.content().get(0)).text();
        String content2 = ((McpSchema.TextContent) result2.content().get(0)).text();
        
        assertTrue(content1.contains("思考过程"));
        assertTrue(content2.contains("思考过程"));
    }
}
