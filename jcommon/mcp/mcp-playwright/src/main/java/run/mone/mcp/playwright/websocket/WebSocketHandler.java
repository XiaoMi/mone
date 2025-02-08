package run.mone.mcp.playwright.websocket;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import run.mone.hive.common.JsonUtils;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.schema.Message;
import run.mone.hive.schema.RoleContext;
import run.mone.mcp.playwright.bo.HistoryMsg;
import run.mone.mcp.playwright.constant.ResultType;
import run.mone.mcp.playwright.role.Chatter;
import run.mone.mcp.playwright.role.RoleClassifier;
import run.mone.mcp.playwright.role.Shopper;
import run.mone.mcp.playwright.role.actions.roleclassifiter.ClassifierAction;
import run.mone.mcp.playwright.role.actions.shopper.OpenTabAction;
import run.mone.mcp.playwright.role.actions.shopper.OperationAction;
import run.mone.mcp.playwright.role.actions.shopper.ScrollAction;
import run.mone.mcp.playwright.service.ChromeTestService;
import run.mone.mcp.playwright.service.LLMService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<WebSocketSession>();

    private CopyOnWriteArrayList<HistoryMsg> msgList = new CopyOnWriteArrayList<>();

    private LLM llm;

    @Resource
    private LLMService llmService;

    @Resource
    private ChromeTestService chromeTestService;

    private static final Map<String, Chatter> sessionIdChatter = new ConcurrentHashMap<>();
    private static final Map<String, Shopper> sessionIdShopper = new ConcurrentHashMap<>();
    private static final Map<String, RoleClassifier> sessionIdRoleClassifier = new ConcurrentHashMap<>();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        initChatter(session);
        initShopperAndRoleClassifier(session);
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload:{}", payload);

        if (payload.equals("ping") || payload.equals("client_connected")) {
            return;
        }

        Shopper shopper = sessionIdShopper.get(session.getId());
        RoleClassifier roleClassifier = sessionIdRoleClassifier.get(session.getId());
        Chatter chatter = sessionIdChatter.get(session.getId());

        JsonObject req = JsonParser.parseString(payload).getAsJsonObject();
//        boolean test = JsonUtils.getValueOrDefault(req, "test", "false").equals("true");
        String from = JsonUtils.getValueOrDefault(req, "from", "chrome");
        String cmd = JsonUtils.getValueOrDefault(req, "cmd", "");

        //来自浏览器
        if (from.equals("chrome")) {
            JsonObject res = new JsonObject();
            String data = req.get("data").getAsString();

            if (data.equals("clear")) {
                log.info("clear");
                return;
            }

            //用来测试
            if (data.startsWith("?test:")) {
                session.sendMessage(new TextMessage(chromeTestService.invoke(data.split(":")[1], req, res)));
                return;
            }

            if (data.startsWith("!!")) {
                shopper.getRc().news.put(Message.builder().content(data).build());
                return;
            }

            if (cmd.equals("action_ping")) {
                log.info("action ping");
                return;
            }

            //chrome直接返回的(目前只有购物)
            if (cmd.equals("shopping")) {
                shopper.getRc().news.put(Message.builder().type("json").content(data).build());
                return;
            }

            roleClassifier.getRc().news.put(Message.builder().sendTo(Lists.newArrayList("RoleClassifier")).content(data).build());
            Message classifiterRes = roleClassifier.run().join();

            String agentName = classifiterRes.getContent();

            //单纯的聊天
            if (agentName.equals("Chatter")) {
                chatter.getRc().news.put(Message.builder().role("user").sendTo(Lists.newArrayList("Chatter")).content(data).build());
                chatter.run().join();
                return;
            }


            //用户有购物意图
            if (agentName.equals("Shopper")) {
                shopper.getRc().news.put(Message.builder().type("string").role("user").content(data).build());
                new Thread(()-> shopper.run()).start();
                return;
            }

            session.sendMessage(new TextMessage(res.toString()));
            return;
        }

        session.sendMessage(new TextMessage(payload));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        sessionIdChatter.remove(session.getId());
        sessionIdShopper.remove(session.getId());
        sessionIdRoleClassifier.remove(session.getId());
    }

    public void sendMessageToAll(String message) throws IOException {
        TextMessage textMessage = new TextMessage(message);
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(textMessage);
            }
        }
    }

    private void initChatter(WebSocketSession session){
        Chatter chatter = new Chatter(session);
        LLMConfig config = LLMConfig.builder().llmProvider(LLMProvider.DOUBAO).build();
        LLM chatLLM = new LLM(config);
        chatter.setLlm(chatLLM);
        chatter.setRc(new RoleContext(chatter.getProfile()));
        sessionIdChatter.put(session.getId(), chatter);
    }

    private void initShopperAndRoleClassifier(WebSocketSession session){
        LLMConfig config = LLMConfig.builder().llmProvider(LLMProvider.GOOGLE_2).build();
        if (config.getLlmProvider() == LLMProvider.GOOGLE_2) {
            config.setUrl(System.getenv("GOOGLE_AI_GATEWAY") + "generateContent");
        }
        llm = new LLM(config);
        Shopper shopper = new Shopper();
        shopper.setLlm(llm);
        shopper.setActions(
                //打开页面
                new OpenTabAction("在tab中打开某个网址"),
                //点击排名第一的商品
                new OperationAction("在网页中执行某些操作(点击 填入内容)"),
                //点击加入购物车
                new ScrollAction("滚动页面")
        );
        shopper.setConsumer(msg->{
            try {
                JsonObject obj = new JsonObject();
                obj.addProperty("data",msg);
                obj.addProperty("type", ResultType.ACTION);
                session.sendMessage(new TextMessage(obj.toString()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


        RoleClassifier roleClassifier = new RoleClassifier();
        roleClassifier.setLlm(llm);
        roleClassifier.setActions(new ClassifierAction());
        roleClassifier.setRc(new RoleContext(roleClassifier.getProfile()));

        sessionIdShopper.put(session.getId(), shopper);
        sessionIdRoleClassifier.put(session.getId(), roleClassifier);
    }
}