package run.mone.mcp.playwright.role.actions;

import run.mone.hive.actions.Action;
import run.mone.hive.schema.Message;

public class RefreshAfterClick extends Action {

    public RefreshAfterClick(String description) {
        super("RefreshAfterClick", description);
        setDescription("""
                #.刷新页面并构建dom树
                <use_mcp_tool>
                <server_name>chrome-server</server_name>
                <tool_name>FullPageAction</tool_name>
                <arguments>
                {
                }
                </arguments>
                </use_mcp_tool>
                """);
        setFunction((req,action,ctx)-> Message.builder().content("""
                <action type="cancelBuildDomTree">
                </action>

                <action type="pause">
                </action>

                <action type="buildDomTree" fullPage="true">
                </action>

                <action type="pause">
                </action>
                """).build());
    }
}
