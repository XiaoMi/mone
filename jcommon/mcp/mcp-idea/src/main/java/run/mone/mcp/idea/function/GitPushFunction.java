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
public class GitPushFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private final IdeaService ideaService;

    public String getName() {
        return "gitPush";
    }

    public String getDesc() {
        return "一健提交代码";
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
                        "description":"需要生成测试的项目"
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
            String result = ideaService.gitPush((String) arguments.get("code"));
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false);
        } catch (Exception e) {
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true);
        }
    }

}
