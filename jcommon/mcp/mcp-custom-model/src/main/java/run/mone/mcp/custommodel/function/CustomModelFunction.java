package run.mone.mcp.custommodel.function;

import java.util.Map;
import java.util.function.Function;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.custommodel.service.CustomModelService;

@Data
@Slf4j
@Component
public class CustomModelFunction implements Function<Map<String, Object>, Flux<McpSchema.CallToolResult>> {
    private static final Gson gson = new Gson();
    
    @Autowired
    private CustomModelService customModelService;
    
    private String name = "custom_model";
    private String desc = "自定义模型接口，支持意图识别和问题标准化";

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> input) {
        return Flux.create(sink -> {
            try {
                String userMessage = (String) input.get("user_message");
                String type = (String) input.getOrDefault("type", "intent"); // 默认为意图识别

                String result;
                if ("intent".equals(type)) {
                    result = customModelService.recognizeIntent(userMessage);
                } else if ("normalize".equals(type)) {
                    result = customModelService.normalizeQuestion(userMessage);
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

    public String getToolSchema() {
        Map<String, Object> schema = Map.of(
            "type", "object",
            "properties", Map.of(
                "user_message", Map.of("type", "string"),
                "type", Map.of(
                    "type", "string",
                    "enum", new String[]{"intent", "normalize"},
                    "default", "intent"
                )
            ),
            "required", new String[]{"user_message"}
        );

        return gson.toJson(schema);
    }
}
