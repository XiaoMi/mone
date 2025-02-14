package run.mone.moner.server.mcp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import run.mone.hive.mcp.hub.McpConnection;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.moner.server.common.GsonUtils;
import run.mone.moner.server.common.Safe;
import run.mone.moner.server.prompt.MonerSystemPrompt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
public class McpOperationService {

    public static String MCP_PATH = System.getProperty("user.home") + "/.mcp/athena_mcp_settings.json";
    public static String MCP_DIR = System.getProperty("user.home") + "/.mcp";

    public static String MCP_FILE = "athena_mcp_settings.json";

    // 获取 MCP 连接，优先从缓存获取
    private static List<Map<String, Object>> getMcpConnections() {
        return MonerSystemPrompt.getMcpInfo();
    }

    // 更新连接缓存
    private static void updateConnectionsCache() {
        CacheService.ins().evictObject(CacheService.tools_key);
        List<Map<String, Object>> mcpInfo = MonerSystemPrompt.getMcpInfo();
        CacheService.ins().cacheObject(CacheService.tools_key, mcpInfo);
    }

    // 获取mcp server json
    public static String fetchMcpJson() {
        String res = "";
        log.info("Checking MCP file existence: {}", MCP_PATH);

        if (createFile())
            return null;
        // 获取文件内容
        String content = null;
        try {
            content = Files.readString(Paths.get(MCP_PATH));
            log.info("Successfully read MCP file content");
            // refresh mcp server
            // TeslaAppComponent.refreshMcpHub(content);
        } catch (IOException e) {
            log.error("read mcp file error", e);
            return null;
        }
        // 前端渲染
        res = content;
        return res;
    }

    private static boolean createFile() {
        if (!Files.exists(Paths.get(MCP_DIR)) || !Files.exists(Paths.get(MCP_PATH))) {
            try {
                log.info("Creating MCP directory and file");
                Files.createDirectories(Paths.get(MCP_DIR));
                // 创建默认配置文件
                String defaultConfig = "{\n  \"mcpServers\": {}\n}";
                Files.write(Paths.get(MCP_PATH), defaultConfig.getBytes(StandardCharsets.UTF_8));

                // TODO 刷新文件系统以识别新创建的文件
//                LocalFileSystem.getInstance().refreshAndFindFileByPath(MCP_PATH);

                log.info("Created and refreshed MCP file");
            } catch (IOException e) {
                log.error("create mcp dir and file error", e);
                return true;
            }
        }
        return false;
    }

    // 获取所有及单个mcp server的tools,如果没有mcpServerName，则获取所有mcp server的tools
    public static String fetchMcpServerTools(String from, String mcpServerName) {
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

    public static String fetchMcpServerVersion(String from, String mcpServerName) {
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

    private static void addServerTools(String from, Map<String, Map<String, Object>> serverTools,
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
    public static String fetchMcpServerStatus(String from, String mcpServerName) {
        log.info("Begin fetchMcpServerStatus with serverName: {}", mcpServerName);

        try {
            // 获取配置内容
            String content = fetchMcpJson();
            if (content == null) {
                log.error("Failed to fetch MCP configuration");
                return null;
            }

            // 处理单个服务器状态查询
            if (StringUtils.isNotBlank(mcpServerName)) {
                return createSingleServerStatus(from, mcpServerName);
            }
            return createAllServersStatus(from, fetchMcpJson());
        } catch (Exception e) {
            log.error("Error processing MCP server status", e);
            return null;
        }
    }

    private static String createSingleServerStatus(String from, String serverName) throws JsonProcessingException {
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

    private static String createAllServersStatus(String from, String content) throws JsonProcessingException {
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
    public static void RetryMcpServerConnection(String from, String mcpServerName) {
        // 刷新连接
        // TODO
//        TeslaAppComponent.refreshMcpHubOneServer(mcpServerName);
//        // 更新缓存
//        updateConnectionsCache();
//        refreshMcpBrowser(project);
    }

    // 在idea里打开mcp配置文件
    public static void openMcpFileSettings(String from) {
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

    public static void listenToTabSave(String from) {
        // 监听的时候可能没有文件，先创建
        createFile();

        // TODO
//        VirtualFile file = LocalFileSystem.getInstance().refreshAndFindFileByPath(MCP_PATH);
//        log.info("Looking for MCP file after refresh: {}, found: {}", MCP_PATH, file != null);
//
//        if (file != null) {
//            Document document = FileDocumentManager.getInstance().getDocument(file);
//            if (document != null) {
//                document.addDocumentListener(new DocumentAdapter() {
//                    @Override
//                    public void documentChanged(@NotNull DocumentEvent e) {
//                        log.info("MCP settings file content has been changed: {}", file.getPath());
//                        //TeslaAppComponent.refreshMcpHub(fetchMcpJson());
//                        refreshMcpBrowser(project);
//                    }
//                }, project);
//
//                project.getMessageBus().connect().subscribe(
//                        VirtualFileManager.VFS_CHANGES,
//                        new BulkFileListener() {
//                            @Override
//                            public void after(@NotNull List<? extends VFileEvent> events) {
//                                for (VFileEvent event : events) {
//                                    VirtualFile changedFile = event.getFile();
//                                    if (changedFile != null && changedFile.getPath().equals(MCP_PATH)) {
//                                        log.info("MCP settings file has been saved: {}", MCP_PATH);
//                                        //TeslaAppComponent.refreshMcpHub(fetchMcpJson());
//                                        refreshMcpBrowser(project);
//                                    }
//                                }
//                            }
//                        });
//            } else {
//                log.error("Could not get document for file: {}", MCP_PATH);
//            }
//        } else {
//            log.error("Could not find MCP file even after refresh: {}", MCP_PATH);
//        }
//
//        project.getMessageBus().connect().subscribe(ProjectManager.TOPIC, new ProjectManagerListener() {
//            @Override
//            public void projectClosing(@NotNull Project project) {
//                JBCefBrowser browser = UltrmanTreeKeyAdapter.browserMap.remove(project.getName());
//                if (browser != null) {
//                    browser.dispose();
//                }
//            }
//        });
    }


    public static void refreshMcpHubOneServer(String mcpServerName, String from) {
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
