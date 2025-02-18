package run.mone.moner.server.mcp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import run.mone.hive.mcp.hub.McpConnection;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.moner.server.common.GsonUtils;
import run.mone.moner.server.common.Safe;
import run.mone.moner.server.prompt.MonerSystemPrompt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangxiaowei6
 * @Date 2025/1/20 14:21
 */

@Slf4j
@Service
public class McpOperationService {

    private final McpConfig mcpConfig;
    private WatchService watchService;
    private volatile boolean isWatching = false;

    public McpOperationService(McpConfig mcpConfig) {
        this.mcpConfig = mcpConfig;
    }

    // 获取 MCP 连接，优先从缓存获取
    private List<Map<String, Object>> getMcpConnections() {
        return MonerSystemPrompt.getMcpInfo();
    }

    // 更新连接缓存
    private void updateConnectionsCache() {
        CacheService.ins().evictObject(CacheService.tools_key);
        List<Map<String, Object>> mcpInfo = MonerSystemPrompt.getMcpInfo();
        CacheService.ins().cacheObject(CacheService.tools_key, mcpInfo);
    }

    // 获取mcp server json
    public String fetchMcpJson(String from) {
        FromType fromType = FromType.fromString(from);
        String mcpPath = mcpConfig.getMcpPath(fromType);
        log.info("Checking MCP file existence for {}: {}", fromType, mcpPath);

        if (createFile(fromType)) {
            return null;
        }

        try {
            String content = Files.readString(Paths.get(mcpPath));
            log.info("Successfully read MCP file content for {}", fromType);
            return content;
        } catch (IOException e) {
            log.error("read mcp file error for " + fromType, e);
            return null;
        }
    }

    private boolean createFile(FromType fromType) {
        String mcpPath = mcpConfig.getMcpPath(fromType);
        String mcpDir = mcpConfig.getMcpDir();

        if (!Files.exists(Paths.get(mcpDir)) || !Files.exists(Paths.get(mcpPath))) {
            try {
                log.info("Creating MCP directory and file for {}", fromType);
                Files.createDirectories(Paths.get(mcpDir));
                String defaultConfig = "{\n  \"mcpServers\": {}\n}";
                Files.write(Paths.get(mcpPath), defaultConfig.getBytes(StandardCharsets.UTF_8));
                log.info("Created MCP file for {}", fromType);
            } catch (IOException e) {
                log.error("create mcp dir and file error for " + fromType, e);
                return true;
            }
        }
        return false;
    }

    // 获取所有及单个mcp server的tools,如果没有mcpServerName，则获取所有mcp server的tools
    public String fetchMcpServerTools(String from, String mcpServerName) {
        log.info("Begin fetchMcpServerTools with serverName: {}", mcpServerName);
        Map<String, Map<String, Object>> serverTools = new HashMap<>();

        try {
            List<Map<String, Object>> connections = getMcpConnections();
            // 如果有error
            Optional<Map<String, Object>> serverOpt = connections.stream()
                    .filter(server -> server.get("name").equals(mcpServerName))
                    .findFirst();

            if (serverOpt.isPresent()) {
                McpConnection connection = (McpConnection) serverOpt.get().get("connection");
                if (connection.getServer().getError() != null) {
                    String error = connection.getServer().getError();
                    throw new IOException("fetchMcpServerTools error: " + error);
                }
            }

            List<CompletableFuture<Void>> futures = new ArrayList<>();

            if (StringUtils.isNotBlank(mcpServerName)) {
                Optional<Map<String, Object>> targetServer = connections.stream()
                        .filter(server -> server.get("name").equals(mcpServerName))
                        .findFirst();

                if (targetServer.isPresent()) {
                    McpConnection connection = (McpConnection) targetServer.get().get("connection");
                    if (connection != null) {
                        futures.add(CompletableFuture
                                .runAsync(() -> Safe.run(() -> addServerTools(from, serverTools, mcpServerName, connection))));
                    } else {
                        log.warn("MCP server not found: {}", mcpServerName);
                    }
                } else {
                    connections.forEach(server -> {
                        futures.add(CompletableFuture
                                .runAsync(() -> Safe.run(() -> addServerTools(from, serverTools,
                                    (String)server.get("name"), 
                                    (McpConnection)server.get("connection")))));
                    });
                }

                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                        .get(1, TimeUnit.SECONDS);

                return new ObjectMapper().writeValueAsString(serverTools);
            }
            return "{}";
        } catch (Exception e) {
            log.error("Error fetching MCP server tools", e);
            return "{}";
        }
    }

