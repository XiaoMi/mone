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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * Default implementation of {@link McpTransportSession} that manages session state
 * and connection lifecycle.
 *
 * <p>
 * This implementation:
 * <ul>
 * <li>Tracks a session ID once initialized</li>
 * <li>Maintains a thread-safe set of active connections</li>
 * <li>Invokes a close callback when gracefully shutting down</li>
 * <li>Disposes all connections when closed</li>
 * </ul>
 *
 * @author Christian Tzolov (original)
 * @author Adapted for custom MCP framework
 */
@Slf4j
public class DefaultMcpTransportSession implements McpTransportSession<Disposable> {

    /**
     * The session identifier, set once upon initialization.
     */
    private final AtomicReference<String> sessionId = new AtomicReference<>();

    /**
     * Set of active connections tracked by this session.
     */
    private final Set<Disposable> connections = ConcurrentHashMap.newKeySet();

    /**
     * Callback to invoke when closing the session, typically for sending DELETE request.
     * Takes the session ID and returns a Publisher that completes when cleanup is done.
     */
    private final Function<String, Publisher<Void>> onClose;

    /**
     * Flag indicating whether this session has been closed.
     */
    private volatile boolean closed = false;

    /**
     * Creates a new DefaultMcpTransportSession with the given close callback.
     *
     * @param onClose function to invoke when closing, receives session ID
     */
    public DefaultMcpTransportSession(Function<String, Publisher<Void>> onClose) {
        this.onClose = onClose;
    }

    @Override
    public Optional<String> sessionId() {
        return Optional.ofNullable(sessionId.get());
    }

    @Override
    public boolean markInitialized(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            return false;
        }

        boolean wasSet = this.sessionId.compareAndSet(null, sessionId);
        if (wasSet) {
            log.debug("Session initialized with ID: {}", sessionId);
        } else {
            String existingId = this.sessionId.get();
            if (!sessionId.equals(existingId)) {
                log.warn("Attempted to initialize session with different ID. Existing: {}, New: {}",
                        existingId, sessionId);
            }
        }
        return wasSet;
    }

    @Override
    public void addConnection(Disposable connection) {
        if (closed) {
            log.warn("Cannot add connection to closed session");
            connection.dispose();
            return;
        }

        boolean added = connections.add(connection);
        if (added) {
            log.debug("Added connection to session. Total connections: {}", connections.size());
        }
    }

    @Override
    public void removeConnection(Disposable connection) {
        boolean removed = connections.remove(connection);
        if (removed) {
            log.debug("Removed connection from session. Remaining connections: {}", connections.size());
        }
    }

    @Override
    public Publisher<Void> closeGracefully() {
        return Mono.defer(() -> {
            if (closed) {
                log.debug("Session already closed");
                return Mono.empty();
            }

            closed = true;
            log.debug("Closing session gracefully. Active connections: {}", connections.size());

            // Dispose all tracked connections
            connections.forEach(connection -> {
                try {
                    if (!connection.isDisposed()) {
                        connection.dispose();
                    }
                } catch (Exception e) {
                    log.warn("Error disposing connection", e);
                }
            });
            connections.clear();

            // Invoke close callback if session was initialized
            String currentSessionId = sessionId.get();
            if (currentSessionId != null) {
                log.debug("Invoking close callback for session: {}", currentSessionId);
                return Mono.from(onClose.apply(currentSessionId));
            }

            return Mono.empty();
        });
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }

        closed = true;
        log.debug("Closing session immediately");

        // Dispose all connections
        connections.forEach(connection -> {
            try {
                if (!connection.isDisposed()) {
                    connection.dispose();
                }
            } catch (Exception e) {
                log.warn("Error disposing connection", e);
            }
        });
        connections.clear();
    }

    /**
     * Returns the number of active connections.
     *
     * @return connection count
     */
    public int connectionCount() {
        return connections.size();
    }

    /**
     * Checks if this session has been closed.
     *
     * @return true if closed
     */
    public boolean isClosed() {
        return closed;
    }

}
