package run.mone.hive.mcp.spec;

import java.util.function.Function;

import com.fasterxml.jackson.core.type.TypeReference;
import reactor.core.publisher.Mono;

import run.mone.hive.mcp.spec.McpSchema.JSONRPCMessage;

/**
 * 
 * ORIGINAL CODE IS FROM SPRING AI!!!
 * 
 * Defines the asynchronous transport layer for the Model Context Protocol (MCP).
 *
 * <p>
 * The McpTransport interface provides the foundation for implementing custom transport
 * mechanisms in the Model Context Protocol. It handles the bidirectional communication
 * between the client and server components, supporting asynchronous message exchange
 * using JSON-RPC format.
 * </p>
 *
 * <p>
 * Implementations of this interface are responsible for:
 * </p>
 * <ul>
 * <li>Managing the lifecycle of the transport connection</li>
 * <li>Handling incoming messages and errors from the server</li>
 * <li>Sending outbound messages to the server</li>
 * </ul>
 *
 * <p>
 * The transport layer is designed to be protocol-agnostic, allowing for various
 * implementations such as WebSocket, HTTP, or custom protocols.
 * </p>
 *
 */
public interface McpTransport {

	/**
	 * Initializes and starts the transport connection.
	 *
	 * <p>
	 * This method should be called before any message exchange can occur. It sets up the
	 * necessary resources and establishes the connection to the server.
	 * </p>
	 */
	Mono<Void> connect(Function<Mono<JSONRPCMessage>, Mono<JSONRPCMessage>> handler);

	/**
	 * Closes the transport connection and releases any associated resources.
	 *
	 * <p>
	 * This method ensures proper cleanup of resources when the transport is no longer
	 * needed. It should handle the graceful shutdown of any active connections.
	 * </p>
	 */
	default void close() {
		this.closeGracefully().subscribe();
	}

	/**
	 * Closes the transport connection and releases any associated resources
	 * asynchronously.
	 * @return a {@link Mono<Void>} that completes when the connection has been closed.
	 */
	Mono<Void> closeGracefully();

	/**
	 * Sends a message to the server asynchronously.
	 *
	 * <p>
	 * This method handles the transmission of messages to the server in an asynchronous
	 * manner. Messages are sent in JSON-RPC format as specified by the MCP protocol.
	 * </p>
	 * @param message the {@link JSONRPCMessage} to be sent to the server
	 * @return a {@link Mono<Void>} that completes when the message has been sent
	 */
	Mono<Void> sendMessage(JSONRPCMessage message);

	/**
	 * Unmarshals the given data into an object of the specified type.
	 * @param <T> the type of the object to unmarshal
	 * @param data the data to unmarshal
	 * @param typeRef the type reference for the object to unmarshal
	 * @return the unmarshalled object
	 */
	<T> T unmarshalFrom(Object data, TypeReference<T> typeRef);

}
