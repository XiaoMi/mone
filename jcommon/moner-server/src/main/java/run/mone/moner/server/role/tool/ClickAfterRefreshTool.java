package run.mone.moner.server.role.tool;

import run.mone.hive.roles.tool.ITool;

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
}

