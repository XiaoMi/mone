/*
 * Copyright 2024-2024 the original author or authors.
 */

package run.mone.hive.mcp.server.transport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
public class HttpServletStreamableServerTransport extends HttpServlet implements ServerMcpTransport {

    private static final Logger logger = LoggerFactory.getLogger(HttpServletStreamableServerTransport.class);

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
     * Constructs a new HttpServletStreamableServerTransport instance.
     * @param objectMapper The ObjectMapper to use for JSON serialization/deserialization of
     * messages.
     * @param mcpEndpoint The endpoint URI where clients should send their JSON-RPC
     * messages via HTTP. This endpoint will handle GET, POST, and DELETE requests.
     * @param disallowDelete Whether to disallow DELETE requests on the endpoint.
     * @throws IllegalArgumentException if any parameter is null
     */
    private HttpServletStreamableServerTransport(ObjectMapper objectMapper, String mcpEndpoint,
            boolean disallowDelete, Duration keepAliveInterval) {
        Assert.notNull(objectMapper, "ObjectMapper must not be null");
        Assert.notNull(mcpEndpoint, "MCP endpoint must not be null");

        this.objectMapper = objectMapper;
        this.mcpEndpoint = mcpEndpoint;
        this.disallowDelete = disallowDelete;

        if (keepAliveInterval != null) {
            this.keepAliveScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "mcp-keepalive");
                t.setDaemon(true);
                return t;
            });

            this.keepAliveScheduler.scheduleAtFixedRate(() -> {
                long now = System.currentTimeMillis();
                Safe.run(() -> sessions.entrySet().forEach(entry -> {
                    if (now - entry.getValue().getUpdateTime() > keepAliveInterval.toMillis()) {
                        logger.info("Session timeout, removing: {}", entry.getKey());
                        entry.getValue().close();
                        sessions.remove(entry.getKey());
                    }
                }));
            }, keepAliveInterval.toSeconds(), keepAliveInterval.toSeconds(), TimeUnit.SECONDS);
        }
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
                    if (session != null) {
                        logger.info("Sending message to specific client: {}", clientId);
                        sendMessageToSession(session, jsonText);
                    }
                    return;
                }

                // Broadcast to all sessions
                this.sessions.values().parallelStream().forEach(session -> {
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

    private void sendMessageToSession(McpSession session, String jsonText) {
        try {
            logger.debug("Sending message to session: {}", session.getId());
            session.sendMessage(jsonText);
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

        String sessionId = request.getHeader(Const.MC_CLIENT_ID);
        if (sessionId == null || sessionId.isBlank()) {
            sessionId = java.util.UUID.randomUUID().toString();
        }

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
            asyncContext.setTimeout(0);

            McpSession session = new McpSession(sessionId, asyncContext, response.getWriter());
            this.sessions.put(sessionId, session);

            // Send initial endpoint event
            session.sendEvent(ENDPOINT_EVENT_TYPE, mcpEndpoint, sessionId);

            final String finalSessionId = sessionId;
            asyncContext.addListener(new jakarta.servlet.AsyncListener() {
                @Override
                public void onComplete(jakarta.servlet.AsyncEvent event) throws IOException {
                    logger.debug("SSE connection completed for session: {}", finalSessionId);
                    sessions.remove(finalSessionId);
                }

                @Override
                public void onTimeout(jakarta.servlet.AsyncEvent event) throws IOException {
                    logger.debug("SSE connection timed out for session: {}", finalSessionId);
                    sessions.remove(finalSessionId);
                }

                @Override
                public void onError(jakarta.servlet.AsyncEvent event) throws IOException {
                    logger.debug("SSE connection error for session: {}", finalSessionId);
                    sessions.remove(finalSessionId);
                }

                @Override
                public void onStartAsync(jakarta.servlet.AsyncEvent event) throws IOException {
                    // No action needed
                }
            });
        } catch (Exception e) {
            logger.error("Failed to handle GET request for session {}: {}", sessionId, e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
        if (!requestURI.endsWith(mcpEndpoint)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (this.isClosing) {
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Server is shutting down");
            return;
        }

        String clientId = request.getHeader(Const.MC_CLIENT_ID);
        if (clientId != null && !authFunction.apply(clientId)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
            return;
        }

        try {
            BufferedReader reader = request.getReader();
            StringBuilder body = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }

            McpSchema.JSONRPCMessage message = McpSchema.deserializeJsonRpcMessage(objectMapper, body.toString());

            // Handle ping requests
            if (message instanceof McpSchema.JSONRPCRequest req && req.method().equals("ping")) {
                if (clientId != null) {
                    logger.debug("Ping from client: {}", clientId);
                    sessions.computeIfPresent(clientId, (k, v) -> {
                        v.setUpdateTime(System.currentTimeMillis());
                        return v;
                    });
                }
                response.setStatus(HttpServletResponse.SC_OK);
                return;
            }

            // Handle initialization request
            if (message instanceof McpSchema.JSONRPCRequest jsonrpcRequest
                    && jsonrpcRequest.method().equals(McpSchema.METHOD_INITIALIZE)) {
                
                // Process initialization through connect handler
                if (connectHandler != null) {
                    McpSchema.JSONRPCMessage result = connectHandler.apply(Mono.just(message)).block();
                    
                    response.setContentType(APPLICATION_JSON);
                    response.setCharacterEncoding(UTF_8);
                    if (clientId != null) {
                        response.setHeader(Const.MC_CLIENT_ID, clientId);
                    }
                    response.setStatus(HttpServletResponse.SC_OK);

                    String jsonResponse = objectMapper.writeValueAsString(result);
                    PrintWriter writer = response.getWriter();
                    writer.write(jsonResponse);
                    writer.flush();
                    return;
                }
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
                    asyncContext.setTimeout(0);

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

            // Handle other requests through connect handler
            if (connectHandler != null) {
                connectHandler.apply(Mono.just(message)).block();
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                this.responseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        new McpError("No message handler registered"));
            }

        } catch (IllegalArgumentException | IOException e) {
            logger.error("Failed to deserialize message: {}", e.getMessage());
            this.responseError(response, HttpServletResponse.SC_BAD_REQUEST,
                    new McpError("Invalid message format: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error handling message: {}", e.getMessage());
            try {
                this.responseError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        new McpError("Error processing message: " + e.getMessage()));
            } catch (IOException ex) {
                logger.error(FAILED_TO_SEND_ERROR_RESPONSE, ex.getMessage());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing message");
            }
        }
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

        String sessionId = request.getHeader(Const.MC_CLIENT_ID);
        if (sessionId == null) {
            this.responseError(response, HttpServletResponse.SC_BAD_REQUEST,
                    new McpError("Session ID required in header"));
            return;
        }

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

    public void responseError(HttpServletResponse response, int httpCode, McpError mcpError) throws IOException {
        response.setContentType(APPLICATION_JSON);
        response.setCharacterEncoding(UTF_8);
        response.setStatus(httpCode);
        String jsonError = objectMapper.writeValueAsString(mcpError);
        PrintWriter writer = response.getWriter();
        writer.write(jsonError);
        writer.flush();
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
    @Override
    public Mono<Void> closeGracefully() {
        return Mono.fromRunnable(() -> {
            this.isClosing = true;
            logger.debug("Initiating graceful shutdown with {} active sessions", this.sessions.size());

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

            logger.debug("Graceful shutdown completed");
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
    private static class McpSession {

        private final String id;
        private final AsyncContext asyncContext;
        private final PrintWriter writer;
        private volatile boolean closed = false;
        private volatile long updateTime = System.currentTimeMillis();
        private final ReentrantLock lock = new ReentrantLock();

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
            sendEvent(MESSAGE_EVENT_TYPE, message, this.id);
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
                this.asyncContext.complete();
                logger.debug("Successfully completed async context for session {}", this.id);
            } catch (Exception e) {
                logger.warn("Failed to complete async context for session {}: {}", this.id, e.getMessage());
            } finally {
                lock.unlock();
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
         * Builds a new instance of {@link HttpServletStreamableServerTransport}
         * with the configured settings.
         * @return A new HttpServletStreamableServerTransport instance
         * @throws IllegalStateException if required parameters are not set
         */
        public HttpServletStreamableServerTransport build() {
            Assert.notNull(this.mcpEndpoint, "MCP endpoint must be set");
            return new HttpServletStreamableServerTransport(
                    objectMapper != null ? objectMapper : new ObjectMapper(),
                    mcpEndpoint, disallowDelete, keepAliveInterval);
        }
    }
}
