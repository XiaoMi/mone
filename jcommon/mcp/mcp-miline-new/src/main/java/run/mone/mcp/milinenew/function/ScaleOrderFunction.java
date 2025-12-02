package run.mone.mcp.milinenew.function;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.milinenew.tools.ScaleOrderTool;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ScaleOrderFunction implements McpFunction {

    @Autowired
    private ScaleOrderTool scaleOrderTool;

    public static final String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "projectId": {
                        "type": ["number", "string"],
                        "description": "项目ID（单个操作时必填）"
                    },
                    "pipelineId": {
                        "type": ["number", "string"],
                        "description": "流水线ID（单个操作时必填）"
                    },
                    "scaleCount": {
                        "type": "number",
                        "description": "扩缩容数量，正数表示扩容，负数表示缩容（单个操作时必填）"
                    },
                    "type": {
                        "type": "number",
                        "description": "工单类型，1=日常扩容, 2=大促扩容, 3=紧急扩容, 4=缩容（批量操作时必填，单个操作时选填，默认根据数量自动判断）"
                    },
                    "reviewers": {
                        "type": "array",
                        "description": "审批人列表（条件必填，根据工单类型和目标实例数判断是否需要）",
                        "items": {
                            "type": "object",
                            "properties": {
                                "type": {
                                    "type": "number",
                                    "description": "审批类型，2=业务leader审核, 3=架构组审核, 7=SRE交付审核"
                                },
                                "username": {
                                    "type": "string",
                                    "description": "审批人用户名"
                                }
                            },
                            "required": ["type", "username"]
                        }
                    },
                    "pipelines": {
                        "type": "array",
                        "description": "流水线列表（批量操作时必填）",
                        "items": {
                            "type": "object",
                            "properties": {
                                "projectId": {
                                    "type": ["number", "string"],
                                    "description": "项目ID"
                                },
                                "pipelineId": {
                                    "type": ["number", "string"],
                                    "description": "流水线ID"
                                },
                                "scaleCount": {
                                    "type": "number",
                                    "description": "扩缩容数量，正数表示扩容，负数表示缩容"
                                }
                            },
                            "required": ["projectId", "pipelineId", "scaleCount"]
                        }
                    },
                    "remark": {
                        "type": "string",
                        "description": "备注（选填）"
                    }
                }
            }
            """;

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        log.info("ScaleOrder arguments: {}", arguments);

        try {
            JsonObject inputJson = convertToJsonObject(arguments);

            JsonObject result = scaleOrderTool.execute(null, inputJson);
            String resultText = scaleOrderTool.formatResult(result);
            boolean isError = result.has("error");

            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent(resultText)),
                    isError
            ));
        } catch (Exception e) {
            log.error("执行scale_order操作时发生异常", e);
            return Flux.just(new McpSchema.CallToolResult(
                    List.of(new McpSchema.TextContent("错误：执行操作失败: " + e.getMessage())),
                    true
            ));
        }
    }

    private JsonObject convertToJsonObject(Map<String, Object> arguments) {
        JsonObject jsonObject = new JsonObject();

        for (Map.Entry<String, Object> entry : arguments.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value == null) {
                continue;
            }

            if (value instanceof Number && (key.equals("projectId") || key.equals("pipelineId"))) {
                jsonObject.addProperty(key, ((Number) value).toString());
            } else {
                JsonElement element = convertValueToJsonElement(value);
                if (element != null) {
                    jsonObject.add(key, element);
                }
            }
        }

        return jsonObject;
    }

    private JsonElement convertValueToJsonElement(Object value) {
        if (value == null) {
            return null;
        }
        
        if (value instanceof Number) {
            Number num = (Number) value;
            if (num instanceof Integer || num instanceof Long || num instanceof Short || num instanceof Byte) {
                return new com.google.gson.JsonPrimitive(num.longValue());
            } else {
                return new com.google.gson.JsonPrimitive(num.doubleValue());
            }
        } else if (value instanceof String) {
            return new com.google.gson.JsonPrimitive((String) value);
        } else if (value instanceof Boolean) {
            return new com.google.gson.JsonPrimitive((Boolean) value);
        } else if (value instanceof List) {
            JsonArray jsonArray = new JsonArray();
            for (Object item : (List<?>) value) {
                JsonElement element = convertValueToJsonElement(item);
                if (element != null) {
                    jsonArray.add(element);
                }
            }
            return jsonArray;
        } else if (value instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> mapValue = (Map<String, Object>) value;
            JsonObject jsonObject = new JsonObject();
            for (Map.Entry<String, Object> entry : mapValue.entrySet()) {
                JsonElement element = convertValueToJsonElement(entry.getValue());
                if (element != null) {
                    jsonObject.add(entry.getKey(), element);
                }
            }
            return jsonObject;
        } else {
            return new com.google.gson.JsonPrimitive(value.toString());
        }
    }

    @Override
    public String getName() {
        return "scale_order";
    }

    @Override
    public String getDesc() {
        return scaleOrderTool.description();
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}

