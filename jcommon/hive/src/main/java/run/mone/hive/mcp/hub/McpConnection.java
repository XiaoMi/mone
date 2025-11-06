
package run.mone.hive.mcp.hub;

import lombok.Data;
import run.mone.hive.mcp.client.McpSyncClient;
import run.mone.hive.mcp.spec.ClientMcpTransport;

@Data
public class McpConnection {

    private final McpServer server;
    private McpSyncClient client;
    private io.modelcontextprotocol.client.McpSyncClient clientV2;
    private ClientMcpTransport transport;
    private io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport transportV2;
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

    public McpConnection(McpServer server, io.modelcontextprotocol.client.McpSyncClient client, McpType type) {
        this.server = server;
        this.clientV2 = client;
        this.type = type;
    }

    public McpConnection(McpServer server, io.modelcontextprotocol.client.McpSyncClient client, 
                        io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport transport, McpType type) {
        this.server = server;
        this.clientV2 = client;
        this.transportV2 = transport;
        this.type = type;
    }

}
