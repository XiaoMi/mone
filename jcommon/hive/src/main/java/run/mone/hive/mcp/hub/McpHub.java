package run.mone.hive.mcp.hub;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import run.mone.hive.common.Safe;
import run.mone.hive.mcp.client.McpClient;
import run.mone.hive.mcp.client.McpSyncClient;
import run.mone.hive.mcp.client.transport.HttpClientSseClientTransport;
import run.mone.hive.mcp.client.transport.ServerParameters;
import run.mone.hive.mcp.client.transport.StdioClientTransport;
import run.mone.hive.mcp.grpc.transport.GrpcClientTransport;
import run.mone.hive.mcp.spec.ClientMcpTransport;
import run.mone.hive.mcp.spec.McpSchema;

import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Data
@Slf4j
public class McpHub {

    private final Map<String, McpConnection> connections = new ConcurrentHashMap<>();

    private final Path settingsPath;

    private WatchService watchService;

    private volatile boolean isConnecting = false;

    private volatile boolean skipFile = false;

    //使用grpc连接mcp
    private Consumer<Object> msgConsumer = msg -> {
    };

    public McpHub(Path settingsPath) throws IOException {
        this(settingsPath, msg -> {
        }, false);
    }


    public McpHub() {
        this(null, msg -> {
        }, true);
    }


    public McpHub(Path settingsPath, Consumer<Object> msgConsumer) throws IOException {
        this(settingsPath, msgConsumer, false);
    }

    @SneakyThrows
    public McpHub(Path settingsPath, Consumer<Object> msgConsumer, boolean skipFile) {
        this.settingsPath = settingsPath;
        this.msgConsumer = msgConsumer;
        this.skipFile = skipFile;

        if (!skipFile) {
            this.watchService = FileSystems.getDefault().newWatchService();
            initializeWatcher();
            initializeMcpServers();
        }

        //用来发ping
        ping();
    }

