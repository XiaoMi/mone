package run.mone.mcp.idea.function;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.idea.service.IdeaService;

/**
 * Create method based on requirements
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CreateMethodFunction implements Function<Map<String, Object>, Flux<McpSchema.CallToolResult>> {

    private final IdeaService ideaService;

    public String getName() {
        return "stream_createMethod";
    }

    public String getDesc() {
        return "Generate method code based on the provided requirements description.";
    }

    public String getToolScheme() {
        return TOOL_SCHEMA;
    }

    private String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "requirements": {
                        "type": "string",
                        "description": "The requirements description for the method to be created"
                    }
                },
                "required": ["requirements"]
            }
            """;

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        log.info("CreateMethodFunction:{}", arguments);
        try {
            Flux<String> result = ideaService.createMethod((String) arguments.get("requirements"));
            JsonObject type = new JsonObject();
            type.addProperty("type", "createMethod");
            return result.map(res -> new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(res)), false));
        } catch (Exception e) {
            return Flux.just(new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true));
        }
    }
} 