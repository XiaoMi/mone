package run.mone.hive.shannon.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import run.mone.hive.shannon.transport.Transport;
import run.mone.hive.shannon.types.permissions.PermissionMode;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Control protocol for bidirectional communication with Claude Code CLI.
 * Handles control requests (interrupt, setModel, etc.) and responses.
 */
public class ControlProtocol {

    private static final Logger logger = LoggerFactory.getLogger(ControlProtocol.class);
    private static final long INITIALIZE_TIMEOUT_MS = 60000; // 60 seconds for initialization

    private final Transport transport;
    private final CorrelationManager correlationManager;

    public ControlProtocol(Transport transport) {
        this.transport = transport;
        this.correlationManager = new CorrelationManager();
    }

    /**
     * Send an initialization message to the CLI.
     *
     * @param hooks hook configurations (optional)
     * @param sdkMcpServers SDK MCP server configurations (optional)
     * @return a future that completes when initialization is acknowledged
     */
    public CompletableFuture<ControlMessage> initialize(
        Map<String, Object> hooks,
        Map<String, Object> sdkMcpServers
    ) {
        Map<String, Object> params = new HashMap<>();
        if (hooks != null) {
            params.put("hooks", hooks);
        }
        if (sdkMcpServers != null) {
            params.put("sdk_mcp_servers", sdkMcpServers);
        }

        return sendRequest("initialize", params, INITIALIZE_TIMEOUT_MS);
    }

    /**
     * Interrupt the current execution.
     *
     * @return a future that completes when the interrupt is acknowledged
     */
    public CompletableFuture<ControlMessage> interrupt() {
        return sendRequest("interrupt", null, 0);
    }

    /**
     * Set the permission mode.
     *
     * @param mode the permission mode to set
     * @return a future that completes when the mode is set
     */
    public CompletableFuture<ControlMessage> setPermissionMode(PermissionMode mode) {
        Map<String, Object> params = new HashMap<>();
        params.put("mode", mode.getValue());
        return sendRequest("set_permission_mode", params, 0);
    }

    /**
     * Set the model.
     *
     * @param model the model to use (e.g., "sonnet", "opus", "haiku")
     * @return a future that completes when the model is set
     */
    public CompletableFuture<ControlMessage> setModel(String model) {
        Map<String, Object> params = new HashMap<>();
        params.put("model", model);
        return sendRequest("set_model", params, 0);
    }

    /**
     * Rewind files to a specific message checkpoint.
     *
     * @param messageId the message ID to rewind to
     * @return a future that completes when files are rewound
     */
    public CompletableFuture<ControlMessage> rewindFiles(String messageId) {
        Map<String, Object> params = new HashMap<>();
        params.put("message_id", messageId);
        return sendRequest("rewind_files", params, 0);
    }

    /**
     * Send a control request.
     *
     * @param action the action to perform
     * @param params parameters for the action (optional)
     * @param timeoutMs timeout in milliseconds (0 for default)
     * @return a future that completes with the response
     */
    public CompletableFuture<ControlMessage> sendRequest(
        String action,
        Map<String, Object> params,
        long timeoutMs
    ) {
        long requestId = correlationManager.nextRequestId();

        ControlMessage request = ControlMessage.request(requestId, action, params);

        CompletableFuture<ControlMessage> responseFuture = timeoutMs > 0
            ? correlationManager.registerRequest(requestId, action, timeoutMs)
            : correlationManager.registerRequest(requestId, action);

        // Send the request to the transport
        Map<String, Object> message = new HashMap<>();
        message.put("type", "control_request");
        message.put("id", requestId);
        message.put("action", action);
        if (params != null) {
            message.put("params", params);
        }

        transport.write(message)
            .exceptionally(error -> {
                logger.error("Failed to send control request: {}", action, error);
                correlationManager.cancel(requestId);
                return null;
            });

        return responseFuture;
    }

    /**
     * Handle a control response from the CLI.
     *
     * @param response the response message
     */
    public void handleResponse(ControlMessage response) {
        correlationManager.handleResponse(response);
    }

    /**
     * Cancel all pending requests.
     */
    public void cancelAll() {
        correlationManager.cancelAll();
    }

    /**
     * Get the number of pending requests.
     */
    public int getPendingCount() {
        return correlationManager.getPendingCount();
    }
}
