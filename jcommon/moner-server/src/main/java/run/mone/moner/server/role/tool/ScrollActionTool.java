package run.mone.moner.server.role.tool;

import run.mone.hive.roles.tool.ITool;

public class ScrollActionTool implements ITool {

    @Override
    public String getName() {
        return "scroll_action";
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
                <scroll_action>
                <arguments>
                {
                  "action1": { "type": "scrollOneScreen", "desc": "滚动屏幕" },
                  "action2": { "type": "pause" },
                  "action3": { "type": "cancelBuildDomTree" },
                  "action4": { "type": "pause" },
                  "action5": { "type": "buildDomTree" },
                  "action6": { "type": "pause" },
                  "action7": {
                    "type": "screenshot",
                    "send": "true",
                    "test": "true",
                    "removeDomTree": "true",
                    "desc": "截图并且把截图回传回来"
                  }
                }
                </arguments>
                </scroll_action>
                """;
    }
}
