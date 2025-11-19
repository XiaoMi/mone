/*
 * Originally from Spring AI MCP Java SDK
 * Adapted for custom MCP framework
 */

package run.mone.hive.mcp.client.transport.streamable;

import run.mone.hive.mcp.spec.McpError;

/**
 * Exception thrown when a server does not recognize a session ID.
 *
 * <p>
 * This exception indicates that:
 * <ul>
 * <li>The client provided a session ID that the server doesn't know about</li>
 * <li>The session may have expired on the server</li>
 * <li>The server may have restarted and lost session state</li>
 * </ul>
 *
 * <p>
 * When this exception is caught, the transport should:
 * <ul>
 * <li>Invalidate the current session</li>
 * <li>Create a new session</li>
 * <li>Optionally re-initialize with the server</li>
 * </ul>
 *
 * @author Christian Tzolov (original)
 * @author Adapted for custom MCP framework
 */
public class McpTransportSessionNotFoundException extends McpError {

    /**
     * Creates a new exception with the given message.
     *
     * @param message the error message
     */
    public McpTransportSessionNotFoundException(String message) {
        super(message);
    }

}
