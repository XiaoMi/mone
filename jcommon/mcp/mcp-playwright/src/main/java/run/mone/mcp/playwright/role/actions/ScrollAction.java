package run.mone.mcp.playwright.role.actions;

import run.mone.hive.actions.Action;
import run.mone.hive.schema.Message;

/**
 * @author goodjava@qq.com
 * @date 2025/2/7 15:36
 * 滚动屏幕
 */
public class ScrollAction extends Action {

    public ScrollAction() {
        setName("ScrollAction");
        setDescription("""
                #.滚动一屏屏幕TOOL(如果你发现有些信息在当前页面没有展示全,但可能在下边的页面,你可以发送滚动屏幕指令)
                <use_mcp_tool>
                <server_name>chrome-server</server_name>
                <tool_name>ScrollAction</tool_name>
                <arguments>
                {
                }
                </arguments>
                </use_mcp_tool>
                """);
        setFunction((req,action,ctx)-> Message.builder().content("""
                 //滚动屏幕
                <action type="scrollOneScreen">
                </action>
                //暂停
                <action type="pause">
                </action>
                
                <action type="cancelBuildDomTree">
                </action>
                
                <action type="pause">
                </action>
                
                <action type="buildDomTree">
                </action>
               
                <action type="pause">
                </action>
                
                //截图 并且 把截图回传回来
                <action type="screenshot" send="true" test="true">
                </action>
                """).build());
    }
}
