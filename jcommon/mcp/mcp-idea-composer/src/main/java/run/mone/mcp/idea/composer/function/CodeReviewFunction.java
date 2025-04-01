package run.mone.mcp.idea.composer.function;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.idea.composer.service.IdeaService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * review code
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CodeReviewFunction implements Function<Map<String, Object>, Flux<McpSchema.CallToolResult>> {

    private IdeaService ideaService;

    public CodeReviewFunction(IdeaService ideaService) {
        this.ideaService = ideaService;
    }

    public String getName() {
        return "stream_codeReview";
    }

    public String getDesc() {
        return "A systematic examination of source code by peers to identify bugs, improve quality, and share knowledge within the development team.";
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
        log.info("CodeReviewFunction:{}", arguments);
        try {
            Flux<String> result = ideaService.reviewCode((String) arguments.get("code"));
            JsonObject type = new JsonObject();
            type.addProperty("type","codeReview");
            return result.map(res -> new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(res)), false));
        } catch (Exception e) {
            return Flux.just(new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true));
        }
    }

}
