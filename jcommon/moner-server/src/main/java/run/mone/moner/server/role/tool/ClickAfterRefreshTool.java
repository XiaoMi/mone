package run.mone.moner.server.role.tool;

import com.google.gson.JsonObject;

import run.mone.hive.common.JsonUtils;
import run.mone.hive.common.ToolDataInfo;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;
import run.mone.moner.server.common.MultiXmlParser;

public class ClickAfterRefreshTool implements ITool {

    @Override
    public String getName() {
        return "click_after_refresh";
    }

    @Override
    public String description() {
        return "点击某个element，然后刷新页面并构建dom树TOOL";
    }

    @Override
    public String parameters() {
        return "- elementId: (required) The id of the element to click";
    }

    @Override
    public String usage() {
        return """
                <click_after_refresh>
                <arguments>
                {
                  "elementId": "12"
                }
                </arguments>
                </click_after_refresh>
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

