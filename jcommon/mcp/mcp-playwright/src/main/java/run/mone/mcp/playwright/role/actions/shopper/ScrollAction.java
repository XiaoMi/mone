package run.mone.mcp.playwright.role.actions.shopper;

import run.mone.hive.actions.Action;
import run.mone.hive.schema.Message;

/**
 * @author goodjava@qq.com
 * @date 2025/2/7 15:36
 * 滚动屏幕
 */
public class ScrollAction extends Action {

    public ScrollAction(String description) {
        super("ScrollAction", description);
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
