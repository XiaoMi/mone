package run.mone.mcp.playwright.role.actions.shopper;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import run.mone.hive.actions.Action;
import run.mone.hive.schema.Message;
import run.mone.mcp.playwright.context.ApplicationContextProvider;
import run.mone.mcp.playwright.service.LLMService;

/**
 * @author goodjava@qq.com
 * @date 2025/2/7 15:18
 */
public class OperationAction extends Action {

    private String actionPrompt = """
            支持的action列表:
            //尽量一次返回一个页面的所有action操作
            //选那个和element最近的数字
            //数字的颜色和这个元素的框是一个颜色
            
            //找到输入框输入内容
            <action type="action" name="fill" elementId="12" value="冰箱">
            在搜索框里输入冰箱
            </action>
            
            //点击搜索按钮
            <action type="action" name="click" elementId="13">
            点击搜索按钮
            </action>
            
            
            根据不同的页面返回不同的action列表:
            页面:需要执行的动作
            
            %s
            
            分析出action列表(你每次只需要返回这个页面的action列表)
            你先要分析出来现在是那个页面(首页,搜索详情页,商品详情页,购物车加购页面)
            然后根据页面返回对应的action列表
            
            
            %s
            
            当前需求:
            %s
            """;


    public OperationAction(String description) {
        super("OperationAction", description);

        setFunction((req, action, ctx) -> {
            Message reqMsg = req.getMessage();
            String data = reqMsg.getContent();

            LLMService llmService = ApplicationContextProvider.getBean(LLMService.class);

            JsonObject jsonObj = JsonParser.parseString(data).getAsJsonObject();
            String img = jsonObj.get("img").getAsString();
            String code = jsonObj.get("code").getAsString();
            //返回页面操作指令
            if (img.startsWith("data:image")) {
                img = img.split("base64,")[1];
                code = "";
            }

            String llmRes = llmService.call(llm, code, img, this.getRole().getPrompt() + "\n" + this.getRole().getGoal());

            return Message.builder().content(llmRes).build();
        });
    }
}
