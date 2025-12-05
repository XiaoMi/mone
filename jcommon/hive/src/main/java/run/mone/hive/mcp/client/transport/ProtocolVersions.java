/*
 * Originally from Spring AI MCP Java SDK
 * Adapted for custom MCP framework
 */

package run.mone.hive.mcp.client.transport;

/**
 * MCP protocol version constants.
 *
 * @author Christian Tzolov
 */
public final class ProtocolVersions {

    /**
     * MCP protocol version 2024-11-05.
     */
    public static final String MCP_2024_11_05 = "2024-11-05";

    /**
     * MCP protocol version 2025-03-26.
     */
    public static final String MCP_2025_03_26 = "2025-03-26";

    /**
     * MCP protocol version 2025-06-18.
     */
    public static final String MCP_2025_06_18 = "2025-06-18";

    private ProtocolVersions() {
        // Prevent instantiation
    }

}
