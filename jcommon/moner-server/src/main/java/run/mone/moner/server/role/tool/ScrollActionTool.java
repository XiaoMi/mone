package run.mone.moner.server.role.tool;

import run.mone.hive.roles.tool.ITool;

public class ScrollActionTool implements ITool {

    @Override
    public String getName() {
        return "ScrollAction";
    }

    @Override
    public String description() {
        return "滚动一屏屏幕TOOL(如果你发现有些信息在当前页面没有展示全,但可能在下边的页面,你可以发送滚动屏幕指令)";
    }

    @Override
    public String parameters() {
        return "(no parameters)";
    }

    @Override
    public String usage() {
        return """
                <use_mcp_tool>
                <server_name>chrome-server</server_name>
                <tool_name>ScrollAction</tool_name>
                <arguments>
                {
                }
                </arguments>
                </use_mcp_tool>
                """;
    }
}

