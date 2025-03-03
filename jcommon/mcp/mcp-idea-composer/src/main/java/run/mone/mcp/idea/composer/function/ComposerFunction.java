package run.mone.mcp.idea.composer.function;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.SneakyThrows;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.idea.composer.config.Const;
import run.mone.mcp.idea.composer.http.HttpClient;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
public class ComposerFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    public ComposerFunction(String port) {
        this.ideaPort = port;
    }

    private String name = "Composer";


    private String desc = "根据需求或者需求图片，生成业务代码，如果有图片，无需知道图片内容，只按要求返回即可";

    private String ideaPort;

    private String toolScheme = """
                {
                    "type": "object",
                    "properties": {
                        "requirement": {
                            "type": "string",
                            "description":"需求描述，用户输入什么就传什么，不要有任何更改，否则会有不好的事情发生"
                        },
                        "fileLists": {
                            "type": "array",
                            "items": {
                                "type": "string"
                            },
                            "description":"文件列表，根据需求分析出来要操作的文件数组，如果没有，则不需要返回"
                        },
                        "folder": {
                            "type": "string",
                            "description":"文件夹绝对路径，取Composer中folder的值，如果Composer中没有，则不需要返回"
                        },
                        "codebase": {
                            "type": "bool",
                            "description":"Composer config 中是否包含codebase，默认是false，如果没有就返回false"
                        },
                        "analyze": {
                            "type": "bool",
                            "description":"Composer config 中是否包含analyze，默认是false，如果没有就返回false"
                        },
                        "bizJar": {
                            "type": "bool",
                            "description":"Composer config 中是否包含bizJar，默认是false，如果没有就返回false"
                        },
                        "bugfix": {
                            "type": "bool",
                            "description":"Composer config 中是否包含bugfix，默认是false，如果没有就返回false"
                        },
                        "knowledgeBase": {
                            "type": "bool",
                            "description":"Composer config 中是否包含knowledgeBase，默认是false，如果没有就返回false"
                        },
                        "imageType": {
                            "type": "string",
                            "description":"Composer中如果有imageType，将imageType的取值返回，没有则不用返回。不需要真正解析图片内容"
                        }
             
                    },
                    "required": ["requirement","projectName"]
                }
                """;

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        JsonObject req = new JsonObject();
        req.addProperty("cmd", "writeCodeMethod");
        req.addProperty("from", "idea_mcp");
        req.addProperty("requirement", (String) arguments.get("requirement"));
        req.addProperty("projectName", (String) arguments.get("projectName"));
        req.add("fileLists", new Gson().toJsonTree(arguments.get("fileLists")));
        req.addProperty("folder", (String) arguments.get("folder"));
        req.addProperty("codebase", (Boolean) arguments.get("codebase"));
        req.addProperty("analyze", (Boolean) arguments.get("analyze"));
        req.addProperty("bizJar", (Boolean) arguments.get("bizJar"));
        req.addProperty("bugfix", (Boolean) arguments.get("bugfix"));
        req.addProperty("knowledgeBase", (Boolean) arguments.get("knowledgeBase"));
        req.addProperty("imageType", (String) arguments.get("imageType"));
        req.addProperty("athenaPluginHost", Const.IP + ideaPort);
        JsonObject res = callAthena(ideaPort, req);
        if (res.get("code") != null && res.get("code").getAsInt() == 0) {
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("已完成")), false);
        }
        return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(new Gson().toJson(res))), true);
    }

    @SneakyThrows
    public static JsonObject callAthena(String ideaPort, JsonObject req) {
        return new HttpClient().post("http://" + Const.IP + ideaPort + "/tianye", req);
    }
}
