package run.mone.hive.shannon.transport;

import reactor.core.publisher.Flux;
import run.mone.hive.shannon.types.messages.Message;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Transport interface for communicating with Claude Code CLI.
 * This is the abstraction layer for different transport types (currently only subprocess stdio).
 */
public interface Transport {

    /**
     * Connect to the Claude Code CLI and initialize the session.
     *
     * @return a future that completes when connected
     */
    CompletableFuture<Void> connect();

    /**
     * Write a message to the CLI.
     *
     * @param message the message to write
     * @return a future that completes when the message is written
     */
    CompletableFuture<Void> write(Map<String, Object> message);

    /**
     * Get a stream of messages from the CLI.
     *
     * @return a flux of messages
     */
    Flux<Message> readMessages();

    /**
     * Check if the transport is ready for communication.
     *
     * @return true if ready
     */
    boolean isReady();

    /**
     * Check if the transport is connected.
     *
     * @return true if connected
     */
    boolean isConnected();

    /**
     * Signal end of input (close stdin).
     *
     * @return a future that completes when input is closed
     */
    CompletableFuture<Void> endInput();

    /**
     * Close the transport and clean up resources.
     *
     * @return a future that completes when closed
     */
    CompletableFuture<Void> close();
}
