package run.mone.hive.shannon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import run.mone.hive.shannon.control.ControlProtocol;
import run.mone.hive.shannon.hooks.HookManager;
import run.mone.hive.shannon.transport.SubprocessCliTransport;
import run.mone.hive.shannon.transport.Transport;
import run.mone.hive.shannon.types.messages.Message;
import run.mone.hive.shannon.types.messages.ResultMessage;
import run.mone.hive.shannon.types.messages.UserMessage;
import run.mone.hive.shannon.types.permissions.PermissionMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Claude Agent SDK Client for bidirectional, stateful conversations.
 * Supports interactive sessions with the Claude Code CLI.
 */
public class ClaudeAgentClient implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(ClaudeAgentClient.class);

    private final ClaudeAgentOptions options;
    private final Transport transport;
    private final ControlProtocol controlProtocol;
    private final HookManager hookManager;

    private boolean connected = false;

    /**
     * Create a new Claude Agent client.
     *
     * @param options configuration options
     */
    public ClaudeAgentClient(ClaudeAgentOptions options) {
        this.options = options;
        this.options.validate();

        // Create transport
        this.transport = new SubprocessCliTransport(options);

        // Create control protocol
        this.controlProtocol = new ControlProtocol(transport);

        // Create hook manager
        this.hookManager = new HookManager();
        if (options.getHooks() != null) {
            hookManager.addHooksFromMap(options.getHooks());
        }
    }

    /**
     * Connect to Claude Code CLI and start a session.
     *
     * @param initialPrompt optional initial prompt to send
     * @return a future that completes when connected
     */
    public CompletableFuture<Void> connect(String initialPrompt) {
        logger.info("Connecting to Claude Code CLI...");

        return transport.connect()
            .thenCompose(v -> {
                // Send initialization if hooks are configured
                if (hookManager.getTotalHookCount() > 0) {
                    return controlProtocol.initialize(
                        convertHooksToMap(),
                        null // SDK MCP servers handled separately
                    ).thenApply(response -> null);
                }
                return CompletableFuture.completedFuture(null);
            })
            .thenCompose(v -> {
                connected = true;
                logger.info("Connected to Claude Code CLI");

                // Send initial prompt if provided
                if (initialPrompt != null && !initialPrompt.isEmpty()) {
                    return query(initialPrompt);
                }
                return CompletableFuture.completedFuture(null);
            });
    }

    /**
     * Connect without an initial prompt.
     */
    public CompletableFuture<Void> connect() {
        return connect(null);
    }

    /**
     * Send a query to Claude.
     *
     * @param prompt the prompt to send
     * @return a future that completes when the query is sent
     */
    public CompletableFuture<Void> query(String prompt) {
        if (!connected) {
            return CompletableFuture.failedFuture(
                new IllegalStateException("Client not connected. Call connect() first."));
        }

        logger.debug("Sending query: {}", prompt);

        UserMessage message = new UserMessage(prompt);

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("type", "user");
        messageData.put("content", message.content());
        messageData.put("uuid", message.uuid());

        return transport.write(messageData);
    }

    /**
     * Receive all messages from the current session as a stream.
     *
     * @return a flux of messages
     */
    public Flux<Message> receiveMessages() {
        if (!connected) {
            return Flux.error(
                new IllegalStateException("Client not connected. Call connect() first."));
        }

        return transport.readMessages();
    }

    /**
     * Receive messages until a ResultMessage is received.
     * This is useful for waiting for a query to complete.
     *
     * @return a flux of messages up to and including the result message
     */
    public Flux<Message> receiveResponse() {
        return receiveMessages()
            .takeUntil(Message::isResult);
    }

    /**
     * Collect all messages from the current session into a list.
     *
     * @return a future that completes with the list of messages
     */
    public CompletableFuture<List<Message>> collectMessages() {
        return receiveMessages()
            .collectList()
            .toFuture();
    }

    /**
     * Wait for the result message.
     *
     * @return a future that completes with the result message
     */
    public CompletableFuture<ResultMessage> waitForResult() {
        return receiveMessages()
            .filter(Message::isResult)
            .cast(ResultMessage.class)
            .next()
            .toFuture();
    }

    /**
     * Interrupt the current execution.
     *
     * @return a future that completes when the interrupt is acknowledged
     */
    public CompletableFuture<Void> interrupt() {
        logger.info("Interrupting execution...");
        return controlProtocol.interrupt()
            .thenApply(response -> {
                logger.info("Execution interrupted");
                return null;
            });
    }

    /**
     * Set the permission mode.
     *
     * @param mode the permission mode
     * @return a future that completes when the mode is set
     */
    public CompletableFuture<Void> setPermissionMode(PermissionMode mode) {
        logger.info("Setting permission mode to: {}", mode);
        return controlProtocol.setPermissionMode(mode)
            .thenApply(response -> {
                logger.info("Permission mode set to: {}", mode);
                return null;
            });
    }

    /**
     * Set the model.
     *
     * @param model the model to use
     * @return a future that completes when the model is set
     */
    public CompletableFuture<Void> setModel(String model) {
        logger.info("Setting model to: {}", model);
        return controlProtocol.setModel(model)
            .thenApply(response -> {
                logger.info("Model set to: {}", model);
                return null;
            });
    }

    /**
     * Rewind files to a specific message checkpoint.
     *
     * @param messageId the message ID to rewind to
     * @return a future that completes when files are rewound
     */
    public CompletableFuture<Void> rewindFiles(String messageId) {
        logger.info("Rewinding files to message: {}", messageId);
        return controlProtocol.rewindFiles(messageId)
            .thenApply(response -> {
                logger.info("Files rewound to message: {}", messageId);
                return null;
            });
    }

    /**
     * Disconnect from Claude Code CLI and clean up resources.
     *
     * @return a future that completes when disconnected
     */
    public CompletableFuture<Void> disconnect() {
        logger.info("Disconnecting from Claude Code CLI...");

        connected = false;

        // Cancel pending control requests
        controlProtocol.cancelAll();

        // Close transport
        return transport.close()
            .thenRun(() -> logger.info("Disconnected from Claude Code CLI"));
    }

    /**
     * Check if the client is connected.
     *
     * @return true if connected
     */
    public boolean isConnected() {
        return connected && transport.isReady();
    }

    /**
     * Get the configured options.
     *
     * @return the options
     */
    public ClaudeAgentOptions getOptions() {
        return options;
    }

    /**
     * Get the hook manager.
     *
     * @return the hook manager
     */
    public HookManager getHookManager() {
        return hookManager;
    }

    /**
     * Convert hooks to a map format for the control protocol.
     *
     * @return the hooks map
     */
    private Map<String, Object> convertHooksToMap() {
        // TODO: Implement hooks conversion
        // This would convert the HookMatcher objects to the format expected by the CLI
        return new HashMap<>();
    }

    @Override
    public void close() throws Exception {
        if (connected) {
            disconnect().join();
        }
    }
}
