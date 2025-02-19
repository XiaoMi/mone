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
public class OpenClassFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
    public OpenClassFunction(String port) {
        this.ideaPort = port;
    }

    private String name = "openClass";

    private String desc = "打开java文件";
    private String ideaPort;

    private String toolScheme = """
                {
                    "type": "object",
                    "properties": {
                        "className": {
                            "type": "string",
                            "description":"要操作的简单类名（非全限定类名）,className与fqcnName必须有一个,如果用户未给出，要询问用户"
                        },
                        "fqcnName": {
                            "type": "string",
                            "description":"要操作的全限定类名（非简单类名）,className与fqcnName必须有一个,如果用户未给出，要询问用户"
                        },
                        "projectName": {
                            "type": "string",
                            "description":"需要操作的项目，如果未给出，要询问用户"
                        }
                    },
                    "required": ["projectName"]
                }
                """;

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        JsonObject req = new JsonObject();
        if (arguments.get("className") != null) { 
            req.addProperty("cmd", "open_code");
            req.addProperty("className", (String) arguments.get("className"));
        } else if (arguments.get("fqcnName") != null) {
            req.addProperty("cmd", "open_code_fqcn");
            req.addProperty("fqcnName", (String) arguments.get("fqcnName"));
        } else {
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: className与fqcnName必须有一个,如果用户未给出，要询问用户")), true);
        }
        req.addProperty("from", "idea_mcp");
        req.addProperty("athenaPluginHost", Const.IP + ideaPort);
        req.addProperty("projectName", (String) arguments.get("projectName"));
        JsonObject res = IdeaFunctions.callAthena(ideaPort, req);
        if (res.get("code") != null && res.get("code").getAsInt() == 0) {
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("已完成")), false);
        }
        return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(new Gson().toJson(res))), true);
    }
}
