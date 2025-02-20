package run.mone.mcp.idea.function;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.idea.service.IdeaService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

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
                    "projectName": {
                        "type": "string",
                        "description": "需要生成注释的项目"
                    },
                    "className": {
                        "type": "string",
                        "description": "需要操作的class"
                    },
                    "code": {
                        "type": "string",
                        "description": "The source code that needs to be reviewed"
                    }
                },
                "required": ["code"]
            }
            """;

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {

        try {
            String result = ideaService.createComment((String) arguments.get("code"));
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false);
        } catch (Exception e) {
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true);
        }
    }
}
