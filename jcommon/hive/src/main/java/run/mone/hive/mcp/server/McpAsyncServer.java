package run.mone.hive.mcp.server;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import run.mone.hive.mcp.grpc.CallToolRequest;
import run.mone.hive.mcp.server.McpServer.PromptRegistration;
import run.mone.hive.mcp.server.McpServer.ResourceRegistration;
import run.mone.hive.mcp.server.McpServer.ToolRegistration;
import run.mone.hive.mcp.server.McpServer.ToolStreamRegistration;
import run.mone.hive.mcp.spec.DefaultMcpSession;
import run.mone.hive.mcp.spec.DefaultMcpSession.NotificationHandler;
import run.mone.hive.mcp.spec.McpError;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.McpSchema.ClientCapabilities;
import run.mone.hive.mcp.spec.McpSchema.LoggingLevel;
import run.mone.hive.mcp.spec.McpSchema.LoggingMessageNotification;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;
import run.mone.hive.mcp.util.Utils;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 
 * ORIGINAL CODE IS FROM SPRING AI!!!
 * 
 * The Model Context Protocol (MCP) server implementation that provides asynchronous
 * communication.
 *
 */
public class McpAsyncServer {

	private final static Logger logger = LoggerFactory.getLogger(McpAsyncServer.class);

	/**
	 * The MCP session implementation that manages bidirectional JSON-RPC communication
	 * between clients and servers.
	 */
	@Getter
	private final DefaultMcpSession mcpSession;

	private final ServerMcpTransport transport;

	private final McpSchema.ServerCapabilities serverCapabilities;

	private final McpSchema.Implementation serverInfo;

	private McpSchema.ClientCapabilities clientCapabilities;

	private McpSchema.Implementation clientInfo;

	/**
	 * Thread-safe list of tool handlers that can be modified at runtime.
	 */
	@Getter
	private final CopyOnWriteArrayList<ToolRegistration> tools;

	@Getter
	private final CopyOnWriteArrayList<ToolStreamRegistration> streamTools;

	private final CopyOnWriteArrayList<McpSchema.ResourceTemplate> resourceTemplates;

	private final ConcurrentHashMap<String, ResourceRegistration> resources;

	private final ConcurrentHashMap<String, PromptRegistration> prompts;

	private LoggingLevel minLoggingLevel = LoggingLevel.DEBUG;

