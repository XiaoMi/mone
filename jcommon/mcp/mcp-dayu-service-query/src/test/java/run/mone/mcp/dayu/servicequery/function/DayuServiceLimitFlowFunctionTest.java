package run.mone.mcp.dayu.servicequery.function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Dayu 服务限流功能测试
 */
@ExtendWith(MockitoExtension.class)
class DayuServiceLimitFlowFunctionTest {

    private DayuServiceLimitFlowFunction limitFlowFunction;

    @BeforeEach
    void setUp() {
        limitFlowFunction = new DayuServiceLimitFlowFunction("http://test.dayu.com", "test-token");
    }

    @Test
    void testGetName() {
        assertEquals("dayu_service_limit_flow", limitFlowFunction.getName());
    }

    @Test
    void testGetDesc() {
        assertTrue(limitFlowFunction.getDesc().contains("管理 Dayu 微服务治理中心的服务限流规则"));
    }

    @Test
    void testGetToolScheme() {
        String schema = limitFlowFunction.getToolScheme();
        assertNotNull(schema);
        assertTrue(schema.contains("action"));
        assertTrue(schema.contains("app"));
        assertTrue(schema.contains("service"));
    }

    @Test
    void testApplyWithListAction() {
        Map<String, Object> args = new HashMap<>();
        args.put("action", "list");
        args.put("app", "test-app");

        Flux<McpSchema.CallToolResult> result = limitFlowFunction.apply(args);
        
        assertNotNull(result);
        // 由于没有真实的HTTP客户端，这里主要测试方法不会抛出异常
        assertDoesNotThrow(() -> result.blockFirst());
    }

    @Test
    void testApplyWithCreateAction() {
        Map<String, Object> args = new HashMap<>();
        args.put("action", "create");
        args.put("app", "test-app");
        args.put("service", "test-service");
        args.put("grade", 1);
        args.put("count", 100);

        Flux<McpSchema.CallToolResult> result = limitFlowFunction.apply(args);
        
        assertNotNull(result);
        assertDoesNotThrow(() -> result.blockFirst());
    }

    @Test
    void testApplyWithMissingApp() {
        Map<String, Object> args = new HashMap<>();
        args.put("action", "list");
        // 缺少app参数

        Flux<McpSchema.CallToolResult> result = limitFlowFunction.apply(args);
        
        assertNotNull(result);
        McpSchema.CallToolResult callResult = result.blockFirst();
        assertTrue(callResult.isError());
        assertTrue(((McpSchema.TextContent) callResult.content().get(0)).text().contains("应用名称不能为空"));
    }

    @Test
    void testApplyWithInvalidAction() {
        Map<String, Object> args = new HashMap<>();
        args.put("action", "invalid");
        args.put("app", "test-app");

        Flux<McpSchema.CallToolResult> result = limitFlowFunction.apply(args);
        
        assertNotNull(result);
        McpSchema.CallToolResult callResult = result.blockFirst();
        assertTrue(callResult.isError());
        assertTrue(((McpSchema.TextContent) callResult.content().get(0)).text().contains("不支持的操作类型"));
    }
}
