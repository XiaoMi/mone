package run.mone.mcp.idea.function;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Data;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
public class CreateCommentFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    public CreateCommentFunction(String port) {
        this.ideaPort = port;
    }

    private String name = "create_comment";

    private String desc = "IDEA operations including create comment";
    private String ideaPort;

    private String toolScheme = """
                {
                    "type": "object",
                    "properties": {
                        "projectName": {
                            "type": "string",
                            "description":"需要生成测试的项目"
                        },
                        "className": {
                            "type": "string",
                            "description":"需要操作的class"
                        }
                    },
                    "required": ["projectName"]
                }
                """;

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        JsonObject req = new JsonObject();
        req.addProperty("cmd", "write_code");
        req.addProperty("cmdName", "createClassComment");
        req.addProperty("oneClass", (String) arguments.get("className"));
        req.addProperty("projectName", (String) arguments.get("projectName"));
        req.addProperty("athenaPluginHost", "127.0.0.1:" + ideaPort);
        JsonObject res = IdeaFunctions.callAthena(ideaPort, req);
        return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(new Gson().toJson(res))), false);
    }
}
