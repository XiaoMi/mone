package run.mone.mcp.playwright.role.actions.roleclassifiter;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.actions.Action;
import run.mone.hive.schema.Message;
import run.mone.hive.utils.XmlParser;

/**
 * @author goodjava@qq.com
 * @date 2025/2/7 16:21
 */
@Slf4j
public class ClassifierAction extends Action {


    public ClassifierAction() {
        setFunction((req, action, ctx) -> {
            String data = req.getMessage().getContent();
            String actionRes = llm.chat("""
                    帮我分析下,用户的想法是否是想购物 如果是则直接返回 type=Shopper
                    定义:
                    Shopper (购物者)
                    Chatter (聊天者)
                    Debugger (调试者)
                    
                    例子:
                    帮我去京东挑选下mac电脑
                    
                    返回的格式:
                    <boltAction type="Shopper" subType="mac电脑"  url="https://www.jd.com">
                    用户想购买mac电脑
                    </boltAction>
                    
                    用户的的想法:
                    """
                    + data);

            XmlParser.ActionItem v = XmlParser.parser0(actionRes).get(0);
            log.info("{}", v);

            String res = "";
            if (v.getType().equals("shopping")) {
                res = "shopping";
            } else {
                res = "chat";
            }
            return Message.builder().content(res).build();
        });
    }
}
