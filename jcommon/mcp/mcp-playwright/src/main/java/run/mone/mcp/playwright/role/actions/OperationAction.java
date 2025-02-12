package run.mone.mcp.playwright.role.actions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import run.mone.hive.actions.Action;
import run.mone.hive.schema.Message;
import run.mone.mcp.playwright.common.Const;
import run.mone.mcp.playwright.common.MultiXmlParser;
import run.mone.mcp.playwright.common.Result;

/**
 * @author goodjava@qq.com
 * @date 2025/2/7 15:18
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OperationAction extends Action {

    private String actionPrompt = """
            支持的action列表:
            //尽量一次返回一个页面的所有action操作
            //选那个和element最近的数字
            //数字的颜色和这个元素的框是一个颜色
            
                 //输入操作
                 //找到输入框输入内容
                 "action1": {
                    "type": "action",
                    "name": "fill",
                    "elementId": "12",
                    "value": "冰箱"
                  }
                  
                  //点击操作
                  //点击搜索按钮 
                  "action2": {
                    "type": "action",
                    "name": "click",
                    "elementId": "13",
                    "desc": "点击搜索按钮"
                  }
            
            """;


    public OperationAction() {
        setName("OperationAction");
        setDescription("""
                #.需要在当前页面执行一系列操作TOOL(比如填入搜索内容后点击搜索按钮)
                + 尽量一次返回一个页面的所有action操作
                + elementId的数字会在元素的右上角
                + 数字的颜色和这个元素的边框一定是一个颜色
                + 必须返回tabId(如果没有,需要你打开相应的tab)
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
                    "tabId": "2"
                  },
                  "action2": {
                    "type": "action",
                    "name": "click",
                    "elementId": "13",
                    "desc": "点击搜索按钮",
                    "tabId": "2"
                  }
                }
                </arguments>
                </use_mcp_tool>
                """);
        setFunction((req, action, ctx) -> {
            Message reqMsg = req.getMessage();
            Result data = (Result) reqMsg.getData();
            String actions = data.getKeyValuePairs().get("arguments");
            String xml = new MultiXmlParser().jsonToXml(actions);

            xml = Const.pause + xml;

            return Message.builder().content(xml).build();
        });
    }
}