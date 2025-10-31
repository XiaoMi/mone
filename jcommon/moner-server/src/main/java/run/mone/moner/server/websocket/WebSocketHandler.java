package run.mone.moner.server.websocket;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import run.mone.hive.common.JsonUtils;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.Message;
import run.mone.moner.server.bo.McpModel;
import run.mone.moner.server.constant.ResultType;
import run.mone.moner.server.context.ApplicationContextProvider;
import run.mone.moner.server.mcp.FromType;
import run.mone.moner.server.mcp.McpOperationService;
import run.mone.moner.server.role.ChromeAgent;
import run.mone.moner.server.role.actions.*;
import run.mone.moner.server.service.ChromeTestService;
import run.mone.moner.server.service.LLMService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<WebSocketSession>();

    private LLM llm;

    @Resource
    private ChromeTestService chromeTestService;

    @Resource
    private LLMService llmService;

    private static final Map<String, ChromeAgent> sessionIdShopper = new ConcurrentHashMap<>();
    
    // 存储每个session对应的ReAct循环线程
    private static final Map<String, Thread> sessionReactThreads = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        initShopperAndRoleClassifier(session);
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload:{}", payload);

        if (payload.equals("ping") || payload.equals("client_connected")) {
            return;
        }

        ChromeAgent chromeAgent = sessionIdShopper.get(session.getId());

        JsonObject req = JsonParser.parseString(payload).getAsJsonObject();
        String from = JsonUtils.getValueOrDefault(req, "from", "chrome");
        String cmd = JsonUtils.getValueOrDefault(req, "cmd", "");

        //来自浏览器
        if (from.equals(FromType.CHROME.getValue())) {
            JsonObject res = new JsonObject();
            String data = req.get("data").getAsString();

            if (data.equals("clear")) {
                log.info("clear");
                chromeAgent.getRc().getNews().clear();
                chromeAgent.getRc().getMemory().clear();
                breakCurrentReactLoop(session);
                return;
            }

            //用来测试
            if (data.startsWith("?test:")) {
                session.sendMessage(new TextMessage(chromeTestService.invoke(data.split(":")[1], req, res)));
                return;
            }

            if (data.startsWith("!!")) {
                chromeAgent.getRc().news.put(Message.builder().content(data).build());
                return;
            }

            if (cmd.equals("action_ping")) {
                log.info("action ping");
                return;
            }

            //chrome直接返回的(目前只有购物)
            if (cmd.equals("shopping")) {
                chromeAgent.getRc().news.put(Message.builder().type("json").role("user").content(data).build());
                return;
            }

            if (cmd.equals("reply")) {
                JsonObject obj = JsonParser.parseString(data).getAsJsonObject();
                // action不成功，需要交给Role处理
                if (!obj.has("success") || !obj.get("success").getAsBoolean()) {
                    log.info("action:{} failed", obj.get("actionType"));
                    chromeAgent.getRc().news.put(Message.builder().type("reply").role("user").content(data).build());
                } else {
                    log.info("action:{} success", obj.get("actionType"));

                    if (obj.has("attributes")) {
                        JsonElement je = obj.get("attributes");
                        if (je.isJsonObject() && je.getAsJsonObject().has("next")) {
                            String next = je.getAsJsonObject().get("next").getAsString();
                            if (next.equals("true")) {
                                JsonObject jo = new JsonObject();
                                jo.addProperty("next","true");
                                chromeAgent.getRc().news.put(Message.builder().type("reply").role("user").content(jo.toString()).build());
                            }
                        }
                    }

                }
                return;
            }


            chromeAgent.getRc().news.put(Message.builder().type("json").role("user").content(data).build());
            
            // 先中断已存在的ReAct循环
            breakCurrentReactLoop(session);
            
            // 创建新的虚拟线程并记录
            Thread reactThread = Thread.ofVirtual().factory().newThread(() -> {
                try {
                    chromeAgent.run();
                } finally {
                    // 线程结束时清理记录
                    sessionReactThreads.remove(session.getId());
                }
            });
            sessionReactThreads.put(session.getId(), reactThread);
            reactThread.start();
            return;
        }

        // 来自athena
        if (from.equals(FromType.ATHENA.getValue())) {
            // TODO: 2025/2/17 
        }
        session.sendMessage(new TextMessage(payload));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        sessionIdShopper.remove(session.getId());
        // 连接关闭时中断对应的ReAct循环
        breakCurrentReactLoop(session);
    }

    public void sendMessageToAll(String message) throws IOException {
        TextMessage textMessage = new TextMessage(message);
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(textMessage);
            }
        }
    }

    @SneakyThrows
    private void initShopperAndRoleClassifier(WebSocketSession session) {
        // LLMConfig config = LLMConfig.builder().llmProvider(LLMProvider.GOOGLE_2).build();

        // if (config.getLlmProvider() == LLMProvider.GOOGLE_2) {
        //     config.setUrl(System.getenv("GOOGLE_AI_GATEWAY") + "streamGenerateContent?alt=sse");
        // }

        // if (config.getLlmProvider() == LLMProvider.OPENROUTER && StringUtils.isNotEmpty(System.getenv("OPENROUTER_AI_GATEWAY"))) {
        //     config.setUrl(System.getenv("OPENROUTER_AI_GATEWAY"));
        // }

        Pair<LLM, McpModel> llmConf = llmService.getLLM(FromType.CHROME.getValue());

        llm = llmConf.getLeft();
        ChromeAgent chromeAgent = new ChromeAgent(session);
        chromeAgent.setLlm(llm);
        chromeAgent.setActions(
                //打开页面
                new OpenTabAction("在tab中打开某个网址"),
                //点击排名第一的商品
                new OperationAction(),
                //点击加入购物车
                new ScrollAction(),
                //获取页面内容
                new GetContentAction(),
                //全屏截图
                new FullPageAction(),
                new CodeAction(),
                //刷新页面
                new ClickAfterRefresh()
        );
        chromeAgent.setConsumer(msg -> {
            try {
                JsonObject obj = new JsonObject();
                obj.addProperty("data", msg);
                obj.addProperty("type", ResultType.ACTION);
                session.sendMessage(new TextMessage(obj.toString()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        McpOperationService mcpOperationService = ApplicationContextProvider.getBean(McpOperationService.class);
        mcpOperationService.initMcpHub(FromType.CHROME.getValue());
        mcpOperationService.listenToTabSave(FromType.CHROME.getValue());

        sessionIdShopper.put(session.getId(), chromeAgent);
    }
    
    /**
     * 中断当前会话对应的ReAct循环
     * @param session WebSocket会话
     */
    private void breakCurrentReactLoop(WebSocketSession session) {
        if (session == null) {
            log.warn("Session is null, cannot break ReAct loop");
            return;
        }
        
        Thread reactThread = sessionReactThreads.get(session.getId());
        if (reactThread != null && reactThread.isAlive()) {
            log.info("Interrupting ReAct loop for session: {}", session.getId());
            reactThread.interrupt();
            sessionReactThreads.remove(session.getId());
        } else {
            log.debug("No active ReAct loop found for session: {}", session.getId());
        }
    }
    
    /**
     * 中断当前会话对应的ReAct循环 (兼容性方法)
     */
    @Deprecated
    private void breakCurrentReactLoop() {
        log.warn("breakCurrentReactLoop() called without session parameter - this method is deprecated");
        // 为了向后兼容，可以中断所有活跃的ReAct循环
        sessionReactThreads.values().forEach(thread -> {
            if (thread.isAlive()) {
                log.info("Interrupting ReAct loop for thread: {}", thread.getName());
                thread.interrupt();
            }
        });
        sessionReactThreads.clear();
    }
}