package run.mone.hive.mcp.function;

import com.google.gson.JsonObject;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.Map;
import java.util.function.Function;

/**
 * @author goodjava@qq.com
 * @date 2025/4/24 15:34
 */
public interface McpFunction extends Function<Map<String, Object>, Flux<McpSchema.CallToolResult>> {

    default void setRoleService(RoleService roleService) {

    }

    default String formatResult(String res) {
        return "<tool_result>" + res + "</tool_result>";
    }

    String getName();

    String getDesc();

    String getToolScheme();
}
