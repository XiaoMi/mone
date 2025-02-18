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
    private String desc = "需要在当前页面执行一系列操作TOOL(比如填入搜索内容后点击搜索按钮)";
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
                                }
                            },
                            "required": ["type", "name", "elementId", "tabId"]
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