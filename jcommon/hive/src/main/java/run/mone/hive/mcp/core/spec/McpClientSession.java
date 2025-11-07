/*
 * Copyright 2024-2024 the original author or authors.
 */

package run.mone.hive.mcp.core.spec;

import run.mone.hive.mcp.json.TypeRef;
import run.mone.hive.mcp.core.util.Assert;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * Default implementation of the MCP (Model Context Protocol) session that manages
 * bidirectional JSON-RPC communication between clients and servers. This implementation
 * follows the MCP specification for message exchange and transport handling.
 *
 * <p>
 * The session manages:
 * <ul>
 * <li>Request/response handling with unique message IDs</li>
 * <li>Notification processing</li>
 * <li>Message timeout management</li>
 * <li>Transport layer abstraction</li>
 * </ul>
 *
 * @author Christian Tzolov
 * @author Dariusz Jędrzejczyk
 * @author Yanming Zhou
 */
public class McpClientSession implements McpSession {

	private static final Logger logger = LoggerFactory.getLogger(McpClientSession.class);

	/** Duration to wait for request responses before timing out */
	private final Duration requestTimeout;

	/** Transport layer implementation for message exchange */
	private final McpClientTransport transport;

	/** Map of pending responses keyed by request ID */
	private final ConcurrentHashMap<Object, MonoSink<McpSchema.JSONRPCResponse>> pendingResponses = new ConcurrentHashMap<>();

	/** Map of pending stream responses keyed by request ID */
	private final ConcurrentHashMap<Object, StreamEntry<?>> pendingStreamResponses = new ConcurrentHashMap<>();

	private static class StreamEntry<T> {
		final FluxSink<T> sink;
		final TypeRef<T> typeRef;
		StreamEntry(FluxSink<T> sink, TypeRef<T> typeRef) {
			this.sink = sink;
			this.typeRef = typeRef;
		}
	}

	/** Map of request handlers keyed by method name */
	private final ConcurrentHashMap<String, RequestHandler<?>> requestHandlers = new ConcurrentHashMap<>();

	/** Map of notification handlers keyed by method name */
	private final ConcurrentHashMap<String, NotificationHandler> notificationHandlers = new ConcurrentHashMap<>();

	/** Session-specific prefix for request IDs */
	private final String sessionPrefix = UUID.randomUUID().toString().substring(0, 8);

	/** Atomic counter for generating unique request IDs */
	private final AtomicLong requestCounter = new AtomicLong(0);

	/**
	 * Functional interface for handling incoming JSON-RPC requests. Implementations
	 * should process the request parameters and return a response.
	 *
	 * @param <T> Response type
	 */
	@FunctionalInterface
	public interface RequestHandler<T> {

		/**
		 * Handles an incoming request with the given parameters.
		 * @param params The request parameters
		 * @return A Mono containing the response object
		 */
		Mono<T> handle(Object params);

	}

	/**
	 * Functional interface for handling incoming JSON-RPC notifications. Implementations
	 * should process the notification parameters without returning a response.
	 */
	@FunctionalInterface
	public interface NotificationHandler {

		/**
		 * Handles an incoming notification with the given parameters.
		 * @param params The notification parameters
		 * @return A Mono that completes when the notification is processed
		 */
		Mono<Void> handle(Object params);

	}

	/**
	 * Creates a new McpClientSession with the specified configuration and handlers.
	 * @param requestTimeout Duration to wait for responses
	 * @param transport Transport implementation for message exchange
	 * @param requestHandlers Map of method names to request handlers
	 * @param notificationHandlers Map of method names to notification handlers
	 * @deprecated Use
	 * {@link #McpClientSession(Duration, McpClientTransport, Map, Map, Function)}
	 */
	@Deprecated
	public McpClientSession(Duration requestTimeout, McpClientTransport transport,
			Map<String, RequestHandler<?>> requestHandlers, Map<String, NotificationHandler> notificationHandlers) {
		this(requestTimeout, transport, requestHandlers, notificationHandlers, Function.identity());
	}

	/**
	 * Creates a new McpClientSession with the specified configuration and handlers.
	 * @param requestTimeout Duration to wait for responses
	 * @param transport Transport implementation for message exchange
	 * @param requestHandlers Map of method names to request handlers
	 * @param notificationHandlers Map of method names to notification handlers
	 * @param connectHook Hook that allows transforming the connection Publisher prior to
	 * subscribing
	 */
	public McpClientSession(Duration requestTimeout, McpClientTransport transport,
			Map<String, RequestHandler<?>> requestHandlers, Map<String, NotificationHandler> notificationHandlers,
			Function<? super Mono<Void>, ? extends Publisher<Void>> connectHook) {

		Assert.notNull(requestTimeout, "The requestTimeout can not be null");
		Assert.notNull(transport, "The transport can not be null");
		Assert.notNull(requestHandlers, "The requestHandlers can not be null");
		Assert.notNull(notificationHandlers, "The notificationHandlers can not be null");

		this.requestTimeout = requestTimeout;
		this.transport = transport;
		this.requestHandlers.putAll(requestHandlers);
		this.notificationHandlers.putAll(notificationHandlers);

		this.transport.connect(mono -> mono.doOnNext(this::handle)).transform(connectHook).subscribe();
	}