    public String fetchMcpServerVersion(String from, String mcpServerName) {
        log.info("Begin fetchMcpServerVersion with serverName: {}", mcpServerName);
        try {
            Optional<Map<String, Object>> serverOpt = getMcpConnections().stream()
                    .filter(server -> server.get("name").equals(mcpServerName))
                    .findFirst();
            if (serverOpt.isPresent()) {
                return ((McpConnection) serverOpt.get().get("connection")).getClient().getServerInfo().version();
            }
            return "";
        } catch (Exception e) {
            log.error("Error fetching MCP server version", e);
            return "";
        }
    }

    private void addServerTools(String from, Map<String, Map<String, Object>> serverTools,
                                       String serverName,
                                       McpConnection connection) {
        McpSchema.ListToolsResult tools = connection.getClient().listTools();
        Map<String, Object> toolsMap = new HashMap<>();

        tools.tools().forEach(tool -> {
            Map<String, String> toolInfo = new HashMap<>();
            toolInfo.put("description", tool.description());
            toolInfo.put("inputSchema", GsonUtils.gson.toJson(tool.inputSchema()));
            toolsMap.put(tool.name(), toolInfo);
        });

        serverTools.put(serverName, toolsMap);
    }

    // 获取单/多个mcp server状态
    public String fetchMcpServerStatus(String from, String mcpServerName) {
        log.info("Begin fetchMcpServerStatus with serverName: {}", mcpServerName);

        try {
            // 获取配置内容
            String content = fetchMcpJson(from);
            if (content == null) {
                log.error("Failed to fetch MCP configuration");
                return null;
            }

            // 处理单个服务器状态查询
            if (StringUtils.isNotBlank(mcpServerName)) {
                return createSingleServerStatus(from, mcpServerName);
            }
            return createAllServersStatus(from, fetchMcpJson(from));
        } catch (Exception e) {
            log.error("Error processing MCP server status", e);
            return null;
        }
    }

    private  String createSingleServerStatus(String from, String serverName) throws JsonProcessingException {
        Map<String, String> status = new HashMap<>();
        List<Map<String, Object>> mcpConnectionMap = getMcpConnections();

        Optional<Map<String, Object>> serverOpt = mcpConnectionMap.stream()
                .filter(server -> server.get("name").equals(serverName))
                .findFirst();

        if (serverOpt.isPresent()) {
            McpConnection mcpConnection = (McpConnection) serverOpt.get().get("connection");
            String serverStatus = mcpConnection.getServer().getStatus();
            status.put(serverName, "connected".equals(serverStatus) ? "1" : "0");
        } else {
            status.put(serverName, "0");
        }

        return new ObjectMapper().writeValueAsString(status);
    }

    private String createAllServersStatus(String from, String content) throws JsonProcessingException {
        Map<String, String> allStatus = new HashMap<>();
        List<Map<String, Object>> mcpConnectionMap = getMcpConnections();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(content);
        JsonNode serversNode = rootNode.get("mcpServers");

        if (serversNode != null && serversNode.isObject()) {
            serversNode.fieldNames().forEachRemaining(name -> {
                Optional<Map<String, Object>> serverOpt = mcpConnectionMap.stream()
                        .filter(server -> server.get("name").equals(name))
                        .findFirst();

                if (serverOpt.isPresent()) {
                    McpConnection mcpConnection = (McpConnection) serverOpt.get().get("connection");
                    String serverStatus = mcpConnection.getServer().getStatus();
                    allStatus.put(name, "connected".equals(serverStatus) ? "1" : "0");
                } else {
                    allStatus.put(name, "0");
                }
            });
        }

        return mapper.writeValueAsString(allStatus);
    }

    // 点击Retry Connection 或者 Restart Server,如果有mcpServerName，则只重试该mcp
    // server，否则重试所有mcp server
    public void RetryMcpServerConnection(String from, String mcpServerName) {
        // 刷新连接
        // TODO
//        TeslaAppComponent.refreshMcpHubOneServer(mcpServerName);
//        // 更新缓存
//        updateConnectionsCache();
//        refreshMcpBrowser(project);
    }

