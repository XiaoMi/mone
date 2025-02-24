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
public class FullPageFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
    private String name = "FullPageAction";
    private String desc = "全屏截图TOOL(如果你发现有些信息在当前页面没有,可能需要全部的页面信息,你可以发送全屏截图指令)";
    private final ObjectMapper objectMapper;

    public FullPageFunction(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "name": {
                        "type": "string",
                        "description": "截图操作的名称"
                    }
                },
                "required": ["name"]
            }
            """;

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        try {
            String name = (String) args.get("name");
            
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("执行全屏截图: " + name)),
                false
            );
        } catch (Exception e) {
            log.error("执行全屏截图失败", e);
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("错误: " + e.getMessage())),
                true
            );
        }
    }
} 