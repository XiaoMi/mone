package run.mone.hive.spring.starter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.schema.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.PreDestroy;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * WebSocket 处理器
 * 支持双向实时通信
 * <p>
 * 配置项: mcp.websocket.enabled=true 启用
 *
 * @author goodjava@qq.com
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "mcp.websocket.enabled", havingValue = "true")
public class WebSocketHandler extends TextWebSocketHandler {

    private final RoleService roleService;
    private final WebSocketProperties webSocketProperties;

    // 使用单例管理会话
    private final WebSocketSessionManager sessionManager = WebSocketSessionManager.getInstance();

    // JSON序列化工具
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 心跳检测线程池
    private final ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();

    // 用于在 session attributes 中存储 clientId 的 key
    private static final String CLIENT_ID_ATTRIBUTE = "clientId";

    // 业务方注入的任务处理函数
    private Function<Map<String, Object>, String> taskHandler;

    /**
     * 设置任务处理函数（Spring 自动注入）
     * 业务方只需定义一个名为 "wsTaskHandler" 的 Bean 即可自动注入
     * <p>
     * 示例:
     * <pre>
     * {@code
     * @Bean("wsTaskHandler")
     * public Function<String, String> wsTaskHandler() {
     *     return task -> {
     *         // 业务处理逻辑
     *         return "处理结果";
     *     };
     * }
     * }
     * </pre>
     *
     * @param taskHandler 任务处理函数，接收任务描述字符串，返回处理结果
     */
    @Autowired(required = false)
    @Qualifier("wsTaskHandler")
    public void setTaskHandler(Function<Map<String, Object>, String> taskHandler) {
        this.taskHandler = taskHandler;
        log.info("WebSocketHandler taskHandler injected: {}", taskHandler != null);
    }

    /**
     * 连接建立成功时调用
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        log.info("WebSocket connection established, sessionId: {}", sessionId);


        // 获取对话ID，从URL参数中获取
        String clientId = extractClientId(session.getUri());
        if (clientId == null) {
            log.error("No client ID provided");
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        // 将 clientId 存储在 session attributes 中，方便后续获取
        session.getAttributes().put(CLIENT_ID_ATTRIBUTE, clientId);

        // 放宽会话级文本消息大小限制（需与容器缓冲区匹配）
        Integer limit = webSocketProperties.getMaxTextMessageSize();
        if (limit != null && limit > 0) {
            try {
                session.setTextMessageSizeLimit(limit);
            } catch (Throwable ignore) {
                // 某些容器实现可能不支持设置该值，忽略即可
            }
        }

        sessionManager.addSession(clientId, session);

        // 发送欢迎消息
        Map<String, Object> welcomeMessage = Map.of(
                "type", "connected",
                "message", "WebSocket connection established successfully",
                "clientId", clientId,
                "timestamp", System.currentTimeMillis()
        );

        sendMessage(clientId, welcomeMessage);

        // 启动心跳检测
        if (sessionManager.getActiveConnectionCount() == 1) {
            startHeartbeat();
        }
    }

    /**
     * 接收到客户端消息时调用
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String clientId = (String) session.getAttributes().get(CLIENT_ID_ATTRIBUTE);
        String payload = message.getPayload();
        log.info("Received message from client {}: {}", clientId, payload);

        try {
            // 解析消息
            Map<String, Object> messageMap = objectMapper.readValue(payload, Map.class);
            String type = (String) messageMap.get("type");

            if (null == type) {
                return;
            }

            // 根据消息类型处理
            switch (type) {
                case "ping":
                    handlePing(clientId);
                    break;
                case "message":
                    handleMessage(clientId, messageMap);
                    break;
                case "agent":
                    handleAgentMessage(clientId, messageMap);
                    break;
                case "broadcast":
                    handleBroadcast(clientId, messageMap);
                    break;
                case "call_response":
                    handleCallResponse(messageMap);
                    break;
                case "call_error":
                    handleCallError(messageMap);
                    break;
                case "task":
                    handleTask(clientId, messageMap);
                    break;
                case "get_clients":
                    handleGetClients(clientId);
                    break;
                case "send_to_client":
                    handleSendToClient(clientId, messageMap);
                    break;
                default:
                    log.warn("Unknown message type: {}", type);
                    sendError(clientId, "Unknown message type: " + type);
            }
        } catch (Exception e) {
            log.error("Error handling message from client {}", clientId, e);
            sendError(clientId, "Error processing message: " + e.getMessage());
        }
    }

    /**
     * 连接关闭时调用
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String clientId = (String) session.getAttributes().get(CLIENT_ID_ATTRIBUTE);
        log.info("WebSocket connection closed for client: {}, status: {}", clientId, status);
        sessionManager.removeSession(clientId);
    }

    /**
     * 传输错误时调用
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String clientId = (String) session.getAttributes().get(CLIENT_ID_ATTRIBUTE);
        log.error("WebSocket transport error for client {}", clientId, exception);
        sessionManager.removeSession(clientId);

        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    /**
     * 处理ping消息
     */
    private void handlePing(String clientId) {
        Map<String, Object> response = Map.of(
                "type", "pong",
                "timestamp", System.currentTimeMillis()
        );
        sendMessage(clientId, response);
    }

