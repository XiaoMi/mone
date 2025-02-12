package run.mone.mcp.playwright.role.actions.shopper;

import run.mone.hive.actions.Action;
import run.mone.hive.schema.Message;
import run.mone.mcp.playwright.common.MultiXmlParser;
import run.mone.mcp.playwright.common.Result;

/**
 * @author goodjava@qq.com
 * @date 2025/2/7 15:15
 */
public class OpenTabAction extends Action {

    public OpenTabAction(String url) {
        setFunction((req, action, ctx) -> {
            Message reqMsg = req.getMessage();
            Result data = (Result) reqMsg.getData();
            String actions = data.getKeyValuePairs().get("arguments");
            String xml = new MultiXmlParser().jsonToXml(actions);
            return Message.builder().data(url).content(xml).build();
        });
    }
}
