package run.mone.agentx.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import run.mone.agentx.service.McpService;
import run.mone.agentx.dto.McpRequest;
import run.mone.hive.common.GsonUtils;
import run.mone.hive.common.Result;
import java.util.Map;
import java.net.URI;
import java.util.HashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    private final McpService mcpService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            // 获取对话ID，从URL参数中获取
            String clientId = extractClientId(session.getUri());
            if (clientId == null) {
                log.error("No client ID provided");
                session.close(CloseStatus.POLICY_VIOLATION);
                return;
            }

            // 存储对话ID
            session.getAttributes().put("clientId", clientId);
            
            // 使用对话ID存储session
            WebSocketHolder.addSession(clientId, session);
            
            log.info("WebSocket connection established for clientId: {}", clientId);
        } catch (Exception e) {
            log.error("Error during WebSocket connection establishment", e);
            session.close(CloseStatus.SERVER_ERROR);
            throw e;
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("Received message, payload: {}", payload);
        
        try {
            // 解析MCP请求
            McpRequest request = GsonUtils.gson.fromJson(payload, McpRequest.class);
            
            // 构建MCP调用参数
            Map<String, String> keyValuePairs = new HashMap<>();
            keyValuePairs.put("outerTag", request.getOuterTag());
            if (request.getContent() != null) {
                keyValuePairs.put("server_name", request.getContent().getServer_name());
                keyValuePairs.put("tool_name", request.getContent().getTool_name());
                keyValuePairs.put("arguments", request.getContent().getArguments());
            }
            
            // 创建Result对象
            Result result = new Result("mcp_request", keyValuePairs);
            
            // 创建消息适配器并直接调用MCP服务
            McpMessageSink sink = new McpMessageSink(session);
            mcpService.callMcp(request.getAgentId(), request.getAgentInstance(), result, sink);
            sink.complete();
            
        } catch (Exception e) {
            log.error("Error processing MCP request", e);
            session.sendMessage(new TextMessage("{\"error\": \"" + e.getMessage() + "\"}"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String clientId = (String) session.getAttributes().get("clientId");
        if (clientId != null) {
            WebSocketHolder.removeSession(clientId);
            log.info("WebSocket connection closed for clientId: {}, status: {}",
                    clientId, status);
        }
    }

    private String extractClientId(URI uri) {
        String query = uri.getQuery();
        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("clientId=")) {
                    return param.substring("clientId=".length());
                }
            }
        }
        return null;
    }
}