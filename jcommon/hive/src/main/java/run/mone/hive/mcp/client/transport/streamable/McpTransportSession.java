/*
 * Originally from Spring AI MCP Java SDK
 * Adapted for custom MCP framework
 */

package run.mone.hive.mcp.client.transport.streamable;

import org.reactivestreams.Publisher;

import java.util.Optional;

/**
 * Represents a transport session that manages connections and their lifecycle.
 *
 * <p>
 * A transport session is responsible for:
 * <ul>
 * <li>Managing a session identifier for stateful server communication</li>
 * <li>Tracking multiple active connections (e.g., SSE streams, HTTP requests)</li>
 * <li>Providing graceful shutdown capabilities</li>
 * </ul>
 *
 * @param <T> the type of connection handle (typically Disposable)
 * @author Christian Tzolov (original)
 * @author Adapted for custom MCP framework
 */
public interface McpTransportSession<T> {

    /**
     * Returns the session identifier if one has been established.
     *
     * @return an Optional containing the session ID, or empty if not yet initialized
     */
    Optional<String> sessionId();

    /**
     * Attempts to mark this session as initialized with the given session ID.
     *
     * <p>
     * This method should be called when the server returns a session ID in a response.
     * It will only succeed on the first call (subsequent calls with different IDs
     * should return false or be ignored).
     *
     * @param sessionId the session ID provided by the server
     * @return true if the session was successfully initialized, false if already initialized
     */
    boolean markInitialized(String sessionId);

    /**
     * Adds a connection to be tracked by this session.
     *
     * <p>
     * The session will manage the lifecycle of all added connections and ensure
     * they are properly disposed when the session closes.
     *
     * @param connection the connection to track
     */
    void addConnection(T connection);

    /**
     * Removes a connection from this session's tracking.
     *
     * <p>
     * This should be called when a connection completes or is disposed externally.
     *
     * @param connection the connection to stop tracking
     */
    void removeConnection(T connection);

    /**
     * Closes this session gracefully, allowing pending operations to complete.
     *
     * <p>
     * This will:
     * <ul>
     * <li>Dispose all tracked connections</li>
     * <li>Send a DELETE request to the server if a session ID exists</li>
     * <li>Mark the session as closed</li>
     * </ul>
     *
     * @return a Publisher that completes when the session is fully closed
     */
    Publisher<Void> closeGracefully();

    /**
     * Immediately closes this session and all tracked connections.
     *
     * <p>
     * This is a synchronous operation that should dispose all resources
     * without waiting for confirmation.
     */
    default void close() {
        // Default implementation can be empty for backward compatibility
    }

}
