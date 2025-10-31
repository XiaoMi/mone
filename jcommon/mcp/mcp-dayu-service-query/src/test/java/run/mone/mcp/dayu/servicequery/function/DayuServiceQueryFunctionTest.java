package run.mone.mcp.dayu.servicequery.function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Dayu 服务查询功能测试类
 */
class DayuServiceQueryFunctionTest {

    private DayuServiceQueryFunction function;

    @BeforeEach
    void setUp() {
        // 使用测试配置
        function = new DayuServiceQueryFunction("http://localhost:8080", "test-token");
    }

    @Test
    void testBasicServiceQuery() {
        Map<String, Object> args = new HashMap<>();
        args.put("serviceName", "TestService");
        
        // 注意：这个测试需要真实的 Dayu 服务运行
        // 在实际环境中，应该使用 Mock 或者测试服务器
        assertDoesNotThrow(() -> {
            McpSchema.CallToolResult result = function.apply(args).blockFirst();
            assertNotNull(result);
            assertFalse(result.isError());
        });
    }

    @Test
    void testServiceQueryWithFilters() {
        Map<String, Object> args = new HashMap<>();
        args.put("serviceName", "GatewayService");
        args.put("group", "car_online");
        args.put("application", "tesla");
        args.put("page", 1);
        args.put("pageSize", 10);
        args.put("myParticipations", false);
        
        assertDoesNotThrow(() -> {
            McpSchema.CallToolResult result = function.apply(args).blockFirst();
            assertNotNull(result);
        });
    }

    @Test
    void testEmptyServiceName() {
        Map<String, Object> args = new HashMap<>();
        // 不设置 serviceName
        
        McpSchema.CallToolResult result = function.apply(args).blockFirst();
        assertTrue(result.isError());
        assertTrue(result.content().get(0).toString().contains("服务名称不能为空"));
    }

    @Test
    void testGetToolSchema() {
        String schema = function.getServiceQueryToolSchema();
        assertNotNull(schema);
        assertTrue(schema.contains("serviceName"));
        assertTrue(schema.contains("group"));
        assertTrue(schema.contains("application"));
    }
}
