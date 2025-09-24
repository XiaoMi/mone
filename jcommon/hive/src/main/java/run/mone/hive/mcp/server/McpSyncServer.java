package run.mone.hive.mcp.server;

import run.mone.hive.mcp.server.McpServer.PromptRegistration;
import run.mone.hive.mcp.server.McpServer.ResourceRegistration;
import run.mone.hive.mcp.server.McpServer.ToolRegistration;
import run.mone.hive.mcp.server.McpServer.ToolStreamRegistration;
import run.mone.hive.mcp.spec.McpError;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.McpSchema.ClientCapabilities;
import run.mone.hive.mcp.spec.McpSchema.LoggingMessageNotification;
import run.mone.hive.mcp.util.Assert;

/**
 * Synchronous wrapper around {@link McpAsyncServer} that provides blocking operations.
 * This class delegates all operations to an underlying async server instance while
 * providing a synchronous API.
 *
 * @author Christian Tzolov
 */
public class McpSyncServer {

	/**
	 * The async server to wrap.
	 */
	private final McpAsyncServer asyncServer;

	/**
	 * Creates a new synchronous server that wraps the provided async server.
	 * @param asyncServer The async server to wrap
	 */
	public McpSyncServer(McpAsyncServer asyncServer) {
		Assert.notNull(asyncServer, "Async server must not be null");
		this.asyncServer = asyncServer;
	}

	/**
	 * Retrieves the list of all roots provided by the client.
	 * @return The list of roots
	 */
	public McpSchema.ListRootsResult listRoots() {
		return this.listRoots(null);
	}

	/**
	 * Retrieves a paginated list of roots provided by the server.
	 * @param cursor Optional pagination cursor from a previous list request
	 * @return The list of roots
	 */
	public McpSchema.ListRootsResult listRoots(String cursor) {
		return this.asyncServer.listRoots(cursor).block();
	}

	/**
	 * Add a new tool handler.
	 * @param toolHandler The tool handler to add
	 */
	public void addTool(ToolRegistration toolHandler) {
		this.asyncServer.addTool(toolHandler).block();
	}

	public void addStreamTool(ToolStreamRegistration toolHandler) {
		this.asyncServer.addStreamTool(toolHandler).block();
	}

	/**
	 * Remove a tool handler.
	 * @param toolName The name of the tool handler to remove
	 */
	public void removeTool(String toolName) {
		this.asyncServer.removeTool(toolName).block();
	}

	/**
	 * Add a new resource handler.
	 * @param resourceHandler The resource handler to add
	 */
	public void addResource(ResourceRegistration resourceHandler) {
		this.asyncServer.addResource(resourceHandler).block();
	}

	/**
	 * Remove a resource handler.
	 * @param resourceUri The URI of the resource handler to remove
	 */
	public void removeResource(String resourceUri) {
		this.asyncServer.removeResource(resourceUri).block();
	}

	/**
	 * Add a new prompt handler.
	 * @param promptRegistration The prompt registration to add
	 */
	public void addPrompt(PromptRegistration promptRegistration) {
		this.asyncServer.addPrompt(promptRegistration).block();
	}

	/**
	 * Remove a prompt handler.
	 * @param promptName The name of the prompt handler to remove
	 */
	public void removePrompt(String promptName) {
		this.asyncServer.removePrompt(promptName).block();
	}

	/**
	 * Notify clients that the list of available tools has changed.
	 */
	public void notifyToolsListChanged() {
		this.asyncServer.notifyToolsListChanged().block();
	}

	/**
	 * Notify clients that the list of available resources has changed.
	 */
	public void notifyResourcesListChanged() {
		this.asyncServer.notifyResourcesListChanged().block();
	}

	/**
	 * Notify clients that the list of available prompts has changed.
	 */
	public void notifyPromptsListChanged() {
		this.asyncServer.notifyPromptsListChanged().block();
	}

	/**
	 * Send a logging message notification to all clients.
	 * @param loggingMessageNotification The logging message notification to send
	 */
	public void loggingNotification(LoggingMessageNotification loggingMessageNotification) {
		this.asyncServer.loggingNotification(loggingMessageNotification).block();
	}

	/**
	 * Get the server capabilities that define the supported features and functionality.
	 * @return The server capabilities
	 */
	public McpSchema.ServerCapabilities getServerCapabilities() {
		return this.asyncServer.getServerCapabilities();
	}

	/**
	 * Get the server implementation information.
	 * @return The server implementation details
	 */
	public McpSchema.Implementation getServerInfo() {
		return this.asyncServer.getServerInfo();
	}

	/**
	 * Get the client capabilities that define the supported features and functionality.
	 * @return The client capabilities
	 */
	public ClientCapabilities getClientCapabilities() {
		return this.asyncServer.getClientCapabilities();
	}

	/**
	 * Get the client implementation information.
	 * @return The client implementation details
	 */
	public McpSchema.Implementation getClientInfo() {
		return this.asyncServer.getClientInfo();
	}

	/**
	 * Close the server gracefully.
	 */
	public void closeGracefully() {
		this.asyncServer.closeGracefully().block();
	}

	/**
	 * Close the server immediately.
	 */
	public void close() {
		this.asyncServer.close();
	}

	/**
	 * Get the underlying async server instance.
	 * @return The wrapped async server
	 */
	public McpAsyncServer getAsyncServer() {
		return this.asyncServer;
	}

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
	public McpSchema.CreateMessageResult createMessage(McpSchema.CreateMessageRequest createMessageRequest) {
		return this.asyncServer.createMessage(createMessageRequest).block();
	}

}