	/**
	 * Create a new McpAsyncServer with the given transport and capabilities.
	 * @param mcpTransport The transport layer implementation for MCP communication
	 * @param serverInfo The server implementation details
	 * @param serverCapabilities The server capabilities
	 * @param tools The list of tool registrations
	 * @param resources The map of resource registrations
	 * @param resourceTemplates The list of resource templates
	 * @param prompts The map of prompt registrations
	 * @param rootsChangeConsumers The list of consumers that will be notified when the
	 * roots list changes
	 */
	public McpAsyncServer(ServerMcpTransport mcpTransport, McpSchema.Implementation serverInfo,
			McpSchema.ServerCapabilities serverCapabilities, List<ToolRegistration> tools,
			Map<String, ResourceRegistration> resources, List<McpSchema.ResourceTemplate> resourceTemplates,
			Map<String, PromptRegistration> prompts, List<Consumer<List<McpSchema.Root>>> rootsChangeConsumers) {

		this.serverInfo = serverInfo;
		this.tools = new CopyOnWriteArrayList<>(tools != null ? tools : List.of());
		this.streamTools = new CopyOnWriteArrayList<>();
		this.resources = !Utils.isEmpty(resources) ? new ConcurrentHashMap<>(resources) : new ConcurrentHashMap<>();
		this.resourceTemplates = !Utils.isEmpty(resourceTemplates) ? new CopyOnWriteArrayList<>(resourceTemplates)
				: new CopyOnWriteArrayList<>();
		this.prompts = !Utils.isEmpty(prompts) ? new ConcurrentHashMap<>(prompts) : new ConcurrentHashMap<>();

		this.serverCapabilities = (serverCapabilities != null) ? serverCapabilities : new McpSchema.ServerCapabilities(
				null, // experimental
				new McpSchema.ServerCapabilities.LoggingCapabilities(), // Enable logging
																		// by default
				!Utils.isEmpty(this.prompts) ? new McpSchema.ServerCapabilities.PromptCapabilities(false) : null,
				!Utils.isEmpty(this.resources) ? new McpSchema.ServerCapabilities.ResourceCapabilities(false, false)
						: null,
				!Utils.isEmpty(this.tools) ? new McpSchema.ServerCapabilities.ToolCapabilities(false) : null);

		Map<String, DefaultMcpSession.RequestHandler> requestHandlers = new HashMap<>();
		
		// Initialize request handlers for standard MCP methods
		requestHandlers.put(McpSchema.METHOD_INITIALIZE, initializeRequestHandler());

		// Ping MUST respond with an empty data, but not NULL response.
		requestHandlers.put(McpSchema.METHOD_PING, (params) -> Mono.<Object>just("pong"));

		// Add tools API handlers if the tool capability is enabled
		if (this.serverCapabilities.tools() != null) {
			requestHandlers.put(McpSchema.METHOD_TOOLS_LIST, toolsListRequestHandler());
			requestHandlers.put(McpSchema.METHOD_TOOLS_CALL, toolsCallRequestHandler());
		}

		// Add resources API handlers if provided
		if (!Utils.isEmpty(this.resources)) {
			requestHandlers.put(McpSchema.METHOD_RESOURCES_LIST, resourcesListRequestHandler());
			requestHandlers.put(McpSchema.METHOD_RESOURCES_READ, resourcesReadRequestHandler());
		}

		// Add resource templates API handlers if provided.
		if (!Utils.isEmpty(this.resourceTemplates)) {
			requestHandlers.put(McpSchema.METHOD_RESOURCES_TEMPLATES_LIST, resourceTemplateListRequestHandler());
		}

		// Add prompts API handlers if provider exists
		if (!Utils.isEmpty(this.prompts)) {
			requestHandlers.put(McpSchema.METHOD_PROMPT_LIST, promptsListRequestHandler());
			requestHandlers.put(McpSchema.METHOD_PROMPT_GET, promptsGetRequestHandler());
		}

		// Add logging API handlers if the logging capability is enabled
		if (this.serverCapabilities.logging() != null) {
			requestHandlers.put(McpSchema.METHOD_LOGGING_SET_LEVEL, setLoggerRequestHandler());
		}

		Map<String, DefaultMcpSession.StreamRequestHandler> streamRequestHandlers = new HashMap<>();

		streamRequestHandlers.put(McpSchema.METHOD_TOOLS_STREAM, toolsStreamRequestHandler());

		Map<String, NotificationHandler> notificationHandlers = new HashMap<>();

		notificationHandlers.put(McpSchema.METHOD_NOTIFICATION_INITIALIZED, (params) -> Mono.empty());

		if (Utils.isEmpty(rootsChangeConsumers)) {
			rootsChangeConsumers = List.of((roots) -> logger
				.warn("Roots list changed notification, but no consumers provided. Roots list changed: {}", roots));
		}
		notificationHandlers.put(McpSchema.METHOD_NOTIFICATION_ROOTS_LIST_CHANGED,
				rootsListChnagedNotificationHandler(rootsChangeConsumers));

		this.transport = mcpTransport;
		this.mcpSession = new DefaultMcpSession(Duration.ofSeconds(10), mcpTransport, requestHandlers,
				streamRequestHandlers, notificationHandlers);
	}

	// ---------------------------------------
	// Lifecycle Management
	// ---------------------------------------
	private DefaultMcpSession.RequestHandler initializeRequestHandler() {
		return params -> {
			McpSchema.InitializeRequest initializeRequest = transport.unmarshalFrom(params,
					new TypeReference<McpSchema.InitializeRequest>() {
					});

			this.clientCapabilities = initializeRequest.capabilities();
			this.clientInfo = initializeRequest.clientInfo();

			logger.info("Client initialize request - Protocol: {}, Capabilities: {}, Info: {}",
					initializeRequest.protocolVersion(), initializeRequest.capabilities(),
					initializeRequest.clientInfo());

			if (!McpSchema.LATEST_PROTOCOL_VERSION.equals(initializeRequest.protocolVersion())) {
				return Mono
					.<Object>error(new McpError(
							"Unsupported protocol version from client: " + initializeRequest.protocolVersion()))
					.publishOn(Schedulers.boundedElastic());
			}

			return Mono
				.<Object>just(new McpSchema.InitializeResult(McpSchema.LATEST_PROTOCOL_VERSION, this.serverCapabilities,
						this.serverInfo, null))
				.publishOn(Schedulers.boundedElastic());
		};
	}

