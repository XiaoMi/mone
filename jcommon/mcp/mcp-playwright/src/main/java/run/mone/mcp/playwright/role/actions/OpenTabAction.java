package run.mone.mcp.playwright.role.actions;

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
        setName("OpenTabAction");
        setDescription("""
                #.创建新标签页(打开标签页后,chrome会渲染+截图发送回来当前页面)
                <use_mcp_tool>
                <server_name>chrome-server</server_name>
                <tool_name>OpenTabAction</tool_name>
                <arguments>
                {
                }
                </arguments>
                </use_mcp_tool>
                
                """);
        setFunction((req, action, ctx) -> {
            Message reqMsg = req.getMessage();
            Result data = (Result) reqMsg.getData();
            String actions = data.getKeyValuePairs().get("arguments");
            String xml = new MultiXmlParser().jsonToXml(actions);
            return Message.builder().data(url).content(xml).build();
        });
    }
}
