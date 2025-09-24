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
public class ClickAfterRefreshFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
    private String name = "ClickAfterRefresh";
    private String desc = "点击某个element(elementId需要你分析出来),然后刷新页面并构建dom树TOOL";
    private final ObjectMapper objectMapper;

    public ClickAfterRefreshFunction(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "elementId": {
                        "type": "string",
                        "description": "ID of the element to click"
                    }
                },
                "required": ["elementId"]
            }
            """;

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        try {
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("ClickAfterRefresh")),
                false
            );
        } catch (Exception e) {
            log.error("Failed to execute ClickAfterRefresh", e);
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Error: " + e.getMessage())),
                true
            );
        }
    }
} 