	/**
	 * Get the server capabilities that define the supported features and functionality.
	 * @return The server capabilities
	 */
	public McpSchema.ServerCapabilities getServerCapabilities() {
		return this.serverCapabilities;
	}

	/**
	 * Get the server implementation information.
	 * @return The server implementation details
	 */
	public McpSchema.Implementation getServerInfo() {
		return this.serverInfo;
	}

	/**
	 * Get the client capabilities that define the supported features and functionality.
	 * @return The client capabilities
	 */
	public ClientCapabilities getClientCapabilities() {
		return this.clientCapabilities;
	}

	/**
	 * Get the client implementation information.
	 * @return The client implementation details
	 */
	public McpSchema.Implementation getClientInfo() {
		return this.clientInfo;
	}

	/**
	 * Gracefully closes the server, allowing any in-progress operations to complete.
	 * @return A Mono that completes when the server has been closed
	 */
	public Mono<Void> closeGracefully() {
		return this.mcpSession.closeGracefully();
	}

	/**
	 * Close the server immediately.
	 */
	public void close() {
		this.mcpSession.close();
	}

	private static TypeReference<McpSchema.ListRootsResult> LIST_ROOTS_RESULT_TYPE_REF = new TypeReference<>() {
	};

	/**
	 * Retrieves the list of all roots provided by the client.
	 * @return A Mono that emits the list of roots result.
	 */
	public Mono<McpSchema.ListRootsResult> listRoots() {
		return this.listRoots(null);
	}

	/**
	 * Retrieves a paginated list of roots provided by the server.
	 * @param cursor Optional pagination cursor from a previous list request
	 * @return A Mono that emits the list of roots result containing
	 */
	public Mono<McpSchema.ListRootsResult> listRoots(String cursor) {
		return this.mcpSession.sendRequest(McpSchema.METHOD_ROOTS_LIST, new McpSchema.PaginatedRequest(cursor),
				LIST_ROOTS_RESULT_TYPE_REF);
	}

	private NotificationHandler rootsListChnagedNotificationHandler(
			List<Consumer<List<McpSchema.Root>>> rootsChangeConsumers) {

		return params -> {
			return listRoots().flatMap(listRootsResult -> Mono.fromRunnable(() -> {
				rootsChangeConsumers.stream().forEach(consumer -> consumer.accept(listRootsResult.roots()));
			}).subscribeOn(Schedulers.boundedElastic())).onErrorResume(error -> {
				logger.error("Error handling roots list change notification", error);
				return Mono.empty();
			}).then();
		};
	};

	// ---------------------------------------
	// Tool Management
	// ---------------------------------------

	/**
	 * Add a new tool registration at runtime.
	 * @param toolRegistration The tool registration to add
	 * @return Mono that completes when clients have been notified of the change
	 */
	public Mono<Void> addTool(ToolRegistration toolRegistration) {
		if (toolRegistration == null) {
			return Mono.error(new McpError("Tool registration must not be null"));
		}
		if (toolRegistration.tool() == null) {
			return Mono.error(new McpError("Tool must not be null"));
		}
		if (toolRegistration.call() == null) {
			return Mono.error(new McpError("Tool call handler must not be null"));
		}
		if (this.serverCapabilities.tools() == null) {
			return Mono.error(new McpError("Server must be configured with tool capabilities"));
		}

		// Check for duplicate tool names
		if (this.tools.stream().anyMatch(th -> th.tool().name().equals(toolRegistration.tool().name()))) {
			return Mono.error(new McpError("Tool with name '" + toolRegistration.tool().name() + "' already exists"));
		}

		this.tools.add(toolRegistration);
		logger.info("Added tool handler: {}", toolRegistration.tool().name());
		if (this.serverCapabilities.tools().listChanged()) {
			return notifyToolsListChanged();
		}
		return Mono.empty();
	}

