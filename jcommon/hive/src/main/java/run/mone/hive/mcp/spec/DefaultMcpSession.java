package run.mone.hive.mcp.spec;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;
import run.mone.hive.mcp.transport.webmvcsse.WebMvcSseServerTransport;
import run.mone.hive.mcp.util.Assert;

/**
 * Default implementation of the MCP (Model Context Protocol) session that
 * manages
 * bidirectional JSON-RPC communication between clients and servers. This
 * implementation
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
 */
public class DefaultMcpSession implements McpSession {

	/** Logger for this class */
	private static final Logger logger = LoggerFactory.getLogger(DefaultMcpSession.class);

	/** Duration to wait for request responses before timing out */
	private final Duration requestTimeout;

	/** Transport layer implementation for message exchange */
	private final McpTransport transport;

	/** Map of pending responses keyed by request ID */
	private final ConcurrentHashMap<Object, MonoSink<McpSchema.JSONRPCResponse>> pendingResponses = new ConcurrentHashMap<>();

	/** Map of pending stream responses keyed by request ID */
	private final ConcurrentHashMap<Object, FluxSink<McpSchema.JSONRPCResponse>> pendingStreamResponses = new ConcurrentHashMap<>();

	/** Map of request handlers keyed by method name */
	private final ConcurrentHashMap<String, RequestHandler> requestHandlers = new ConcurrentHashMap<>();

	private final ConcurrentHashMap<String, StreamRequestHandler> streamRequestHandlers = new ConcurrentHashMap<>();

	/** Map of notification handlers keyed by method name */
	private final ConcurrentHashMap<String, NotificationHandler> notificationHandlers = new ConcurrentHashMap<>();

	/** Session-specific prefix for request IDs */
	private final String sessionPrefix = UUID.randomUUID().toString().substring(0, 8);

	/** Atomic counter for generating unique request IDs */
	private final AtomicLong requestCounter = new AtomicLong(0);

	private final Disposable connection;
	/**
	 * Functional interface for handling incoming JSON-RPC requests. Implementations
	 * should process the request parameters and return a response.
	 */
	@FunctionalInterface
	public interface RequestHandler {

		/**
		 * Handles an incoming request with the given parameters.
		 * 
		 * @param params The request parameters
		 * @return A Mono containing the response object
		 */
		Mono<Object> handle(Object params);

	}

	@FunctionalInterface
	public interface StreamRequestHandler {

		/**
		* Handles an incoming stream request with the given parameters.
		* @param params The request parameters
		* @return A Flux containing the response object
		*/
		Flux<Object> handle(Object params);

	}

	/**
	 * Functional interface for handling incoming JSON-RPC notifications.
	 * Implementations
	 * should process the notification parameters without returning a response.
	 */
	@FunctionalInterface
	public interface NotificationHandler {

		/**
		 * Handles an incoming notification with the given parameters.
		 * 
		 * @param params The notification parameters
		 * @return A Mono that completes when the notification is processed
		 */
		Mono<Void> handle(Object params);

	}

