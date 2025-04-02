package run.mone.mcp.idea.composer.function;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.idea.composer.service.IdeaService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class MethodRenameFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private IdeaService ideaService;

    public MethodRenameFunction(IdeaService ideaService) {
        this.ideaService = ideaService;
    }

    public String getName() {
        return "generateMethodName";
    }

    public String getDesc() {
        return "Generate a new meaningful name for the given method based on its code implementation";
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
                        "description": "The source code of the method that needs a new name"
                    }
                },
                "required": ["code"]
            }
            """;

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        try {
            String result = ideaService.methodRename((String) arguments.get("code"));
            String newName = ideaService.extractContent(result, "methodName");
            String name = ideaService.extractContent(result, "old");

            JsonObject data = new JsonObject();
            data.addProperty("type", "rename");
            data.addProperty("methodName", name);
            data.addProperty("newName", newName);

            log.info("data:{}", data);

            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result, data.toString())), false);
        } catch (Exception e) {
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true);
        }
    }
}