	public Mono<Void> addStreamTool(ToolStreamRegistration toolRegistration) {
		if (toolRegistration == null) {
			return Mono.error(new McpError("Tool registration must not be null"));
		}
		if (toolRegistration.tool() == null) {
			return Mono.error(new McpError("Tool must not be null"));
		}
		if (toolRegistration.call() == null) {
			return Mono.error(new McpError("Tool call handler must not be null"));
		}
		if (this.serverCapabilities.tools() == null) {
			return Mono.error(new McpError("Server must be configured with tool capabilities"));
		}

		// Check for duplicate tool names
		if (this.streamTools.stream().anyMatch(th -> th.tool().name().equals(toolRegistration.tool().name()))) {
			return Mono.error(new McpError("Tool with name '" + toolRegistration.tool().name() + "' already exists"));
		}

		this.streamTools.add(toolRegistration);
		logger.info("Added tool handler: {}", toolRegistration.tool().name());
		if (this.serverCapabilities.tools().listChanged()) {
			return notifyToolsListChanged();
		}
		return Mono.empty();
	}
	

	/**
	 * Remove a tool handler at runtime.
	 * @param toolName The name of the tool handler to remove
	 * @return Mono that completes when clients have been notified of the change
	 */
	public Mono<Void> removeTool(String toolName) {
		if (toolName == null) {
			return Mono.error(new McpError("Tool name must not be null"));
		}
		if (this.serverCapabilities.tools() == null) {
			return Mono.error(new McpError("Server must be configured with tool capabilities"));
		}

		boolean removed = this.tools.removeIf(toolRegistration -> toolRegistration.tool().name().equals(toolName));
		if (removed) {
			logger.info("Removed tool handler: {}", toolName);
			if (this.serverCapabilities.tools().listChanged()) {
				return notifyToolsListChanged();
			}
			return Mono.empty();
		}
		return Mono.error(new McpError("Tool with name '" + toolName + "' not found"));
	}

	/**
	 * Notifies clients that the list of available tools has changed.
	 * @return A Mono that completes when all clients have been notified
	 */
	public Mono<Void> notifyToolsListChanged() {
		return this.mcpSession.sendNotification(McpSchema.METHOD_NOTIFICATION_TOOLS_LIST_CHANGED, null);
	}

	private DefaultMcpSession.RequestHandler toolsListRequestHandler() {
		return params -> {

			List<Tool> toolsRes = new ArrayList<>();

			List<Tool> tools = this.tools.stream().map(toolRegistration -> toolRegistration.tool()).toList();
			List<Tool> streamTools = this.streamTools.stream().map(toolRegistration -> toolRegistration.tool()).toList();

			toolsRes.addAll(tools);
			toolsRes.addAll(streamTools);

			return Mono.just(new McpSchema.ListToolsResult(toolsRes, null));
		};
	}

	private DefaultMcpSession.RequestHandler toolsCallRequestHandler() {
		// TODO: handle tool call request
		return params -> {
			//grpc
			if (params instanceof CallToolRequest ctr) {
				Optional<McpServer.ToolRegistration> toolRegistration = this.tools.stream()
						.filter(tr -> ctr.getMethod().equals(tr.tool().name()))
						.findAny();

				if (toolRegistration.isEmpty()) {
					return Mono.error(new McpError("Tool not found: " + ctr.getName()));
				}

				Map<String, Object> objectMap = ctr.getArgumentsMap().entrySet().stream()
						.collect(Collectors.toMap(
								Map.Entry::getKey,
								Map.Entry::getValue
						));

				return Mono.fromCallable(() -> toolRegistration.get().call().apply(objectMap))
						.map(result -> (Object) result)
						.subscribeOn(Schedulers.boundedElastic());
			}

			//sse stido
			McpSchema.CallToolRequest callToolRequest = transport.unmarshalFrom(params,
					new TypeReference<McpSchema.CallToolRequest>() {
					});

			Optional<ToolRegistration> toolRegistration = this.tools.stream()
				.filter(tr -> callToolRequest.name().equals(tr.tool().name()))
				.findAny();

			if (toolRegistration.isEmpty()) {
				return Mono.<Object>error(new McpError("Tool not found: " + callToolRequest.name()));
			}

			return Mono.fromCallable(() -> toolRegistration.get().call().apply(callToolRequest.arguments()))
				.map(result -> (Object) result)
				.subscribeOn(Schedulers.boundedElastic());
		};
	}

