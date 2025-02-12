package run.mone.mcp.playwright.role.actions;

import run.mone.hive.actions.Action;
import run.mone.hive.schema.Message;

/**
 * @author goodjava@qq.com
 * @date 2025/2/7 15:36
 * 全屏截图
 */
public class FullPageAction extends Action {

    public FullPageAction(String description) {
        super("FullPageAction", description);
        setFunction((req, action, ctx) -> Message.builder().content("""
                //滚动屏幕
                <action type="scrollToBottom">
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

                <action type="scrollToTop">
                </action>

                <action type="pause">
                </action>

                //全屏截图 并且 把截图回传回来
                <action type="screenshotFullPage" send="true">
                </action>
                """).build());
    }
}
