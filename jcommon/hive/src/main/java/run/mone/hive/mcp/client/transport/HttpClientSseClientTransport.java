package run.mone.hive.mcp.client.transport;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import run.mone.hive.configs.Const;
import run.mone.hive.mcp.client.transport.FlowSseClient.SseEvent;
import run.mone.hive.mcp.spec.ClientMcpTransport;
import run.mone.hive.mcp.spec.McpError;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.McpSchema.JSONRPCMessage;
import run.mone.hive.mcp.util.Assert;

import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * ORIGINAL CODE IS FROM SPRING AI!!!
 * <p>
 * Server-Sent Events (SSE) implementation of the
 * {@link org.springframework.ai.mcp.spec.McpTransport} that follows the MCP HTTP with SSE
 * transport specification, using Java's HttpClient.
 *
 * <p>
 * This transport implementation establishes a bidirectional communication channel between
 * client and server using SSE for server-to-client messages and HTTP POST requests for
 * client-to-server messages. The transport:
 * <ul>
 * <li>Establishes an SSE connection to receive server messages</li>
 * <li>Handles endpoint discovery through SSE events</li>
 * <li>Manages message serialization/deserialization using Jackson</li>
 * <li>Provides graceful connection termination</li>
 * </ul>
 *
 * <p>
 * The transport supports two types of SSE events:
 * <ul>
 * <li>'endpoint' - Contains the URL for sending client messages</li>
 * <li>'message' - Contains JSON-RPC message payload</li>
 * </ul>
 *
 * @see run.mone.hive.mcp.spec.McpTransport
 * @see run.mone.hive.mcp.spec.ClientMcpTransport
 */
public class HttpClientSseClientTransport implements ClientMcpTransport {

    private final static Logger logger = LoggerFactory.getLogger(HttpClientSseClientTransport.class);

    /**
     * SSE event type for JSON-RPC messages
     */
    private final static String MESSAGE_EVENT_TYPE = "message";

    /**
     * SSE event type for endpoint discovery
     */
    private final static String ENDPOINT_EVENT_TYPE = "endpoint";

    /**
     * Default SSE endpoint path
     */
    private final static String SSE_ENDPOINT = "/sse";

    /**
     * Base URI for the MCP server
     */
    private final String baseUri;

    /**
     * SSE client for handling server-sent events. Uses the /sse endpoint
     */
    private final FlowSseClient sseClient;

    /**
     * HTTP client for sending messages to the server. Uses HTTP POST over the message
     * endpoint
     */
    private final HttpClient httpClient;

    /**
     * JSON object mapper for message serialization/deserialization
     */
    protected ObjectMapper objectMapper;

    /**
     * Flag indicating if the transport is in closing state
     */
    private volatile boolean isClosing = false;

    /**
     * Latch for coordinating endpoint discovery
     */
    private CountDownLatch closeLatch = new CountDownLatch(1);

    /**
     * Holds the discovered message endpoint URL
     */
    private final AtomicReference<String> messageEndpoint = new AtomicReference<>();

    /**
     * Holds the SSE connection future
     */
    private final AtomicReference<CompletableFuture<Void>> connectionFuture = new AtomicReference<>();

    @Setter
    @Getter
    private String clientId;

    /**
     * Creates a new transport instance with default HTTP client and object mapper.
     *
     * @param baseUri the base URI of the MCP server
     */
    public HttpClientSseClientTransport(String baseUri) {
        this(HttpClient.newBuilder(), baseUri, new ObjectMapper());
    }