    private void dismissPendingResponses() {
        this.pendingResponses.forEach((id, sink) -> {
            logger.warn("Abruptly terminating exchange for request {}", id);
            sink.error(new RuntimeException("MCP session with server terminated"));
        });
        this.pendingResponses.clear();

        this.pendingStreamResponses.forEach((id, entry) -> {
            logger.warn("Abruptly terminating stream for request {}", id);
            try {
                entry.sink.error(new RuntimeException("MCP streaming session with server terminated"));
            } catch (Throwable t) {
                logger.debug("Failed to error stream sink for {}", id, t);
            }
        });
        this.pendingStreamResponses.clear();
    }

	private void handle(McpSchema.JSONRPCMessage message) {
		if (message instanceof McpSchema.JSONRPCResponse response) {
			logger.debug("Received response: {}", response);
			if (response.id() != null) {
				// streaming first
				StreamEntry<?> streamEntry = pendingStreamResponses.get(response.id());
				if (streamEntry != null) {
					@SuppressWarnings("unchecked")
					StreamEntry<Object> entry = (StreamEntry<Object>) streamEntry;
					try {
						if (response.error() != null) {
							entry.sink.error(new McpError(response.error()));
							pendingStreamResponses.remove(response.id());
							return;
						}
						Object value = this.transport.unmarshalFrom(response.result(), entry.typeRef);
						entry.sink.next(value);
						if (isStreamComplete(value)) {
							entry.sink.complete();
							pendingStreamResponses.remove(response.id());
						}
						return;
					}
					catch (Throwable t) {
						entry.sink.error(t);
						pendingStreamResponses.remove(response.id());
						return;
					}
				}

				// single response path
				var sink = pendingResponses.remove(response.id());
				if (sink == null) {
					logger.warn("Unexpected response for unknown id {}", response.id());
				}
				else {
					sink.success(response);
				}
			}
			else {
				logger.error("Discarded MCP request response without session id. "
						+ "This is an indication of a bug in the request sender code that can lead to memory "
						+ "leaks as pending requests will never be completed.");
			}
		}
		else if (message instanceof McpSchema.JSONRPCRequest request) {
			logger.debug("Received request: {}", request);
			handleIncomingRequest(request).onErrorResume(error -> {
				var errorResponse = new McpSchema.JSONRPCResponse(McpSchema.JSONRPC_VERSION, request.id(), null,
						new McpSchema.JSONRPCResponse.JSONRPCError(McpSchema.ErrorCodes.INTERNAL_ERROR,
								error.getMessage(), null));
				return Mono.just(errorResponse);
			}).flatMap(this.transport::sendMessage).onErrorComplete(t -> {
				logger.warn("Issue sending response to the client, ", t);
				return true;
			}).subscribe();
		}
		else if (message instanceof McpSchema.JSONRPCNotification notification) {
			logger.debug("Received notification: {}", notification);
			handleIncomingNotification(notification).onErrorComplete(t -> {
				logger.error("Error handling notification: {}", t.getMessage());
				return true;
			}).subscribe();
		}
		else {
			logger.warn("Received unknown message type: {}", message);
		}
	}

	/**
	 * Handles an incoming JSON-RPC request by routing it to the appropriate handler.
	 * @param request The incoming JSON-RPC request
	 * @return A Mono containing the JSON-RPC response
	 */
	private Mono<McpSchema.JSONRPCResponse> handleIncomingRequest(McpSchema.JSONRPCRequest request) {
		return Mono.defer(() -> {
			var handler = this.requestHandlers.get(request.method());
			if (handler == null) {
				MethodNotFoundError error = getMethodNotFoundError(request.method());
				return Mono.just(new McpSchema.JSONRPCResponse(McpSchema.JSONRPC_VERSION, request.id(), null,
						new McpSchema.JSONRPCResponse.JSONRPCError(McpSchema.ErrorCodes.METHOD_NOT_FOUND,
								error.message(), error.data())));
			}

			return handler.handle(request.params())
				.map(result -> new McpSchema.JSONRPCResponse(McpSchema.JSONRPC_VERSION, request.id(), result, null));
		});
	}

	record MethodNotFoundError(String method, String message, Object data) {
	}

	private MethodNotFoundError getMethodNotFoundError(String method) {
		switch (method) {
			case McpSchema.METHOD_ROOTS_LIST:
				return new MethodNotFoundError(method, "Roots not supported",
						Map.of("reason", "Client does not have roots capability"));
			default:
				return new MethodNotFoundError(method, "Method not found: " + method, null);
		}
	}

	/**
	 * Handles an incoming JSON-RPC notification by routing it to the appropriate handler.
	 * @param notification The incoming JSON-RPC notification
	 * @return A Mono that completes when the notification is processed
	 */
	private Mono<Void> handleIncomingNotification(McpSchema.JSONRPCNotification notification) {
		return Mono.defer(() -> {
			var handler = notificationHandlers.get(notification.method());
			if (handler == null) {
				logger.warn("No handler registered for notification method: {}", notification);
				return Mono.empty();
			}
			return handler.handle(notification.params());
		});
	}

