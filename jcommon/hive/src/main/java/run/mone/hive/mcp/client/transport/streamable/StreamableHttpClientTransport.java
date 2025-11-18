/*
 * Originally from Spring AI MCP Java SDK
 * Adapted for custom MCP framework
 *
 * Full implementation of the Streamable HTTP protocol with complete connection management.
 */

package run.mone.hive.mcp.client.transport.streamable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import run.mone.hive.mcp.client.transport.HttpHeaders;
import run.mone.hive.mcp.client.transport.ProtocolVersions;
import run.mone.hive.mcp.client.transport.ResponseSubscribers;
import run.mone.hive.mcp.client.transport.customizer.McpAsyncHttpClientRequestCustomizer;
import run.mone.hive.mcp.client.transport.customizer.McpSyncHttpClientRequestCustomizer;
import run.mone.hive.mcp.spec.ClientMcpTransport;
import run.mone.hive.mcp.spec.McpError;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.util.Assert;
import run.mone.hive.mcp.util.Utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Full implementation of the Streamable HTTP protocol with complete connection management.
 *
 * <p>
 * This implementation provides:
 * <ul>
 * <li>Complete session management with {@link McpTransportSession}</li>
 * <li>Stream resumability with {@link McpTransportStream}</li>
 * <li>Connection pooling and lifecycle management</li>
 * <li>Exception handling and session recovery</li>
 * <li>Context propagation via {@link McpTransportContext}</li>
 * </ul>
 *
 * @author Christian Tzolov (original)
 * @author Adapted for custom MCP framework
 */
@Slf4j
public class StreamableHttpClientTransport implements ClientMcpTransport {

    private static final String MCP_PROTOCOL_VERSION = ProtocolVersions.MCP_2025_06_18;
    private static final String DEFAULT_ENDPOINT = "/mcp";
    private static final String MESSAGE_EVENT_TYPE = "message";
    private static final String APPLICATION_JSON = "application/json";
    private static final String TEXT_EVENT_STREAM = "text/event-stream";

    public static final int NOT_FOUND = 404;
    public static final int METHOD_NOT_ALLOWED = 405;
    public static final int BAD_REQUEST = 400;

    private final HttpClient httpClient;
    private final HttpRequest.Builder requestBuilder;
    private final ObjectMapper objectMapper;
    private final URI baseUri;
    private final String endpoint;
    private final boolean openConnectionOnStartup;
    private final boolean resumableStreams;
    private final McpAsyncHttpClientRequestCustomizer httpRequestCustomizer;

    private final AtomicReference<McpTransportSession<Disposable>> activeSession = new AtomicReference<>();
    private final AtomicReference<Function<Mono<McpSchema.JSONRPCMessage>, Mono<McpSchema.JSONRPCMessage>>> handler = new AtomicReference<>();
    private final AtomicReference<Consumer<Throwable>> exceptionHandler = new AtomicReference<>();

