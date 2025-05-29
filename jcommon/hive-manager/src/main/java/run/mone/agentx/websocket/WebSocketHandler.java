package run.mone.agentx.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import run.mone.agentx.entity.Agent;
import run.mone.agentx.entity.User;
import run.mone.agentx.service.AgentAccessService;
import run.mone.agentx.service.AgentService;
import run.mone.agentx.service.McpService;
import run.mone.agentx.dto.McpRequest;
import run.mone.hive.common.GsonUtils;
import run.mone.hive.common.ToolDataInfo;

import java.net.URI;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    private final McpService mcpService;
    private final AgentAccessService agentAccessService;
    private final AgentService agentService;

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

            //设置userId和userName
            setUserAttributesFromSession(session);

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

    private static void setUserAttributesFromSession(WebSocketSession session) {
        String userName = "";
        String userId = "";
        if (session instanceof StandardWebSocketSession sws) {
            if (sws.getPrincipal() instanceof UsernamePasswordAuthenticationToken token && token.getPrincipal() instanceof User user) {
                userName = user.getUsername();
                userId = String.valueOf(user.getId());
            }
        }
        session.getAttributes().put("userId", userId);
        session.getAttributes().put("userName", userName);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("Received message, payload: {}", payload);

        try {
            // 解析MCP请求
            McpRequest request = GsonUtils.gson.fromJson(payload, McpRequest.class);
            // 创建Result对象
            ToolDataInfo toolData = new ToolDataInfo("mcp_request", request.getMapData());
            toolData.setFrom("hive_manager");
            String userId = session.getAttributes().getOrDefault("userId", "").toString();
            toolData.setUserId(userId);
            toolData.setAgentId(String.valueOf(request.getAgentId()));

            Agent agent = agentService.findById(request.getAgentId()).block();
            if (!agent.getIsPublic()) {
                if (!agentAccessService.validateAccess(request.getAgentId(), userId).block()) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "用户没有权限访问该Agent");
                }
            }

            // 创建消息适配器并直接调用MCP服务
            McpMessageSink sink = new McpMessageSink(session);
            String userName = session.getAttributes().getOrDefault("userName", "").toString();
            mcpService.callMcp(userName, request.getAgentId(), request.getAgentInstance(), payload, toolData, sink);
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