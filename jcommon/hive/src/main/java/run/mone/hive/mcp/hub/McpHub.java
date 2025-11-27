package run.mone.hive.mcp.hub;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import io.modelcontextprotocol.json.jackson.JacksonMcpJsonMapper;
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

    private final String prefix;

    private volatile boolean isConnecting = false;

    private volatile boolean skipFile = false;

    //使用grpc连接mcp
    private Consumer<Object> msgConsumer = msg -> {
    };

    public McpHub(Path settingsPath) throws IOException {
        this(settingsPath, msg -> {
        }, false, "");
    }

    public McpHub(Path settingsPath, String prefix) throws IOException {
        this(settingsPath, msg -> {
        }, false, prefix);
    }




    public McpHub() {
        this(null, msg -> {
        }, true, "");
    }


    public McpHub(Path settingsPath, Consumer<Object> msgConsumer) throws IOException {
        this(settingsPath, msgConsumer, false, "");
    }

    public McpHub(Path settingsPath, Consumer<Object> msgConsumer, boolean skipFile) throws IOException {
        this(settingsPath, msgConsumer, skipFile, "");
    }

    @SneakyThrows
    public McpHub(Path settingsPath, Consumer<Object> msgConsumer, boolean skipFile, String prefix) {
        this.settingsPath = settingsPath;
        this.msgConsumer = msgConsumer;
        this.skipFile = skipFile;
        this.prefix = prefix != null ? prefix : "";

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
                // 如果是HTTP类型，使用V2的客户端
                if (value.getType() == McpType.HTTP && value.getClientV2() != null) {
                    value.getClientV2().ping();
                } else {
                    value.getClient().ping();
                }
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
                            // 根据类型关闭不同的transport
                            if (conn.getType() == McpType.HTTP && conn.getTransportV2() != null) {
                                // HTTP类型的transport V2没有close方法，只有closeGracefully
                            } else if (conn.getTransport() != null) {
                                conn.getTransport().close();
                            }
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
            // 如果是HTTP类型，使用V2的客户端
            if (v.getType() == McpType.HTTP && v.getClientV2() != null) {
                try {
                    v.getClientV2().close();
                } catch (Exception e) {
                    log.warn("Failed to close HTTP connection: {}", e.getMessage());
                }
            } else if (v.getTransport() != null) {
                v.getTransport().close();
            }
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
        Set<String> newNames = new HashSet<>();
        for (String key : newServers.keySet()) {
            newNames.add(prefix + key);
        }

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
            String name = prefix + entry.getKey();
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
            String name = prefix + entry.getKey();
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
        String configType = config.getType().toLowerCase();
        
        // HTTP类型使用V2的实现
        if ("http".equals(configType)) {
            connectToServerHttp(name, config);
            return;
        }
        
        // 其他类型使用原有实现
        ClientMcpTransport transport = null;
        switch (configType) {
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
            McpSchema.InitializeResult res = client.initialize();
            server.setStatus("connected");
            //放入serverInfo
            server.setServerInfo(res.serverInfo());
            //放入工具(tool)
            server.setTools(client.getTools().tools());
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
    
    /**
     * HTTP类型的连接方法，使用 io.modelcontextprotocol 的实现
     */
    private void connectToServerHttp(String name, ServerParameters config) {
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport transportV2 = 
            io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport
                .builder(config.getUrl())
                    .jsonMapper(new JacksonMcpJsonMapper(objectMapper))
                    .endpoint(config.getUrl())
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        io.modelcontextprotocol.client.McpSyncClient clientV2 = 
            io.modelcontextprotocol.client.McpClient.sync(transportV2)
                .requestTimeout(Duration.ofSeconds(120))
                .capabilities(io.modelcontextprotocol.spec.McpSchema.ClientCapabilities.builder()
                    .roots(true)
                    .build())
                .build();

        McpServer server = new McpServer(name, config.toString());
        server.setServerParameters(config);
        McpConnection connection = new McpConnection(server, clientV2, transportV2, McpType.fromString(config.getType()));
        connection.setKey(name);
        connections.put(name, connection);
        
        try {
            //这里真的会连接过去
            io.modelcontextprotocol.spec.McpSchema.InitializeResult res = clientV2.initialize();
            server.setStatus("connected");
            //放入serverInfo
            server.setServerInfoV2(res.serverInfo());
            //放入工具(tool)
            server.setToolsV2(clientV2.listTools().tools());
        } catch (Exception e) {
            log.error("Failed to connect to MCP server {}: ", name, e);
            server.setStatus("disconnected");
            server.setError(e.getMessage());
            // Clean up failed connection
            try {
                clientV2.closeGracefully();
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
                // 如果是HTTP类型，使用V2的客户端和transport
                if (connection.getType() == McpType.HTTP && connection.getClientV2() != null) {
                    if (connection.getClientV2() != null) {
                        connection.getClientV2().closeGracefully();
                    }
                } else {
                    // 其他类型使用原有的客户端和transport
                    if (connection.getTransport() != null) {
                        connection.getTransport().closeGracefully();
                    }
                    if (connection.getClient() != null) {
                        connection.getClient().closeGracefully();
                    }
                }
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
        
        // 如果是HTTP类型，使用V2的客户端
        if (connection.getType() == McpType.HTTP && connection.getClientV2() != null) {
            io.modelcontextprotocol.spec.McpSchema.CallToolRequest requestV2 = 
                new io.modelcontextprotocol.spec.McpSchema.CallToolRequest(toolName, toolArguments);
            io.modelcontextprotocol.spec.McpSchema.CallToolResult resultV2 = connection.getClientV2().callTool(requestV2);
            
            // 转换结果为旧版本的类型
            return convertCallToolResultFromV2(resultV2);
        }
        
        // 其他类型使用原有的客户端
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
        
        // 如果是HTTP类型，使用V2的客户端
        if (connection.getType() == McpType.HTTP && connection.getClientV2() != null) {
            io.modelcontextprotocol.spec.McpSchema.CallToolRequest requestV2 = 
                new io.modelcontextprotocol.spec.McpSchema.CallToolRequest(toolName, toolArguments);
            
            // V2的客户端只有同步方法 callTool，没有 callToolStream
            // 所以这里调用 callTool 并包装成 Flux
            return Flux.create(sink -> {
                try {
                    io.modelcontextprotocol.spec.McpSchema.CallToolResult resultV2 = connection.getClientV2().callTool(requestV2);
                    McpSchema.CallToolResult result = convertCallToolResultFromV2(resultV2);
                    sink.next(result);
                    sink.complete();
                } catch (Exception e) {
                    sink.error(e);
                }
            });
        }
        
        // 其他类型使用原有的客户端
        McpSchema.CallToolRequest request = new McpSchema.CallToolRequest(toolName, toolArguments);
        return connection.getClient().callToolStream(request);
    }
    
    /**
     * 将V2版本的CallToolResult转换为旧版本
     */
    private McpSchema.CallToolResult convertCallToolResultFromV2(io.modelcontextprotocol.spec.McpSchema.CallToolResult resultV2) {
        // 转换content列表
        List<McpSchema.Content> contents = Lists.newArrayList();
        if (resultV2.content() != null) {
            for (io.modelcontextprotocol.spec.McpSchema.Content contentV2 : resultV2.content()) {
                if (contentV2 instanceof io.modelcontextprotocol.spec.McpSchema.TextContent) {
                    io.modelcontextprotocol.spec.McpSchema.TextContent textContentV2 = 
                        (io.modelcontextprotocol.spec.McpSchema.TextContent) contentV2;
                    contents.add(new McpSchema.TextContent(textContentV2.text()));
                } else if (contentV2 instanceof io.modelcontextprotocol.spec.McpSchema.ImageContent) {
                    io.modelcontextprotocol.spec.McpSchema.ImageContent imageContentV2 = 
                        (io.modelcontextprotocol.spec.McpSchema.ImageContent) contentV2;
                    contents.add(new McpSchema.ImageContent(imageContentV2.data(), imageContentV2.mimeType()));
                } else if (contentV2 instanceof io.modelcontextprotocol.spec.McpSchema.EmbeddedResource) {
                    io.modelcontextprotocol.spec.McpSchema.EmbeddedResource resourceV2 = 
                        (io.modelcontextprotocol.spec.McpSchema.EmbeddedResource) contentV2;
                    // 创建对应的旧版本资源对象
                    contents.add(new McpSchema.TextContent("Embedded Resource: " + resourceV2.resource().uri()));
                }
            }
        }
        
        return new McpSchema.CallToolResult(contents, resultV2.isError());
    }

    public void dispose() {
        for (McpConnection connection : connections.values()) {
            try {
                // 如果是HTTP类型，使用V2的客户端
                if (connection.getType() == McpType.HTTP && connection.getClientV2() != null) {
                    if (connection.getClientV2() != null) {
                        connection.getClientV2().close();
                    }
                } else {
                    // 其他类型使用原有的客户端和transport
                    if (connection.getTransport() != null) {
                        connection.getTransport().close();
                    }
                    if (connection.getClient() != null) {
                        connection.getClient().close();
                    }
                }
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