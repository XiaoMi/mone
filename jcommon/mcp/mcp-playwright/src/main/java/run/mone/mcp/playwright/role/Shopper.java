package run.mone.mcp.playwright.role;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.Environment;
import run.mone.hive.common.AiTemplate;
import run.mone.hive.roles.Role;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;
import run.mone.mcp.playwright.common.Const;
import run.mone.mcp.playwright.common.MultiXmlParser;
import run.mone.mcp.playwright.common.Result;
import run.mone.mcp.playwright.context.ApplicationContextProvider;
import run.mone.mcp.playwright.service.LLMService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2025/2/7 14:58
 * 购物者
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class Shopper extends Role {


    private Consumer<String> consumer;

    private String userPrompt = """
            ===========
            ${goal}
            ===========
            历史聊天记录:
            ${history}
            ===========
            当前打开的tab:
            ${tabs}
            ===========
            <% 
            if (code != "") {
            %>
            当前页面的code:(辅助图片来参考选用那些序号,如果页面code是空,则说明没有打开相应页面)
            ${code}
            ===========
            <%
            }
            %>
            请帮我判断使用那个tool\n
            """;


    public Shopper() {
        super("Shopper", "购物者");
        setEnvironment(new Environment());

        this.goal = """
                购物步骤:
                1.创建京东首页tab(发现没有code的时候,必须调用这个接口)(OpenTabAction)
                2.在首页的搜索框里输入要买的东西(根据用户的需求分析出来),然后点击搜索按钮 (OperationAction)
                3.搜素详情页:点击商品列表中你觉得最符合要求的商品的购买链接(如果找不到对应的商品 滚动下屏幕 ScrollAction)
                4.商品详情页:点击 加入购物车 按钮(红色大按钮) (如果找不到对应的按钮 滚动下屏幕 ScrollAction)
                5.购物车加购页面:点击去购物车结算按钮 (如果找不到对应的按钮 滚动下屏幕 ScrollAction)
                6.到达购物车列表页面就可以结束了(attempt_completion)
                
                需要注意的点:
                如果页面信息不全,可以滚动下页面
                """;

        super.prompt = """
                你是一个浏览器操作专家.你总是能把用户的需求,翻译成专业的操作工具(tool).
                参数里可能会一个一张页面的图片,这个图片中有每个可以操作的元素的序号.
                
                支持的工具:
                
                #.创建新标签页(打开标签页后,chrome会渲染+截图发送回来当前页面)
                <use_mcp_tool>
                <server_name>chrome-server</server_name>
                <tool_name>OpenTabAction</tool_name>
                <arguments>
                {
                }
                </arguments>
                </use_mcp_tool>
                
                #.滚动一屏屏幕(如果你发现有些信息在当前页面没有展示全,但可能在下边的页面,你可以发送滚动屏幕指令)
                <use_mcp_tool>
                <server_name>chrome-server</server_name>
                <tool_name>ScrollAction</tool_name>
                <arguments>
                {
                }
                </arguments>
                </use_mcp_tool>
                
                
                #.需要在当前页面执行一系列操作(比如填入搜索内容后点击搜索按钮)
                + 尽量一次返回一个页面的所有action操作
                + 选那个和element最近的数字
                + 数字的颜色和这个元素的边框一定是一个颜色
                + 数字在元素的右侧
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
                
                
                #.当前你发现你不能解决问题的时候,你可以返回:
                <ask_followup_question>
                <question>Your question here</question>
                </ask_followup_question>
                
                #.当你发现所有任务都结束后,你必须返回:
                <attempt_completion>
                <result>
                Your final result description here
                </result>
                <command>Command to demonstrate result (optional)</command>
                </attempt_completion>
                
                你每次只能返回一个工具
                
                """;
    }


    @SneakyThrows
    @Override
    public CompletableFuture<Message> run() {

        ActionContext context = new ActionContext();

        int i = 0;
        while (i++ < 20) {
            ActionReq req = new ActionReq();
            req.setRole(Role.builder().name("user").build());
            Message msg = this.rc.getNews().poll(2, TimeUnit.MINUTES);

            String img = "";
            String code = "";
            String tabs = "";

            if (msg.getContent().equals("!!quit")) {
                log.info("!!quit");
                break;
            }


            if (msg.getType().equals("json")) {
                JsonObject obj = JsonParser.parseString(msg.getContent()).getAsJsonObject();
                img = obj.get("img").getAsString();
                code = obj.get("code").getAsString();
                tabs = obj.get("tabs").toString();
                //google gemini 不需要前边的内容
                if (img.startsWith("data:image")) {
                    img = img.split("base64,")[1];
                }
                msg.setContent("\n图片占位符\n");
                msg.setRole("user");
            }

            this.getRc().getMemory().add(msg);

            //历史聊天记录
            String history = this.getRc().getMemory().getStorage().stream().map(it -> it.getRole() + ":" + it.getContent()).collect(Collectors.joining("\n"));

            LLMService llmService = ApplicationContextProvider.getBean(LLMService.class);

            String userPrompt = AiTemplate.renderTemplate(this.userPrompt, ImmutableMap.of("history", history, "goal", this.goal, "code", code, "tabs", tabs));

            String res = llmService.call(this.llm, userPrompt, img, this.prompt);
            log.info("res:{}", res);
            List<Result> list = new MultiXmlParser().parse(res);
            Result result = list.get(0);

            this.getRc().getMemory().add(Message.builder().role("assistant").content(res).build());

            if (result.getTag().equals("attempt_completion") || result.getTag().equals("ask_followup_question")) {
                consumer.accept(Const.actionTemplate.formatted("end", result.getTag()));
                break;
            }

            if (result.getKeyValuePairs().getOrDefault("tool_name", "").equals("OpenTabAction")) {
                String content = this.getActions().get(0).run(req, context).join().getContent();
                consumer.accept(content);
            }

            if (result.getKeyValuePairs().getOrDefault("tool_name", "").equals("ScrollAction")) {
                String content = this.getActions().get(2).run(req, context).join().getContent();
                consumer.accept(content);
            }

            if (result.getKeyValuePairs().getOrDefault("tool_name", "").equals("OperationAction")) {
                req.setMessage(Message.builder().data(result).build());
                String content = this.getActions().get(1).run(req, context).join().getContent();
                consumer.accept(content);
            }

        }
        try {
            return CompletableFuture.completedFuture(Message.builder().build());
        } finally {
            this.getRc().clearNews();
            this.getRc().getMemory().clear();
        }
    }
}
