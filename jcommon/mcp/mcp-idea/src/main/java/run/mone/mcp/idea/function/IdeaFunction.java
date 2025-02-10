
package run.mone.mcp.idea.function;

import com.google.gson.JsonObject;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.idea.http.HttpClient;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@Slf4j
public class IdeaFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "ideaOperation";

    private String desc = "IDEA operations including closing all editors and reading current editor content";

    private int ideaPort;

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "operation": {
                        "type": "string",
                        "enum": ["closeAllEditors", "getCurrentEditorContent"],
                        "description":"The operation to perform on IDEA"
                    }
                },
                "required": ["operation"]
            }
            """;

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        String operation = (String) arguments.get("operation");

        log.info("operation: {}", operation);

        try {
            String result = switch (operation) {
                case "closeAllEditors" -> closeAllEditors();
                case "getCurrentEditorContent" -> getCurrentEditorContent();
                default -> throw new IllegalArgumentException("Unknown operation: " + operation);
            };

            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false);
        } catch (Exception e) {
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true);
        }
    }

    @SneakyThrows
    private String closeAllEditors() {
        JsonObject req = new JsonObject();
        req.addProperty("cmd", "close_all_tab");
        new HttpClient().post("http://127.0.0.1:" + ideaPort + "/tianye", req);
        return "All editors closed";
    }

    @SneakyThrows
    private String getCurrentEditorContent() {
        JsonObject req = new JsonObject();
        req.addProperty("cmd", "get_current_editor_content");
        JsonObject res = new HttpClient().post("http://127.0.0.1:" + ideaPort + "/tianye", req);
        return res.toString();
    }
}
