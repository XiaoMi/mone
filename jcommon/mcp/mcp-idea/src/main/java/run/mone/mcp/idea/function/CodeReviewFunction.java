package run.mone.mcp.idea.function;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Data;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.idea.config.Const;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
public class CodeReviewFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    public CodeReviewFunction(String port) {
        this.ideaPort = port;
    }

    private String name = "code_review";

    private String desc = "IDEA operations including review select code";
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
        req.addProperty("cmd", "code_review");
        req.addProperty("athenaPluginHost", Const.IP + ideaPort);
        JsonObject res = IdeaFunctions.callAthena(ideaPort, req);
        if (res.get("code") != null && res.get("code").getAsInt() == 0) {
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("已完成代码建议")), false);
        }
        return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(new Gson().toJson(res))), true);
    }
}