	/**
	 * Generates a unique request ID in a non-blocking way. Combines a session-specific
	 * prefix with an atomic counter to ensure uniqueness.
	 * @return A unique request ID string
	 */
	private String generateRequestId() {
		return this.sessionPrefix + "-" + this.requestCounter.getAndIncrement();
	}

	/**
	 * Sends a JSON-RPC request and returns a stream of responses.
	 * @param <T> The expected response type
	 * @param method The method name to call
	 * @param requestParams The request parameters
	 * @param typeRef Type reference for response deserialization
	 * @return A Flux containing the response stream
	 */
	public <T> Flux<T> sendRequestStream(String method, Object requestParams, TypeRef<T> typeRef) {
		String requestId = this.generateRequestId();
		// gRPC 传输层原生支持流式，直接走传输层的 sendStreamMessage
		if (this.transport instanceof run.mone.hive.mcp.grpc.transport.GrpcClientTransport gct) {
			McpSchema.JSONRPCRequest jsonrpcRequest = new McpSchema.JSONRPCRequest(McpSchema.JSONRPC_VERSION, method,
					requestId, requestParams);
			return gct.sendStreamMessage(jsonrpcRequest)
				.map(obj -> this.transport.unmarshalFrom(obj, typeRef));
		}

		return Flux.<T>create(sink -> {
			this.pendingStreamResponses.put(requestId, new StreamEntry<>(sink, typeRef));
			McpSchema.JSONRPCRequest jsonrpcRequest = new McpSchema.JSONRPCRequest(McpSchema.JSONRPC_VERSION, method,
					requestId, requestParams);
			this.transport.sendMessage(jsonrpcRequest).subscribe(v -> {
			}, error -> {
				this.pendingStreamResponses.remove(requestId);
				sink.error(error);
			});
		}).doOnCancel(() -> this.pendingStreamResponses.remove(requestId));
	}

	private boolean isStreamComplete(Object result) {
		if (result instanceof McpSchema.CallToolResult ctr) {
			if (ctr.content() == null || ctr.content().isEmpty()) {
				return false;
			}
			McpSchema.Content c = ctr.content().get(0);
			if (c instanceof McpSchema.TextContent tc) {
				return "[DONE]".equals(tc.text());
			}
		}
		return false;
	}

	/**
	 * Sends a JSON-RPC request and returns the response.
	 * @param <T> The expected response type
	 * @param method The method name to call
	 * @param requestParams The request parameters
	 * @param typeRef Type reference for response deserialization
	 * @return A Mono containing the response
	 */
	@Override
	public <T> Mono<T> sendRequest(String method, Object requestParams, TypeRef<T> typeRef) {
		String requestId = this.generateRequestId();

		return Mono.deferContextual(ctx -> Mono.<McpSchema.JSONRPCResponse>create(pendingResponseSink -> {
			logger.debug("Sending message for method {}", method);
			this.pendingResponses.put(requestId, pendingResponseSink);
			McpSchema.JSONRPCRequest jsonrpcRequest = new McpSchema.JSONRPCRequest(McpSchema.JSONRPC_VERSION, method,
					requestId, requestParams);
			this.transport.sendMessage(jsonrpcRequest).contextWrite(ctx).subscribe(v -> {
			}, error -> {
				this.pendingResponses.remove(requestId);
				pendingResponseSink.error(error);
			});
		})).timeout(this.requestTimeout).handle((jsonRpcResponse, deliveredResponseSink) -> {
			if (jsonRpcResponse.error() != null) {
				logger.error("Error handling request: {}", jsonRpcResponse.error());
				deliveredResponseSink.error(new McpError(jsonRpcResponse.error()));
			}
			else {
				if (typeRef.getType().equals(Void.class)) {
					deliveredResponseSink.complete();
				}
				else {
					deliveredResponseSink.next(this.transport.unmarshalFrom(jsonRpcResponse.result(), typeRef));
				}
			}
		});
	}

	/**
	 * Sends a JSON-RPC notification.
	 * @param method The method name for the notification
	 * @param params The notification parameters
	 * @return A Mono that completes when the notification is sent
	 */
	@Override
	public Mono<Void> sendNotification(String method, Object params) {
		McpSchema.JSONRPCNotification jsonrpcNotification = new McpSchema.JSONRPCNotification(McpSchema.JSONRPC_VERSION,
				method, params);
		return this.transport.sendMessage(jsonrpcNotification);
	}

	/**
	 * Closes the session gracefully, allowing pending operations to complete.
	 * @return A Mono that completes when the session is closed
	 */
    @Override
    public Mono<Void> closeGracefully() {
        return Mono.fromRunnable(this::dismissPendingResponses);
    }

	/**
	 * Closes the session immediately, potentially interrupting pending operations.
	 */
    @Override
    public void close() {
        dismissPendingResponses();
    }

}
