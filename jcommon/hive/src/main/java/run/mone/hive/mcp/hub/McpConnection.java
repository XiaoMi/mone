
package run.mone.hive.mcp.hub;

import lombok.Data;
import run.mone.hive.mcp.core.client.McpSyncClient;
import run.mone.hive.mcp.core.spec.McpClientTransport;

@Data
public class McpConnection {

    private final McpServer server;
    private McpSyncClient client;
    private McpClientTransport transport;
    private final McpType type;

    private String key;

    private int errorNum;

    public McpConnection(McpServer server, McpSyncClient client, McpClientTransport transport) {
        this(server, client, transport, McpType.STDIO);
    }

    public McpConnection(McpServer server, McpSyncClient client, McpClientTransport transport, McpType type) {
        this.server = server;
        this.client = client;
        this.transport = transport;
        this.type = type;
    }

}
