package run.mone.moner.server.role.tool;

import run.mone.hive.roles.tool.ITool;

public class FullPageActionTool implements ITool {

    @Override
    public String getName() {
        return "FullPageAction";
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
                <use_mcp_tool>
                <server_name>chrome-server</server_name>
                <tool_name>FullPageAction</tool_name>
                <arguments>
                {
                  "name": "全屏信息获取"
                }
                </arguments>
                </use_mcp_tool>
                """;
    }
}