	private DefaultMcpSession.StreamRequestHandler toolsStreamRequestHandler() {
		return params -> {
			//logger.info("Received tools stream request: {}", params);

			//grpc
			if (params instanceof CallToolRequest ctr) {
				Optional<McpServer.ToolStreamRegistration> toolRegistration = this.streamTools.stream()
						.filter(tr -> ctr.getMethod().equals(tr.tool().name()))
						.findAny();

				if (toolRegistration.isEmpty()) {
					return Flux.error(new McpError("Tool not found: " + ctr.getMethod()));
				}

				McpServer.ToolStreamRegistration tool = toolRegistration.get();

				logger.info("Handling tools stream request with tool: {}", tool);

				Map<String, Object> objectMap = ctr.getArgumentsMap().entrySet().stream()
						.collect(Collectors.toMap(
								Map.Entry::getKey,
								Map.Entry::getValue
						));

				return Flux.from(tool.call()
						.apply(objectMap)
						.subscribeOn(Schedulers.boundedElastic()));
			}

			//sse
			// this is where we handle tools stream request
			McpSchema.CallToolRequest callToolRequest = transport.unmarshalFrom(params,
					new TypeReference<McpSchema.CallToolRequest>() {
					});
			
			Optional<ToolStreamRegistration> toolRegistration = this.streamTools.stream()
				.filter(tr -> callToolRequest.name().equals(tr.tool().name()))
				.findAny();

			if (toolRegistration.isEmpty()) {
				return Flux.error(new McpError("Tool not found: " + callToolRequest.name()));
			}	

			ToolStreamRegistration tool = toolRegistration.get();

			logger.info("Handling tools stream request with tool: {}", tool);

			return Flux.from(tool.call()
				.apply(callToolRequest.arguments())
				.subscribeOn(Schedulers.boundedElastic()));
		};
	}

	// ---------------------------------------
	// Resource Management
	// ---------------------------------------

	/**
	 * Add a new resource handler at runtime.
	 * @param resourceHandler The resource handler to add
	 * @return Mono that completes when clients have been notified of the change
	 */
	public Mono<Void> addResource(ResourceRegistration resourceHandler) {
		if (resourceHandler == null || resourceHandler.resource() == null) {
			return Mono.error(new McpError("Resource must not be null"));
		}

		if (this.serverCapabilities.resources() == null) {
			return Mono.error(new McpError("Server must be configured with resource capabilities"));
		}

		if (this.resources.containsKey(resourceHandler.resource().uri())) {
			return Mono
				.error(new McpError("Resource with URI '" + resourceHandler.resource().uri() + "' already exists"));
		}

		this.resources.put(resourceHandler.resource().uri(), resourceHandler);
		logger.info("Added resource handler: {}", resourceHandler.resource().uri());
		if (this.serverCapabilities.resources().listChanged()) {
			return notifyResourcesListChanged();
		}
		return Mono.empty();
	}

	/**
	 * Remove a resource handler at runtime.
	 * @param resourceUri The URI of the resource handler to remove
	 * @return Mono that completes when clients have been notified of the change
	 */
	public Mono<Void> removeResource(String resourceUri) {
		if (resourceUri == null) {
			return Mono.error(new McpError("Resource URI must not be null"));
		}
		if (this.serverCapabilities.resources() == null) {
			return Mono.error(new McpError("Server must be configured with resource capabilities"));
		}

		ResourceRegistration removed = this.resources.remove(resourceUri);
		if (removed != null) {
			logger.info("Removed resource handler: {}", resourceUri);
			if (this.serverCapabilities.resources().listChanged()) {
				return notifyResourcesListChanged();
			}
			return Mono.empty();
		}
		return Mono.error(new McpError("Resource with URI '" + resourceUri + "' not found"));
	}

