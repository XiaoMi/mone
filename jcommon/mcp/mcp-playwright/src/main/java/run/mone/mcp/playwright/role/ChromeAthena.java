package run.mone.mcp.playwright.role;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import run.mone.hive.Environment;
import run.mone.hive.common.AiTemplate;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.roles.Role;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;
import run.mone.mcp.playwright.bo.ChatWebSocketResp;
import run.mone.mcp.playwright.common.Const;
import run.mone.mcp.playwright.common.GsonUtils;
import run.mone.mcp.playwright.common.JsonUtils;
import run.mone.mcp.playwright.common.MultiXmlParser;
import run.mone.mcp.playwright.common.Result;
import run.mone.mcp.playwright.constant.ResultType;
import run.mone.mcp.playwright.context.ApplicationContextProvider;
import run.mone.mcp.playwright.service.LLMService;

import java.lang.reflect.Type;
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
public class ChromeAthena extends Role {


    private Consumer<String> consumer;

    private WebSocketSession session;

    private static final Type LIST_STRING = new TypeToken<List<String>>() {
    }.getType();

    private String userPrompt = """
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


    public ChromeAthena(WebSocketSession session) {
        super("Shopper", "购物者");
        setEnvironment(new Environment());

//        this.goal = """
//                购物步骤:(操作推荐)
//                1.创建京东首页tab(发现没有code的时候,必须调用这个接口)(OpenTabAction)
//                2.在首页的搜索框里输入要买的东西(根据用户的需求分析出来),然后点击搜索按钮 (OperationAction)
//                3.搜素详情页:你选择一个你觉得最合适的商品,点击这个商品的大图,你要忽略所有广告的图片
//                4.商品详情页:点击 加入购物车 按钮(红色大按钮)(OperationAction) (如果找不到对应的按钮 滚动屏幕 ScrollAction)
//                5.购物车加购页面:点击去购物车结算按钮(OperationAction) (如果找不到对应的按钮 滚动下屏幕 ScrollAction)
//                6.到达购物车列表页面就可以结束了(attempt_completion)
//
//                需要注意的点:
//                如果页面信息不全,可以滚动下页面
//                """;

//        super.prompt = """
//                你是一个浏览器操作专家.你总是能把用户的需求,翻译成专业的操作工具(tool).
//                参数里可能会一个一张页面的图片,这个图片中有每个可以操作的元素的序号.
//
//                支持的工具:
//
//                #.创建新标签页(打开标签页后,chrome会渲染+截图发送回来当前页面)
//                <use_mcp_tool>
//                <server_name>chrome-server</server_name>
//                <tool_name>OpenTabAction</tool_name>
//                <arguments>
//                {
//                }
//                </arguments>
//                </use_mcp_tool>
//
//                #.滚动一屏屏幕(如果你发现有些信息在当前页面没有展示全,但可能在下边的页面,你可以发送滚动屏幕指令)
//                <use_mcp_tool>
//                <server_name>chrome-server</server_name>
//                <tool_name>ScrollAction</tool_name>
//                <arguments>
//                {
//                }
//                </arguments>
//                </use_mcp_tool>
//
//
//                #.需要在当前页面执行一系列操作(比如填入搜索内容后点击搜索按钮)
//                + 尽量一次返回一个页面的所有action操作
//                + elementId的数字会在元素的右上角
//                + 数字的颜色和这个元素的边框一定是一个颜色
//                + 必须返回tabId(如果没有,需要你打开相应的tab)
//
//                <use_mcp_tool>
//                <server_name>chrome-server</server_name>
//                <tool_name>OperationAction</tool_name>
//                <arguments>
//                {
//                "action1": {
//                    "type": "action",
//                    "name": "fill",
//                    "elementId": "12",
//                    "value": "冰箱",
//                    "tabId": "2"
//                  },
//                  "action2": {
//                    "type": "action",
//                    "name": "click",
//                    "elementId": "13",
//                    "desc": "点击搜索按钮",
//                    "tabId": "2"
//                  }
//                }
//                </arguments>
//                </use_mcp_tool>
//
//                # chat
//                Description: A tool for handling general conversations and chat interactions. This tool should be used when the user's input is conversational in nature and doesn't require specific functional tools. It enables natural dialogue-based interactions in scenarios where other specialized tools are not applicable. Use this tool for engaging in general discussions, providing information, or offering support through conversation.
//                Parameters:
//                - message: (required) The chat message to respond to the user. The message should be natural, friendly, and maintain coherence and relevance with the user's input.
//                Usage:
//                <chat>
//                <message>Your chat message here</message>
//                </chat>
//
//
//                #.当前你发现你不能解决问题的时候,你可以返回:
//                <ask_followup_question>
//                <question>Your question here</question>
//                </ask_followup_question>
//
//                #.当你发现所有任务都结束后,你必须返回:
//                <attempt_completion>
//                <result>
//                Your final result description here
//                </result>
//                <command>Command to demonstrate result (optional)</command>
//                </attempt_completion>
//
//                你每次只能返回一个工具
//
//                """;
        super.prompt = """
                你是一个浏览器操作专家。你的任务是根据用户的需求选择合适的工具，并执行相应的操作。
                
                在以下两种场景下，你需要使用不同的工具：
                1. 购物需求
                2. 聊天需求
                
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
                
                # chat
                Description: A tool for handling general conversations and chat interactions. This tool should be used when the user's input is conversational in nature and doesn't require specific functional tools. It enables natural dialogue-based interactions in scenarios where other specialized tools are not applicable. Use this tool for engaging in general discussions, providing information, or offering support through conversation.
                Parameters:
                - message: (required) The chat message to respond to the user. The message should be natural, friendly, and maintain coherence and relevance with the user's input.
                Usage:
                <chat>
                <message>Your chat message here</message>
                </chat>
                
                
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
                
                每次操作只能返回一个工具，只需要返回工具内容即可，不用描述你用到了哪个工具。
                
                首先，你需要判断用户需求属于购物还是聊天。
                
                如果用户需求描述中包含购物相关的关键词，例如"购物"、"买"、"购买"等，请使用购物相关的工具。
                以下是具体的购物步骤：
                1.创建京东首页tab(发现没有code的时候,必须调用这个接口)(OpenTabAction)
                2.在首页的搜索框里输入要买的东西(根据用户的需求分析出来),然后点击搜索按钮 (OperationAction)
                3.搜素详情页:你选择一个你觉得最合适的商品,点击这个商品的大图,你要忽略所有广告的图片
                4.商品详情页:点击 加入购物车 按钮(红色大按钮)(OperationAction) (如果找不到对应的按钮 滚动屏幕 ScrollAction)
                5.购物车加购页面:点击去购物车结算按钮(OperationAction) (如果找不到对应的按钮 滚动下屏幕 ScrollAction)
                6.到达购物车列表页面就可以结束了(attempt_completion)
                
                如果用户需求描述中更偏向于日常对话、问候等，请使用聊天工具。
                
                举例：
                - 用户描述："我要买一个新的手机"
                请使用购物相关的工具。
                
                - 用户描述："你好，今天过得怎么样？"
                <chat><message>我今天过得非常充实</message></chat>。
                """;
        this.session = session;
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
            if (msg != null) {
                List<String> images = null;
                String code = "";
                String tabs = "";
                String text = "";

                if (msg.getType().equals("json")) {
                    JsonObject obj = JsonParser.parseString(msg.getContent()).getAsJsonObject();
                    text = JsonUtils.getValueOrDefault(obj, "text", "");
                    JsonArray imgs = obj.getAsJsonArray("img");
                    if (imgs != null) {
                        images = GsonUtils.gson.fromJson(imgs, LIST_STRING);
                        if (llm.getLlmProvider() == LLMProvider.GOOGLE_2) {
                            //google gemini 不需要前边的内容
                            images = images.stream().map(img -> {
                                if (img.startsWith("data:image")) {
                                    return img.split("base64,")[1];
                                }
                                return img;
                            }).collect(Collectors.toList());
                        }

                    }
                    code = JsonUtils.getValueOrDefault(obj, "code", "");
                    tabs = JsonUtils.getValueOrDefault(obj, "tabs", "");
                    msg.setContent(text);
                    msg.setRole("user");
                }

                this.getRc().getMemory().add(msg);

                //历史聊天记录
                String history = this.getRc().getMemory().getStorage().stream().map(it -> it.getRole() + ":" + it.getContent()).collect(Collectors.joining("\n"));

                LLMService llmService = ApplicationContextProvider.getBean(LLMService.class);

                String userPrompt = AiTemplate.renderTemplate(this.userPrompt, ImmutableMap.of("history", history, "code", code, "tabs", tabs));

                String res = llmService.callStream(this, this.llm, userPrompt, images, this.prompt);
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
        }
        try {
            return CompletableFuture.completedFuture(Message.builder().build());
        } finally {
            this.getRc().clearNews();
            this.getRc().getMemory().clear();
        }
    }


    @Override
    public void sendMessage(Message message) {
        sendMessage(message.getContent(), message.getType());
    }

    private void sendMessage(String content, String messageType) {
        try {
            ChatWebSocketResp resp = ChatWebSocketResp.builder().roleName(name).roleType("ASSISTANT").content(content).messageType(messageType).type(ResultType.CHAT).build();
            session.sendMessage(new TextMessage(GsonUtils.gson.toJson(resp)));
        } catch (Exception e) {
            log.error("send message error, ", e);
        }
    }
}
