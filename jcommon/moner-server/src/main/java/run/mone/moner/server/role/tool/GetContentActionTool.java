package run.mone.moner.server.role.tool;

import run.mone.hive.roles.tool.ITool;

public class GetContentActionTool implements ITool {

    @Override
    public String getName() {
        return "GetContentAction";
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
                <use_mcp_tool>
                <server_name>chrome-server</server_name>
                <tool_name>GetContentAction</tool_name>
                <arguments>
                {
                }
                </arguments>
                </use_mcp_tool>
                """;
    }
}

