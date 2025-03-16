/*
 * Copyright 2024-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package run.mone.hive.mcp.transport.webmvcsse;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import run.mone.hive.common.Safe;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.spec.McpError;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.hive.mcp.util.Assert;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.web.servlet.function.ServerResponse.SseBuilder;

/**
 * Server-side implementation of the Model Context Protocol (MCP) transport layer using
 * HTTP with Server-Sent Events (SSE) through Spring WebMVC. This implementation provides
 * a bridge between synchronous WebMVC operations and reactive programming patterns to
 * maintain compatibility with the reactive transport interface.
 *
 * <p>
 * Key features:
 * <ul>
 * <li>Implements bidirectional communication using HTTP POST for client-to-server
 * messages and SSE for server-to-client messages</li>
 * <li>Manages client sessions with unique IDs for reliable message delivery</li>
 * <li>Supports graceful shutdown with proper session cleanup</li>
 * <li>Provides JSON-RPC message handling through configured endpoints</li>
 * <li>Includes built-in error handling and logging</li>
 * </ul>
 *
 * <p>
 * The transport operates on two main endpoints:
 * <ul>
 * <li>{@code /sse} - The SSE endpoint where clients establish their event stream
 * connection</li>
 * <li>A configurable message endpoint where clients send their JSON-RPC messages via HTTP
 * POST</li>
 * </ul>
 *
 * <p>
 * This implementation uses {@link ConcurrentHashMap} to safely manage multiple client
 * sessions in a thread-safe manner. Each client session is assigned a unique ID and
 * maintains its own SSE connection.
 *
 * @author Christian Tzolov
 * @see ServerMcpTransport
 * @see RouterFunction
 */
public class WebMvcSseServerTransport implements ServerMcpTransport {

    private final static Logger logger = LoggerFactory.getLogger(WebMvcSseServerTransport.class);

    /**
     * Event type for JSON-RPC messages sent through the SSE connection.
     */
    public final static String MESSAGE_EVENT_TYPE = "message";

    /**
     * Event type for sending the message endpoint URI to clients.
     */
    public final static String ENDPOINT_EVENT_TYPE = "endpoint";

    /**
     * Default SSE endpoint path as specified by the MCP transport specification.
     */
    public final static String SSE_ENDPOINT = "/sse";

    private final ObjectMapper objectMapper;

    private final String messageEndpoint;

    private final RouterFunction<ServerResponse> routerFunction;

    /**
     * Map of active client sessions, keyed by session ID.
     */
    private final ConcurrentHashMap<String, ClientSession> sessions = new ConcurrentHashMap<>();

    /**
     * Flag indicating if the transport is shutting down.
     */
    private volatile boolean isClosing = false;

    /**
     * The function to process incoming JSON-RPC messages and produce responses.
     */
    private Function<Mono<McpSchema.JSONRPCMessage>, Mono<McpSchema.JSONRPCMessage>> connectHandler;

    private Function<McpSchema.JSONRPCRequest, Flux<McpSchema.JSONRPCResponse>> streamHandler;

