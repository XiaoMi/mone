package run.mone.moner.server.role.tool;

import run.mone.hive.roles.tool.ITool;

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
}

