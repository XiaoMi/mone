
package run.mone.hive.mcp.hub;

import lombok.Data;
import run.mone.hive.mcp.client.McpClient;
import run.mone.hive.mcp.client.McpSyncClient;
import run.mone.hive.mcp.client.transport.ServerParameters;
import run.mone.hive.mcp.client.transport.StdioClientTransport;
import run.mone.hive.mcp.spec.ClientMcpTransport;
import run.mone.hive.mcp.spec.McpSchema;

import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class McpHub {
    private final Map<String, McpConnection> connections = new ConcurrentHashMap<>();
    private final Path settingsPath;
    private WatchService watchService;
    private volatile boolean isConnecting = false;

    public McpHub(Path settingsPath) throws IOException {
        this.settingsPath = settingsPath;
        this.watchService = FileSystems.getDefault().newWatchService();
        initializeWatcher();
        initializeMcpServers();
    }

    // 局部刷新
    public void refreshMcpServer(String mcpServerName) {
        try {
            String content = new String(Files.readAllBytes(settingsPath));
            Map<String, ServerParameters> newConfig = parseServerConfigAtOnce(content, mcpServerName);
            updateServerConnectionsAtOnce(newConfig);
        } catch (IOException e) {
            System.err.println("Failed to process MCP settings change: " + e.getMessage());
        }
    }

    public void close() {
        connections.keySet().forEach(this::deleteConnection);
    }

    private void initializeWatcher() throws IOException {
        Path parent = settingsPath.getParent();
        parent.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

        Thread watchThread = new Thread(() -> {
            try {
                while (true) {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                            Path changed = (Path) event.context();
                            if (changed.endsWith(settingsPath.getFileName())) {
                                processSettingsChange();
                            }
                        }
                    }
                    key.reset();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        watchThread.setDaemon(true);
        watchThread.start();
    }

    private void initializeMcpServers() {
        try {
            String content = new String(Files.readAllBytes(settingsPath));
            Map<String, ServerParameters> config = parseServerConfig(content);
            updateServerConnections(config);
        } catch (IOException e) {
            System.err.println("Failed to initialize MCP servers: " + e.getMessage());
        }
    }

    private Map<String, ServerParameters> parseServerConfig(String content) {
        return McpSettings.fromContent(content).getMcpServers();
    }

    private Map<String, ServerParameters> parseServerConfigAtOnce(String content, String mcpServerName) {
        return McpSettings.fromContentAtOnce(content, mcpServerName).getMcpServers();
    }

    private void processSettingsChange() {
        try {
            String content = new String(Files.readAllBytes(settingsPath));
            Map<String, ServerParameters> newConfig = parseServerConfig(content);
            updateServerConnections(newConfig);
        } catch (IOException e) {
            System.err.println("Failed to process MCP settings change: " + e.getMessage());
        }
    }

    private synchronized void updateServerConnections(Map<String, ServerParameters> newServers) {
        isConnecting = true;
        Set<String> currentNames = new HashSet<>(connections.keySet());
        Set<String> newNames = new HashSet<>(newServers.keySet());

        // Delete removed servers
        for (String name : currentNames) {
            if (!newNames.contains(name)) {
                deleteConnection(name);
                System.out.println("Deleted MCP server: " + name);
            }
        }

        // Update or add servers
        for (Map.Entry<String, ServerParameters> entry : newServers.entrySet()) {
            String name = entry.getKey();
            ServerParameters config = entry.getValue();
            McpConnection currentConnection = connections.get(name);

            if (currentConnection == null) {
                // New server
                try {
                    connectToServer(name, config);
                } catch (Exception e) {
                    System.err.println("Failed to connect to new MCP server " + name + ": " + e.getMessage());
                }
            } else if (!currentConnection.getServer().getConfig().equals(config.toString())) {
                // Existing server with changed config
                try {
                    deleteConnection(name);
                    connectToServer(name, config);
                    System.out.println("Reconnected MCP server with updated config: " + name);
                } catch (Exception e) {
                    System.err.println("Failed to reconnect MCP server " + name + ": " + e.getMessage());
                }
            }
        }

        isConnecting = false;
    }

    // 只刷新指定的
    private synchronized void updateServerConnectionsAtOnce(Map<String, ServerParameters> newServers) {
        isConnecting = true;

        // Update or add servers
        for (Map.Entry<String, ServerParameters> entry : newServers.entrySet()) {
            String name = entry.getKey();
            ServerParameters config = entry.getValue();
            McpConnection currentConnection = connections.get(name);

            if (currentConnection == null || currentConnection.getServer().getStatus().equals("disconnected")) {
                // New server
                try {
                    connectToServer(name, config);
                } catch (Exception e) {
                    System.err.println("Failed to connect to new MCP server " + name + ": " + e.getMessage());
                }
            } else if (!currentConnection.getServer().getConfig().equals(config.toString())) {
                // Existing server with changed config
                try {
                    deleteConnection(name);
                    connectToServer(name, config);
                    System.out.println("Reconnected MCP server with updated config: " + name);
                } catch (Exception e) {
                    System.err.println("Failed to reconnect MCP server " + name + ": " + e.getMessage());
                }
            }
        }

        isConnecting = false;
    }

    private void connectToServer(String name, ServerParameters config) {
        ClientMcpTransport transport = new StdioClientTransport(config);
        McpSyncClient client = McpClient.using(transport)
                .requestTimeout(Duration.ofSeconds(10))
                .capabilities(McpSchema.ClientCapabilities.builder()
                        .roots(true)
                        .build())
                .sync();

        McpServer server = new McpServer(name, config.toString());
        server.setServerParameters(config);
        McpConnection connection = new McpConnection(server, client, transport);
        connections.put(name, connection);

        try {
            client.initialize();
            server.setStatus("connected");
            server.setTools(client.listTools().tools());
            // Fetch resources and resource templates if needed
        } catch (Exception e) {
            server.setStatus("disconnected");
            server.setError(e.getMessage());
        }
    }

    private void deleteConnection(String name) {
        McpConnection connection = connections.remove(name);
        if (connection != null) {
            try {
                connection.getTransport().closeGracefully();
                connection.getClient().closeGracefully();
            } catch (Exception e) {
                System.err.println("Failed to close transport for " + name + ": " + e.getMessage());
            }
        }
    }

    public synchronized void restartConnection(String serverName) {
        isConnecting = true;
        McpConnection connection = connections.get(serverName);
        if (connection != null) {
            McpServer server = connection.getServer();
            ServerParameters config = ServerParameters.builder(server.getConfig()).build();

            System.out.println("Restarting " + serverName + " MCP server...");
            server.setStatus("connecting");
            server.setError("");

            try {
                deleteConnection(serverName);
                connectToServer(serverName, config);
                System.out.println(serverName + " MCP server connected");
            } catch (Exception e) {
                System.err.println("Failed to restart connection for " + serverName + ": " + e.getMessage());
            }
        }
        isConnecting = false;
    }

    public McpSchema.CallToolResult callTool(String serverName, String
            toolName, Map<String, Object> toolArguments) {
        McpConnection connection = connections.get(serverName);
        if (connection == null) {
            throw new IllegalArgumentException("No connection found for server: " + serverName);
        }
        McpSchema.CallToolRequest request = new McpSchema.CallToolRequest(toolName, toolArguments);
        return connection.getClient().callTool(request);
    }

    public void dispose() {
        for (McpConnection connection : connections.values()) {
            try {
                connection.getTransport().close();
                connection.getClient().close();
            } catch (Exception e) {
                System.err.println("Failed to close connection: " + e.getMessage());
            }
        }
        connections.clear();
        try {
            watchService.close();
        } catch (IOException e) {
            System.err.println("Failed to close watch service: " + e.getMessage());
        }
    }

    public List<McpServer> getServers() {
        return new ArrayList<>(connections.values().stream().map(McpConnection::getServer).toList());
    }

}