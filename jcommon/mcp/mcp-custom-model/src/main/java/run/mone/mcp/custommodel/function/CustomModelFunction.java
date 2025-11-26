package run.mone.mcp.custommodel.function;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.custommodel.service.CustomModelService;

@Data
@Slf4j
@Component
public class CustomModelFunction implements McpFunction {

    private static final String TOOL_SCHEMA = """
            {
              "type": "object",
              "properties": {
                "user_message": {
                  "type": "string",
                  "description": "用户消息，用于intent和normalize类型"
                },
                "texts": {
                  "type": "array",
                  "items": {
                    "type": "string"
                  },
                  "description": "文本数组，用于predict类型"
                },
                "type": {
                  "type": "string",
                  "enum": ["intent", "normalize", "predict"],
                  "default": "intent",
                  "description": "当用户要进行分类，请使用intent；当将用户的问题进行标准化时，请选择normalize；当需要文本预测时，请选择predict"
                }
              },
              "required": []
            }
            """;
    
    @Autowired
    private CustomModelService customModelService;
    
    private String name = "stream_custom_model";
    private String desc = "自定义模型接口，支持意图识别、问题标准化和文本预测";

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> input) {
        return Flux.create(sink -> {
            try {
                String type = (String) input.getOrDefault("type", "intent"); // 默认为意图识别

                String result;
                if ("intent".equals(type)) {
                    String userMessage = (String) input.get("user_message");
                    if (userMessage == null) {
                        throw new IllegalArgumentException("user_message is required for intent type");
                    }
                    result = customModelService.recognizeIntent(userMessage);
                } else if ("normalize".equals(type)) {
                    String userMessage = (String) input.get("user_message");
                    if (userMessage == null) {
                        throw new IllegalArgumentException("user_message is required for normalize type");
                    }
                    result = customModelService.normalizeQuestion(userMessage);
                } else if ("predict".equals(type)) {
                    @SuppressWarnings("unchecked")
                    List<String> texts = (List<String>) input.get("texts");
                    if (texts == null || texts.isEmpty()) {
                        throw new IllegalArgumentException("texts is required for predict type");
                    }
                    
                    result = customModelService.predictTexts(texts);
                } else {
                    throw new IllegalArgumentException("Invalid type: " + type);
                }

                List<McpSchema.Content> contents = new ArrayList<>();
                contents.add(new McpSchema.TextContent(result));
                
                McpSchema.CallToolResult callResult = new McpSchema.CallToolResult(
                        contents,
                        false
                );

                sink.next(callResult);
                sink.complete();
            } catch (Exception e) {
                log.error("Error processing request", e);
                List<McpSchema.Content> errorContents = new ArrayList<>();
                errorContents.add(new McpSchema.TextContent(e.getMessage()));
                
                sink.next(new McpSchema.CallToolResult(
                        errorContents,
                        true
                ));
                sink.complete();
            }
        });
    }

    @Override
    public String getToolScheme() {
        return TOOL_SCHEMA;
    }
}
