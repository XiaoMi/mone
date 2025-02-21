package run.mone.mcp.idea.function;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.JsonObject;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.idea.service.IdeaService;

@Component
@RequiredArgsConstructor
public class CreateCommentFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private final IdeaService ideaService;

    public String getName() {
        return "createComment";
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
                    },
                    "methodName": {
                        "type": "string",
                        "description": "The name of the method to be reviewed"
                    }
                },
                "required": ["code", "methodName"]
            }
            """;

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        try {
            String code = (String) arguments.get("code");
            String methodName = (String) arguments.get("methodName");

            JsonObject type = new JsonObject();
            type.addProperty("type", "comment");
            type.addProperty("methodName", methodName);

            String result = ideaService.createComment(code);
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(type.toString(), result)), false);
        } catch (Exception e) {
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true);
        }
    }
}
