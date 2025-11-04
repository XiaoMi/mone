package run.mone.moner.server.role.tool;

import com.google.gson.JsonObject;

import run.mone.hive.common.JsonUtils;
import run.mone.hive.common.ToolDataInfo;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;
import run.mone.moner.server.common.MultiXmlParser;

public class GetContentActionTool implements ITool {

    @Override
    public String getName() {
        return "get_content";
    }

    @Override
    public String description() {
        return "获取页面内容TOOL";
    }

    @Override
    public String parameters() {
        return "(no parameters)";
    }

    @Override
    public String usage() {
        return """
                <get_content>
                <arguments>
                {
                }
                </arguments>
                </get_content>
                """;
    }

    @Override
    public boolean callerRunTrigger() {
        return true;
    }

    @Override
    public String formatResult(JsonObject res) {
        return res.get("xml").getAsString();
    }

    @Override
    public boolean needExecute() {
        return true;
    }

    @Override public boolean toolInfoAsParam() {
        return true;
    }

    @Override
    public JsonObject execute(ReactorRole role, JsonObject req) {
        JsonObject res = new JsonObject();
        ToolDataInfo toolDataInfo = JsonUtils.gson.fromJson(req.get("_tool_info_as_param_"), ToolDataInfo.class);
        if (toolDataInfo != null) {
            try {
                String actions = toolDataInfo.getKeyValuePairs().get("arguments");
                String xml = new MultiXmlParser().jsonToXml(actions);
                res.addProperty("xml", xml);
            } catch (Exception e) {
                res.addProperty("error", e.getMessage());
            }
        }
        return res;
    }

    @Override
    public boolean show() {
        return true;
    }
}