    /**
     * Constructs a new WebMvcSseServerTransport instance.
     *
     * @param objectMapper    The ObjectMapper to use for JSON serialization/deserialization
     *                        of messages.
     * @param messageEndpoint The endpoint URI where clients should send their JSON-RPC
     *                        messages via HTTP POST. This endpoint will be communicated to clients through the
     *                        SSE connection's initial endpoint event.
     * @throws IllegalArgumentException if either objectMapper or messageEndpoint is null
     */
    public WebMvcSseServerTransport(ObjectMapper objectMapper, String messageEndpoint) {
        Assert.notNull(objectMapper, "ObjectMapper must not be null");
        Assert.notNull(messageEndpoint, "Message endpoint must not be null");

        this.objectMapper = objectMapper;
        this.messageEndpoint = messageEndpoint;
        this.routerFunction = RouterFunctions.route()
                .GET(SSE_ENDPOINT, this::handleSseConnection)
                .POST(messageEndpoint, this::handleMessage)
                .build();

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            Safe.run(() -> sessions.entrySet().forEach(it -> {
                if (now - it.getValue().getUpdateTime() > TimeUnit.SECONDS.toMillis(15)) {
                    logger.info("offline:{}", it.getKey());
                    sessions.remove(it.getKey());
                }
            }));
        }, 15, 15, TimeUnit.SECONDS);
    }

    /**
     * Sets up the message handler for this transport. In the WebMVC SSE implementation,
     * this method only stores the handler for later use, as connections are initiated by
     * clients rather than the server.
     *
     * @param connectionHandler The function to process incoming JSON-RPC messages and
     *                          produce responses
     * @return An empty Mono since the server doesn't initiate connections
     */
    @Override
    public Mono<Void> connect(
            Function<Mono<McpSchema.JSONRPCMessage>, Mono<McpSchema.JSONRPCMessage>> connectionHandler) {
        this.connectHandler = connectionHandler;
        // Server-side transport doesn't initiate connections
        return Mono.empty();
    }

    public Mono<Void> connectStream(
            Function<McpSchema.JSONRPCRequest, Flux<McpSchema.JSONRPCResponse>> streamHandler) {
        this.streamHandler = streamHandler;
        // Server-side transport doesn't initiate connections
        return Mono.empty();
    }

    /**
     * Broadcasts a message to all connected clients through their SSE connections. The
     * message is serialized to JSON and sent as an SSE event with type "message". If any
     * errors occur during sending to a particular client, they are logged but don't
     * prevent sending to other clients.
     *
     * @param message The JSON-RPC message to broadcast to all connected clients
     * @return A Mono that completes when the broadcast attempt is finished
     */
    @Override
    public Mono<Void> sendMessage(McpSchema.JSONRPCMessage message) {
        return Mono.fromRunnable(() -> {
            if (sessions.isEmpty()) {
                logger.debug("No active sessions to broadcast message to");
                return;
            }

            try {
                String jsonText = objectMapper.writeValueAsString(message);
                // TODO: log level should be DEBUG
                logger.info("Attempting to broadcast message to {} active sessions", sessions.size());

                String clientId = "";
                if (message instanceof McpSchema.JSONRPCResponse jrc) {
                    if (StringUtils.isNotEmpty(jrc.clientId())) {
                        clientId = jrc.clientId();
                    }
                }

                //如果clientId 不为空,则传递给相应的client
                if (StringUtils.isNotEmpty(clientId)) {
                    logger.info("send message to :{}", clientId);
                    ClientSession session = sessions.get(clientId);
                    if (null != session) {
                        sendMessageToSession(session, jsonText);
                    }
                    return;
                }

                sessions.values().forEach(session -> {
                    sendMessageToSession(session, jsonText);
                });


            } catch (IOException e) {
                logger.error("Failed to serialize message: {}", e.getMessage());
            }
        });
    }

    private static void sendMessageToSession(ClientSession session, String jsonText) {
        try {
            logger.info("Sending message to session: {}, message: {}", session.id, jsonText);
            session.sseBuilder.id(session.id).event(MESSAGE_EVENT_TYPE).data(jsonText);
        } catch (Exception e) {
            logger.error("Failed to send message to session {}: {}", session.id, e.getMessage());
            session.sseBuilder.error(e);
        }
    }

    /**
     * Handles new SSE connection requests from clients by creating a new session and
     * establishing an SSE connection. This method:
     * <ul>
     * <li>Generates a unique session ID</li>
     * <li>Creates a new ClientSession with an SSE builder</li>
     * <li>Sends an initial endpoint event to inform the client where to send
     * messages</li>
     * <li>Maintains the session in the sessions map</li>
     * </ul>
     *
     * @param request The incoming server request
     * @return A ServerResponse configured for SSE communication, or an error response if
     * the server is shutting down or the connection fails
     * <p>
     * sse 的连接会走到这里(本质是一个get)
     */
    private ServerResponse handleSseConnection(ServerRequest request) {
        if (this.isClosing) {
            return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE).body("Server is shutting down");
        }

        //本质是clientId
        List<String> clientList = request.headers().header(Const.MC_CLIENT_ID);
        String sessionId = CollectionUtils.isEmpty(clientList) ? UUID.randomUUID().toString() : clientList.get(0);
        logger.debug("Creating new SSE connection for session: {}", sessionId);

        // Send initial endpoint event
        try {
            return ServerResponse.sse(sseBuilder -> {

                ClientSession session = new ClientSession(sessionId, sseBuilder);
                this.sessions.put(sessionId, session);

                try {
                    session.sseBuilder.id(session.id).event(ENDPOINT_EVENT_TYPE).data(messageEndpoint);
                } catch (Exception e) {
                    logger.error("Failed to poll event from session queue: {}", e.getMessage());
                    sseBuilder.error(e);
                }
            }, Duration.ZERO);
        } catch (Exception e) {
            logger.error("Failed to send initial endpoint event to session {}: {}", sessionId, e.getMessage());
            sessions.remove(sessionId);
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String clientId(ServerRequest request) {
        List<String> list = request.headers().header(Const.MC_CLIENT_ID);
        String clientId = "";
        if (!CollectionUtils.isEmpty(list)) {
            clientId = list.get(0);
        }
        return clientId;
    }

    /**
     * Handles incoming JSON-RPC messages from clients. This method:
     * <ul>
     * <li>Deserializes the request body into a JSON-RPC message</li>
     * <li>Processes the message through the configured connect handler</li>
     * <li>Returns appropriate HTTP responses based on the processing result</li>
     * </ul>
     *
     * @param request The incoming server request containing the JSON-RPC message
     * @return A ServerResponse indicating success (200 OK) or appropriate error status
     * with error details in case of failures
     */
    private ServerResponse handleMessage(ServerRequest request) {
        if (this.isClosing) {
            return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE).body("Server is shutting down");
        }

        try {
            String body = request.body(String.class);

            //客户端id(每次客户端都会是一个新的post,但clientId并不会发生变化),每次本质就是一个Post请求过来
            String clientId = clientId(request);

            McpSchema.JSONRPCMessage message = McpSchema.deserializeJsonRpcMessage(objectMapper, body);

            //发过来的ping请求
            if (message instanceof McpSchema.JSONRPCRequest req && req.method().equals("ping")) {
                if (StringUtils.isNotEmpty(clientId)) {
                    sessions.computeIfPresent(clientId, (k, v) -> {
                        v.setUpdateTime(System.currentTimeMillis());
                        return v;
                    });
                }
            }

            // Handle tools stream requests
            if (message instanceof McpSchema.JSONRPCRequest req
                    && req.method().equals(McpSchema.METHOD_TOOLS_STREAM)) {
                logger.info("WebMvcSseServerTransport, Handling tools stream request: {}", req);
                // handle tools stream request
                if (streamHandler != null) {
                    //获取projectName,用来二次分发
                    String projectName = getProjectName(req);
                    streamHandler.apply(req)
                            .log()
                            .subscribe(
                                    response -> {
                                        McpSchema.JSONRPCResponse res = new McpSchema.JSONRPCResponse(
                                                response.jsonrpc(),
                                                response.id(),
                                                response.result(),
                                                response.error(),
                                                response.complete(),
                                                clientId,
                                                projectName
                                        );
                                        sendMessage(res).subscribe();
                                    },
                                    error -> {
                                        logger.error("Error handling tools stream request: {}", error.getMessage());
                                        sendMessage(new McpSchema.JSONRPCResponse(
                                                McpSchema.JSONRPC_VERSION, req.id(), null, new McpSchema.JSONRPCResponse.JSONRPCError(500, error.getMessage(), null
                                        ), true, clientId, projectName
                                        )).subscribe();
                                    }


                            );
                } else {
                    logger.warn("No stream handler registered for tools stream request");
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new McpError("No stream handler registered for tools stream request"));
                }
                return ServerResponse.ok().build();
            }
            // Convert the message to a Mono, apply the handler, and block for the
            // response
            Mono.just(message).transform(connectHandler).block();
            return ServerResponse.ok().build();
        } catch (IllegalArgumentException | IOException e) {
            logger.error("Failed to deserialize message: {}", e.getMessage());
            return ServerResponse.badRequest().body(new McpError("Invalid message format"));
        } catch (Exception e) {
            logger.error("Error handling message: {}", e.getMessage());
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new McpError(e.getMessage()));
        }
    }

    private static String getProjectName(McpSchema.JSONRPCRequest req) {
        if (null != req.params() && req.params() instanceof Map map) {
            if (map.containsKey(Const.PROJECT_NAME)) {
                return map.get(Const.PROJECT_NAME).toString();
            }
        }
        return "";
    }

    /**
     * Represents an active client session with its associated SSE connection. Each
     * session maintains:
     * <ul>
     * <li>A unique session identifier</li>
     * <li>An SSE builder for sending server events to the client</li>
     * <li>Logging of session lifecycle events</li>
     * </ul>
     */
    @Data
    private static class ClientSession {

        private final String id;

        private final SseBuilder sseBuilder;

        private volatile long updateTime = System.currentTimeMillis();

        /**
         * Creates a new client session with the specified ID and SSE builder.
         *
         * @param id         The unique identifier for this session
         * @param sseBuilder The SSE builder for sending server events to the client
         */
        ClientSession(String id, SseBuilder sseBuilder) {
            this.id = id;
            this.sseBuilder = sseBuilder;
            logger.debug("Session {} initialized with SSE emitter", id);
        }

        /**
         * Closes this session by completing the SSE connection. Any errors during
         * completion are logged but do not prevent the session from being marked as
         * closed.
         */
        void close() {
            logger.debug("Closing session: {}", id);
            try {
                sseBuilder.complete();
                logger.debug("Successfully completed SSE emitter for session {}", id);
            } catch (Exception e) {
                logger.warn("Failed to complete SSE emitter for session {}: {}", id, e.getMessage());
                // sseBuilder.error(e);
            }
        }

    }

    /**
     * Converts data from one type to another using the configured ObjectMapper. This is
     * particularly useful for handling complex JSON-RPC parameter types.
     *
     * @param data    The source data object to convert
     * @param typeRef The target type reference
     * @param <T>     The target type
     * @return The converted object of type T
     */
    @Override
    public <T> T unmarshalFrom(Object data, TypeReference<T> typeRef) {
        return this.objectMapper.convertValue(data, typeRef);
    }

    /**
     * Initiates a graceful shutdown of the transport. This method:
     * <ul>
     * <li>Sets the closing flag to prevent new connections</li>
     * <li>Closes all active SSE connections</li>
     * <li>Removes all session records</li>
     * </ul>
     *
     * @return A Mono that completes when all cleanup operations are finished
     */
    @Override
    public Mono<Void> closeGracefully() {
        return Mono.fromRunnable(() -> {
            this.isClosing = true;
            logger.debug("Initiating graceful shutdown with {} active sessions", sessions.size());

            sessions.values().forEach(session -> {
                String sessionId = session.id;
                session.close();
                sessions.remove(sessionId);
            });

            logger.info("Graceful shutdown completed");
        });
    }

    /**
     * Returns the RouterFunction that defines the HTTP endpoints for this transport. The
     * router function handles two endpoints:
     * <ul>
     * <li>GET /sse - For establishing SSE connections</li>
     * <li>POST [messageEndpoint] - For receiving JSON-RPC messages from clients</li>
     * </ul>
     *
     * @return The configured RouterFunction for handling HTTP requests
     */
    public RouterFunction<ServerResponse> getRouterFunction() {
        return this.routerFunction;
    }

}
