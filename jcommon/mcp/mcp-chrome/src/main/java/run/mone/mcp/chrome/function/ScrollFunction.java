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
public class ScrollFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
    private String name = "ScrollAction";
    private String desc = "滚动一屏屏幕TOOL(如果你发现有些信息在当前页面没有展示全,但可能在下边的页面,你可以发送滚动屏幕指令)";
    private final ObjectMapper objectMapper;

    public ScrollFunction(ObjectMapper objectMapper) {
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
                List.of(new McpSchema.TextContent("执行滚动屏幕操作")),
                false
            );
        } catch (Exception e) {
            log.error("滚动屏幕失败", e);
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("错误: " + e.getMessage())),
                true
            );
        }
    }
} 