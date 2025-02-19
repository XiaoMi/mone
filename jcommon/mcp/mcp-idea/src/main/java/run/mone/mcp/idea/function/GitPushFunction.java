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
public class GitPushFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    public GitPushFunction(String port) {
        this.ideaPort = port;
    }

    private String name = "gitPush";

    private String desc = "根据项目名，一健提交代码";
    private String ideaPort;

    private String toolScheme = """
                {
                    "type": "object",
                    "properties": {
                        "projectName": {
                            "type": "string",
                            "description":"需要生成测试的项目"
                        }
                    },
                    "required": ["projectName"]
                }
                """;

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        JsonObject req = new JsonObject();
        req.addProperty("cmd", "git_push");
        req.addProperty("projectName", (String) arguments.get("projectName"));
        req.addProperty("athenaPluginHost", Const.IP + ideaPort);
        JsonObject res = IdeaFunctions.callAthena(ideaPort, req);
        return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(new Gson().toJson(res))), false);
    }

}
