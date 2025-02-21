package run.mone.mcp.log.function;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.Map;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2025/2/21 14:08
 */
public class HeraLogFunctionTest {

    @Test
    public void testQueryUserProject() {
        HeraLogFunction heraLogFunction = new HeraLogFunction();
        McpSchema.CallToolResult apply = heraLogFunction.apply(Map.of(
                "type", "query_project",
                "app_id", "301410",
                "env_id", "932279"
        ));
        Assert.assertNotNull(apply);
    }

    @Test
    public void testAccess() {
        HeraLogFunction heraLogFunction = new HeraLogFunction();
        McpSchema.CallToolResult apply = heraLogFunction.apply(Map.of(
                "type", "access_log",
                "app_id", "91350",
                "env_id", "930219",
                "user_name", "wangtao29",
                "space_id", "35",
                "store_id", "121444"
        ));
        Assert.assertNotNull(apply);
    }
}