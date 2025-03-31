package run.mone.mcp.idea.composer.function;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.idea.composer.service.IdeaService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
@Slf4j
public class GitPushFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private IdeaService ideaService;

    public GitPushFunction(IdeaService ideaService) {
        this.ideaService = ideaService;
    }

    public String getName() {
        return "gitPush";
    }

    public String getDesc() {
        return "一健提交代码(git push)";
    }

    public String getToolScheme() {
        return TOOL_SCHEMA;
    }

    private String TOOL_SCHEMA = """
            {
                "type": "object",
                "properties": {
                    
                },
                "required": []
            }
            """;

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        try {
            String result = ideaService.gitPush((String) arguments.get("code"));
            String commit = ideaService.extractContent(result, "commit");
            JsonObject data = new JsonObject();
            data.addProperty("type", getName());
            data.addProperty("commit", commit);

            log.info("data:{}", data);

            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(data.toString(), data.toString())), false);
        } catch (Exception e) {
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true);
        }
    }

}