    // 在idea里打开mcp配置文件
    public void openMcpFileSettings(String from) {
        log.info("begin openMcpFileSettings");
        // 确保目录和文件存在
        // TODO
//        try {
//            Path dirPath = Paths.get(MCP_DIR);
//            Path filePath = Paths.get(MCP_PATH);
//
//            if (!Files.exists(dirPath)) {
//                Files.createDirectories(dirPath);
//            }
//
//            if (!Files.exists(filePath)) {
//                String defaultConfig = "{\n  \"mcpServers\": {}\n}";
//                Files.write(filePath, defaultConfig.getBytes(StandardCharsets.UTF_8));
//            }
//
//            VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(MCP_PATH);
//            if (virtualFile == null) {
//                log.error("Cannot find virtual file for path: {}", MCP_PATH);
//                return;
//            }
//
//            ApplicationManager.getApplication().invokeLater(() -> {
//                virtualFile.refresh(false, false);
//                FileEditorManager.getInstance(project).openFile(virtualFile, true);
//                IdeFocusManager.getInstance(project).requestFocus(
//                        FileEditorManager.getInstance(project).getSelectedEditor().getComponent(), true);
//            });
//        } catch (IOException e) {
//            log.error("Error opening MCP settings file", e);
//        }
    }

    public void listenToTabSave(String from) {
        FromType fromType = FromType.fromString(from);
        // 确保文件存在
        createFile(fromType);
        
        if (isWatching) {
            return; // 如果已经在监听，则直接返回
        }

        try {
            watchService = FileSystems.getDefault().newWatchService();
            Path mcpDir = Paths.get(mcpConfig.getMcpDir());
            
            // 注册监听目录的文件变化事件
            mcpDir.register(watchService, 
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_CREATE);
            
            isWatching = true;
            
            // 在新线程中启动监听
            CompletableFuture.runAsync(() -> {
                try {
                    while (isWatching) {
                        WatchKey key = watchService.take(); // 阻塞等待事件
                        
                        for (WatchEvent<?> event : key.pollEvents()) {
                            @SuppressWarnings("unchecked")
                            WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                            Path changedFile = pathEvent.context();
                            
                            // 检查是否是我们关注的配置文件
                            if (changedFile.toString().endsWith("_mcp_settings.json")) {
                                log.info("MCP settings file changed: {}", changedFile);
                                handleFileChange(changedFile.toString());
                            }
                        }
                        
                        if (!key.reset()) {
                            log.error("Watch key has been unregistered");
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    log.error("File watching interrupted", e);
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    log.error("Error watching file changes", e);
                }
            });
            
            // 添加关闭钩子
            Runtime.getRuntime().addShutdownHook(new Thread(this::stopWatching));
            
            log.info("Started watching MCP configuration directory: {}", mcpDir);
        } catch (IOException e) {
            log.error("Failed to setup file watching", e);
        }
    }

    private void handleFileChange(String changedFileName) {
        try {
            // 根据文件名判断是哪个from类型的配置发生变化
            FromType fromType = null;
            for (Map.Entry<FromType, String> entry : mcpConfig.getAllMcpPaths().entrySet()) {
                if (entry.getValue().endsWith(changedFileName)) {
                    fromType = entry.getKey();
                    break;
                }
            }
            
            if (fromType != null) {
                String content = fetchMcpJson(fromType.getValue());
                if (content != null) {
                    log.info("Configuration updated for {}", fromType);
                    // TODO: 这里可以添加配置更新后的处理逻辑
                    // 比如发送事件通知其他组件配置已更新
                }
            }
        } catch (Exception e) {
            log.error("Error handling file change", e);
        }
    }

    public void stopWatching() {
        isWatching = false;
        if (watchService != null) {
            try {
                watchService.close();
            } catch (IOException e) {
                log.error("Error closing watch service", e);
            }
        }
    }

    public void refreshMcpHubOneServer(String mcpServerName, String from) {
        log.info("Begin refreshMcpHubOneServer with server: {}", mcpServerName);

        // TODO
//        Safe.run(() -> {
//            McpHub mcpHub = Optional.ofNullable(AthenaContext.ins().getMcpHub())
//                    .orElseThrow(() -> new IllegalStateException("McpHub is not initialized"));
//
//            McpConnection connection = mcpHub.getConnections().get(mcpServerName);
//            if (connection != null) {
//                connection.getClient().closeGracefully();
//                connection.getTransport().closeGracefully();
//                mcpHub.getConnections().remove(mcpServerName);
//            }
//
//            mcpHub.refreshMcpServer(mcpServerName);
//            log.info("Completed refreshing MCP server: {}", mcpServerName);
//
//        }, e -> log.error("Failed to refresh MCP server: {}", mcpServerName, e));
    }
}