	/**
	 * Creates a new DefaultMcpSession with the specified configuration and
	 * handlers.
	 * 
	 * @param requestTimeout       Duration to wait for responses
	 * @param transport            Transport implementation for message exchange
	 * @param requestHandlers      Map of method names to request handlers
	 * @param notificationHandlers Map of method names to notification handlers
	 */
	public DefaultMcpSession(Duration requestTimeout, McpTransport transport,
			Map<String, RequestHandler> requestHandlers,
			Map<String, StreamRequestHandler> streamRequestHandlers,
			Map<String, NotificationHandler> notificationHandlers) {

		Assert.notNull(requestTimeout, "The requstTimeout can not be null");
		Assert.notNull(transport, "The transport can not be null");
		Assert.notNull(requestHandlers, "The requestHandlers can not be null");
		Assert.notNull(notificationHandlers, "The notificationHandlers can not be null");

		this.requestTimeout = requestTimeout;
		this.transport = transport;
		this.requestHandlers.putAll(requestHandlers);
		this.streamRequestHandlers.putAll(streamRequestHandlers);
		this.notificationHandlers.putAll(notificationHandlers);

		// TODO: consider mono.transformDeferredContextual where the Context contains
		// the
		// Observation associated with the individual message - it can be used to
		// create child Observation and emit it together with the message to the
		// consumer
		this.connection = this.transport.connect(mono -> mono.doOnNext(message -> {
			if (message instanceof McpSchema.JSONRPCResponse response) {
				logger.info("Received Response: {}", response);
				var sink = pendingResponses.remove(response.id());
				var streamSink = pendingStreamResponses.get(response.id());
				if (sink == null && streamSink == null) {
					logger.warn("Unexpected response for unknown id {}", response.id());
				} else {
					if (sink != null) {
						sink.success(response);
					}
					if (streamSink != null) {
						// TODO: complete the sink at last msg
						logger.info("Received stream response: {}", response);
						streamSink.next(response);
					}
				}
			} else if (message instanceof McpSchema.JSONRPCRequest request) {
				logger.info("Received request: {}", request);
				handleIncomingRequest(request).subscribe(response -> transport.sendMessage(response).subscribe(),
						error -> {
							var errorResponse = new McpSchema.JSONRPCResponse(McpSchema.JSONRPC_VERSION,
									request.id(),
									null, new McpSchema.JSONRPCResponse.JSONRPCError(
											McpSchema.ErrorCodes.INTERNAL_ERROR, error.getMessage(), null));
							transport.sendMessage(errorResponse).subscribe();
						});
			} else if (message instanceof McpSchema.JSONRPCNotification notification) {
				logger.info("Received notification: {}", notification);
				handleIncomingNotification(notification).subscribe(null,
						error -> logger.error("Error handling notification: {}", error.getMessage()));
			}
		})).subscribe();

		if (this.transport instanceof WebMvcSseServerTransport) {
			((WebMvcSseServerTransport) this.transport).connectStream(req -> {
				return handleToolsStreamRequest(req);
			});
		}

	}

	private Flux<McpSchema.JSONRPCResponse> handleToolsStreamRequest(McpSchema.JSONRPCRequest request) {
		logger.info("Received tools stream request: {}", request);
		return Flux.defer(() -> {
			logger.info("Handling tools stream request: {}", request);
			var handler = this.streamRequestHandlers.get(request.method());
			if (handler == null) {
				logger.error("No handler registered for stream request method: {}", request.method());
				return Flux.error(new McpError("No handler registered for stream request method: " + request.method()));
			} else {
				return handler.handle(request.params())
					.map(response -> new McpSchema.JSONRPCResponse(McpSchema.JSONRPC_VERSION, request.id(), response, null))
					.onErrorResume(error -> Flux.error(new McpError("Error handling tools stream request: " + error.getMessage())));
			}
		});
	}

	/**
	 * Handles an incoming JSON-RPC request by routing it to the appropriate
	 * handler.
	 * 
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
					.map(result -> new McpSchema.JSONRPCResponse(McpSchema.JSONRPC_VERSION, request.id(), result, null))
					.onErrorResume(error -> Mono.just(new McpSchema.JSONRPCResponse(McpSchema.JSONRPC_VERSION,
							request.id(),
							null, new McpSchema.JSONRPCResponse.JSONRPCError(McpSchema.ErrorCodes.INTERNAL_ERROR,
									error.getMessage(), null)))); // TODO: add error message through the data field
		});
	}

	record MethodNotFoundError(String method, String message, Object data) {
	}

	public static MethodNotFoundError getMethodNotFoundError(String method) {
		switch (method) {
			case McpSchema.METHOD_ROOTS_LIST:
				return new MethodNotFoundError(method, "Roots not supported",
						Map.of("reason", "Client does not have roots capability"));
			default:
				return new MethodNotFoundError(method, "Method not found: " + method, null);
		}
	}

	/**
	 * Handles an incoming JSON-RPC notification by routing it to the appropriate
	 * handler.
	 * 
	 * @param notification The incoming JSON-RPC notification
	 * @return A Mono that completes when the notification is processed
	 */
	private Mono<Void> handleIncomingNotification(McpSchema.JSONRPCNotification notification) {
		return Mono.defer(() -> {
			var handler = notificationHandlers.get(notification.method());
			if (handler == null) {
				logger.error("No handler registered for notification method: {}", notification.method());
				return Mono.empty();
			}
			return handler.handle(notification.params());
		});
	}

