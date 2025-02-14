package run.mone.mcp.idea.function;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Data;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
public class MethodRenameFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    public MethodRenameFunction(String port) {
        this.ideaPort = port;
    }

    private String name = "rename_method";

    private String desc = "IDEA operations including rename selected method";
    private String ideaPort;

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
            
                },
                "required": []
            }
            """;

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        JsonObject req = new JsonObject();
        req.addProperty("cmd", "rename_method");
        req.addProperty("athenaPluginHost", "127.0.0.1:" + ideaPort);
        JsonObject res = IdeaFunctions.callAthena(ideaPort, req);
        if (res.get("code") != null && res.get("code").getAsInt() == 0) {
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("已完成命名")), false);
        }
        return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(new Gson().toJson(res))), true);
    }
}