    /**
     * 处理普通消息
     */
    private void handleMessage(String clientId, Map<String, Object> messageMap) {
        // 这里可以调用 roleService 处理业务逻辑
        Object data = messageMap.get("data");
        log.info("Processing message from client {}: {}", clientId, data);

        // 发送响应
        Map<String, Object> response = Map.of(
                "type", "message_response",
                "status", "success",
                "originalMessage", data,
                "timestamp", System.currentTimeMillis()
        );
        sendMessage(clientId, response);
    }

    /**
     * 处理广播请求
     */
    private void handleBroadcast(String clientId, Map<String, Object> messageMap) {
        Object data = messageMap.get("data");
        log.info("Broadcasting message from client {}: {}", clientId, data);

        Map<String, Object> broadcastMessage = Map.of(
                "type", "broadcast",
                "from", clientId,
                "data", data,
                "timestamp", System.currentTimeMillis()
        );

        broadcast(broadcastMessage);
    }

    /**
     * 处理 Agent 消息请求
     */
    private void handleAgentMessage(String clientId, Map<String, Object> messageMap) {
        try {
            Map<String, Object> data = (Map<String, Object>) messageMap.get("data");

            // 从 data 中获取参数
            String content = data.containsKey("content") ?
                    (String) data.get("content") : "";

            String userId = data.containsKey("userId") ?
                    (String) data.get("userId") : "";

            String agentId = data.containsKey("agentId") ?
                    (String) data.get("agentId") : "";

            log.info("Processing agent message for client {}: content={}, userId={}, agentId={}",
                    clientId, content, userId, agentId);

            // 构建 Message 对象
            Message message = Message.builder()
                    .content(content)
                    .role("user")
                    .sentFrom("ws_" + clientId)
                    .clientId(clientId)
                    .userId(userId)
                    .agentId(agentId)
                    .createTime(System.currentTimeMillis())
                    .build();

            // 调用 RoleService.receiveMsg 并订阅响应
            roleService.receiveMsg(message)
                    .subscribe(
                            response -> {
                                // Agent 返回的每个消息片段
                                log.debug("Agent response for client {}: {}", clientId, response);
                                Map<String, Object> responseMessage = Map.of(
                                        "type", "agent_response",
                                        "data", response,
                                        "timestamp", System.currentTimeMillis()
                                );
                                sendMessage(clientId, responseMessage);
                            },
                            error -> {
                                // 错误处理
                                log.error("Agent error for client: {}", clientId, error);
                                Map<String, Object> errorMessage = Map.of(
                                        "type", "agent_error",
                                        "error", error.getMessage(),
                                        "timestamp", System.currentTimeMillis()
                                );
                                sendMessage(clientId, errorMessage);
                            },
                            () -> {
                                // 完成处理
                                log.info("Agent processing completed for client: {}", clientId);
                                Map<String, Object> completeMessage = Map.of(
                                        "type", "agent_complete",
                                        "message", "Agent processing completed",
                                        "timestamp", System.currentTimeMillis()
                                );
                                sendMessage(clientId, completeMessage);
                            }
                    );

        } catch (Exception e) {
            log.error("Failed to process agent message for client: {}", clientId, e);
            Map<String, Object> errorMessage = Map.of(
                    "type", "error",
                    "message", "Failed to process agent message: " + e.getMessage(),
                    "timestamp", System.currentTimeMillis()
            );
            sendMessage(clientId, errorMessage);
        }
    }

