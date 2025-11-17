/*
 * Originally from Spring AI MCP Java SDK
 * Adapted for custom MCP framework
 */

package run.mone.hive.mcp.client.transport.streamable;

import org.reactivestreams.Publisher;
import reactor.util.function.Tuple2;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.Optional;

/**
 * Represents a resumable SSE stream for receiving server-sent events.
 *
 * <p>
 * A transport stream manages:
 * <ul>
 * <li>A unique stream identifier</li>
 * <li>The last received event ID (for resumption after disconnection)</li>
 * <li>Processing of incoming SSE events</li>
 * <li>Automatic reconnection capability</li>
 * </ul>
 *
 * <p>
 * When a stream disconnects, it can be resumed by providing the last event ID
 * to the server, allowing it to replay missed events.
 *
 * @param <T> the type of connection handle (typically Disposable)
 * @author Christian Tzolov (original)
 * @author Adapted for custom MCP framework
 */
public interface McpTransportStream<T> {

    /**
     * Returns a unique identifier for this stream.
     *
     * @return the stream ID
     */
    String streamId();

    /**
     * Returns the ID of the last successfully processed event.
     *
     * <p>
     * This ID can be sent to the server via the Last-Event-ID header
     * to resume the stream from the next event after this one.
     *
     * @return an Optional containing the last event ID, or empty if none received yet
     */
    Optional<String> lastId();

    /**
     * Processes a stream of SSE events and their associated messages.
     *
     * <p>
     * Each event may contain:
     * <ul>
     * <li>An optional event ID for resumption</li>
     * <li>One or more JSON-RPC messages</li>
     * </ul>
     *
     * <p>
     * This method should:
     * <ul>
     * <li>Track the last event ID for resumption</li>
     * <li>Emit all messages from the events</li>
     * <li>Handle reconnection if the stream is resumable</li>
     * </ul>
     *
     * @param events a Publisher of tuples containing (Optional event ID, messages)
     * @return a Publisher that emits all JSON-RPC messages from the events
     */
    Publisher<McpSchema.JSONRPCMessage> consumeSseStream(
            Publisher<Tuple2<Optional<String>, Iterable<McpSchema.JSONRPCMessage>>> events);

}
