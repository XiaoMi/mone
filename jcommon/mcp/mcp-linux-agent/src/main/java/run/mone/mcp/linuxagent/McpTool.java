package run.mone.mcp.linuxagent;

import run.mone.hive.mcp.spec.McpSchema;

import java.util.Map;
import java.util.function.Function;

public interface McpTool extends Function<Map<String, Object>, McpSchema.CallToolResult> {
    String getName();
    String getDesc();
    String getToolScheme();
}
