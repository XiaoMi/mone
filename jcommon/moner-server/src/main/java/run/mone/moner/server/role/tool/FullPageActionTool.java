package run.mone.moner.server.role.tool;

import com.google.gson.JsonObject;

import run.mone.hive.common.JsonUtils;
import run.mone.hive.common.ToolDataInfo;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;
import run.mone.moner.server.common.MultiXmlParser;

public class FullPageActionTool implements ITool {

    @Override
    public String getName() {
        return "full_page";
    }

    @Override
    public String description() {
        return "全屏截图TOOL(如果你发现有些信息在当前页面没有,可能需要全部的页面信息,你可以发送全屏截图指令)";
    }

    @Override
    public String parameters() {
        return "- name: (optional) Description or label for the action";
    }

    @Override
    public String usage() {
        return """
                <full_page>
                <arguments>
                {
                  "name": "全屏信息获取"
                }
                </arguments>
                </full_page>
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

