package run.mone.hive.mcp.client;

import run.mone.hive.mcp.spec.McpSchema;

import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2025/4/9 10:38
 */
public class MonerMcpInterceptor {

    public boolean before(String toolName, Map<String, Object> toolArguments) {
        return true;
    }

    public void after(String toolName, McpSchema.CallToolResult toolRes) {

    }
}
