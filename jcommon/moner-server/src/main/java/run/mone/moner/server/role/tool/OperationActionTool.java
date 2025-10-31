package run.mone.moner.server.role.tool;

import run.mone.hive.roles.tool.ITool;

public class OperationActionTool implements ITool {

    @Override
    public String getName() {
        return "OperationAction";
    }

    @Override
    public String description() {
        return """
                需要在当前页面执行一系列操作TOOL(比如填入搜索内容后点击搜索按钮)
                - 尽量一次返回一个页面的所有action操作
                - elementId的数字会在元素的右上角
                - 数字的颜色和这个元素的边框一定是一个颜色
                - 必须返回tabId(如果没有,需要你打开相应的tab)
                - 支持操作类型: fill(填入), click(点击), focus(聚焦), search(搜索), select(选择)
                """;
    }

    @Override
    public String parameters() {
        return """
                - actionX: (required, one or more) A set of action objects like action1, action2, ...
                  - type: (required) always \"action\"
                  - name: (required) one of [fill, click, focus, search, select]
                  - elementId: (required) target element id
                  - value: (optional) value to input/select
                  - desc: (optional) description
                  - tabId: (required) the tab id to act on
                  - next: (optional) whether to proceed automatically (\"true\"/\"false\")
                """;
    }

    @Override
    public String usage() {
        return """
                <use_mcp_tool>
                <server_name>chrome-server</server_name>
                <tool_name>OperationAction</tool_name>
                <arguments>
                {
                  "action1": {
                    "type": "action",
                    "name": "fill",
                    "elementId": "12",
                    "value": "冰箱",
                    "tabId": "2",
                    "next": "false"
                  },
                  "action2": {
                    "type": "action",
                    "name": "click",
                    "elementId": "13",
                    "desc": "点击搜索按钮",
                    "tabId": "2",
                    "next": "false"
                  },
                  "action3": {
                    "type": "action",
                    "name": "focus",
                    "elementId": "14",
                    "desc": "聚焦到输入框",
                    "tabId": "2",
                    "next": "false"
                  },
                  "action4": {
                    "type": "action",
                    "name": "search",
                    "elementId": "15",
                    "value": "搜索关键词",
                    "desc": "执行搜索操作",
                    "tabId": "2",
                    "next": "false"
                  },
                  "action5": {
                    "type": "action",
                    "name": "select",
                    "elementId": "16",
                    "value": "选项值",
                    "desc": "选择下拉框选项",
                    "tabId": "2",
                    "next": "false"
                  }
                }
                </arguments>
                </use_mcp_tool>
                """;
    }
}

