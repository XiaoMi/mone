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
public class OperationFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
    private String name = "OperationAction";
    private String desc = """
            需要在当前页面执行一系列操作TOOL(比如填入搜索内容后点击搜索按钮)
            + 尽量一次返回一个页面的所有action操作
            + elementId的数字会在元素的右上角,请你从图中信息中找到elementId
            + 数字的颜色和这个元素的边框一定是一个颜色
            + 必须返回tabId(如果没有,需要你打开相应的tab)
            """;
    private final ObjectMapper objectMapper;

    public OperationFunction(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "actions": {
                        "type": "object",
                        "additionalProperties": {
                            "type": "object",
                            "properties": {
                                "type": {
                                    "type": "string",
                                    "description": "操作类型，如 action"
                                },
                                "name": {
                                    "type": "string",
                                    "description": "操作名称，如 fill, click 等"
                                },
                                "elementId": {
                                    "type": "string",
                                    "description": "要操作的元素ID"
                                },
                                "value": {
                                    "type": "string",
                                    "description": "操作值，如填充的内容"
                                },
                                "desc": {
                                    "type": "string",
                                    "description": "操作描述"
                                },
                                "tabId": {
                                    "type": "string",
                                    "description": "标签页ID"
                                },
                                "waiting": {
                                    "type": "boolean",
                                    "description": "是否需要接收回调信息,通常为true,除非显式指定"
                                }
                            },
                            "required": ["type", "name", "elementId", "tabId", "waiting"]
                        }
                    }
                },
                "required": ["actions"]
            }
            """;

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> args) {
        try {
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("OperationAction")),
                false
            );
        } catch (Exception e) {
            log.error("执行操作序列失败", e);
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("错误: " + e.getMessage())),
                true
            );
        }
    }
} 