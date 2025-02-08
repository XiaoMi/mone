package run.mone.mcp.playwright.role.actions.shopper;

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


    public OperationAction(String description) {
        super("OperationAction", description);
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