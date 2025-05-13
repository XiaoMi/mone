
package run.mone.hive.mcp.hub;

import lombok.Data;
import run.mone.hive.mcp.client.McpSyncClient;
import run.mone.hive.mcp.spec.ClientMcpTransport;

@Data
public class McpConnection {

    private final McpServer server;
    private final McpSyncClient client;
    private final ClientMcpTransport transport;
    private final McpType type;

    private String key;

    private int errorNum;

    public McpConnection(McpServer server, McpSyncClient client, ClientMcpTransport transport) {
        this(server, client, transport, McpType.STDIO);
    }

    public McpConnection(McpServer server, McpSyncClient client, ClientMcpTransport transport, McpType type) {
        this.server = server;
        this.client = client;
        this.transport = transport;
        this.type = type;
    }

}
