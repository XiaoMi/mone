package run.mone.hive.shannon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.mone.hive.shannon.exceptions.ClaudeAgentException;
import run.mone.hive.shannon.types.messages.Message;
import run.mone.hive.shannon.types.messages.ResultMessage;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Simple facade for Claude Agent SDK.
 * Provides static methods for quick one-shot queries and streaming.
 */
public class ClaudeAgentSDK {

    private static final Logger logger = LoggerFactory.getLogger(ClaudeAgentSDK.class);

    /**
     * Execute a simple one-shot query to Claude Code CLI.
     * This is a fire-and-forget operation that creates a client, executes the query,
     * waits for the result, and cleans up.
     *
     * @param prompt the prompt to send
     * @param options configuration options
     * @return a future that completes with the result message
     */
    public static CompletableFuture<ResultMessage> query(String prompt, ClaudeAgentOptions options) {
        logger.info("Executing one-shot query...");

        ClaudeAgentClient client = new ClaudeAgentClient(options);

        return client.connect(prompt)
            .thenCompose(v -> client.waitForResult())
            .whenComplete((result, error) -> {
                // Clean up client
                try {
                    client.disconnect().join();
                } catch (Exception e) {
                    logger.warn("Error disconnecting client", e);
                }
            });
    }

    /**
     * Execute a simple query with default options.
     *
     * @param prompt the prompt to send
     * @return a future that completes with the result message
     */
    public static CompletableFuture<ResultMessage> query(String prompt) {
        return query(prompt, ClaudeAgentOptions.defaults());
    }

    /**
     * Execute a streaming query to Claude Code CLI.
     * Returns a stream of messages as they arrive.
     *
     * @param prompt the prompt to send
     * @param options configuration options
     * @return a flux of messages
     */
    public static Flux<Message> queryStream(String prompt, ClaudeAgentOptions options) {
        logger.info("Executing streaming query...");

        ClaudeAgentClient client = new ClaudeAgentClient(options);

        return Flux.defer(() -> {
            // Connect and start receiving messages
            return Mono.fromFuture(client.connect(prompt))
                .thenMany(client.receiveMessages())
                .doFinally(signalType -> {
                    // Clean up client when stream completes
                    try {
                        client.disconnect().join();
                    } catch (Exception e) {
                        logger.warn("Error disconnecting client", e);
                    }
                });
        });
    }

    /**
     * Execute a streaming query with default options.
     *
     * @param prompt the prompt to send
     * @return a flux of messages
     */
    public static Flux<Message> queryStream(String prompt) {
        return queryStream(prompt, ClaudeAgentOptions.defaults());
    }

    /**
     * Execute a query and collect all messages into a list.
     *
     * @param prompt the prompt to send
     * @param options configuration options
     * @return a future that completes with the list of all messages
     */
    public static CompletableFuture<List<Message>> queryAndCollect(
        String prompt,
        ClaudeAgentOptions options
    ) {
        logger.info("Executing query and collecting messages...");

        ClaudeAgentClient client = new ClaudeAgentClient(options);

        return client.connect(prompt)
            .thenCompose(v -> client.collectMessages())
            .whenComplete((messages, error) -> {
                // Clean up client
                try {
                    client.disconnect().join();
                } catch (Exception e) {
                    logger.warn("Error disconnecting client", e);
                }
            });
    }

    /**
     * Execute a query and collect all messages with default options.
     *
     * @param prompt the prompt to send
     * @return a future that completes with the list of all messages
     */
    public static CompletableFuture<List<Message>> queryAndCollect(String prompt) {
        return queryAndCollect(prompt, ClaudeAgentOptions.defaults());
    }

    /**
     * Create a new bidirectional client for interactive sessions.
     * Use this when you need to send multiple queries and control the session.
     *
     * @param options configuration options
     * @return a new client instance
     */
    public static ClaudeAgentClient createClient(ClaudeAgentOptions options) {
        return new ClaudeAgentClient(options);
    }

    /**
     * Create a new bidirectional client with default options.
     *
     * @return a new client instance
     */
    public static ClaudeAgentClient createClient() {
        return new ClaudeAgentClient(ClaudeAgentOptions.defaults());
    }

    /**
     * Utility method to extract text content from messages.
     *
     * @param messages the messages to extract text from
     * @return concatenated text content
     */
    public static String extractText(List<Message> messages) {
        StringBuilder sb = new StringBuilder();

        for (Message message : messages) {
            if (message.isAssistant()) {
                var assistant = message.asAssistant();
                assistant.content().forEach(block -> {
                    if (block.isText()) {
                        sb.append(block.asText().text()).append("\n");
                    }
                });
            } else if (message.isUser()) {
                sb.append(message.asUser().content()).append("\n");
            } else if (message.isSystem()) {
                sb.append(message.asSystem().message()).append("\n");
            }
        }

        return sb.toString().trim();
    }

    /**
     * Check if a query result was successful.
     *
     * @param result the result message
     * @return true if successful
     */
    public static boolean isSuccess(ResultMessage result) {
        return result != null && result.isSuccessful();
    }

    /**
     * Get error message from a failed result.
     *
     * @param result the result message
     * @return the error message, or null if no error
     */
    public static String getError(ResultMessage result) {
        return result != null ? result.error() : null;
    }

    // Private constructor to prevent instantiation
    private ClaudeAgentSDK() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