    /**
     * 处理客户端调用响应
     * 客户端处理完 call 请求后，返回此消息解除调用者阻塞
     */
    @SuppressWarnings("unchecked")
    private void handleCallResponse(Map<String, Object> messageMap) {
        String resId = (String) messageMap.get("resId");
        if (resId == null) {
            log.warn("Call response missing resId");
            return;
        }

        Map<String, Object> data = (Map<String, Object>) messageMap.get("data");
        log.info("Received call response for resId: {}", resId);

        WebSocketCaller.getInstance().handleResponse(resId, data != null ? data : Map.of());
    }

    /**
     * 处理客户端调用错误响应
     */
    private void handleCallError(Map<String, Object> messageMap) {
        String resId = (String) messageMap.get("resId");
        if (resId == null) {
            log.warn("Call error missing resId");
            return;
        }

        String errorMessage = (String) messageMap.get("error");
        log.error("Received call error for resId: {}, error: {}", resId, errorMessage);

        WebSocketCaller.getInstance().handleError(resId, errorMessage != null ? errorMessage : "Unknown error");
    }

    /**
     * 处理任务请求
     * 调用业务方注入的 taskHandler 处理任务
     * <p>
     * 客户端发送格式:
     * <pre>
     * {
     *     "type": "task",
     *     "data": {
     *         "task": "你想让agent干的事情"
     *     }
     * }
     * </pre>
     */
    @SuppressWarnings("unchecked")
    private void handleTask(String clientId, Map<String, Object> messageMap) {
        Map<String, Object> data = (Map<String, Object>) messageMap.get("data");
        String task = data != null ? (String) data.get("task") : null;

        log.info("Processing task for client {}: {}", clientId, task);

        if (taskHandler == null) {
            log.warn("TaskHandler not configured");
            Map<String, Object> errorResponse = Map.of(
                    "type", "task_error",
                    "error", "TaskHandler not configured, please inject a Function<String, String> Bean named 'wsTaskHandler'",
                    "timestamp", System.currentTimeMillis()
            );
            sendMessage(clientId, errorResponse);
            return;
        }

        if (task == null || task.isEmpty()) {
            Map<String, Object> errorResponse = Map.of(
                    "type", "task_error",
                    "error", "Task content is empty",
                    "timestamp", System.currentTimeMillis()
            );
            sendMessage(clientId, errorResponse);
            return;
        }

        try {
            long startTime = System.currentTimeMillis();
            String result = taskHandler.apply(data);
            long duration = System.currentTimeMillis() - startTime;

            log.info("Task completed for client {}, duration: {}ms", clientId, duration);

            Map<String, Object> response = Map.of(
                    "type", "task_response",
                    "task", task,
                    "result", result != null ? result : "",
                    "duration", duration,
                    "timestamp", System.currentTimeMillis()
            );
            sendMessage(clientId, response);

        } catch (Exception e) {
            log.error("Task execution failed for client {}: {}", clientId, task, e);
            Map<String, Object> errorResponse = Map.of(
                    "type", "task_error",
                    "task", task,
                    "error", e.getMessage() != null ? e.getMessage() : "Unknown error",
                    "timestamp", System.currentTimeMillis()
            );
            sendMessage(clientId, errorResponse);
        }
    }

    /**
     * 获取所有已连接的客户端列表
     */
    private void handleGetClients(String clientId) {
        log.info("Getting client list for client: {}", clientId);

        Set<String> clientIds = sessionManager.getActiveClientIds();

        Map<String, Object> response = Map.of(
                "type", "clients_list",
                "clients", clientIds.toArray(new String[0]),
                "count", clientIds.size(),
                "timestamp", System.currentTimeMillis()
        );
        sendMessage(clientId, response);
    }

