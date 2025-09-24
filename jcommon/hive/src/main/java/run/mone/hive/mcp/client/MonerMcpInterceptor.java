package run.mone.hive.mcp.client;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2025/4/9 10:38
 */
@Slf4j
public class MonerMcpInterceptor {

    protected boolean before(String toolName, Map<String, Object> toolArguments) {
        log.info("call mcp tool:{} params:{}", toolName, toolArguments);
        return true;
    }

    protected void after(String toolName, McpSchema.CallToolResult toolRes) {
        log.info("call mcp tool:{} finish res:{}", toolName, toolRes);
    }
}