	/**
	 * Notifies clients that the list of available resources has changed.
	 * @return A Mono that completes when all clients have been notified
	 */
	public Mono<Void> notifyResourcesListChanged() {
		return this.mcpSession.sendNotification(McpSchema.METHOD_NOTIFICATION_RESOURCES_LIST_CHANGED, null);
	}

	private DefaultMcpSession.RequestHandler resourcesListRequestHandler() {
		return params -> {
			var resourceList = this.resources.values().stream().map(ResourceRegistration::resource).toList();
			return Mono.just(new McpSchema.ListResourcesResult(resourceList, null));
		};
	}

	private DefaultMcpSession.RequestHandler resourceTemplateListRequestHandler() {
		return params -> Mono.just(new McpSchema.ListResourceTemplatesResult(this.resourceTemplates, null));

	}

	private DefaultMcpSession.RequestHandler resourcesReadRequestHandler() {
		return params -> {
			McpSchema.ReadResourceRequest resourceRequest = transport.unmarshalFrom(params,
					new TypeReference<McpSchema.ReadResourceRequest>() {
					});
			var resourceUri = resourceRequest.uri();
			if (this.resources.containsKey(resourceUri)) {
				return Mono.fromCallable(() -> this.resources.get(resourceUri).readHandler().apply(resourceRequest))
					.map(result -> (Object) result)
					.subscribeOn(Schedulers.boundedElastic());
			}
			return Mono.error(new McpError("Resource not found: " + resourceUri));
		};
	}

	// ---------------------------------------
	// Prompt Management
	// ---------------------------------------

	/**
	 * Add a new prompt handler at runtime.
	 * @param promptRegistration The prompt handler to add
	 * @return Mono that completes when clients have been notified of the change
	 */
	public Mono<Void> addPrompt(PromptRegistration promptRegistration) {
		if (promptRegistration == null) {
			return Mono.error(new McpError("Prompt registration must not be null"));
		}
		if (this.serverCapabilities.prompts() == null) {
			return Mono.error(new McpError("Server must be configured with prompt capabilities"));
		}

		if (this.prompts.containsKey(promptRegistration.prompt().name())) {
			return Mono
				.error(new McpError("Prompt with name '" + promptRegistration.prompt().name() + "' already exists"));
		}

		this.prompts.put(promptRegistration.prompt().name(), promptRegistration);

		logger.info("Added prompt handler: {}", promptRegistration.prompt().name());

		// Servers that declared the listChanged capability SHOULD send a notification,
		// when the list of available prompts changes
		if (this.serverCapabilities.prompts().listChanged()) {
			return notifyPromptsListChanged();
		}
		return Mono.empty();
	}

	/**
	 * Remove a prompt handler at runtime.
	 * @param promptName The name of the prompt handler to remove
	 * @return Mono that completes when clients have been notified of the change
	 */
	public Mono<Void> removePrompt(String promptName) {
		if (promptName == null) {
			return Mono.error(new McpError("Prompt name must not be null"));
		}
		if (this.serverCapabilities.prompts() == null) {
			return Mono.error(new McpError("Server must be configured with prompt capabilities"));
		}

		PromptRegistration removed = this.prompts.remove(promptName);

		if (removed != null) {
			logger.info("Removed prompt handler: {}", promptName);
			// Servers that declared the listChanged capability SHOULD send a
			// notification, when the list of available prompts changes
			if (this.serverCapabilities.prompts().listChanged()) {
				return this.notifyPromptsListChanged();
			}
			return Mono.empty();
		}
		return Mono.error(new McpError("Prompt with name '" + promptName + "' not found"));
	}

	/**
	 * Notifies clients that the list of available prompts has changed.
	 * @return A Mono that completes when all clients have been notified
	 */
	public Mono<Void> notifyPromptsListChanged() {
		return this.mcpSession.sendNotification(McpSchema.METHOD_NOTIFICATION_PROMPTS_LIST_CHANGED, null);
	}

