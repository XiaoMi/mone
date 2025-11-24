/*
 * Originally from Spring AI MCP Java SDK
 * Adapted for custom MCP framework
 */

package run.mone.hive.mcp.client.transport.streamable;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * Default implementation of {@link McpTransportStream} with resumability support.
 *
 * <p>
 * This implementation:
 * <ul>
 * <li>Generates a unique stream ID</li>
 * <li>Tracks the last received event ID (if resumable)</li>
 * <li>Can automatically reconnect using the reconnection function</li>
 * <li>Processes SSE events and emits JSON-RPC messages</li>
 * </ul>
 *
 * @author Christian Tzolov (original)
 * @author Adapted for custom MCP framework
 */
@Slf4j
public class DefaultMcpTransportStream implements McpTransportStream<Disposable> {

    /**
     * Unique identifier for this stream instance.
     */
    private final String streamId;

    /**
     * Whether this stream supports resumption via Last-Event-ID.
     */
    private final boolean resumable;

    /**
     * The last successfully processed event ID.
     */
    private final AtomicReference<String> lastEventId = new AtomicReference<>();

    /**
     * Function to reconnect the stream, taking the current stream as parameter.
     */
    private final Function<McpTransportStream<Disposable>, Mono<Disposable>> reconnectFunction;

    /**
     * Creates a new stream with the given configuration.
     *
     * @param resumable whether to track event IDs for resumption
     * @param reconnectFunction function to reconnect this stream
     */
    public DefaultMcpTransportStream(
            boolean resumable,
            Function<McpTransportStream<Disposable>, Mono<Disposable>> reconnectFunction) {
        this.streamId = UUID.randomUUID().toString();
        this.resumable = resumable;
        this.reconnectFunction = reconnectFunction;
        log.debug("Created stream {} (resumable: {})", streamId, resumable);
    }

    @Override
    public String streamId() {
        return streamId;
    }

    @Override
    public Optional<String> lastId() {
        return Optional.ofNullable(lastEventId.get());
    }

    @Override
    public Publisher<McpSchema.JSONRPCMessage> consumeSseStream(
            Publisher<Tuple2<Optional<String>, Iterable<McpSchema.JSONRPCMessage>>> events) {

        return Flux.from(events)
                .flatMap(tuple -> {
                    Optional<String> eventId = tuple.getT1();
                    Iterable<McpSchema.JSONRPCMessage> messages = tuple.getT2();

                    // Update last event ID if provided and resumable
                    if (resumable && eventId.isPresent()) {
                        String id = eventId.get();
                        lastEventId.set(id);
                        log.debug("Stream {} updated last event ID: {}", streamId, id);
                    }

                    // Emit all messages from this event
                    return Flux.fromIterable(messages);
                })
                .doOnComplete(() -> log.debug("Stream {} completed", streamId))
                .doOnError(error -> log.error("Stream {} error: {}", streamId, error.getMessage()))
                .onErrorResume(error -> {
                    log.warn("Stream {} encountered error, attempting reconnection", streamId);

                    // Attempt to reconnect if a reconnection function is provided
                    if (reconnectFunction != null) {
                        return reconnectFunction.apply(this)
                                .then(Mono.<McpSchema.JSONRPCMessage>empty())
                                .flux();
                    }

                    return Flux.error(error);
                });
    }

    /**
     * Returns whether this stream is resumable.
     *
     * @return true if resumable
     */
    public boolean isResumable() {
        return resumable;
    }

}
