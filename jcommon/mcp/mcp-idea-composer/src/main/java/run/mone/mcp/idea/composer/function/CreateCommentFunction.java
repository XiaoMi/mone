package run.mone.mcp.idea.composer.function;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.idea.composer.service.IdeaService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;


/**
 * 生成注释
 */
@Component
@RequiredArgsConstructor
public class CreateCommentFunction implements Function<Map<String, Object>, Flux<McpSchema.CallToolResult>> {

    private IdeaService ideaService;

    public CreateCommentFunction(IdeaService ideaService) {
        this.ideaService = ideaService;
    }

    public String getName() {
        return "stream_CreateComment";
    }

    public String getDesc() {
        return "IDEA operations including create comment";
    }

    public String getToolScheme() {
        return TOOL_SCHEMA;
    }

    private String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    "code": {
                        "type": "string",
                        "description": "The source code that needs to be reviewed"
                    }
                },
                "required": ["code"]
            }
            """;

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        try {
            String code = (String) arguments.get("code");
            JsonObject type = new JsonObject();
            type.addProperty("type", "comment");
            Flux<String> result = ideaService.createComment(code);
            return result.map(res -> new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(res)), false));
        } catch (Exception e) {
            return Flux.just(new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true));
        }
    }
}
