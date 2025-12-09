/*
 * Copyright 2024-2024 the original author or authors.
 */

package run.mone.hive.mcp.server.transport.streamable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.Setter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.mone.hive.common.Safe;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.server.McpAsyncServer;
import run.mone.hive.mcp.spec.McpError;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.hive.mcp.util.Assert;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Server-side implementation of the Model Context Protocol (MCP) streamable transport
 * layer using HTTP with Server-Sent Events (SSE) through HttpServlet. This implementation
 * provides a bridge between synchronous HttpServlet operations and reactive programming
 * patterns to maintain compatibility with the reactive transport interface.
 *
 * <p>
 * This is adapted from the original HttpServletStreamableServerTransportProvider
 * to work with the hive MCP framework, providing streamable HTTP transport functionality
 * without Spring dependencies but compatible with the hive transport interfaces.
 *
 * @author Zachary German (original)
 * @author Christian Tzolov (original)
 * @author Dariusz Jędrzejczyk (original)
 * @author Adapted for hive MCP framework
 * @see ServerMcpTransport
 * @see HttpServlet
 */
@WebServlet(asyncSupported = true)
@Data
public class HttpServletStreamableServerTransport extends HttpServlet implements ServerMcpTransport {

    private static final Logger logger = LoggerFactory.getLogger(HttpServletStreamableServerTransport.class);

    private McpAsyncServer mcpServer;

    /**
     * Event type for JSON-RPC messages sent through the SSE connection.
     */
    public static final String MESSAGE_EVENT_TYPE = "message";

    /**
     * Event type for sending the message endpoint URI to clients.
     */
    public static final String ENDPOINT_EVENT_TYPE = "endpoint";

    /**
     * Header name for the response media types accepted by the requester.
     */
    private static final String ACCEPT = "Accept";

    public static final String UTF_8 = "UTF-8";

    public static final String APPLICATION_JSON = "application/json";

    public static final String TEXT_EVENT_STREAM = "text/event-stream";

    public static final String FAILED_TO_SEND_ERROR_RESPONSE = "Failed to send error response: {}";

    /**
     * The endpoint URI where clients should send their JSON-RPC messages. Defaults to
     * "/mcp".
     */
    private final String mcpEndpoint;

    /**
     * Flag indicating whether DELETE requests are disallowed on the endpoint.
     */
    private final boolean disallowDelete;

    private final ObjectMapper objectMapper;

    /**
     * Map of active client sessions, keyed by mcp-session-id.
     */
    private final ConcurrentHashMap<String, McpSession> sessions = new ConcurrentHashMap<>();

    /**
     * Flag indicating if the transport is shutting down.
     */
    private volatile boolean isClosing = false;

    /**
     * Keep-alive scheduler for managing session pings. Activated if keepAliveInterval is
     * set. Disabled by default.
     */
    private ScheduledExecutorService keepAliveScheduler;

    /**
     * The function to process incoming JSON-RPC messages and produce responses.
     */
    private Function<Mono<McpSchema.JSONRPCMessage>, Mono<McpSchema.JSONRPCMessage>> connectHandler;

    private Function<McpSchema.JSONRPCRequest, Flux<McpSchema.JSONRPCResponse>> streamHandler;

    @Setter
    private Function<String, Boolean> authFunction = token -> true;

    /**
     * Token validator for bearer token authentication
     */
    @Setter
    private TokenValidator tokenValidator;

    /**
     * Token authentication cache
     */
    private TokenAuthCache tokenAuthCache;

    /**
     * 是否使用 progress 通知类型发送定时消息，默认为 true
     * true: 使用 notifications/progress
     * false: 使用 notifications/message
     */
    @Setter
    private boolean useProgressNotification = false;

    /**
     * 是否启用 session 超时清理，默认为 false（不清理）
     * true: 根据 keepAliveInterval 清理超时的 session
     * false: 不清理超时的 session
     */
    @Setter
    private boolean enableSessionTimeout = false;

    /**
     * 定时广播消息格式类型
     * 0: 使用 JSONRPCResponse (tools/call 返回结果格式，默认)
     * 1: 使用 notifications/progress
     * 2: 使用 notifications/message
     */
    @Setter
    private int broadcastMessageType = 2;

    /**
     * Progress 通知的方法名
     */
    private static final String METHOD_NOTIFICATION_PROGRESS = "notifications/progress";

    /**
     * 消息队列，用于存储待发送给特定客户端的消息
     */
    private final ConcurrentLinkedQueue<QueuedMessage> messageQueue = new ConcurrentLinkedQueue<>();

    /**
     * 队列消息数据类，包含 sessionId 和消息内容
     */
    @lombok.AllArgsConstructor
    @lombok.Getter
    public static class QueuedMessage {
        private final String sessionId;
        private final String content;
        private final int messageType; // 0: JSONRPCResponse, 1: progress, 2: message
    }