    /**
     * Creates a new transport instance with custom HTTP client builder and object mapper.
     *
     * @param clientBuilder the HTTP client builder to use
     * @param baseUri       the base URI of the MCP server
     * @param objectMapper  the object mapper for JSON serialization/deserialization
     * @throws IllegalArgumentException if objectMapper or clientBuilder is null
     */
    public HttpClientSseClientTransport(HttpClient.Builder clientBuilder, String baseUri, ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "ObjectMapper must not be null");
        Assert.hasText(baseUri, "baseUri must not be empty");
        Assert.notNull(clientBuilder, "clientBuilder must not be null");
        this.baseUri = baseUri;
        this.objectMapper = objectMapper;
        this.httpClient = clientBuilder.connectTimeout(Duration.ofSeconds(10)).build();
        this.sseClient = new FlowSseClient(this.httpClient);
    }

    /**
     * Establishes the SSE connection with the server and sets up message handling.
     *
     * <p>
     * This method:
     * <ul>
     * <li>Initiates the SSE connection</li>
     * <li>Handles endpoint discovery events</li>
     * <li>Processes incoming JSON-RPC messages</li>
     * </ul>
     *
     * @param handler the function to process received JSON-RPC messages
     * @return a Mono that completes when the connection is established
     * <p>
     * 连接到的是sse,用来返回结果的流
     */
    @Override
    public Mono<Void> connect(Function<Mono<JSONRPCMessage>, Mono<JSONRPCMessage>> handler) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        connectionFuture.set(future);
        sseClient.subscribe(this.baseUri + SSE_ENDPOINT, new FlowSseClient.SseEventHandler() {
            @Override
            public void onEvent(SseEvent event) {
                if (isClosing) {
                    return;
                }

                try {
                    if (ENDPOINT_EVENT_TYPE.equals(event.type())) {
                        String endpoint = event.data();
                        messageEndpoint.set(endpoint);
                        closeLatch.countDown();
                        future.complete(null);
                    } else if (MESSAGE_EVENT_TYPE.equals(event.type())) {
                        JSONRPCMessage message = McpSchema.deserializeJsonRpcMessage(objectMapper, event.data());
                        handler.apply(Mono.just(message)).subscribe();
                    } else {
                        logger.error("Received unrecognized SSE event type: " + event.type());
                    }
                } catch (IOException e) {
                    logger.error("Error processing SSE event", e);
                    future.completeExceptionally(e);
                }
            }

            @Override
            public void onError(Throwable error) {
                if (!isClosing) {
                    logger.error("SSE connection error", error);
                    future.completeExceptionally(error);
                }
            }
        }, this.clientId);

        return Mono.fromFuture(future);
    }

    /**
     * Sends a JSON-RPC message to the server.
     *
     * <p>
     * This method waits for the message endpoint to be discovered before sending the
     * message. The message is serialized to JSON and sent as an HTTP POST request.
     *
     * @param message the JSON-RPC message to send
     * @return a Mono that completes when the message is sent
     * @throws McpError if the message endpoint is not available or the wait times out
     */
    @Override
    public Mono<Void> sendMessage(JSONRPCMessage message) {
        if (isClosing) {
            return Mono.empty();
        }

        try {
            if (!closeLatch.await(10, TimeUnit.SECONDS)) {
                return Mono.error(new McpError("Failed to wait for the message endpoint"));
            }
        } catch (InterruptedException e) {
            return Mono.error(new McpError("Failed to wait for the message endpoint"));
        }

        String endpoint = messageEndpoint.get();
        if (endpoint == null) {
            return Mono.error(new McpError("No message endpoint available"));
        }

        try {
            String jsonText = this.objectMapper.writeValueAsString(message);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.baseUri + endpoint))
                    .header("Content-Type", "application/json")
                    .header(Const.MC_CLIENT_ID, this.clientId)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonText))
                    .build();

            return Mono.fromFuture(
                    //每次调用都是一次新的post请求
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding()).thenAccept(response -> {
                        if (response.statusCode() != 200) {
                            logger.error("Error sending message: {}", response.statusCode());
                        }
                    }));
        } catch (IOException e) {
            if (!isClosing) {
                return Mono.error(new RuntimeException("Failed to serialize message", e));
            }
            return Mono.empty();
        }
    }

    /**
     * Gracefully closes the transport connection.
     *
     * <p>
     * Sets the closing flag and cancels any pending connection future. This prevents new
     * messages from being sent and allows ongoing operations to complete.
     *
     * @return a Mono that completes when the closing process is initiated
     */
    @Override
    public Mono<Void> closeGracefully() {
        return Mono.fromRunnable(() -> {
            isClosing = true;
            CompletableFuture<Void> future = connectionFuture.get();
            if (future != null && !future.isDone()) {
                future.cancel(true);
            }
        });
    }

    /**
     * Unmarshals data to the specified type using the configured object mapper.
     *
     * @param data    the data to unmarshal
     * @param typeRef the type reference for the target type
     * @param <T>     the target type
     * @return the unmarshalled object
     */
    @Override
    public <T> T unmarshalFrom(Object data, TypeReference<T> typeRef) {
        return this.objectMapper.convertValue(data, typeRef);
    }

}
