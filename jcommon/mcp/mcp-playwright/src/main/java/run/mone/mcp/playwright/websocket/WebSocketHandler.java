package run.mone.mcp.playwright.websocket;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import run.mone.m78.client.util.GsonUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    private LLM llm;

    public WebSocketHandler() {
        LLMConfig config = LLMConfig.builder().llmProvider(LLMProvider.DOUBAO).build();
        if (config.getLlmProvider() == LLMProvider.GOOGLE_2) {
            config.setUrl(System.getenv("GOOGLE_AI_GATEWAY") + "generateContent");
        }
        llm = new LLM(config);

    }

    private String prompt = """
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
            
            3.渲染页面
            <action type="buildDomTree">
            $message
            </action>
            
            4.取消渲染
            <action type="cancelBuildDomTree">
            $message
            </action>
            
            
            5.取消标记页面元素
            
            5.滚动一屏屏幕
            <action type="scrollOneScreen">
            $message
            </action>
            
            6.输入内容 回车 或者点击控件
            7.产生通知
            <action type="notification">
            $message
            </action>
            
            8.获取当前窗口的所有标签
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