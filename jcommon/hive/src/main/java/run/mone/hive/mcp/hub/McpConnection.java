
package run.mone.hive.mcp.hub;

import run.mone.hive.mcp.client.McpSyncClient;
import run.mone.hive.mcp.spec.ClientMcpTransport;

public class McpConnection {
    private final McpServer server;
    private final McpSyncClient client;
    private final ClientMcpTransport transport;

    public McpConnection(McpServer server, McpSyncClient client, ClientMcpTransport transport) {
        this.server = server;
        this.client = client;
        this.transport = transport;
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
}
