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
        setFunction((req,action,ctx)->{
            return Message.builder().content("""
                    <action type="scrollOneScreen">
                     $message
                    </action>
                    """).build();
        });
    }
}
