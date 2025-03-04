
package run.mone.hive.mcp.hub;

import run.mone.hive.mcp.client.McpSyncClient;
import run.mone.hive.mcp.spec.ClientMcpTransport;

public class McpConnection {

    private final McpServer server;
    private final McpSyncClient client;
    private final ClientMcpTransport transport;
    private final McpType type;

    public McpConnection(McpServer server, McpSyncClient client, ClientMcpTransport transport) {
        this(server, client, transport, McpType.STDIO);
    }

    public McpConnection(McpServer server, McpSyncClient client, ClientMcpTransport transport, McpType type) {
        this.server = server;
        this.client = client;
        this.transport = transport;
        this.type = type;
    }

    public McpServer getServer() {
        return server;
    }

    public McpSyncClient getClient() {
        return client;
    }

    public ClientMcpTransport getTransport() {
        return transport;
    }

    public McpType getType() {
        return type;
    }
}
