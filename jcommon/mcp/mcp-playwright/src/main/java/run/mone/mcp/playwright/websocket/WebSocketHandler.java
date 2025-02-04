package run.mone.mcp.playwright.websocket;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import run.mone.hive.common.AiTemplate;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.schema.AiMessage;
import run.mone.mcp.playwright.service.LLMService;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    private LLM llm;

    @Resource
    private LLMService llmService;

    public WebSocketHandler() {
        LLMConfig config = LLMConfig.builder().llmProvider(LLMProvider.DOUBAO).build();
        if (config.getLlmProvider() == LLMProvider.GOOGLE_2) {
            config.setUrl(System.getenv("GOOGLE_AI_GATEWAY") + "generateContent");
        }
        llm = new LLM(config);
    }

    private List<String> messageList = Lists.newArrayList("打开京东", "jd首页:搜索冰箱", "搜素详情页:点击排名第一的连接", "商品详情页:点击加入购物车按钮", "购物车加购页面:点击去购物车结算按钮");

    private int index = 0;


    public static String prompt = """
            你是一个浏览器操作专家.你总是能把用户的需求,翻译成专业的操作指令.
            你支持的指令:
            1.打开tab,并且带上url,用来打开某个页面
            <action type="openTab">
            </action>
            
            1.创建新标签页
            问题:
            新建标签页,打开baidu
            
            返回结果:
            <action type="createNewTab" url="https://www.baidu.com">
            打开百度
            </action>
            
            2.关闭某个tab,带上tabName
            <action type="closeTab">
            $message
            </action>
            
            3.截屏,截取屏幕(当前可视区域)
            <action type="screenshot">
            $message
            </action>
            
            4.渲染页面
            <action type="buildDomTree">
            $message
            </action>
            
            5.取消渲染
            <action type="cancelBuildDomTree">
            $message
            </action>
            
            
            6.取消标记页面元素
            
            7.滚动一屏屏幕
            <action type="scrollOneScreen">
            $message
            </action>
            
            8.当chrome 返回页面内容和图片后,你就需要返回这个action了 (当浏览器返回页面源码还有页面截图的时候,你就需要返回这个action了,这个action往往是多个 name=click(点击)  fill 填入内容  enter 回车  elementId=要操作的元素id,截图和源码里都有)
            //尽量一次返回一个页面的所有action操作
            <action type="action" name="fill" elementId="12" value="冰箱">
            在搜索框里输入冰箱
            </action>
            
            <action type="action" name="click" elementId="13">
            点击搜索按钮
            </action>
            
            
            9.产生通知
            <action type="notification">
            $message
            </action>
            
            10.获取当前窗口的所有标签
            <action type="getCurrentWindowTabs" service="tabManager">
            </action>
            
            返回的内容格式:
            
            <action type="$type">
            </action>
            
            Example:
            打开tab:
            <action type="tab" url="http://www.baidu.com">
            </action>
            
            截屏:
            <action type="screenshot">
            </action>
            
            用户需求:
            ${data}
            
            你的返回:
            
            
            """;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload:{}", payload);
        if (payload.equals("ping")) {
            log.info("ping");
            return;
        }

        JsonObject obj = JsonParser.parseString(payload).getAsJsonObject();
        if (obj.has("from")) {
            String from = obj.get("from").getAsString();
            //来自浏览器
            if (from.equals("chrome")) {
                String data = obj.get("data").getAsString();

                String cmd = "";
                if (obj.has("cmd")) {
                    cmd = obj.get("cmd").getAsString();
                }

                //需要ai来返回操作内容
                if (data.equals("cz")) {
                    String d = "帮我搜索下苹果笔记本";
                    String ask = AiTemplate.renderTemplate(prompt, ImmutableMap.of("data", d));
                    AiMessage msg = AiMessage.builder().build();
                    String answer = llm.ask(Lists.newArrayList(msg));
                    log.info("answer:{}", answer);
                    return;
                }

                //购物
                if (cmd.equals("shopping")) {
                    String msg = messageList.get(this.index);
                    JsonObject res = new JsonObject();
                    //打开购物的页面
                    if (this.index == 0) {
                        res.addProperty("data", msg + "\n" +
                                """
                                        <action type="createNewTab" url="https://www.jd.com/" auto="true">
                                        打开京东
                                        </action>
                                        """);
                        sendMessageToAll(res.toString());
                        this.index++;
                        return;
                    }

                    //结束了
                    if (this.index == messageList.size()) {
                        res.addProperty("data", """
                                <action type="finish" url="https://www.jd.com/">
                                </action>
                                """);
                        this.index = 0;
                        sendMessageToAll(res.toString());
                        return;
                    }

                    //要开始处理页面了,都是内容+截图
                    String code = obj.get("src").getAsString();
                    String img = obj.get("img").getAsString();
                    String llmRes = llmService.call(llm, code, img);
                    res.addProperty("data", llmRes);
                    sendMessageToAll(res.toString());
                    this.index++;
                    return;

                }


                String ask = AiTemplate.renderTemplate(prompt, ImmutableMap.of("data", data));
                String answer = llm.chat(ask);

                JsonObject res = new JsonObject();
                res.addProperty("data", answer);
                sendMessageToAll(res.toString());
                return;
            }
        }

        sendMessageToAll(payload);
    }

    @Override

    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    public void sendMessageToAll(String message) throws IOException {
        TextMessage textMessage = new TextMessage(message);
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(textMessage);
            }
        }
    }
}