    private void ping() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            Safe.run(() -> this.connections.forEach((key, value) -> Safe.run(() -> {
                value.getClient().ping();
            }, ex -> {
                if (null == ex) {
                    value.setErrorNum(0);
                } else {
                    //发生错误了
                    value.setErrorNum(value.getErrorNum() + 1);
                    if (value.getErrorNum() >= 3) {
                        value.setErrorNum(0);
                        McpConnection conn = this.connections.get(key);
                        Safe.run(() -> {
                            value.getTransport().close();
                        });
                        //尝试再连接过去
                        String name = conn.getServer().getName();
                        ServerParameters params = conn.getServer().getServerParameters();
                        log.info("reconnect:{}", name);
                        updateServerConnections(ImmutableMap.of(name,params),true);
                    }
                }
            })));
        }, 5, 5, TimeUnit.SECONDS);
    }

    //移除废弃的连接
    public void removeConnection(String key) {
        McpConnection v = this.connections.remove(key);
        log.info("remove connection:{}", v);
        if (null != v) {
            v.getTransport().close();
        }
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
            updateServerConnections(config,true);
        } catch (IOException e) {
            log.error("Failed to initialize MCP servers: ", e);
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
            updateServerConnections(newConfig,true);
        } catch (IOException e) {
            System.err.println("Failed to process MCP settings change: " + e.getMessage());
        }
    }

    public synchronized void updateServerConnections(Map<String, ServerParameters> newServers, boolean removeOld) {
        isConnecting = true;
        Set<String> currentNames = new HashSet<>(connections.keySet());
        Set<String> newNames = new HashSet<>(newServers.keySet());

        if (removeOld) {
            // Delete removed servers
            for (String name : currentNames) {
                if (!newNames.contains(name)) {
                    deleteConnection(name);
                    log.info("Deleted MCP server: " + name);
                }
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
                    log.error("Failed to connect to new MCP server " + name + ": " + e.getMessage());
                }
            } else {
                // Existing server with changed config
                try {
                    deleteConnection(name);
                    connectToServer(name, config);
                    log.info("Reconnected MCP server with updated config: " + name);
                } catch (Exception e) {
                    log.error("Failed to reconnect MCP server " + name + ": " + e.getMessage());
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
                    log.info("Reconnected MCP server with updated config: " + name);
                } catch (Exception e) {
                    log.error("Failed to reconnect MCP server " + name + ": " + e.getMessage());
                }
            }
        }

        isConnecting = false;
    }

    public void connectToServer(String name, ServerParameters config) {
        ClientMcpTransport transport = null;
        switch (config.getType().toLowerCase()) {
            case "grpc":
                transport = new GrpcClientTransport(config);
                break;
            case "stdio":
                transport = new StdioClientTransport(config);
                break;
            case "sse":
                if (!config.isSseRemote()) {
                    startSseServer(config);
                }
                transport = new HttpClientSseClientTransport(config.getUrl());
                break;
            default:
                throw new IllegalArgumentException("Unsupported transport type: " + config.getType());
        }

        //建立连接(grpc就直接连过去了)
        McpSyncClient client = McpClient.using(transport)
                .requestTimeout(Duration.ofSeconds(120))
                .msgConsumer(msgConsumer)
                .capabilities(McpSchema.ClientCapabilities.builder()
                        .roots(true)
                        .build())
                .sync();

        McpServer server = new McpServer(name, config.toString());
        server.setServerParameters(config);
        McpConnection connection = new McpConnection(server, client, transport, McpType.fromString(config.getType()));
        connection.setKey(name);
        connections.put(name, connection);
        try {
            //这里真的会连接过去
            client.initialize();
            server.setStatus("connected");
            //放入工具(tool)
            server.setTools(client.listTools().tools());
        } catch (Exception e) {
            log.error("Failed to connect to MCP server {}: ", name, e);
            server.setStatus("disconnected");
            server.setError(e.getMessage());
            // Clean up failed connection
//            connections.remove(name);
            try {
                transport.closeGracefully();
                client.closeGracefully();
            } catch (Exception closeEx) {
                log.warn("Failed to clean up connection resources for {}: {}", name, closeEx.getMessage());
            }
        }
    }

    private void startSseServer(ServerParameters config) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        List<String> fullCommand = new ArrayList<>();
        fullCommand.add(config.getCommand());
        fullCommand.addAll(config.getArgs());

        processBuilder.command(fullCommand);
        processBuilder.environment().putAll(config.getEnv());
        try {
            Process process = processBuilder.start();
            // process.waitFor();
            TimeUnit.SECONDS.sleep(10); // FIXME: 需要优化
        } catch (Throwable e) {
            log.error("Failed to start SSE server: ", e);
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

    public Flux<McpSchema.CallToolResult> callToolStream(String serverName, String
            toolName, Map<String, Object> toolArguments) {
        McpConnection connection = connections.get(serverName);
        if (connection == null) {
            McpHubHolder.remove(serverName);
            return Flux.create(sink -> {
                sink.next(new McpSchema.CallToolResult(Lists.newArrayList(new McpSchema.TextContent("No connection found for server: " + serverName)), true));
                sink.complete();
            });
        }
        McpSchema.CallToolRequest request = new McpSchema.CallToolRequest(toolName, toolArguments);
        return connection.getClient().callToolStream(request);
    }

    public void dispose() {
        for (McpConnection connection : connections.values()) {
            try {
                connection.getTransport().close();
                connection.getClient().close();
            } catch (Exception e) {
                log.error("Failed to close connection: " + e.getMessage());
            }
        }
        connections.clear();
        if (!skipFile) {
            try {
                watchService.close();
            } catch (IOException e) {
                log.error("Failed to close watch service: " + e.getMessage());
            }
        }
    }

    public List<McpServer> getServers() {
        return new ArrayList<>(connections.values().stream().map(McpConnection::getServer).toList());
    }

}