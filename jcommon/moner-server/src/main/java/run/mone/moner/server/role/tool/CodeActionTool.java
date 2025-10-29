package run.mone.moner.server.role.tool;

import run.mone.hive.roles.tool.ITool;

public class CodeActionTool implements ITool {

    @Override
    public String getName() {
        return "CodeAction";
    }

    @Override
    public String description() {
        return "代码TOOL(当可以通过JavaScript代码实现所需功能时，生成并注入代码执行)";
    }

    @Override
    public String parameters() {
        return """
                - name: (optional) Description for the script
                - code: (required) JavaScript code to execute in the current page
                """;
    }

    @Override
    public String usage() {
        return """
                <use_mcp_tool>
                <server_name>chrome-server</server_name>
                <tool_name>CodeAction</tool_name>
                <arguments>
                {
                  "name": "自定义脚本",
                  "code": "alert(123);"
                }
                </arguments>
                </use_mcp_tool>
                """;
    }
}

