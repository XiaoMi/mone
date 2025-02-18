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
public class OpenTabFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
    private String name = "OpenTabAction";
    private String desc = "创建新标签页TOOL(打开标签页后,chrome会渲染+截图发送回来当前页面)";
    private final ObjectMapper objectMapper;

    public OpenTabFunction(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "action": {
                        "type": "object",
                        "properties": {
                            "type": {
                                "type": "string",
                                "description": "操作类型，固定为createNewTab"
                            },
                            "url": {
                                "type": "string",
                                "description": "需要打开的URL地址"
                            },
                            "auto": {
                                "type": "boolean",
                                "description": "是否自动执行"
                            },
                            "desc": {
                                "type": "string",
                                "description": "操作描述"
                            }
                        },
                        "required": ["type", "url"]
                    }
                },
                "required": ["action"]
            }
            """;

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> action = (Map<String, Object>) args.get("action");
            String url = (String) action.get("url");
            String desc = (String) action.get("desc");
            
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("打开新标签页: " + url + (desc != null ? " (" + desc + ")" : ""))),
                false
            );
        } catch (Exception e) {
            log.error("创建新标签页失败", e);
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("错误: " + e.getMessage())),
                true
            );
        }
    }
} 