package run.mone.mcp.idea.function;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import lombok.RequiredArgsConstructor;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.idea.service.IdeaService;

@Slf4j
@Component
@RequiredArgsConstructor
public class MethodRenameFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private final IdeaService ideaService;

    public String getName() {
        return "renameMethod";
    }

    public String getDesc() {
        return "IDEA operations including rename selected method";
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
                        "description": "The name of the method to be renamed"
                    }
                },
                "required": ["code", "methodName"]
            }
            """;

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        try {
            String methodName = arguments.get("methodName").toString();
            String result = ideaService.methodRename((String) arguments.get("code"));
            String newName = ideaService.extractContent(result, "methodName");

            JsonObject type = new JsonObject();
            type.addProperty("type", "rename");
            type.addProperty("methodName", methodName);
            type.addProperty("newName", newName);

            log.info("type:{}", type);

            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false);
        } catch (Exception e) {
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true);
        }
    }
}
