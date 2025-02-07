package run.mone.mcp.playwright.role.actions.shopper;

import run.mone.hive.actions.Action;
import run.mone.hive.schema.Message;

/**
 * @author goodjava@qq.com
 * @date 2025/2/7 15:15
 */
public class OpenTabAction extends Action {

    public OpenTabAction(String url) {
        setFunction((req, action, ctx) -> {
            return Message.builder().data(url).data(
                    """
                            <action type="createNewTab" url="https://www.jd.com/" auto="true">
                            打开京东
                            </action>
                            """
            ).build();
        });
    }
}