    /**
     * 发送消息给指定的客户端
     * <p>
     * 客户端发送格式:
     * <pre>
     * {
     *     "type": "send_to_client",
     *     "data": {
     *         "targetClientId": "目标客户端ID",
     *         "message": "要发送的消息内容"
     *     }
     * }
     * </pre>
     */
    @SuppressWarnings("unchecked")
    private void handleSendToClient(String clientId, Map<String, Object> messageMap) {
        Map<String, Object> data = (Map<String, Object>) messageMap.get("data");

        if (data == null) {
            sendError(clientId, "Missing data field");
            return;
        }

        String targetClientId = (String) data.get("targetClientId");
        Object messageContent = data.get("message");

        if (targetClientId == null || targetClientId.isEmpty()) {
            sendError(clientId, "Missing targetClientId");
            return;
        }

        if (messageContent == null) {
            sendError(clientId, "Missing message content");
            return;
        }

        log.info("Sending message from {} to {}: {}", clientId, targetClientId, messageContent);

        // 检查目标客户端是否存在
        if (!sessionManager.isSessionActive(targetClientId)) {
            Map<String, Object> errorResponse = Map.of(
                    "type", "send_to_client_error",
                    "targetClientId", targetClientId,
                    "error", "Target client not found or disconnected",
                    "timestamp", System.currentTimeMillis()
            );
            sendMessage(clientId, errorResponse);
            return;
        }

        // 发送消息给目标客户端（将 JSON 字符串解析为 Map 发送）
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> targetMessage = objectMapper.readValue((String) messageContent, Map.class);
            sendMessage(targetClientId, targetMessage);
        } catch (Exception e) {
            log.error("Failed to parse message content as JSON: {}", messageContent, e);
            sendError(clientId, "Invalid JSON format in message content");
            return;
        }

        // 确认发送成功
        Map<String, Object> confirmResponse = Map.of(
                "type", "send_to_client_success",
                "targetClientId", targetClientId,
                "message", "Message sent successfully",
                "timestamp", System.currentTimeMillis()
        );
        sendMessage(clientId, confirmResponse);
    }

    /**
     * 发送消息到指定客户端
     */
    public void sendMessage(String clientId, Map<String, Object> message) {
        sessionManager.sendMessage(clientId, message);
    }

    /**
     * 广播消息到所有客户端
     */
    public void broadcast(Map<String, Object> message) {
        sessionManager.broadcast(message);
    }

    /**
     * 发送错误消息
     */
    private void sendError(String clientId, String errorMessage) {
        Map<String, Object> error = Map.of(
                "type", "error",
                "message", errorMessage,
                "timestamp", System.currentTimeMillis()
        );
        sendMessage(clientId, error);
    }

    /**
     * 启动心跳检测
     */
    private void startHeartbeat() {
        heartbeatExecutor.scheduleAtFixedRate(() -> {
            log.debug("Sending heartbeat to {} clients", sessionManager.getActiveConnectionCount());

            Map<String, Object> heartbeat = Map.of(
                    "action", "heartbeat",
                    "type", "heartbeat",
                    "timestamp", System.currentTimeMillis()
            );

            for (String clientId : sessionManager.getActiveClientIds()) {
                if (sessionManager.isSessionActive(clientId)) {
                    sendMessage(clientId, heartbeat);
                } else {
                    sessionManager.removeSession(clientId);
                }
            }
        }, 30, 30, TimeUnit.SECONDS);
    }

    /**
     * 获取当前连接数
     */
    public int getActiveConnectionCount() {
        return sessionManager.getActiveConnectionCount();
    }

    /**
     * 获取所有活跃的客户端ID
     */
    public java.util.Set<String> getActiveClientIds() {
        return sessionManager.getActiveClientIds();
    }

    /**
     * 关闭指定客户端的会话
     */
    public void closeSession(String clientId) {
        sessionManager.closeSession(clientId);
    }

    /**
     * 清理资源
     */
    @PreDestroy
    public void cleanup() {
        log.info("Cleaning up WebSocket handler, closing {} sessions", sessionManager.getActiveConnectionCount());

        // 关闭心跳线程池
        heartbeatExecutor.shutdown();
        try {
            if (!heartbeatExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                heartbeatExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            heartbeatExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // 关闭所有会话
        sessionManager.closeAllSessions();
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
