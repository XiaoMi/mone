package run.mone.mcp.git.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.Map;
import java.util.function.Function;

@Data
@Slf4j
public class GitHubFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> stringObjectMap) {
        return null;
    }
}