    private StreamableHttpClientTransport(ObjectMapper objectMapper, HttpClient httpClient,
            HttpRequest.Builder requestBuilder, String baseUri, String endpoint, boolean resumableStreams,
            boolean openConnectionOnStartup, McpAsyncHttpClientRequestCustomizer httpRequestCustomizer) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
        this.requestBuilder = requestBuilder;
        this.baseUri = URI.create(baseUri);
        this.endpoint = endpoint;
        this.resumableStreams = resumableStreams;
        this.openConnectionOnStartup = openConnectionOnStartup;
        this.httpRequestCustomizer = httpRequestCustomizer;
        this.activeSession.set(createTransportSession());
    }

    public static Builder builder(String baseUri) {
        return new Builder(baseUri);
    }

    @Override
    public Mono<Void> connect(Function<Mono<McpSchema.JSONRPCMessage>, Mono<McpSchema.JSONRPCMessage>> handler) {
        return Mono.deferContextual(ctx -> {
            this.handler.set(handler);
            if (this.openConnectionOnStartup) {
                log.debug("Eagerly opening connection on startup");
                return this.reconnect(null).onErrorComplete(t -> {
                    log.warn("Eager connect failed", t);
                    return true;
                }).then();
            }
            return Mono.empty();
        });
    }

    public void setExceptionHandler(Consumer<Throwable> handler) {
        log.debug("Exception handler registered");
        this.exceptionHandler.set(handler);
    }

    private McpTransportSession<Disposable> createTransportSession() {
        Function<String, Publisher<Void>> onClose = sessionId -> sessionId == null ? Mono.empty()
                : createDelete(sessionId);
        return new DefaultMcpTransportSession(onClose);
    }

    private McpTransportSession<Disposable> createClosedSession(McpTransportSession<Disposable> existingSession) {
        var existingSessionId = Optional.ofNullable(existingSession)
            .filter(session -> !(session instanceof ClosedMcpTransportSession))
            .flatMap(McpTransportSession::sessionId)
            .orElse(null);
        return new ClosedMcpTransportSession(existingSessionId);
    }

    private Publisher<Void> createDelete(String sessionId) {
        var uri = Utils.resolveUri(this.baseUri, this.endpoint);
        return Mono.deferContextual(ctx -> {
            var builder = this.requestBuilder.copy()
                .uri(uri)
                .header("Cache-Control", "no-cache")
                .header(HttpHeaders.MCP_SESSION_ID, sessionId)
                .header(HttpHeaders.PROTOCOL_VERSION, MCP_PROTOCOL_VERSION)
                .DELETE();
            var transportContext = ctx.getOrDefault(McpTransportContext.KEY, McpTransportContext.EMPTY);
            return Mono.from(this.httpRequestCustomizer.customize(builder, "DELETE", uri, null, transportContext));
        }).flatMap(requestBuilder -> {
            var request = requestBuilder.build();
            return Mono.fromFuture(() -> this.httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()));
        }).then();
    }

    private void handleException(Throwable t) {
        log.debug("Handling exception for session {}", sessionIdOrPlaceholder(this.activeSession.get()), t);
        if (t instanceof McpTransportSessionNotFoundException) {
            McpTransportSession<?> invalidSession = this.activeSession.getAndSet(createTransportSession());
            log.warn("Server does not recognize session {}. Invalidating.", invalidSession.sessionId());
            invalidSession.close();
        }
        Consumer<Throwable> handler = this.exceptionHandler.get();
        if (handler != null) {
            handler.accept(t);
        }
    }

    @Override
    public Mono<Void> closeGracefully() {
        return Mono.defer(() -> {
            log.debug("Graceful close triggered");
            McpTransportSession<Disposable> currentSession = this.activeSession.getAndUpdate(this::createClosedSession);
            if (currentSession != null) {
                return Mono.from(currentSession.closeGracefully());
            }
            return Mono.empty();
        });
    }

    private Mono<Disposable> reconnect(McpTransportStream<Disposable> stream) {
        return Mono.deferContextual(ctx -> {
            if (stream != null) {
                log.debug("Reconnecting stream {} with lastId {}", stream.streamId(), stream.lastId());
            } else {
                log.debug("Reconnecting with no prior stream");
            }

            final AtomicReference<Disposable> disposableRef = new AtomicReference<>();
            final McpTransportSession<Disposable> transportSession = this.activeSession.get();
            var uri = Utils.resolveUri(this.baseUri, this.endpoint);

            Disposable connection = Mono.deferContextual(connectionCtx -> {
                HttpRequest.Builder requestBuilder = this.requestBuilder.copy();

                if (transportSession != null && transportSession.sessionId().isPresent()) {
                    requestBuilder = requestBuilder.header(HttpHeaders.MCP_SESSION_ID,
                            transportSession.sessionId().get());
                }

                if (stream != null && stream.lastId().isPresent()) {
                    requestBuilder = requestBuilder.header(HttpHeaders.LAST_EVENT_ID, stream.lastId().get());
                }

                var builder = requestBuilder.uri(uri)
                    .header(HttpHeaders.ACCEPT, TEXT_EVENT_STREAM)
                    .header("Cache-Control", "no-cache")
                    .header(HttpHeaders.PROTOCOL_VERSION, MCP_PROTOCOL_VERSION)
                    .GET();
                var transportContext = connectionCtx.getOrDefault(McpTransportContext.KEY, McpTransportContext.EMPTY);
                return Mono.from(this.httpRequestCustomizer.customize(builder, "GET", uri, null, transportContext));
            })
                .flatMapMany(requestBuilder -> Flux.<ResponseSubscribers.ResponseEvent>create(
                        sseSink -> this.httpClient
                            .sendAsync(requestBuilder.build(),
                                    responseInfo -> ResponseSubscribers.sseToBodySubscriber(responseInfo, sseSink))
                            .whenComplete((response, throwable) -> {
                                if (throwable != null) {
                                    sseSink.error(throwable);
                                } else {
                                    log.debug("SSE connection established successfully");
                                }
                            }))
                    .map(responseEvent -> (ResponseSubscribers.SseResponseEvent) responseEvent)
                    .flatMap(responseEvent -> {
                        int statusCode = responseEvent.responseInfo().statusCode();

                        if (statusCode >= 200 && statusCode < 300) {
                            if (MESSAGE_EVENT_TYPE.equals(responseEvent.sseEvent().event())) {
                                try {
                                    McpSchema.JSONRPCMessage message = McpSchema.deserializeJsonRpcMessage(
                                            this.objectMapper, responseEvent.sseEvent().data());

                                    Tuple2<Optional<String>, Iterable<McpSchema.JSONRPCMessage>> idWithMessages = Tuples
                                        .of(Optional.ofNullable(responseEvent.sseEvent().id()),
                                                List.of(message));

                                    McpTransportStream<Disposable> sessionStream = stream != null ? stream
                                            : new DefaultMcpTransportStream(this.resumableStreams, this::reconnect);
                                    log.debug("Connected stream {}", sessionStream.streamId());

                                    return Flux.from(sessionStream.consumeSseStream(Flux.just(idWithMessages)));
                                } catch (IOException ioException) {
                                    return Flux.<McpSchema.JSONRPCMessage>error(new McpError(
                                            "Error parsing JSON-RPC message: " + responseEvent));
                                }
                            } else {
                                log.debug("Received SSE event with type: {}", responseEvent.sseEvent());
                                return Flux.empty();
                            }
                        } else if (statusCode == METHOD_NOT_ALLOWED) {
                            log.debug("The server does not support SSE streams, using request-response mode.");
                            return Flux.empty();
                        } else if (statusCode == NOT_FOUND) {
                            if (transportSession != null && transportSession.sessionId().isPresent()) {
                                String sessionIdRepresentation = sessionIdOrPlaceholder(transportSession);
                                McpTransportSessionNotFoundException exception = new McpTransportSessionNotFoundException(
                                        "Session not found for session ID: " + sessionIdRepresentation);
                                return Flux.<McpSchema.JSONRPCMessage>error(exception);
                            }
                            return Flux.<McpSchema.JSONRPCMessage>error(
                                    new McpError("Server Not Found. Status code:" + statusCode));
                        } else if (statusCode == BAD_REQUEST) {
                            if (transportSession != null && transportSession.sessionId().isPresent()) {
                                String sessionIdRepresentation = sessionIdOrPlaceholder(transportSession);
                                McpTransportSessionNotFoundException exception = new McpTransportSessionNotFoundException(
                                        "Session not found for session ID: " + sessionIdRepresentation);
                                return Flux.<McpSchema.JSONRPCMessage>error(exception);
                            }
                            return Flux.<McpSchema.JSONRPCMessage>error(
                                    new McpError("Bad Request. Status code:" + statusCode));
                        }

                        return Flux.<McpSchema.JSONRPCMessage>error(new McpError(
                                "Received unrecognized SSE event type: " + responseEvent.sseEvent().event()));
                    })
                    .flatMap(jsonrpcMessage -> this.handler.get().apply(Mono.just(jsonrpcMessage)))
                    .onErrorMap(CompletionException.class, t -> t.getCause())
                    .onErrorComplete(t -> {
                        this.handleException(t);
                        return true;
                    })
                    .doFinally(s -> {
                        Disposable ref = disposableRef.getAndSet(null);
                        if (ref != null) {
                            transportSession.removeConnection(ref);
                        }
                    }))
                .contextWrite(ctx)
                .subscribe();

            disposableRef.set(connection);
            transportSession.addConnection(connection);
            return Mono.just(connection);
        });
    }

    private BodyHandler<Void> toSendMessageBodySubscriber(FluxSink<ResponseSubscribers.ResponseEvent> sink) {
        BodyHandler<Void> responseBodyHandler = responseInfo -> {
            String contentType = responseInfo.headers().firstValue(HttpHeaders.CONTENT_TYPE).orElse("").toLowerCase();

            if (contentType.contains(TEXT_EVENT_STREAM)) {
                log.debug("Received SSE stream response, using line subscriber");
                return ResponseSubscribers.sseToBodySubscriber(responseInfo, sink);
            } else if (contentType.contains(APPLICATION_JSON)) {
                log.debug("Received response, using string subscriber");
                return ResponseSubscribers.aggregateBodySubscriber(responseInfo, sink);
            }

            log.debug("Received Bodyless response, using discarding subscriber");
            return ResponseSubscribers.bodilessBodySubscriber(responseInfo, sink);
        };

        return responseBodyHandler;
    }

    public String toString(McpSchema.JSONRPCMessage message) {
        try {
            return this.objectMapper.writeValueAsString(message);
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize JSON-RPC message", e);
        }
    }

    @Override
    public Mono<Object> sendMessage(McpSchema.JSONRPCMessage sentMessage) {
        return Mono.create(deliveredSink -> {
            log.debug("Sending message {}", sentMessage);

            final AtomicReference<Disposable> disposableRef = new AtomicReference<>();
            final McpTransportSession<Disposable> transportSession = this.activeSession.get();

            var uri = Utils.resolveUri(this.baseUri, this.endpoint);
            String jsonBody = this.toString(sentMessage);

            Disposable connection = Mono.deferContextual(ctx -> {
                HttpRequest.Builder requestBuilder = this.requestBuilder.copy();

                if (transportSession != null && transportSession.sessionId().isPresent()) {
                    requestBuilder = requestBuilder.header(HttpHeaders.MCP_SESSION_ID,
                            transportSession.sessionId().get());
                }

                var builder = requestBuilder.uri(uri)
                    .header(HttpHeaders.ACCEPT, APPLICATION_JSON + ", " + TEXT_EVENT_STREAM)
                    .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                    .header(HttpHeaders.PROTOCOL_VERSION, MCP_PROTOCOL_VERSION)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody));
                var transportContext = ctx.getOrDefault(McpTransportContext.KEY, McpTransportContext.EMPTY);
                return Mono.from(this.httpRequestCustomizer.customize(builder, "POST", uri, jsonBody, transportContext));
            }).flatMapMany(requestBuilder -> Flux.<ResponseSubscribers.ResponseEvent>create(responseEventSink -> {
                Mono.fromFuture(this.httpClient
                    .sendAsync(requestBuilder.build(), this.toSendMessageBodySubscriber(responseEventSink))
                    .whenComplete((response, throwable) -> {
                        if (throwable != null) {
                            responseEventSink.error(throwable);
                        } else {
                            log.debug("Request completed successfully");
                        }
                    })).onErrorMap(CompletionException.class, t -> t.getCause()).onErrorComplete().subscribe();
            })).flatMap(responseEvent -> {
                if (transportSession.markInitialized(
                        responseEvent.responseInfo().headers().firstValue("mcp-session-id").orElseGet(() -> null))) {
                    reconnect(null).contextWrite(deliveredSink.contextView()).subscribe();
                }

                String sessionRepresentation = sessionIdOrPlaceholder(transportSession);
                int statusCode = responseEvent.responseInfo().statusCode();

                if (statusCode >= 200 && statusCode < 300) {
                    String contentType = responseEvent.responseInfo()
                        .headers()
                        .firstValue(HttpHeaders.CONTENT_TYPE)
                        .orElse("")
                        .toLowerCase();

                    String contentLength = responseEvent.responseInfo()
                        .headers()
                        .firstValue(HttpHeaders.CONTENT_LENGTH)
                        .orElse(null);

                    if (contentType.isBlank() || "0".equals(contentLength)) {
                        log.debug("No body returned for POST in session {}", sessionRepresentation);
                        deliveredSink.success();
                        return Flux.empty();
                    } else if (contentType.contains(TEXT_EVENT_STREAM)) {
                        return Flux.just(((ResponseSubscribers.SseResponseEvent) responseEvent).sseEvent())
                            .flatMap(sseEvent -> {
                                try {
                                    McpSchema.JSONRPCMessage message = McpSchema
                                        .deserializeJsonRpcMessage(this.objectMapper, sseEvent.data());

                                    Tuple2<Optional<String>, Iterable<McpSchema.JSONRPCMessage>> idWithMessages = Tuples
                                        .of(Optional.ofNullable(sseEvent.id()), List.of(message));

                                    McpTransportStream<Disposable> sessionStream = new DefaultMcpTransportStream(
                                            this.resumableStreams, this::reconnect);

                                    log.debug("Connected stream {}", sessionStream.streamId());
                                    deliveredSink.success();

                                    return Flux.from(sessionStream.consumeSseStream(Flux.just(idWithMessages)));
                                } catch (IOException ioException) {
                                    return Flux.<McpSchema.JSONRPCMessage>error(new McpError(
                                            "Error parsing JSON-RPC message: " + responseEvent));
                                }
                            });
                    } else if (contentType.contains(APPLICATION_JSON)) {
                        deliveredSink.success();
                        String data = ((ResponseSubscribers.AggregateResponseEvent) responseEvent).data();
                        if (sentMessage instanceof McpSchema.JSONRPCNotification) {
                            log.warn("Notification: {} received non-compliant response: {}", sentMessage,
                                    Utils.hasText(data) ? data : "[empty]");
                            return Mono.empty();
                        }

                        try {
                            return Mono.just(McpSchema.deserializeJsonRpcMessage(objectMapper, data));
                        } catch (IOException e) {
                            return Mono.error(new McpError(
                                    "Error deserializing JSON-RPC message: " + responseEvent));
                        }
                    }
                    log.warn("Unknown media type {} returned for POST in session {}", contentType,
                            sessionRepresentation);

                    return Flux.<McpSchema.JSONRPCMessage>error(
                            new RuntimeException("Unknown media type returned: " + contentType));
                } else if (statusCode == NOT_FOUND) {
                    if (transportSession != null && transportSession.sessionId().isPresent()) {
                        McpTransportSessionNotFoundException exception = new McpTransportSessionNotFoundException(
                                "Session not found for session ID: " + sessionRepresentation);
                        return Flux.<McpSchema.JSONRPCMessage>error(exception);
                    }
                    return Flux.<McpSchema.JSONRPCMessage>error(new McpError(
                            "Server Not Found. Status code:" + statusCode));
                } else if (statusCode == BAD_REQUEST) {
                    if (transportSession != null && transportSession.sessionId().isPresent()) {
                        McpTransportSessionNotFoundException exception = new McpTransportSessionNotFoundException(
                                "Session not found for session ID: " + sessionRepresentation);
                        return Flux.<McpSchema.JSONRPCMessage>error(exception);
                    }
                    return Flux.<McpSchema.JSONRPCMessage>error(new McpError(
                            "Bad Request. Status code:" + statusCode));
                }

                return Flux.<McpSchema.JSONRPCMessage>error(
                        new RuntimeException("Failed to send message: " + responseEvent));
            })
                .flatMap(jsonRpcMessage -> this.handler.get().apply(Mono.just(jsonRpcMessage)))
                .onErrorMap(CompletionException.class, t -> t.getCause())
                .onErrorComplete(t -> {
                    this.handleException(t);
                    deliveredSink.error(t);
                    return true;
                })
                .doFinally(s -> {
                    log.debug("SendMessage finally: {}", s);
                    Disposable ref = disposableRef.getAndSet(null);
                    if (ref != null) {
                        transportSession.removeConnection(ref);
                    }
                })
                .contextWrite(deliveredSink.contextView())
                .subscribe();

            disposableRef.set(connection);
            transportSession.addConnection(connection);
        });
    }

    private static String sessionIdOrPlaceholder(McpTransportSession<?> transportSession) {
        return transportSession.sessionId().orElse("[missing_session_id]");
    }

    @Override
    public <T> T unmarshalFrom(Object data, TypeReference<T> typeRef) {
        return this.objectMapper.convertValue(data, typeRef);
    }

    /**
     * Builder for {@link StreamableHttpClientTransport}.
     */
    public static class Builder {

        private final String baseUri;
        private ObjectMapper objectMapper;
        private HttpClient.Builder clientBuilder = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1);
        private String endpoint = DEFAULT_ENDPOINT;
        private boolean resumableStreams = true;
        private boolean openConnectionOnStartup = false;
        private HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        private McpAsyncHttpClientRequestCustomizer httpRequestCustomizer = McpAsyncHttpClientRequestCustomizer.NOOP;
        private Duration connectTimeout = Duration.ofSeconds(10);

        private Builder(String baseUri) {
            Assert.hasText(baseUri, "baseUri must not be empty");
            this.baseUri = baseUri;
        }

        public Builder objectMapper(ObjectMapper objectMapper) {
            Assert.notNull(objectMapper, "objectMapper must not be null");
            this.objectMapper = objectMapper;
            return this;
        }

        public Builder clientBuilder(HttpClient.Builder clientBuilder) {
            Assert.notNull(clientBuilder, "clientBuilder must not be null");
            this.clientBuilder = clientBuilder;
            return this;
        }

        public Builder requestBuilder(HttpRequest.Builder requestBuilder) {
            Assert.notNull(requestBuilder, "requestBuilder must not be null");
            this.requestBuilder = requestBuilder;
            return this;
        }

        public Builder endpoint(String endpoint) {
            Assert.hasText(endpoint, "endpoint must be a non-empty String");
            this.endpoint = endpoint;
            return this;
        }

        public Builder resumableStreams(boolean resumableStreams) {
            this.resumableStreams = resumableStreams;
            return this;
        }

        public Builder openConnectionOnStartup(boolean openConnectionOnStartup) {
            this.openConnectionOnStartup = openConnectionOnStartup;
            return this;
        }

        public Builder httpRequestCustomizer(McpSyncHttpClientRequestCustomizer syncHttpRequestCustomizer) {
            this.httpRequestCustomizer = McpAsyncHttpClientRequestCustomizer.fromSync(syncHttpRequestCustomizer);
            return this;
        }

        public Builder asyncHttpRequestCustomizer(McpAsyncHttpClientRequestCustomizer asyncHttpRequestCustomizer) {
            this.httpRequestCustomizer = asyncHttpRequestCustomizer;
            return this;
        }

        public Builder connectTimeout(Duration connectTimeout) {
            Assert.notNull(connectTimeout, "connectTimeout must not be null");
            this.connectTimeout = connectTimeout;
            return this;
        }

        public StreamableHttpClientTransport build() {
            HttpClient httpClient = this.clientBuilder.connectTimeout(this.connectTimeout).build();
            ObjectMapper mapper = objectMapper != null ? objectMapper : new ObjectMapper();
            return new StreamableHttpClientTransport(mapper, httpClient, requestBuilder, baseUri,
                    endpoint, resumableStreams, openConnectionOnStartup, httpRequestCustomizer);
        }
    }

}
