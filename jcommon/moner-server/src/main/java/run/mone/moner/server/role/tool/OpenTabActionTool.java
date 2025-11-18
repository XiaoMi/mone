package run.mone.moner.server.role.tool;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;
import run.mone.hive.schema.Message;
import run.mone.hive.common.JsonUtils;
import run.mone.hive.common.ToolDataInfo;
import run.mone.moner.server.common.MultiXmlParser;

public class OpenTabActionTool implements ITool {

    @Override
    public String getName() {
        return "open_tab";
    }

    @Override
    public String description() {
        return "创建新标签页TOOL(打开标签页后,chrome会渲染+截图发送回来当前页面,需要根据用户请求确定具体的action中属性)";
    }

    @Override
    public String parameters() {
        return """
                - action: (required) Object describing the tab creation
                  - type: (required) should be \"createNewTab\"
                  - url: (required) target URL to open
                  - auto: (optional) whether to auto-activate the tab (\"true\"/\"false\")
                  - desc: (optional) description
                """;
    }

    @Override
    public String usage() {
        String taskProgress = """
            <task_progress>
            Checklist here (optional)
            </task_progress>
            """;
        if (!taskProgress()) {
            taskProgress = "";
        }
        return """
                <open_tab>
                <arguments>
                {
                  "action": {
                    "type": "createNewTab",
                    "url": "https://www.jd.com/",
                    "auto": "true",
                    "desc": "打开京东"
                  }
                }
                </arguments>
                </open_tab>
                """.formatted(taskProgress);
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

