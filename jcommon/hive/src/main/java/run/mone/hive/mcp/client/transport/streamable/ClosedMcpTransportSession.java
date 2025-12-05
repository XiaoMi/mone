/*
 * Originally from Spring AI MCP Java SDK
 * Adapted for custom MCP framework
 */

package run.mone.hive.mcp.client.transport.streamable;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Represents a closed transport session that rejects all operations.
 *
 * <p>
 * This implementation is used when:
 * <ul>
 * <li>A session is explicitly closed by the client</li>
 * <li>A session needs to be marked as closed without allowing further operations</li>
 * <li>During shutdown or cleanup phases</li>
 * </ul>
 *
 * <p>
 * All mutating operations (addConnection, removeConnection, markInitialized) are no-ops,
 * and the session ID is preserved from the previous session if available.
 *
 * @author Christian Tzolov (original)
 * @author Adapted for custom MCP framework
 */
@Slf4j
public class ClosedMcpTransportSession implements McpTransportSession<Disposable> {

    /**
     * The session ID from the previously active session, if any.
     */
    private final String sessionId;

    /**
     * Creates a closed session with no session ID.
     */
    public ClosedMcpTransportSession() {
        this(null);
    }

    /**
     * Creates a closed session preserving the given session ID.
     *
     * @param sessionId the session ID to preserve, may be null
     */
    public ClosedMcpTransportSession(String sessionId) {
        this.sessionId = sessionId;
        log.debug("Created closed session with ID: {}", sessionId);
    }

    @Override
    public Optional<String> sessionId() {
        return Optional.ofNullable(sessionId);
    }

    @Override
    public boolean markInitialized(String sessionId) {
        log.warn("Attempted to initialize a closed session with ID: {}", sessionId);
        return false;
    }

    @Override
    public void addConnection(Disposable connection) {
        log.warn("Attempted to add connection to closed session, disposing immediately");
        if (connection != null && !connection.isDisposed()) {
            connection.dispose();
        }
    }

    @Override
    public void removeConnection(Disposable connection) {
        // No-op for closed session
        log.debug("Attempted to remove connection from closed session (no-op)");
    }

    @Override
    public Publisher<Void> closeGracefully() {
        log.debug("closeGracefully called on already closed session");
        return Mono.empty();
    }

    @Override
    public void close() {
        log.debug("close called on already closed session (no-op)");
    }

}
