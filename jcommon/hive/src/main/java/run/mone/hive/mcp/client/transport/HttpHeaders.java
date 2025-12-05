/*
 * Originally from Spring AI MCP Java SDK
 * Adapted for custom MCP framework
 */

package run.mone.hive.mcp.client.transport;

/**
 * HTTP header constants used in MCP transport.
 *
 * @author Christian Tzolov
 */
public final class HttpHeaders {

    /**
     * The MCP session ID header.
     */
    public static final String MCP_SESSION_ID = "mcp-session-id";

    /**
     * The MCP protocol version header.
     */
    public static final String PROTOCOL_VERSION = "mcp-protocol-version";

    /**
     * The Last-Event-ID header used for SSE stream resumption.
     */
    public static final String LAST_EVENT_ID = "Last-Event-ID";

    /**
     * The Accept header.
     */
    public static final String ACCEPT = "Accept";

    /**
     * The Content-Type header.
     */
    public static final String CONTENT_TYPE = "Content-Type";

    /**
     * The Content-Length header.
     */
    public static final String CONTENT_LENGTH = "Content-Length";

    /**
     * The Cache-Control header.
     */
    public static final String CACHE_CONTROL = "Cache-Control";

    private HttpHeaders() {
        // Prevent instantiation
    }

}
