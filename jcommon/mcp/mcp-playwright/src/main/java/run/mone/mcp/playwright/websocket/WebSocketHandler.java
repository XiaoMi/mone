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
import run.mone.hive.utils.XmlParser;
import run.mone.mcp.playwright.service.ChromeTestService;
import run.mone.mcp.playwright.service.LLMService;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<WebSocketSession>();

    private LLM llm;

    @Resource
    private LLMService llmService;

    @Resource
    private ChromeTestService chromeTestService;

    public WebSocketHandler() {
        LLMConfig config = LLMConfig.builder().llmProvider(LLMProvider.GOOGLE_2).build();
        if (config.getLlmProvider() == LLMProvider.GOOGLE_2) {
            config.setUrl(System.getenv("GOOGLE_AI_GATEWAY") + "generateContent");
        }
        llm = new LLM(config);
    }


    private String itemName = "mac苹果电脑";


    //这是一个购物的任务列表
    private List<String> messageList = Lists.newArrayList(
            "打开京东",
            "jd首页:搜索 ",
            "搜素详情页:点击排名第一的商品的图片(在商品列表里,有图)",
            "商品详情页:点击 加入购物车 按钮(红色大按钮)",
            "购物车加购页面:点击去购物车结算按钮"
    );

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
            
            8.当给你一张截图,并且让你返回合适的action列表的时候,你就需要返回这个action类型了(这个action往往是多个 name=click(点击)  fill=填入内容  enter=回车  elementId=要操作的元素id,截图和源码里都有)
            //尽量一次返回一个页面的所有action操作
            //选哪个和element最近的数字
            //数字的颜色和这个元素的框是一个颜色
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
            %s
            """;

    private String text = """
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

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("index:{} payload:{}", this.index, payload);

        if (payload.equals("ping") || payload.equals("client_connected")) {
            log.info(payload);
            return;
        }

        JsonObject req = JsonParser.parseString(payload).getAsJsonObject();

        if (req.has("from")) {
            String from = req.get("from").getAsString();
            //来自浏览器
            if (from.equals("chrome")) {
                JsonObject res = new JsonObject();
                String data = req.get("data").getAsString();

                if (data.equals("clear")) {
                    log.info("clear");
                    this.index = 0;
                    return;
                }

                //用来测试
                if (data.startsWith("?test:")) {
                    sendMessageToAll(chromeTestService.invoke(data.split(":")[1], req, res));
                    return;
                }

                String cmd = "";
                if (req.has("cmd")) {
                    cmd = req.get("cmd").getAsString();
                }

                if (cmd.equals("action_ping")) {
                    log.info("action ping");
                    return;
                }

                if (this.index == 0) {
                    String actionRes = llm.chat("""
                            帮我分析下,用户的想法是否是想购物 如果是则直接返回shopping,不然返回chat.
                            
                            例子:
                            帮我去京东挑选下mac电脑
                            
                            返回的格式:
                            <boltAction type="shopping" subType="mac电脑"  url="https://www.jd.com">
                            用户想购买mac电脑
                            </boltAction>
                            
                            用的的想法:
                            """
                            + data);

                    XmlParser.ActionItem v = XmlParser.parser0(actionRes).get(0);
                    log.info("{}", v);

                    if (v.getType().equals("shopping")) {
                        data = "shopping";
                        this.itemName = v.getSubType();
                    } else {
                        cmd = "chat";
                    }
                }

                //单纯的聊天
                if (cmd.equals("chat")) {
                    String chatRes = llm.chat(data);
                    chatRes = "<action type=\"chat\">" + chatRes + "</action>";
                    sendMessageToAll(chatRes);
                    return;
                }


                //购物
                if (data.equals("shopping") || cmd.equals("shopping")) {
                    //任务已经完成
                    if (this.index >= this.messageList.size()) {
                        sendMessageToAll("""
                                <action type="end" url="https://www.jd.com/">
                                </action>
                                """);
                        return;
                    }

                    String msg = messageList.get(this.index);

                    //打开购物的页面(打开新的tab页)
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

                    if (this.index == 1) {
                        msg = msg + itemName;
                    }

                    //要开始处理页面了,都是内容+截图
                    if (cmd.equals("shopping")) {
                        try {
                            JsonObject jsonObj = JsonParser.parseString(data).getAsJsonObject();
                            String img = jsonObj.get("img").getAsString();
                            String code = jsonObj.get("code").getAsString();

                            //返回页面操作指令
                            if (true) {
                                if (img.startsWith("data:image")) {
                                    img = img.split("base64,")[1];
                                    code = "";
                                }
                                String t = text.formatted(String.join("\n", messageList), code, msg);
                                String llmRes = llmService.call(llm, t, img);

                                res.addProperty("data", llmRes);
                                sendMessageToAll(res.toString());
                            }
                            this.index++;
                        } catch (Throwable ex) {
                            log.error(ex.getMessage(), ex);
                        }
                    }

                    return;

                }


                String ask = AiTemplate.renderTemplate(prompt, ImmutableMap.of("data", data));
                String answer = llm.chat(ask);
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