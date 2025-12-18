package run.mone.hive.shannon.types.mcp;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Configuration for in-process SDK MCP servers.
 * These servers run in the same JVM process and don't require subprocess communication.
 * The instance should be a MCP server implementation (e.g., io.modelcontextprotocol.server.McpServer).
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SdkMcpConfig extends McpServerConfig {

    /**
     * The in-process MCP server instance.
     * Type is Object to avoid dependency on specific MCP SDK version.
     * Should be compatible with MCP server interfaces.
     */
    private final Object instance;

    public SdkMcpConfig(Object instance) {
        super("sdk");
        this.instance = instance;
    }
}