	/**
	 * Generates a unique request ID in a non-blocking way. Combines a
	 * session-specific
	 * prefix with an atomic counter to ensure uniqueness.
	 * 
	 * @return A unique request ID string
	 */
	private String generateRequestId() {
		return this.sessionPrefix + "-" + this.requestCounter.getAndIncrement();
	}

	/**
	 * Sends a JSON-RPC request and returns the response.
	 * 
	 * @param <T>           The expected response type
	 * @param method        The method name to call
	 * @param requestParams The request parameters
	 * @param typeRef       Type reference for response deserialization
	 * @return A Mono containing the response
	 */
	@Override
	public <T> Mono<T> sendRequest(String method, Object requestParams, TypeReference<T> typeRef) {
		String requestId = this.generateRequestId();

		return Mono.<McpSchema.JSONRPCResponse>create(sink -> {
			this.pendingResponses.put(requestId, sink);
			McpSchema.JSONRPCRequest jsonrpcRequest = new McpSchema.JSONRPCRequest(McpSchema.JSONRPC_VERSION, method,
					requestId, requestParams);
			this.transport.sendMessage(jsonrpcRequest)
					// TODO: It's most efficient to create a dedicated Subscriber here
					.subscribe(v -> {
					}, error -> {
						this.pendingResponses.remove(requestId);
						sink.error(error);
					});
		}).timeout(this.requestTimeout).handle((jsonRpcResponse, sink) -> {
			if (jsonRpcResponse.error() != null) {
				sink.error(new McpError(jsonRpcResponse.error()));
			} else {
				if (typeRef.getType().equals(Void.class)) {
					sink.complete();
				} else {
					sink.next(this.transport.unmarshalFrom(jsonRpcResponse.result(), typeRef));
				}
			}
		});
	}

	/**
	 * Sends a JSON-RPC request and returns a stream of responses.
	 * 
	 * @param <T>           The expected response type
	 * @param method        The method name to call
	 * @param requestParams The request parameters
	 * @param typeRef       Type reference for response deserialization
	 * @return A Flux containing the response
	 */
	@Override
	public <T> Flux<T> sendRequestStream(String method, Object requestParams, TypeReference<T> typeRef) {
		String requestId = this.generateRequestId();
		
		return Flux.<McpSchema.JSONRPCResponse>create(sink -> {
			this.pendingStreamResponses.put(requestId, sink);
			McpSchema.JSONRPCRequest jsonrpcRequest = new McpSchema.JSONRPCRequest(McpSchema.JSONRPC_VERSION, method,
					requestId, requestParams);
			
			this.transport.sendMessage(jsonrpcRequest)
				.subscribe(v -> {
					// 预期很快到这里, 因为stream是异步的
				}, error -> {
					// 处理错误
					sink.error(error);
				});
		}).map(response -> this.transport.unmarshalFrom(response.result(), typeRef))
		.doOnCancel(() -> {
			// 处理取消逻辑
			this.pendingStreamResponses.remove(requestId);
		});
	}

	/**
	 * Sends a JSON-RPC notification.
	 * 
	 * @param method The method name for the notification
	 * @param params The notification parameters
	 * @return A Mono that completes when the notification is sent
	 */
	@Override
	public Mono<Void> sendNotification(String method, Map<String, Object> params) {
		McpSchema.JSONRPCNotification jsonrpcNotification = new McpSchema.JSONRPCNotification(McpSchema.JSONRPC_VERSION,
				method, params);
		return this.transport.sendMessage(jsonrpcNotification);
	}

	/**
	 * Closes the session gracefully, allowing pending operations to complete.
	 * 
	 * @return A Mono that completes when the session is closed
	 */
	@Override
	public Mono<Void> closeGracefully() {
		this.connection.dispose();
		return transport.closeGracefully();
	}

	/**
	 * Closes the session immediately, potentially interrupting pending operations.
	 */
	@Override
	public void close() {
		this.connection.dispose();
		transport.close();
	}

}
