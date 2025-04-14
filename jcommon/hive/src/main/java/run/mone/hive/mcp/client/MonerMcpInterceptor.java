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

    public boolean before(String toolName, Map<String, Object> toolArguments) {
        log.info("call tool:{} params:{}", toolName, toolArguments);
        return true;
    }

    public void after(String toolName, McpSchema.CallToolResult toolRes) {
        log.info("call tool:{} finish res:{}", toolName, toolRes);
    }
}