	private DefaultMcpSession.RequestHandler promptsListRequestHandler() {
		return params -> {
			// TODO: Implement pagination
			// McpSchema.PaginatedRequest request = transport.unmarshalFrom(params,
			// new TypeReference<McpSchema.PaginatedRequest>() {
			// });

			var promptList = this.prompts.values().stream().map(PromptRegistration::prompt).toList();

			return Mono.just(new McpSchema.ListPromptsResult(promptList, null));
		};
	}

	private DefaultMcpSession.RequestHandler promptsGetRequestHandler() {
		return params -> {
			McpSchema.GetPromptRequest promptRequest = transport.unmarshalFrom(params,
					new TypeReference<McpSchema.GetPromptRequest>() {
					});

			// Implement prompt retrieval logic here
			if (this.prompts.containsKey(promptRequest.name())) {
				return Mono
					.fromCallable(() -> this.prompts.get(promptRequest.name()).promptHandler().apply(promptRequest))
					.map(result -> (Object) result)
					.subscribeOn(Schedulers.boundedElastic());
			}

			return Mono.error(new McpError("Prompt not found: " + promptRequest.name()));
		};
	}

	// ---------------------------------------
	// Logging Management
	// ---------------------------------------

	/**
	 * Send a logging message notification to all connected clients. Messages below the
	 * current minimum logging level will be filtered out.
	 * @param loggingMessageNotification The logging message to send
	 * @return A Mono that completes when the notification has been sent
	 */
	public Mono<Void> loggingNotification(LoggingMessageNotification loggingMessageNotification) {

		if (loggingMessageNotification == null) {
			return Mono.error(new McpError("Logging message must not be null"));
		}

		Map<String, Object> params = this.transport.unmarshalFrom(loggingMessageNotification,
				new TypeReference<Map<String, Object>>() {
				});

		if (loggingMessageNotification.level().level() < minLoggingLevel.level()) {
			return Mono.empty();
		}

		return this.mcpSession.sendNotification(McpSchema.METHOD_NOTIFICATION_MESSAGE, params);
	}

	/**
	 * Handles requests to set the minimum logging level. Messages below this level will
	 * not be sent.
	 * @return A handler that processes logging level change requests
	 */
	private DefaultMcpSession.RequestHandler setLoggerRequestHandler() {
		return params -> {
			McpSchema.LoggingLevel setLoggerRequest = transport.unmarshalFrom(params,
					new TypeReference<McpSchema.LoggingLevel>() {
					});

			this.minLoggingLevel = setLoggerRequest;

			return Mono.empty();
		};
	}

	// ---------------------------------------
	// Sampling
	// ---------------------------------------
	private static TypeReference<McpSchema.CreateMessageResult> CREATE_MESSAGE_RESULT_TYPE_REF = new TypeReference<>() {
	};

	/**
	 * Create a new message using the sampling capabilities of the client. The Model
	 * Context Protocol (MCP) provides a standardized way for servers to request LLM
	 * sampling (“completions” or “generations”) from language models via clients. This
	 * flow allows clients to maintain control over model access, selection, and
	 * permissions while enabling servers to leverage AI capabilities—with no server API
	 * keys necessary. Servers can request text or image-based interactions and optionally
	 * include context from MCP servers in their prompts.
	 * @param createMessageRequest The request to create a new message
	 * @return A Mono that completes when the message has been created
	 * @throws McpError if the client has not been initialized or does not support
	 * sampling capabilities
	 * @throws McpError if the client does not support the createMessage method
	 * @see McpSchema.CreateMessageRequest
	 * @see McpSchema.CreateMessageResult
	 * @see <a href=
	 * "https://spec.modelcontextprotocol.io/specification/client/sampling/">Sampling
	 * Specification</a>
	 */
	public Mono<McpSchema.CreateMessageResult> createMessage(McpSchema.CreateMessageRequest createMessageRequest) {

		if (this.clientCapabilities == null) {
			return Mono.error(new McpError("Client must be initialized. Call the initialize method first!"));
		}
		if (this.clientCapabilities.sampling() == null) {
			return Mono.error(new McpError("Client must be configured with sampling capabilities"));
		}
		return this.mcpSession.sendRequest(McpSchema.METHOD_SAMPLING_CREATE_MESSAGE, createMessageRequest,
				CREATE_MESSAGE_RESULT_TYPE_REF);
	}

}
