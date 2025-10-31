package run.mone.moner.server.role.tool;

import run.mone.hive.roles.tool.ITool;

public class OpenTabActionTool implements ITool {

    @Override
    public String getName() {
        return "OpenTabAction";
    }

    @Override
    public String description() {
        return "创建新标签页TOOL(打开标签页后,chrome会渲染+截图发送回来当前页面)";
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
        return """
                <use_mcp_tool>
                <server_name>chrome-server</server_name>
                <tool_name>OpenTabAction</tool_name>
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
                </use_mcp_tool>
                """;
    }
}