    /**
     * Constructs a new HttpServletStreamableServerTransport instance.
     * @param objectMapper The ObjectMapper to use for JSON serialization/deserialization of
     * messages.
     * @param mcpEndpoint The endpoint URI where clients should send their JSON-RPC
     * messages via HTTP. This endpoint will handle GET, POST, and DELETE requests.
     * @param disallowDelete Whether to disallow DELETE requests on the endpoint.
     * @throws IllegalArgumentException if any parameter is null
     */
    private HttpServletStreamableServerTransport(ObjectMapper objectMapper, String mcpEndpoint,
            boolean disallowDelete, Duration keepAliveInterval, Duration tokenCacheTtl) {
        Assert.notNull(objectMapper, "ObjectMapper must not be null");
        Assert.notNull(mcpEndpoint, "MCP endpoint must not be null");

        this.objectMapper = objectMapper;
        this.mcpEndpoint = mcpEndpoint;
        this.disallowDelete = disallowDelete;

        // Initialize token auth cache with default TTL of 5 minutes
        this.tokenAuthCache = new TokenAuthCache(tokenCacheTtl != null ? tokenCacheTtl : Duration.ofMinutes(5));

        // 启用会话清理机制，定期清理已关闭的会话
        this.keepAliveScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "mcp-session-cleaner");
            t.setDaemon(true);
            return t;
        });

        // 每30秒清理一次已关闭的会话
        this.keepAliveScheduler.scheduleAtFixedRate(() -> {
            Safe.run(() -> {
                List<String> closedSessions = sessions.entrySet().stream()
                    .filter(entry -> entry.getValue().isClosed())
                    .map(Map.Entry::getKey)
                    .collect(java.util.stream.Collectors.toList());

                if (!closedSessions.isEmpty()) {
                    logger.debug("Cleaning up {} closed sessions", closedSessions.size());
                    closedSessions.forEach(sessions::remove);
                }

                // 如果启用了 session 超时清理，且设置了 keepAliveInterval，检查超时会话
                if (enableSessionTimeout && keepAliveInterval != null) {
                    long now = System.currentTimeMillis();
                    sessions.entrySet().removeIf(entry -> {
                        if (now - entry.getValue().getUpdateTime() > keepAliveInterval.toMillis()) {
                            logger.info("Session timeout, removing: {}", entry.getKey());
                            entry.getValue().close();
                            return true;
                        }
                        return false;
                    });
                }
            });
        }, 30, 30, TimeUnit.SECONDS);

        // 每10秒向所有客户端广播系统时间通知
        this.keepAliveScheduler.scheduleAtFixedRate(() -> {
            Safe.run(() -> {
                if (sessions.isEmpty()) {
                    return;
                }

                // 构建当前系统时间
                java.time.LocalDateTime now = java.time.LocalDateTime.now();
                String timeData = now.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                McpSchema.JSONRPCMessage message;
                String messageTypeName;

                switch (broadcastMessageType) {
                    case 1:
                        // 使用 notifications/progress 类型
                        Map<String, Object> progressParams = new java.util.HashMap<>();
                        progressParams.put("progressToken", "system-time");
                        progressParams.put("progress", System.currentTimeMillis() % 100);  // 0-99 循环进度
                        progressParams.put("total", 100.0);
                        progressParams.put("message", timeData);

                        message = new McpSchema.JSONRPCNotification(
                            McpSchema.JSONRPC_VERSION,
                            METHOD_NOTIFICATION_PROGRESS,
                            progressParams
                        );
                        messageTypeName = "progress";
                        break;

                    case 2:
                        // 使用 notifications/message 类型
                        Map<String, Object> logParams = new java.util.HashMap<>();
                        logParams.put("level", "info");
                        logParams.put("logger", "system");
                        logParams.put("data", timeData);

                        message = new McpSchema.JSONRPCNotification(
                            McpSchema.JSONRPC_VERSION,
                            McpSchema.METHOD_NOTIFICATION_MESSAGE,
                            logParams
                        );
                        messageTypeName = "message";
                        break;

                    default:
                        // 默认 (0): 使用 JSONRPCResponse (tools/call 返回结果格式)
                        McpSchema.CallToolResult toolResult = new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent(timeData)),
                            false
                        );

                        message = new McpSchema.JSONRPCResponse(
                            McpSchema.JSONRPC_VERSION,
//                            "system-time-" + System.currentTimeMillis(),
                                2,
                            toolResult,
                            null
                        );
                        messageTypeName = "response";
                        break;
                }

                logger.info("Broadcasting system time ({}) to {} sessions: {}",
                    messageTypeName, sessions.size(), timeData);
                sendMessage(message).subscribe();
            });
        }, 10, 10, TimeUnit.SECONDS);

        // 每100毫秒处理消息队列中的消息
        this.keepAliveScheduler.scheduleAtFixedRate(() -> {
            Safe.run(() -> {
                QueuedMessage queuedMessage;
                while ((queuedMessage = messageQueue.poll()) != null) {
                    String sessionId = queuedMessage.getSessionId();
                    String content = queuedMessage.getContent();
                    int msgType = queuedMessage.getMessageType();

                    McpSession session = sessions.get(sessionId);
                    if (session == null || session.isClosed()) {
                        logger.warn("Session {} not found or closed, skipping queued message", sessionId);
                        continue;
                    }

                    try {
                        McpSchema.JSONRPCMessage message;
                        switch (msgType) {
                            case 1:
                                // notifications/progress
                                Map<String, Object> progressParams = new java.util.HashMap<>();
                                progressParams.put("progressToken", "queued-message");
                                progressParams.put("progress", 100);
                                progressParams.put("total", 100.0);
                                progressParams.put("message", content);
                                message = new McpSchema.JSONRPCNotification(
                                    McpSchema.JSONRPC_VERSION,
                                    METHOD_NOTIFICATION_PROGRESS,
                                    progressParams
                                );
                                break;
                            case 2:
                                // notifications/message
                                Map<String, Object> logParams = new java.util.HashMap<>();
                                logParams.put("level", "info");
                                logParams.put("logger", "queue");
                                logParams.put("data", content);
                                message = new McpSchema.JSONRPCNotification(
                                    McpSchema.JSONRPC_VERSION,
                                    McpSchema.METHOD_NOTIFICATION_MESSAGE,
                                    logParams
                                );
                                break;
                            default:
                                // JSONRPCResponse (tools/call format)
                                McpSchema.CallToolResult toolResult = new McpSchema.CallToolResult(
                                    List.of(new McpSchema.TextContent(content)),
                                    false
                                );
                                message = new McpSchema.JSONRPCResponse(
                                    McpSchema.JSONRPC_VERSION,
                                    "queued-" + System.currentTimeMillis(),
                                    toolResult,
                                    null
                                );
                                break;
                        }

                        String jsonText = objectMapper.writeValueAsString(message);
                        sendMessageToSession(session, jsonText);
                        logger.debug("Sent queued message to session {}: {}", sessionId, content);
                    } catch (Exception e) {
                        logger.error("Failed to send queued message to session {}: {}", sessionId, e.getMessage());
                    }
                }
            });
        }, 100, 100, TimeUnit.MILLISECONDS);
    }

    @Override
    public Mono<Void> connect(Function<Mono<McpSchema.JSONRPCMessage>, Mono<McpSchema.JSONRPCMessage>> handler) {
        this.connectHandler = handler;
        return Mono.empty();
    }

    public Mono<Void> connectStream(Function<McpSchema.JSONRPCRequest, Flux<McpSchema.JSONRPCResponse>> streamHandler) {
        this.streamHandler = streamHandler;
        return Mono.empty();
    }

    /**
     * Broadcasts a notification to all connected clients through their SSE connections.
     * If any errors occur during sending to a particular client, they are logged but
     * don't prevent sending to other clients.
     * @param message The JSON-RPC message to broadcast
     * @return A Mono that completes when the broadcast attempt is finished
     */
    @Override
    public Mono<Object> sendMessage(McpSchema.JSONRPCMessage message) {
        return Mono.fromRunnable(() -> {
            if (this.sessions.isEmpty()) {
                logger.debug("No active sessions to broadcast message to");
                return;
            }

            logger.debug("Attempting to broadcast message to {} active sessions", this.sessions.size());

            try {
                String jsonText = objectMapper.writeValueAsString(message);

                // Handle targeted messages based on clientId
                String clientId = extractClientId(message);
                if (clientId != null && !clientId.isEmpty()) {
                    McpSession session = sessions.get(clientId);
                    if (session != null && !session.isClosed()) {
                        logger.info("Sending message to specific client: {}", clientId);
                        sendMessageToSession(session, jsonText);
                    } else {
                        logger.warn("Target session {} not found or closed", clientId);
                    }
                    return;
                }

                // Broadcast to all active sessions
                List<McpSession> activeSessions = this.sessions.values().stream()
                    .filter(session -> !session.isClosed())
                    .collect(java.util.stream.Collectors.toList());
                    
                logger.debug("Broadcasting to {} active sessions out of {} total", 
                    activeSessions.size(), this.sessions.size());
                    
                activeSessions.parallelStream().forEach(session -> {
                    sendMessageToSession(session, jsonText);
                });
            } catch (Exception e) {
                logger.error("Failed to serialize or send message: {}", e.getMessage());
            }
        });
    }

    /**
     * Sends a stream message to clients. This implementation delegates to sendMessage
     * for compatibility with the existing transport interface.
     * @param message The JSON-RPC message to send as a stream
     * @return A Flux that emits the result when the message has been sent
     */
    @Override
    public Flux<Object> sendStreamMessage(McpSchema.JSONRPCMessage message) {
        return sendMessage(message).flux();
    }

    private String extractClientId(McpSchema.JSONRPCMessage message) {
        if (message instanceof McpSchema.JSONRPCResponse response) {
            return response.clientId();
        }
        if (message instanceof McpSchema.JSONRPCNotification notification) {
            Map<String, Object> params = notification.params();
            if (params != null && params.containsKey(Const.CLIENT_ID)) {
                return params.get(Const.CLIENT_ID).toString();
            }
        }
        return null;
    }

    /**
     * Extracts bearer token from the Authorization header or URL parameter.
     * Priority:
     * 1. Authorization header (Bearer token)
     * 2. URL query parameter (?token=xxx)
     * @param request The HTTP servlet request
     * @return The bearer token, or null if not found
     */
    private String extractBearerToken(HttpServletRequest request) {
        // First, try to get from Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7).trim();
        }

        // If not found in header, try to get from URL query parameter
        String tokenParam = request.getParameter("token");
        if (tokenParam != null && !tokenParam.trim().isEmpty()) {
            return tokenParam.trim();
        }

        return null;
    }

    /**
     * Injects user information into tool call parameters.
     * This adds user info to the arguments of CallToolRequest.
     * @param params The original request params
     * @param userInfo User information to inject
     * @return Enriched params with user info injected into arguments
     */
    private Object injectUserInfoToToolCall(Object params, Map<String, Object> userInfo) {
        if (params == null || userInfo == null || userInfo.isEmpty()) {
            return params;
        }

        try {
            // Deserialize params to CallToolRequest
            McpSchema.CallToolRequest callToolRequest = objectMapper.convertValue(params,
                new TypeReference<McpSchema.CallToolRequest>() {});

            if (callToolRequest != null && callToolRequest.arguments() != null) {
                // Create new arguments map with user info
                Map<String, Object> enrichedArguments = new java.util.HashMap<>(callToolRequest.arguments());

                // Inject user info with special prefix to avoid conflicts
                enrichedArguments.put(Const.USER_INFO, userInfo);

                // Also inject individual user fields for convenience
                if (userInfo.containsKey("userId")) {
                    enrichedArguments.putIfAbsent(Const.TOKEN_USER_ID, userInfo.get("userId"));
                }
                if (userInfo.containsKey("username")) {
                    enrichedArguments.putIfAbsent(Const.TOKEN_USERNAME, userInfo.get("username"));
                }

                logger.debug("Injected user info into tool call arguments: userId={}, username={}",
                    userInfo.get("userId"), userInfo.get("username"));

                // Create new CallToolRequest with enriched arguments
                return new McpSchema.CallToolRequest(
                    callToolRequest.name(),
                    enrichedArguments
                );
            }
        } catch (Exception e) {
            logger.warn("Failed to inject user info into tool call parameters: {}", e.getMessage());
        }

        return params;
    }

    /**
     * Validates a bearer token using the configured token validator and cache.
     * @param token The bearer token to validate
     * @return true if the token is valid, false otherwise
     * @deprecated Use {@link #validateBearerTokenWithUserInfo(String)} instead to get user info
     */
    @Deprecated
    private boolean validateBearerToken(String token) {
        TokenValidator.ValidationResult result = validateBearerTokenWithUserInfo(token);
        return result != null && result.isValid();
    }

    /**
     * Validates a bearer token and returns the full validation result including user info.
     * @param token The bearer token to validate
     * @return ValidationResult containing validity status and user info, or null if token is invalid
     */
    private TokenValidator.ValidationResult validateBearerTokenWithUserInfo(String token) {
        if (token == null || token.isEmpty()) {
            logger.debug("Token is null or empty");
            return null;
        }

        // Check cache first
        TokenValidator.ValidationResult cachedResult = tokenAuthCache.get(token);
        if (cachedResult != null) {
            logger.debug("Token validation result from cache: valid={}, userInfo={}",
                cachedResult.isValid(), cachedResult.getUserInfo());
            return cachedResult;
        }

        // Token not in cache, validate using the token validator
        if (tokenValidator == null) {
            logger.warn("Token validator is not configured");
            return null;
        }

        try {
            TokenValidator.ValidationResult result = tokenValidator.validate(token);

            // Cache the result with custom TTL if provided
            if (result.getTtl() != null) {
                tokenAuthCache.put(token, result, result.getTtl());
            } else {
                tokenAuthCache.put(token, result);
            }

            logger.info("Token validation result from validator: valid={}, userInfo={}",
                result.isValid(), result.getUserInfo());
            return result;
        } catch (Exception e) {
            logger.error("Error validating token: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Sends a message to a session through its listening stream.
     * This ensures notifications are sent through the proper SSE connection
     * established by the client's GET request.
     *
     * @param session The session to send the message to
     * @param jsonText The JSON message text
     */
    private void sendMessageToSession(McpSession session, String jsonText) {
        try {
            // Ensure the session has an active listening stream before sending
            // Notifications must be sent through the listeningStreamRef created in GET request
            if (!session.hasActiveListeningStream()) {
                logger.warn("Session {} does not have an active listening stream, skipping message send",
                        session.getId());
                return;
            }

            logger.debug("Sending message to session: {}, message:{}", session.getId(), jsonText);

            // Send through the listening stream reference
            session.getListeningStreamRef().sendNotification(jsonText);

        } catch (IOException e) {
            // 连接已断开，清理会话
            logger.warn("Client connection lost for session {}: {}", session.getId(), e.getMessage());
            sessions.remove(session.getId());
            session.close();
        } catch (Exception e) {
            logger.error("Failed to send message to session {}: {}", session.getId(), e.getMessage());
            sessions.remove(session.getId());
            session.close();
        }
    }

    /**
     * Handles GET requests to establish SSE connections and message replay.
     * @param request The HTTP servlet request
     * @param response The HTTP servlet response
     * @throws ServletException If a servlet-specific error occurs
     * @throws IOException If an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        logger.debug("Request URI with GET method, uri: {}, params: {}", requestURI, request.getQueryString());
        if (!requestURI.endsWith(mcpEndpoint)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (this.isClosing) {
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Server is shutting down");
            return;
        }

        List<String> badRequestErrors = new ArrayList<>();

        String accept = request.getHeader(ACCEPT);
        if (accept == null || !accept.contains(TEXT_EVENT_STREAM)) {
            badRequestErrors.add("text/event-stream required in Accept header");
        }

        // Bearer token authentication
        if (tokenValidator != null) {
            String bearerToken = extractBearerToken(request);
            if (!validateBearerToken(bearerToken)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing bearer token");
                return;
            }
        }

        // String sessionId = request.getHeader(Const.MC_CLIENT_ID);
        String sessionId = request.getHeader(HttpHeaders.MCP_SESSION_ID);
        if (sessionId == null || sessionId.isBlank()) {
            sessionId = java.util.UUID.randomUUID().toString();
        }

        // Legacy auth function support
        if (!authFunction.apply(sessionId)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
            return;
        }

        if (!badRequestErrors.isEmpty()) {
            String combinedMessage = String.join("; ", badRequestErrors);
            this.responseError(response, HttpServletResponse.SC_BAD_REQUEST, new McpError(combinedMessage));
            return;
        }

        logger.debug("Handling GET request for session: {}", sessionId);

        try {
            response.setContentType(TEXT_EVENT_STREAM);
            response.setCharacterEncoding(UTF_8);
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Connection", "keep-alive");
            response.setHeader("Access-Control-Allow-Origin", "*");

            AsyncContext asyncContext = request.startAsync();
            // 设置合理的超时时间，避免连接无限期挂起
            asyncContext.setTimeout(0);

            McpSession session = new McpSession(sessionId, asyncContext, response.getWriter());
            this.sessions.put(sessionId, session);

            // Send initial endpoint event
            session.sendEvent(ENDPOINT_EVENT_TYPE, mcpEndpoint, sessionId);

            // Establish new listening stream - notification 必须通过这个 listeningStreamRef 发送
            // 这个引用是在客户端发起 GET 请求时创建的长期 SSE 连接机制
            McpSession.ListeningStreamRef listeningStreamRef = session.createListeningStream();

            final String finalSessionId = sessionId;
            asyncContext.addListener(new jakarta.servlet.AsyncListener() {
                @Override
                public void onComplete(jakarta.servlet.AsyncEvent event) throws IOException {
                    logger.debug("SSE connection completed for session: {}", finalSessionId);
                    listeningStreamRef.close();
                    sessions.remove(finalSessionId);
                }

                @Override
                public void onTimeout(jakarta.servlet.AsyncEvent event) throws IOException {
                    logger.debug("SSE connection timed out for session: {}", finalSessionId);
                    listeningStreamRef.close();
                    sessions.remove(finalSessionId);
                }

                @Override
                public void onError(jakarta.servlet.AsyncEvent event) throws IOException {
                    Throwable throwable = event.getThrowable();
                    if (throwable instanceof java.io.EOFException) {
                        logger.debug("Client disconnected (EOFException) for session: {}", finalSessionId);
                    } else {
                        logger.warn("SSE connection error for session: {}, error: {}",
                                finalSessionId, throwable != null ? throwable.getMessage() : "Unknown error");
                    }
                    listeningStreamRef.close();
                    sessions.remove(finalSessionId);
                }

                @Override
                public void onStartAsync(jakarta.servlet.AsyncEvent event) throws IOException {
                    // No action needed
                }
            });
        } catch (Exception e) {
            logger.error("Failed to handle GET request for session {}: {}", sessionId, e.getMessage());
            // 清理可能已创建的会话
            sessions.remove(sessionId);
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (IOException ioEx) {
                logger.error("Failed to send error response: {}", ioEx.getMessage());
            }
        }
    }

    /**
     * Handles POST requests for incoming JSON-RPC messages from clients.
     * @param request The HTTP servlet request containing the JSON-RPC message
     * @param response The HTTP servlet response
     * @throws ServletException If a servlet-specific error occurs
     * @throws IOException If an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        logger.debug("Request URI with POST method, uri: {}", requestURI);
        if (!requestURI.endsWith(mcpEndpoint)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (this.isClosing) {
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Server is shutting down");
            return;
        }

        // Bearer token authentication with user info extraction
        TokenValidator.ValidationResult validationResult = null;
        if (tokenValidator != null) {
            String bearerToken = extractBearerToken(request);
            validationResult = validateBearerTokenWithUserInfo(bearerToken);

            if (validationResult == null || !validationResult.isValid()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing bearer token");
                return;
            }
        }

        // Legacy auth
        String clientId = request.getHeader(Const.MC_CLIENT_ID);
        if (clientId != null && !authFunction.apply(clientId)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
            return;
        }

        // Extract user info for later injection
        final Map<String, Object> userInfo = (validationResult != null) ? validationResult.getUserInfo() : new java.util.HashMap<>();

        try {
            // 设置请求字符编码为UTF-8，避免中文乱码
            request.setCharacterEncoding(UTF_8);

            // 检查请求体是否为空
            if (request.getContentLength() == 0) {
                logger.warn("Received empty POST request");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Empty request body");
                return;
            }

            StringBuilder body = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
            
            // 检查请求体是否为空
            if (body.isEmpty()) {
                logger.warn("Received POST request with empty body");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Empty request body");
                return;
            }

            McpSchema.JSONRPCMessage message = McpSchema.deserializeJsonRpcMessage(objectMapper, body.toString());
            logger.debug("Request URI with POST method, uri: {}, body: {}", requestURI, body.toString());

            // Handle ping requests
            if (message instanceof McpSchema.JSONRPCRequest req && req.method().equals(McpSchema.METHOD_PING)) {
                // 兼容新旧两种 session header
                String sessionId = request.getHeader(HttpHeaders.MCP_SESSION_ID);
                if (sessionId == null || sessionId.isBlank()) {
                    sessionId = clientId;
                }
                handlePing(response, req, sessionId);
                return;
            }

            // Handle resources/list request - return empty list quickly
            if (message instanceof McpSchema.JSONRPCRequest req && req.method().equals(McpSchema.METHOD_RESOURCES_LIST)) {
                handleResourcesList(response, req);
                return;
            }

            // Handle resources/templates/list request - return empty list quickly
            if (message instanceof McpSchema.JSONRPCRequest req && req.method().equals(McpSchema.METHOD_RESOURCES_TEMPLATES_LIST)) {
                handleResourcesTemplateList(response, req);
                return;
            }

            // Handle initialization request
            if (message instanceof McpSchema.JSONRPCRequest jsonrpcRequest
                    && jsonrpcRequest.method().equals(McpSchema.METHOD_INITIALIZE)) {

                if (handleMethodInitialize(request, response, jsonrpcRequest, body, message)) return;
            }

            if (message instanceof McpSchema.JSONRPCRequest req && req.method().equals(McpSchema.METHOD_TOOLS_CALL)) {
                handleToolsCall(response, req, userInfo);
                return;
            }


            //获取工具列表
            if (message instanceof McpSchema.JSONRPCRequest req && req.method().equals(McpSchema.METHOD_TOOLS_LIST)) {
                handleToolsList(response, req);
                return;
            }


            // Handle tools stream requests
            if (message instanceof McpSchema.JSONRPCRequest req
                    && req.method().equals(McpSchema.METHOD_TOOLS_STREAM)) {
                logger.debug("Handling tools stream request: {}", req);
                
                if (streamHandler != null) {
                    response.setContentType(TEXT_EVENT_STREAM);
                    response.setCharacterEncoding(UTF_8);
                    response.setHeader("Cache-Control", "no-cache");
                    response.setHeader("Connection", "keep-alive");
                    response.setHeader("Access-Control-Allow-Origin", "*");

                    AsyncContext asyncContext = request.startAsync();
                    // 设置合理的超时时间用于流式响应
                    asyncContext.setTimeout(600000); // 10分钟超时，适合长时间流式响应

                    McpSession sessionTransport = new McpSession(clientId != null ? clientId : "stream", 
                            asyncContext, response.getWriter());

                    streamHandler.apply(req)
                            .subscribe(
                                    streamResponse -> {
                                        try {
                                            sessionTransport.sendMessage(objectMapper.writeValueAsString(streamResponse));
                                        } catch (Exception e) {
                                            logger.error("Failed to send stream response: {}", e.getMessage());
                                            asyncContext.complete();
                                        }
                                    },
                                    error -> {
                                        logger.error("Error handling stream request: {}", error.getMessage());
                                        asyncContext.complete();
                                    },
                                    () -> {
                                        logger.debug("Stream completed");
                                        asyncContext.complete();
                                    }
                            );
                } else {
                    logger.warn("No stream handler registered for tools stream request");
                    this.responseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                            new McpError("No stream handler registered"));
                }
                return;
            }

            // Handle other requests (responses will be sent through SSE connection)
            if (connectHandler != null) {
                // 设置响应头，即使响应体为空
                response.setContentType(APPLICATION_JSON);
                response.setCharacterEncoding(UTF_8);
                response.setStatus(HttpServletResponse.SC_ACCEPTED);  // 202: 请求已接受，响应将通过 SSE 发送

                // 异步处理请求，响应会通过 SSE 连接发送
                // 兼容新旧两种 session header
                String requestSessionId = request.getHeader(HttpHeaders.MCP_SESSION_ID);
                if (requestSessionId == null || requestSessionId.isBlank()) {
                    requestSessionId = request.getHeader(Const.MC_CLIENT_ID);
                }
                if (requestSessionId == null) {
                    requestSessionId = clientId;
                }
                String finalRequestSessionId = requestSessionId;

                connectHandler.apply(Mono.just(message))
                    .subscribe(
                        result -> logger.debug("Request handled successfully, response sent via SSE for session: {}", finalRequestSessionId),
                        error -> logger.error("Error handling request for session: {}", finalRequestSessionId, error)
                    );
            } else {
                this.responseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        new McpError("No message handler registered"));
            }

        } catch (java.io.EOFException e) {
            logger.debug("Client disconnected while reading request body: {}", e.getMessage());
            // 不需要发送响应，客户端已断开
        } catch (IllegalArgumentException | IOException e) {
            logger.error("Failed to deserialize message: {}", e.getMessage());
            try {
                this.responseError(response, HttpServletResponse.SC_BAD_REQUEST,
                        new McpError("Invalid message format: " + e.getMessage()));
            } catch (IOException ioEx) {
                logger.debug("Failed to send error response, client may have disconnected: {}", ioEx.getMessage());
            }
        } catch (Exception e) {
            logger.error("Error handling message:", e);
            try {
                this.responseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        new McpError("Error processing message: " + e.getMessage()));
            } catch (IOException ex) {
                logger.debug("Failed to send error response, client may have disconnected: {}", ex.getMessage());
            }
        }
    }

    private void handleToolsList(HttpServletResponse response, McpSchema.JSONRPCRequest req) throws IOException {
        logger.info("Handling tools list request: {}", req);
        if (mcpServer != null && mcpServer.toolsListRequestHandler() != null) {
            try {
                McpSchema.ListToolsResult result = (McpSchema.ListToolsResult) mcpServer.toolsListRequestHandler().handle(null).block();

                McpSchema.JSONRPCResponse toolsListResponse = new McpSchema.JSONRPCResponse(
                    McpSchema.JSONRPC_VERSION,
                    req.id(),
                    result,
                    null
                );
                responseJsonRpc(response, toolsListResponse);
            } catch (Exception e) {
                logger.error("Error handling tools list request: {}", e.getMessage(), e);
                McpSchema.JSONRPCResponse errorResponse = new McpSchema.JSONRPCResponse(
                    McpSchema.JSONRPC_VERSION,
                    req.id(),
                    null,
                    new McpSchema.JSONRPCResponse.JSONRPCError(
                        McpSchema.ErrorCodes.INTERNAL_ERROR,
                        "Failed to get tools list: " + e.getMessage(),
                        null
                    )
                );
                responseJsonRpc(response, errorResponse);
            }
        } else {
            logger.warn("MCP server or tools list handler is not available");
            this.responseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    new McpError("Tools list handler not available"));
        }
    }

    private void handleToolsCall(HttpServletResponse response, McpSchema.JSONRPCRequest req, Map<String, Object> userInfo) throws IOException {
        logger.info("call tool: {}", req);
        if (mcpServer != null && mcpServer.toolsCallRequestHandler() != null) {
            try {
                // Inject user info into tool call arguments
                Object enrichedParams = injectUserInfoToToolCall(req.params(), userInfo);

                Object handlerResult = mcpServer.toolsCallRequestHandler().handle(enrichedParams).block();
                McpSchema.CallToolResult result;

                // Handle both direct result and Flux wrapped result
                if (handlerResult instanceof Flux) {
                    result = (McpSchema.CallToolResult) ((Flux<?>) handlerResult).blockFirst();
                } else {
                    result = (McpSchema.CallToolResult) handlerResult;
                }

                McpSchema.JSONRPCResponse toolsCallResponse = new McpSchema.JSONRPCResponse(
                        McpSchema.JSONRPC_VERSION,
                        req.id(),
                        result,
                        null
                );
                logger.info("Sending tools/call response: {}", objectMapper.writeValueAsString(toolsCallResponse));
                responseJsonRpc(response, toolsCallResponse);
            } catch (Exception e) {
                logger.error("Error handling tools call request: {}", e.getMessage(), e);
                McpSchema.JSONRPCResponse errorResponse = new McpSchema.JSONRPCResponse(
                    McpSchema.JSONRPC_VERSION,
                    req.id(),
                    null,
                    new McpSchema.JSONRPCResponse.JSONRPCError(
                        McpSchema.ErrorCodes.INTERNAL_ERROR,
                        "Failed to call tool: " + e.getMessage(),
                        null
                    )
                );
                responseJsonRpc(response, errorResponse);
            }
        } else {
            logger.warn("MCP server or tools call handler is not available");
            this.responseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    new McpError("Tools call handler not available"));
        }
    }

    private boolean handleMethodInitialize(HttpServletRequest request, HttpServletResponse response, McpSchema.JSONRPCRequest jsonrpcRequest, StringBuilder body, McpSchema.JSONRPCMessage message) throws IOException {
        // setup session for initialization
        logger.info("handle initialization request:{}", body.toString());
        String sessionId = request.getHeader(HttpHeaders.MCP_SESSION_ID);
        if (sessionId == null || sessionId.isBlank()) {
            sessionId = request.getHeader(Const.MC_CLIENT_ID);
        }
        if (sessionId == null || sessionId.isBlank()) {
            sessionId = java.util.UUID.randomUUID().toString();
        }

        // Process initialization - response should be returned directly in HTTP body
        // 对于 Streamable HTTP，初始化响应必须在 HTTP response body 中返回，
        // 而不是通过 SSE 发送
        if (connectHandler != null) {
            String finalSessionId = sessionId;

            try {
                // 设置响应头
                response.setContentType(APPLICATION_JSON);
                response.setCharacterEncoding(UTF_8);
                response.setHeader(HttpHeaders.MCP_SESSION_ID, finalSessionId);
                response.setStatus(HttpServletResponse.SC_OK);

                // 创建一个临时的 session 来捕获响应
                // 由于 connectHandler 会通过 sendMessage 发送响应，我们需要
                // 捕获这个响应并写入 HTTP body
                PrintWriter writer = response.getWriter();
                java.util.concurrent.CompletableFuture<String> responseFuture = new java.util.concurrent.CompletableFuture<>();

                // 创建临时 session，其 sendMessage 会将响应保存到 future
                McpSession tempSession = new McpSession(finalSessionId, null, writer) {
                    @Override
                    public void sendJsonMessage(String msg) throws IOException {
                        responseFuture.complete(msg);
                    }

                    // Override to always return true for initialization session
                    // This ensures the message is sent even without a GET SSE connection
                    @Override
                    public boolean hasActiveListeningStream() {
                        return true;
                    }

                    @Override
                    public ListeningStreamRef getListeningStreamRef() {
                        // Return a fake listening stream ref that delegates to sendJsonMessage
                        return new ListeningStreamRef(this);
                    }
                };

                // 临时添加到 sessions map，让 sendMessage 能找到它
                sessions.put(finalSessionId, tempSession);

                // 处理初始化请求
                connectHandler.apply(Mono.just(message)).block();

                // 等待响应（最多 5 秒）
                String jsonResponse = responseFuture.get(5, TimeUnit.SECONDS);

                // 写入响应体
                writer.write(jsonResponse);
                writer.flush();

                // 移除临时 session
                sessions.remove(finalSessionId);

                logger.info("handled initialization for session: {}", finalSessionId);
            } catch (java.util.concurrent.TimeoutException e) {
                sessions.remove(finalSessionId);
                logger.error("Initialization timeout for session: {}", finalSessionId, e);
                response.sendError(HttpServletResponse.SC_REQUEST_TIMEOUT,
                    "Initialization timeout");
            } catch (Exception e) {
                sessions.remove(finalSessionId);
                logger.error("Error handling initialization for session: {}", finalSessionId, e);
                try {
                    response.setContentType(APPLICATION_JSON);
                    response.setCharacterEncoding(UTF_8);
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                    McpSchema.JSONRPCResponse errorResponse = new McpSchema.JSONRPCResponse(
                        McpSchema.JSONRPC_VERSION,
                        jsonrpcRequest.id(),
                        null,
                        new McpSchema.JSONRPCResponse.JSONRPCError(
                            McpSchema.ErrorCodes.INTERNAL_ERROR,
                            e.getMessage(),
                            null
                        )
                    );
                    String errorJson = objectMapper.writeValueAsString(errorResponse);
                    response.getWriter().write(errorJson);
                    response.getWriter().flush();
                } catch (IOException ioEx) {
                    logger.error("Error writing error response: {}", ioEx.getMessage());
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Initialization failed: " + e.getMessage());
                }
            }

            return true;
        }
        return false;
    }

    private void handleResourcesTemplateList(HttpServletResponse response, McpSchema.JSONRPCRequest req) throws IOException {
        logger.debug("Handling resources/templates/list request: {}", req);
        McpSchema.ListResourceTemplatesResult result = new McpSchema.ListResourceTemplatesResult(
            List.of(),  // empty resource templates list
            null        // no next cursor
        );
        McpSchema.JSONRPCResponse resourceTemplatesListResponse = new McpSchema.JSONRPCResponse(
            McpSchema.JSONRPC_VERSION,
            req.id(),
            result,
            null
        );
        responseJsonRpc(response, resourceTemplatesListResponse);
    }

    private void handleResourcesList(HttpServletResponse response, McpSchema.JSONRPCRequest req) throws IOException {
        logger.debug("Handling resources/list request: {}", req);
        McpSchema.ListResourcesResult result = new McpSchema.ListResourcesResult(
            List.of(),  // empty resources list
            null        // no next cursor
        );
        McpSchema.JSONRPCResponse resourcesListResponse = new McpSchema.JSONRPCResponse(
            McpSchema.JSONRPC_VERSION,
            req.id(),
            result,
            null
        );
        responseJsonRpc(response, resourcesListResponse);
    }

    private void handlePing(HttpServletResponse response, McpSchema.JSONRPCRequest req, String clientId) throws IOException {
        if (clientId != null) {
            logger.debug("Ping from client: {}", clientId);
            sessions.computeIfPresent(clientId, (k, v) -> {
                v.setUpdateTime(System.currentTimeMillis());
                return v;
            });
        }

        McpSchema.JSONRPCResponse pingResponse = new McpSchema.JSONRPCResponse(
            McpSchema.JSONRPC_VERSION,
            req.id(),
            Map.of(),
            null
        );
        responseJsonRpc(response, pingResponse);
    }

    /**
     * Handles DELETE requests for session deletion.
     * @param request The HTTP servlet request
     * @param response The HTTP servlet response
     * @throws ServletException If a servlet-specific error occurs
     * @throws IOException If an I/O error occurs
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        if (!requestURI.endsWith(mcpEndpoint)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (this.isClosing) {
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Server is shutting down");
            return;
        }

        if (this.disallowDelete) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        // Bearer token authentication
        if (tokenValidator != null) {
            String bearerToken = extractBearerToken(request);
            if (!validateBearerToken(bearerToken)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing bearer token");
                return;
            }
        }

//        String sessionId = request.getHeader(Const.MC_CLIENT_ID);
        String sessionId = request.getHeader(HttpHeaders.MCP_SESSION_ID);
        if (sessionId == null) {
            this.responseError(response, HttpServletResponse.SC_BAD_REQUEST,
                    new McpError("Session ID required in header"));
            return;
        }

        // Legacy auth function support
        if (!authFunction.apply(sessionId)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
            return;
        }

        McpSession session = this.sessions.get(sessionId);
        if (session == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            session.close();
            this.sessions.remove(sessionId);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            logger.error("Failed to delete session {}: {}", sessionId, e.getMessage());
            try {
                this.responseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        new McpError(e.getMessage()));
            } catch (IOException ex) {
                logger.error(FAILED_TO_SEND_ERROR_RESPONSE, ex.getMessage());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting session");
            }
        }
    }

    /**
     * Sends a JSON-RPC response to the client
     * @param response The HTTP servlet response
     * @param jsonRpcResponse The JSON-RPC response object to send
     * @throws IOException If an I/O error occurs
     */
    private void responseJsonRpc(HttpServletResponse response, McpSchema.JSONRPCResponse jsonRpcResponse) throws IOException {
        try {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(APPLICATION_JSON);
            response.setCharacterEncoding(UTF_8);

            PrintWriter writer = response.getWriter();
            writer.write(objectMapper.writeValueAsString(jsonRpcResponse));
            writer.flush();
        } catch (IOException e) {
            logger.debug("Failed to send JSON-RPC response, client may have disconnected: {}", e.getMessage());
            throw e;
        }
    }

    public void responseError(HttpServletResponse response, int httpCode, McpError mcpError) throws IOException {
        try {
            response.setContentType(APPLICATION_JSON);
            response.setCharacterEncoding(UTF_8);
            response.setStatus(httpCode);
            String jsonError = objectMapper.writeValueAsString(mcpError);
            PrintWriter writer = response.getWriter();
            writer.write(jsonError);
            writer.flush();
        } catch (IOException e) {
            // 客户端可能已断开连接，记录日志但不抛出异常
            logger.debug("Failed to send error response, client may have disconnected: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Sends an SSE event to a client with a specific ID.
     * @param writer The writer to send the event through
     * @param eventType The type of event (message or endpoint)
     * @param data The event data
     * @param id The event ID
     * @throws IOException If an error occurs while writing the event
     */
    private void sendEvent(PrintWriter writer, String eventType, String data, String id) throws IOException {
        if (id != null) {
            writer.write("id: " + id + "\n");
        }
        writer.write("event: " + eventType + "\n");
        writer.write("data: " + data + "\n\n");
        writer.flush();

        if (writer.checkError()) {
            throw new IOException("Client disconnected");
        }
    }

    /**
     * Converts data from one type to another using the configured ObjectMapper.
     * @param data The source data object to convert
     * @param typeRef The target type reference
     * @return The converted object of type T
     * @param <T> The target type
     */
    @Override
    public <T> T unmarshalFrom(Object data, TypeReference<T> typeRef) {
        return this.objectMapper.convertValue(data, typeRef);
    }

    /**
     * Initiates a graceful shutdown of the transport.
     * @return A Mono that completes when all cleanup operations are finished
     */
    /**
     * 清理已关闭的会话
     */
    public void cleanupClosedSessions() {
        List<String> closedSessions = sessions.entrySet().stream()
            .filter(entry -> entry.getValue().isClosed())
            .map(Map.Entry::getKey)
            .collect(java.util.stream.Collectors.toList());
            
        if (!closedSessions.isEmpty()) {
            logger.debug("Removing {} closed sessions", closedSessions.size());
            closedSessions.forEach(sessions::remove);
        }
    }
    
    /**
     * 获取活跃会话数量
     */
    public int getActiveSessionCount() {
        return (int) sessions.values().stream()
            .filter(session -> !session.isClosed())
            .count();
    }

    /**
     * 将消息加入队列，稍后发送给指定的客户端
     * @param sessionId 目标客户端的 session ID
     * @param content 消息内容
     */
    public void enqueueMessage(String sessionId, String content) {
        enqueueMessage(sessionId, content, 0);
    }

    /**
     * 将消息加入队列，稍后发送给指定的客户端
     * @param sessionId 目标客户端的 session ID
     * @param content 消息内容
     * @param messageType 消息类型: 0=JSONRPCResponse, 1=progress, 2=message
     */
    public void enqueueMessage(String sessionId, String content, int messageType) {
        if (sessionId == null || sessionId.isEmpty()) {
            logger.warn("Cannot enqueue message with null or empty sessionId");
            return;
        }
        if (content == null) {
            logger.warn("Cannot enqueue message with null content for session {}", sessionId);
            return;
        }
        messageQueue.offer(new QueuedMessage(sessionId, content, messageType));
        logger.debug("Enqueued message for session {}: {}", sessionId, content);
    }

    /**
     * 获取当前消息队列中待发送的消息数量
     * @return 队列中的消息数量
     */
    public int getQueuedMessageCount() {
        return messageQueue.size();
    }

    @Override
    public Mono<Void> closeGracefully() {
        return Mono.fromRunnable(() -> {
            this.isClosing = true;
            logger.info("Initiating graceful shutdown with {} total sessions ({} active)", 
                this.sessions.size(), getActiveSessionCount());

            this.sessions.values().parallelStream().forEach(session -> {
                try {
                    session.close();
                } catch (Exception e) {
                    logger.error("Failed to close session {}: {}", session.getId(), e.getMessage());
                }
            });

            this.sessions.clear();

            if (this.keepAliveScheduler != null) {
                this.keepAliveScheduler.shutdown();
                try {
                    if (!this.keepAliveScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                        this.keepAliveScheduler.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    this.keepAliveScheduler.shutdownNow();
                }
            }

            // Shutdown token auth cache
            if (this.tokenAuthCache != null) {
                this.tokenAuthCache.shutdown();
            }

            logger.info("Graceful shutdown completed");
        });
    }

    /**
     * Cleans up resources when the servlet is being destroyed.
     */
    @Override
    public void destroy() {
        closeGracefully().block();
        super.destroy();
    }

    /**
     * Implementation of MCP session for HttpServlet SSE sessions. This
     * class handles the transport-level communication for a specific client session.
     *
     * <p>
     * This class is thread-safe and uses a ReentrantLock to synchronize access to the
     * underlying PrintWriter to prevent race conditions when multiple threads attempt to
     * send messages concurrently.
     */
    @Data
    static class McpSession {

        private final String id;
        private final AsyncContext asyncContext;
        private final PrintWriter writer;
        private volatile boolean closed = false;
        private volatile long updateTime = System.currentTimeMillis();
        private final ReentrantLock lock = new ReentrantLock();

        /**
         * Reference to the listening stream created when client establishes GET SSE connection.
         * This ensures notifications are sent through the proper long-lived SSE channel.
         */
        private volatile ListeningStreamRef listeningStreamRef;

        /**
         * 检查会话是否已关闭
         */
        public boolean isClosed() {
            return this.closed;
        }

        /**
         * Creates a listening stream for this session. This stream is used to send
         * server-initiated notifications to the client through the SSE connection
         * established via GET request.
         *
         * @return A reference to the listening stream
         */
        public ListeningStreamRef createListeningStream() {
            lock.lock();
            try {
                if (this.listeningStreamRef != null) {
                    logger.warn("Listening stream already exists for session: {}, closing old stream", this.id);
                    this.listeningStreamRef.close();
                }
                this.listeningStreamRef = new ListeningStreamRef(this);
                logger.debug("Created listening stream for session: {}", this.id);
                return this.listeningStreamRef;
            } finally {
                lock.unlock();
            }
        }

        /**
         * Checks if this session has an active listening stream.
         *
         * @return true if there's an active listening stream, false otherwise
         */
        public boolean hasActiveListeningStream() {
            return this.listeningStreamRef != null && !this.listeningStreamRef.isClosed();
        }

        /**
         * Gets the listening stream reference for this session.
         *
         * @return The listening stream reference, or null if not created
         */
        public ListeningStreamRef getListeningStreamRef() {
            return this.listeningStreamRef;
        }

        /**
         * Creates a new session with the specified ID and SSE writer.
         * @param id The unique identifier for this session
         * @param asyncContext The async context for the session
         * @param writer The writer for sending server events to the client
         */
        McpSession(String id, AsyncContext asyncContext, PrintWriter writer) {
            this.id = id;
            this.asyncContext = asyncContext;
            this.writer = writer;
            logger.debug("Session {} initialized with SSE writer", id);
        }

        /**
         * Sends a message to the client through the SSE connection.
         * @param message The message to send
         */
        public void sendMessage(String message) throws IOException {
            // sendEvent(MESSAGE_EVENT_TYPE, message, this.id);
            sendJsonMessage(message);
        }

        public void sendJsonMessage(String message) throws IOException {
            if (this.closed) {
                logger.debug("Attempted to send message to closed session: {}", this.id);
                throw new IOException("Session is closed");
            }

            lock.lock();
            try {
                if (this.closed) {
                    throw new IOException("Session was closed during message send");
                }

                // SSE 格式: event: message\ndata: <json>\n\n
                writer.write("event: " + MESSAGE_EVENT_TYPE + "\n");
                writer.write("data: " + message + "\n\n");
                writer.flush();

                // 检查连接状态
                if (writer.checkError()) {
                    this.closed = true;
                    throw new IOException("Client disconnected");
                }
            } finally {
                lock.unlock();
            }
        }

        /**
         * Sends an SSE event to the client.
         * @param eventType The type of event
         * @param data The event data
         * @param eventId The event ID
         */
        public void sendEvent(String eventType, String data, String eventId) throws IOException {
            if (this.closed) {
                logger.debug("Attempted to send event to closed session: {}", this.id);
                return;
            }

            lock.lock();
            try {
                if (this.closed) {
                    logger.debug("Session {} was closed during event send attempt", this.id);
                    return;
                }

                if (eventId != null) {
                    writer.write("id: " + eventId + "\n");
                }
                writer.write("event: " + eventType + "\n");
                writer.write("data: " + data + "\n\n");
                writer.flush();

                if (writer.checkError()) {
                    throw new IOException("Client disconnected");
                }

                logger.debug("Event sent to session {} with ID {}", this.id, eventId);
            } finally {
                lock.unlock();
            }
        }

        /**
         * Closes this session by completing the SSE connection.
         */
        public void close() {
            lock.lock();
            try {
                if (this.closed) {
                    logger.debug("Session {} already closed", this.id);
                    return;
                }

                this.closed = true;

                // Close listening stream if exists
                if (this.listeningStreamRef != null) {
                    this.listeningStreamRef.close();
                }

                // 安全地关闭 AsyncContext
                if (this.asyncContext != null) {
                    try {
                        this.asyncContext.complete();
                        logger.debug("Successfully completed async context for session {}", this.id);
                    } catch (IllegalStateException e) {
                        // AsyncContext 可能已经被关闭
                        logger.debug("AsyncContext already completed for session {}: {}", this.id, e.getMessage());
                    }
                }
            } catch (Exception e) {
                logger.warn("Failed to complete async context for session {}: {}", this.id, e.getMessage());
            } finally {
                lock.unlock();
            }
        }

        /**
         * Reference to a listening stream that handles server-initiated notifications
         * through the SSE connection established by GET requests.
         *
         * <p>This class ensures that notifications are sent through the proper
         * long-lived SSE connection channel created when the client issues a GET request.
         */
        static class ListeningStreamRef {
            private final McpSession session;
            private volatile boolean closed = false;

            ListeningStreamRef(McpSession session) {
                this.session = session;
                logger.debug("ListeningStreamRef created for session: {}", session.getId());
            }

            /**
             * Sends a notification through this listening stream.
             *
             * @param message The message to send
             * @throws IOException if an error occurs while sending
             */
            public void sendNotification(String message) throws IOException {
                if (closed) {
                    logger.warn("Attempted to send notification through closed listening stream for session: {}",
                            session.getId());
                    throw new IOException("Listening stream is closed");
                }
                logger.info("notification:{}", message);
                session.sendMessage(message);
            }

            /**
             * Checks if this listening stream is closed.
             *
             * @return true if closed, false otherwise
             */
            public boolean isClosed() {
                return closed;
            }

            /**
             * Closes this listening stream.
             */
            public void close() {
                if (!closed) {
                    closed = true;
                    logger.debug("ListeningStreamRef closed for session: {}", session.getId());
                }
            }
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for creating instances of {@link HttpServletStreamableServerTransport}.
     */
    public static class Builder {

        private ObjectMapper objectMapper;
        private String mcpEndpoint = "/mcp";
        private boolean disallowDelete = false;
        private Duration keepAliveInterval;
        private TokenValidator tokenValidator;
        private Duration tokenCacheTtl;

        /**
         * Sets the ObjectMapper to use for JSON serialization/deserialization of MCP
         * messages.
         * @param objectMapper The ObjectMapper instance. Must not be null.
         * @return this builder instance
         * @throws IllegalArgumentException if ObjectMapper is null
         */
        public Builder objectMapper(ObjectMapper objectMapper) {
            Assert.notNull(objectMapper, "ObjectMapper must not be null");
            this.objectMapper = objectMapper;
            return this;
        }

        /**
         * Sets the endpoint URI where clients should send their JSON-RPC messages.
         * @param mcpEndpoint The MCP endpoint URI. Must not be null.
         * @return this builder instance
         * @throws IllegalArgumentException if mcpEndpoint is null
         */
        public Builder mcpEndpoint(String mcpEndpoint) {
            Assert.notNull(mcpEndpoint, "MCP endpoint must not be null");
            this.mcpEndpoint = mcpEndpoint;
            return this;
        }

        /**
         * Sets whether to disallow DELETE requests on the endpoint.
         * @param disallowDelete true to disallow DELETE requests, false otherwise
         * @return this builder instance
         */
        public Builder disallowDelete(boolean disallowDelete) {
            this.disallowDelete = disallowDelete;
            return this;
        }

        /**
         * Sets the keep-alive interval for the transport. If set, a keep-alive scheduler
         * will be activated to periodically ping active sessions.
         * @param keepAliveInterval The interval for keep-alive pings. If null, no
         * keep-alive will be scheduled.
         * @return this builder instance
         */
        public Builder keepAliveInterval(Duration keepAliveInterval) {
            this.keepAliveInterval = keepAliveInterval;
            return this;
        }

        /**
         * Sets the token validator for bearer token authentication.
         * @param tokenValidator The token validator instance. If null, bearer token auth is disabled.
         * @return this builder instance
         */
        public Builder tokenValidator(TokenValidator tokenValidator) {
            this.tokenValidator = tokenValidator;
            return this;
        }

        /**
         * Sets the TTL for token authentication cache entries.
         * @param tokenCacheTtl The cache TTL. If null, defaults to 5 minutes.
         * @return this builder instance
         */
        public Builder tokenCacheTtl(Duration tokenCacheTtl) {
            this.tokenCacheTtl = tokenCacheTtl;
            return this;
        }

        /**
         * Builds a new instance of {@link HttpServletStreamableServerTransport}
         * with the configured settings.
         * @return A new HttpServletStreamableServerTransport instance
         * @throws IllegalStateException if required parameters are not set
         */
        public HttpServletStreamableServerTransport build() {
            Assert.notNull(this.mcpEndpoint, "MCP endpoint must be set");
            HttpServletStreamableServerTransport transport = new HttpServletStreamableServerTransport(
                    objectMapper != null ? objectMapper : new ObjectMapper(),
                    mcpEndpoint, disallowDelete, keepAliveInterval, tokenCacheTtl);

            // Set token validator if provided
            if (tokenValidator != null) {
                transport.setTokenValidator(tokenValidator);
            }

            return transport;
        }
    }
}
