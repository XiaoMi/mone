package run.mone.moner.server.role.tool;

import run.mone.hive.roles.tool.ITool;

public class CodeActionTool implements ITool {

    @Override
    public String getName() {
        return "code_action";
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
                <code_action>
                <arguments>
                {
                  "name": "自定义脚本",
                  "code": "alert(123);"
                }
                </arguments>
                </code_action>
                """;
    }
}

