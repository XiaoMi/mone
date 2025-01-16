package run.mone.m78.server.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import run.mone.m78.gateway.KnowledgeGatewayService;
import run.mone.m78.gateway.bo.RequestBO;
import run.mone.m78.service.common.GsonUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class KnowledgeHandler extends TextWebSocketHandler {

    private ExecutorService pool = Executors.newFixedThreadPool(200);

    private int maxConnectionSize;

    private KnowledgeGatewayService knowledgeGatewayService;
    public KnowledgeHandler(int maxConnectionSize, KnowledgeGatewayService knowledgeGatewayService) {
        this.maxConnectionSize = maxConnectionSize;
        this.knowledgeGatewayService = knowledgeGatewayService;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        pool.submit(() -> {
            try {
                RequestBO requestBO = GsonUtils.gson.fromJson(message.getPayload(), RequestBO.class);
                knowledgeGatewayService.queryKnowledge(requestBO, requestBO.getType(), session.getId());
            } catch (Exception e) {
                log.error("knowledge ws handleTextMessage error : ", e);
            }
        });
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        if (session.getHandshakeHeaders() != null && session.getHandshakeHeaders().get("serverIp") != null && session.getHandshakeHeaders().get("serverIp").size() > 0) {
            log.info("knowledge ws connection established from : {}", session.getHandshakeHeaders().get("serverIp").get(0));
        } else {
            log.warn("knowledge ws connection established from unknown ip");
        }
        // max session
        if (KnowledgeSessionHolder.INSTANCE.getSessionSize() >= maxConnectionSize) {
            log.error("knowledge ws connection max session is exceed the threshold value : " + maxConnectionSize);
            session.close();
            return;
        }
        KnowledgeSessionHolder.INSTANCE.setSession(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("knowledge ws transport error : ", exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        KnowledgeSessionHolder.INSTANCE.clearSession(session.getId(), "");
    }
}
