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
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.mcp.service.RoleService;
import run.mone.hive.schema.Message;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Flux;

import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.PreDestroy;
import java.net.URI;
import java.util.List;
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

    /**
     * 图片分析使用的 LLM Provider，默认使用 DOUBAO_VISION
     */
    @Value("${mcp.websocket.img.provider:DOUBAO_VISION}")
    private String imgLlmProvider;

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
                case "client_response":
                    handleClientResponse(messageMap);
                    break;
                case "device_info":
                    handleDeviceInfo(clientId, messageMap);
                    break;
                case "heartbeat":
                    // 忽略心跳消息，不需要处理
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
     * 处理设备信息消息
     * Android 客户端在连接时发送设备信息，包括屏幕尺寸
     *
     * 消息格式:
     * <pre>
     * {
     *     "type": "device_info",
     *     "clientId": "设备ID",
     *     "screenWidth": 1080,
     *     "screenHeight": 2400,
     *     "device": "设备型号",
     *     "brand": "品牌",
     *     ...
     * }
     * </pre>
     */
    private void handleDeviceInfo(String clientId, Map<String, Object> messageMap) {
        log.info("Received device info from client {}: {}", clientId, messageMap);

        // 提取屏幕尺寸
        Object screenWidthObj = messageMap.get("screenWidth");
        Object screenHeightObj = messageMap.get("screenHeight");

        if (screenWidthObj != null && screenHeightObj != null) {
            int screenWidth = ((Number) screenWidthObj).intValue();
            int screenHeight = ((Number) screenHeightObj).intValue();

            // 更新屏幕尺寸缓存
            ScreenSizeCache.getInstance().updateScreenSize(clientId, screenWidth, screenHeight);

            log.info("Cached screen size for client {}: {}x{}", clientId, screenWidth, screenHeight);
        } else {
            log.warn("Device info missing screenWidth or screenHeight for client {}", clientId);
        }

        // 可以在这里存储其他设备信息（如设备型号、品牌等）供后续使用
    }

    /**
     * 处理普通消息
     */
    @SuppressWarnings("unchecked")
    private void handleMessage(String clientId, Map<String, Object> messageMap) {
        // 这里可以调用 roleService 处理业务逻辑
        Object data = messageMap.get("data");
        log.info("Processing message from client {}: {}", clientId, data);

        // 检查是否有 cmd 字段
        if (data instanceof Map) {
            Map<String, Object> dataMap = (Map<String, Object>) data;
            String cmd = (String) dataMap.get("cmd");

            if ("img".equals(cmd)) {
                // 处理图片分析请求
                handleImageAnalysis(clientId, dataMap);
                return;
            }
        }

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
     * 处理图片分析请求
     * 当 cmd=img 时，调用大模型分析图片
     *
     * 客户端发送格式:
     * <pre>
     * {
     *     "type": "message",
     *     "data": {
     *         "cmd": "img",
     *         "image": "base64编码的图片数据",
     *         "prompt": "用户提示词",
     *         "systemPrompt": "系统提示词(可选)"
     *     }
     * }
     * </pre>
     */
    private void handleImageAnalysis(String clientId, Map<String, Object> dataMap) {
        String imageBase64 = (String) dataMap.get("image");
        String userPrompt = (String) dataMap.get("prompt");
        String systemPrompt = dataMap.containsKey("systemPrompt") ?
                (String) dataMap.get("systemPrompt") : "你是一个智能助手，请分析图片内容并回答用户的问题。";

        // 参数校验
        if (imageBase64 == null || imageBase64.isEmpty()) {
            Map<String, Object> errorResponse = Map.of(
                    "type", "img_analysis_error",
                    "error", "图片数据不能为空",
                    "timestamp", System.currentTimeMillis()
            );
            sendMessage(clientId, errorResponse);
            return;
        }

        if (userPrompt == null || userPrompt.isEmpty()) {
            userPrompt = "请描述这张图片的内容";
        }

        log.info("Processing image analysis for client {}, prompt: {}", clientId, userPrompt);

        try {
            // 确定 LLMProvider
            LLMProvider llmProvider;
            try {
                llmProvider = LLMProvider.valueOf(imgLlmProvider);
            } catch (IllegalArgumentException e) {
                llmProvider = LLMProvider.DOUBAO_VISION;
                log.warn("Invalid imgLlmProvider: {}, using default: DOUBAO_VISION", imgLlmProvider);
            }

            // 创建 LLM 实例
            LLM llm = new LLM(LLMConfig.builder()
                    .llmProvider(llmProvider)
                    .temperature(0.7)
                    .thinking(true)
                    .build());

            // 构建包含图片的消息
            LLM.LLMCompoundMsg compoundMsg = LLM.getLlmCompoundMsg(userPrompt,
                    Message.builder()
                            .images(Lists.newArrayList(imageBase64))
                            .build());

            // 根据图片数据判断图片类型
            String imageType = detectImageType(imageBase64);
            compoundMsg.setImageType(imageType);

            log.info("使用模型: {} 分析图片, 图片类型: {}", llmProvider, imageType);

            // 调用大模型进行图片分析
            String finalUserPrompt = userPrompt;
            Flux<String> flux = llm.compoundMsgCall(compoundMsg, Prompt.prompt);
            flux.collect(Collectors.joining())
                    .subscribe(
                            result -> {
                                // 成功回调
                                log.info("Image analysis completed for client {}", clientId);
                                Map<String, Object> successResponse = new java.util.HashMap<>();
                                successResponse.put("type", "img_analysis_response");
                                successResponse.put("result", result);
                                successResponse.put("prompt", finalUserPrompt);
                                successResponse.put("timestamp", System.currentTimeMillis());

                                // 解析点击坐标信息并归一化到设备分辨率 (1440x3200)
                                final int screenWidth = 1440;
                                final int screenHeight = 3200;
                                List<int[]> points = parseClickPoints(result);
                                if (!points.isEmpty()) {
                                    successResponse.put("points", points.stream()
                                            .map(p -> {
                                                // 将相对坐标(0-1000)转换为设备屏幕绝对坐标
                                                int absoluteX = (int) (p[0] / 1000.0 * screenWidth);
                                                int absoluteY = (int) (p[1] / 1000.0 * screenHeight);
                                                return Map.of("x", absoluteX, "y", absoluteY);
                                            })
                                            .toList());
                                    successResponse.put("result",successResponse.get("result")+"\n"+successResponse.get("points"));
                                    log.info("Parsed {} click points from result (normalized to {}x{})",
                                            points.size(), screenWidth, screenHeight);
                                }

                                sendMessage(clientId, successResponse);
                            },
                            error -> {
                                // 错误回调
                                log.error("Image analysis failed for client {}: {}", clientId, error.getMessage(), error);
                                Map<String, Object> errorResponse = Map.of(
                                        "type", "img_analysis_error",
                                        "error", error.getMessage() != null ? error.getMessage() : "图片分析失败",
                                        "timestamp", System.currentTimeMillis()
                                );
                                sendMessage(clientId, errorResponse);
                            }
                    );

        } catch (Exception e) {
            log.error("Failed to process image analysis for client {}: {}", clientId, e.getMessage(), e);
            Map<String, Object> errorResponse = Map.of(
                    "type", "img_analysis_error",
                    "error", e.getMessage() != null ? e.getMessage() : "图片分析处理异常",
                    "timestamp", System.currentTimeMillis()
            );
            sendMessage(clientId, errorResponse);
        }
    }

    /**
     * 解析大模型返回结果中的点击坐标
     * 匹配格式: click(point='<point>x y</point>')
     *
     * @param result 大模型返回的结果
     * @return 坐标列表，每个元素是 [x, y] 数组
     */
    private List<int[]> parseClickPoints(String result) {
        List<int[]> points = new java.util.ArrayList<>();
        if (result == null || result.isEmpty()) {
            return points;
        }

        // 匹配 click(point='<point>x y</point>') 格式
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                "click\\s*\\(\\s*point\\s*=\\s*['\"]<point>\\s*(\\d+)\\s+(\\d+)\\s*</point>['\"]\\s*\\)");
        java.util.regex.Matcher matcher = pattern.matcher(result);

        while (matcher.find()) {
            try {
                int x = Integer.parseInt(matcher.group(1));
                int y = Integer.parseInt(matcher.group(2));
                points.add(new int[]{x, y});
                log.debug("Parsed click point: ({}, {})", x, y);
            } catch (NumberFormatException e) {
                log.warn("Failed to parse click point coordinates: {}", e.getMessage());
            }
        }

        return points;
    }

    /**
     * 检测图片类型
     * 根据 base64 数据的前缀判断图片类型
     */
    private String detectImageType(String base64Data) {
        if (base64Data == null || base64Data.isEmpty()) {
            return "jpeg";
        }

        // 检查是否包含 data URI scheme
        if (base64Data.startsWith("data:")) {
            if (base64Data.startsWith("data:image/png")) {
                return "png";
            } else if (base64Data.startsWith("data:image/gif")) {
                return "gif";
            } else if (base64Data.startsWith("data:image/webp")) {
                return "webp";
            }
            return "png";
        }

        // 通过 base64 数据的头部字节判断
        try {
            byte[] bytes = java.util.Base64.getDecoder().decode(
                    base64Data.length() > 20 ? base64Data.substring(0, 20) : base64Data);
            if (bytes.length >= 8) {
                // PNG: 89 50 4E 47 0D 0A 1A 0A
                if (bytes[0] == (byte) 0x89 && bytes[1] == 0x50 && bytes[2] == 0x4E && bytes[3] == 0x47) {
                    return "png";
                }
                // GIF: 47 49 46 38
                if (bytes[0] == 0x47 && bytes[1] == 0x49 && bytes[2] == 0x46 && bytes[3] == 0x38) {
                    return "gif";
                }
                // JPEG: FF D8 FF
                if (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xD8 && bytes[2] == (byte) 0xFF) {
                    return "jpeg";
                }
            }
        } catch (Exception e) {
            log.debug("Failed to detect image type from base64 data: {}", e.getMessage());
        }

        return "jpeg";
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
                    //每次清空记录
                    .clearHistory(true)
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
     * 发送消息给指定的客户端（阻塞式，等待客户端响应）
     * <p>
     * 客户端发送格式:
     * <pre>
     * {
     *     "type": "send_to_client",
     *     "data": {
     *         "targetClientId": "目标客户端ID",
     *         "message": "要发送的消息内容",
     *         "timeout": 30  // 可选，超时时间（秒），默认30秒
     *     }
     * }
     * </pre>
     * <p>
     * 发送给目标客户端的消息格式:
     * <pre>
     * {
     *     "type": "message_from_client",
     *     "reqId": "请求ID，客户端需要在响应中返回相同的resId",
     *     "fromClientId": "发送者ID",
     *     "message": { ... },
     *     "timestamp": 1234567890
     * }
     * </pre>
     * <p>
     * 客户端响应格式:
     * <pre>
     * {
     *     "type": "client_response",
     *     "resId": "与reqId相同",
     *     "data": { ... }
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
        // 获取超时时间，默认30秒
        Integer timeout = data.get("timeout") != null ? ((Number) data.get("timeout")).intValue() : 30;

        if (targetClientId == null || targetClientId.isEmpty()) {
            sendError(clientId, "Missing targetClientId");
            return;
        }

        if (messageContent == null) {
            sendError(clientId, "Missing message content");
            return;
        }

        log.info("Sending blocking message from {} to {}: {}", clientId, targetClientId, messageContent);

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

        // 生成唯一请求ID
        String reqId = java.util.UUID.randomUUID().toString().replace("-", "");

        // 创建 CompletableFuture 用于等待响应
        java.util.concurrent.CompletableFuture<Map<String, Object>> future = new java.util.concurrent.CompletableFuture<>();
        WebSocketCaller.getInstance().registerPendingRequest(reqId, future);

        try {
            // 解析消息内容并直接把 reqId 放入
            Map<String, Object> targetMessage;
            if (messageContent instanceof String) {
                // 字符串类型：解析成 Map，直接放入 reqId 后发送
                targetMessage = new java.util.HashMap<>(objectMapper.readValue((String) messageContent, Map.class));
                targetMessage.put("reqId", reqId);
            } else if (messageContent instanceof Map) {
                // Map 类型：复制一份，放入 reqId 后发送
                targetMessage = new java.util.HashMap<>((Map<String, Object>) messageContent);
                targetMessage.put("reqId", reqId);
            } else {
                sendError(clientId, "Invalid message format");
                return;
            }

            // 发送消息给目标客户端（直接发送带 reqId 的消息）
            sendMessage(targetClientId, targetMessage);

            log.info("Waiting for response from client {}, reqId: {}, timeout: {}s", targetClientId, reqId, timeout);

            // 阻塞等待响应
            Map<String, Object> response = future.get(timeout, java.util.concurrent.TimeUnit.SECONDS);

            log.info("Received response for reqId: {}, response: {}", reqId, response);

            // 返回成功响应给调用者
            Map<String, Object> confirmResponse = new java.util.HashMap<>();
            confirmResponse.put("type", "send_to_client_success");
            confirmResponse.put("targetClientId", targetClientId);
            confirmResponse.put("reqId", reqId);
            confirmResponse.put("response", response);
            confirmResponse.put("message", "Message processed successfully");
            confirmResponse.put("timestamp", System.currentTimeMillis());
            sendMessage(clientId, confirmResponse);

        } catch (java.util.concurrent.TimeoutException e) {
            log.error("Timeout waiting for response from client {}, reqId: {}", targetClientId, reqId);
            Map<String, Object> errorResponse = Map.of(
                    "type", "send_to_client_error",
                    "targetClientId", targetClientId,
                    "reqId", reqId,
                    "error", "Timeout waiting for client response",
                    "timestamp", System.currentTimeMillis()
            );
            sendMessage(clientId, errorResponse);
        } catch (Exception e) {
            log.error("Failed to send message to client {}: {}", targetClientId, e.getMessage(), e);
            Map<String, Object> errorResponse = Map.of(
                    "type", "send_to_client_error",
                    "targetClientId", targetClientId,
                    "reqId", reqId,
                    "error", e.getMessage() != null ? e.getMessage() : "Unknown error",
                    "timestamp", System.currentTimeMillis()
            );
            sendMessage(clientId, errorResponse);
        } finally {
            // 清理等待的请求
            WebSocketCaller.getInstance().removePendingRequest(reqId);
        }
    }

    /**
     * 处理客户端响应消息
     * 当目标客户端处理完消息后，发送此响应解除调用者阻塞
     * <p>
     * 客户端响应格式:
     * <pre>
     * {
     *     "type": "client_response",
     *     "resId": "与reqId相同",
     *     "data": { ... }
     * }
     * </pre>
     */
    @SuppressWarnings("unchecked")
    private void handleClientResponse(Map<String, Object> messageMap) {
        String resId = (String) messageMap.get("resId");
        if (resId == null) {
            log.warn("Client response missing resId");
            return;
        }

        Map<String, Object> data = (Map<String, Object>) messageMap.get("data");
        log.info("Received client response for resId: {}", resId);

        WebSocketCaller.getInstance().handleResponse(resId, data != null ? data : Map.of());
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
