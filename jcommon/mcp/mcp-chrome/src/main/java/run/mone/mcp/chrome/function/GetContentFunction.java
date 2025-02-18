package run.mone.mcp.chrome.function;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@Slf4j
public class GetContentFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
    private String name = "GetContentAction";
    private String desc = "获取页面内容TOOL";
    private final ObjectMapper objectMapper;

    public GetContentFunction(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                },
                "required": []
            }
            """;

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        try {
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("获取页面内容")),
                false
            );
        } catch (Exception e) {
            log.error("获取页面内容失败", e);
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("错误: " + e.getMessage())),
                true
            );
        }
    }
} 