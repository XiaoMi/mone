package run.mone.hive.spring.starter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.configs.Const;
import run.mone.hive.mcp.function.McpFunction;
import run.mone.hive.mcp.server.McpAsyncServer;
import run.mone.hive.mcp.server.McpServer.ToolStreamRegistration;
import run.mone.hive.mcp.server.McpSyncServer;
import run.mone.hive.mcp.server.transport.streamable.HttpServletServerLoader;
import run.mone.hive.mcp.server.transport.streamable.HttpServletStreamableServerTransport;
import run.mone.hive.mcp.spec.McpSchema.ServerCapabilities;
import run.mone.hive.mcp.spec.McpSchema.Tool;
import run.mone.hive.mcp.spec.ServerMcpTransport;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class McpServer {

    private final ServerMcpTransport transport;

    private McpSyncServer syncServer;

    private McpAsyncServer asyncServer;

    private HttpServletServerLoader httpServerLoader;

    private final List<McpFunction> functionList;

    private final Map<String, String> meta;

    public McpSyncServer start() {
        // 如果 transport 是 HttpServletStreamableServerTransport 类型，使用 HttpServletServerLoader 方式
        if (transport instanceof HttpServletStreamableServerTransport) {
            return startWithHttpLoader();
        }

        // 其他类型使用原有方式
        McpSyncServer syncServer = run.mone.hive.mcp.server.McpServer.using(transport)
                .serverInfo(meta.getOrDefault(Const.AGENT_SERVER_NAME, "ai_agent_server"), meta.getOrDefault(Const.AGENT_SERVER_VERSION, "0.0.1"), meta)
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .sync();

        functionList.forEach(function -> {
            if (function.getName().startsWith("stream_")) {
                var toolStreamRegistration = new ToolStreamRegistration(
                        new Tool(function.getName(), function.getDesc(), function.getToolScheme()), function
                );
                syncServer.addStreamTool(toolStreamRegistration);
            } else {
                syncServer.addTool(new run.mone.hive.mcp.server.McpServer.ToolRegistration(new Tool(function.getName(), function.getDesc(), function.getToolScheme()), function));
            }
        });

        return syncServer;
    }

    /**
     * 使用 HttpServletServerLoader 方式启动服务器
     * 参考 HttpServletMcpServerExample 的实现
     */
    private McpSyncServer startWithHttpLoader() {
        log.info("检测到 HTTP transport，使用 HttpServletServerLoader 方式启动服务器");

        // 从 meta 中获取配置参数，如果没有则使用默认值
        int port = Integer.parseInt(meta.getOrDefault(Const.HTTP_PORT, "8081"));
        String mcpEndpoint = meta.getOrDefault(Const.HTTP_ENDPOINT, "/mcp");
        int keepAliveSeconds = Integer.parseInt(meta.getOrDefault(Const.HTTP_KEEPALIVE_SECONDS, "30"));
        boolean disallowDelete = Boolean.parseBoolean(meta.getOrDefault(Const.HTTP_DISALLOW_DELETE, "false"));
        boolean enableAuth = Boolean.parseBoolean(meta.getOrDefault(Const.HTTP_ENABLE_AUTH, "false"));

        // 创建 ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 创建 HttpServletServerLoader
        httpServerLoader = HttpServletServerLoader.builder()
                .port(port)
                .mcpEndpoint(mcpEndpoint)
                .keepAliveInterval(Duration.ofSeconds(keepAliveSeconds))
                .disallowDelete(disallowDelete)
                .enableAuth(enableAuth)
                .objectMapper(objectMapper)
                .authFunction(this::authenticateClient)
                .build();

        // 创建并配置异步 MCP 服务器
        asyncServer = httpServerLoader.createServerBuilder()
                .serverInfo(
                        meta.getOrDefault(Const.AGENT_SERVER_NAME, "ai_agent_server"),
                        meta.getOrDefault(Const.AGENT_SERVER_VERSION, "0.0.1"),
                        meta
                )
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .async();

        // 注册工具
        functionList.forEach(function -> {
            // 将 asyncServer 实例注入到 McpFunction 中，使其能够调用 loggingNotification 等方法
            function.setMcpAsyncServer(asyncServer);

            if (function.getName().startsWith("stream_")) {
                var toolStreamRegistration = new ToolStreamRegistration(
                        new Tool(function.getName(), function.getDesc(), function.getToolScheme()), function
                );
                asyncServer.addStreamTool(toolStreamRegistration);
            } else {
                asyncServer.addTool(new run.mone.hive.mcp.server.McpServer.ToolRegistration(new Tool(function.getName(), function.getDesc(), function.getToolScheme()), function));
            }
        });

        // 启动 HTTP 服务器
        try {
            httpServerLoader.start();
            log.info("=== MCP HTTP 服务器已启动 ===");
            log.info("服务器地址: {}", httpServerLoader.getServerUrl());
            log.info("服务器状态: {}", httpServerLoader.isRunning() ? "运行中" : "已停止");
            log.info("注册工具数量: {}", functionList.size());
        } catch (Exception e) {
            log.error("启动 HTTP 服务器失败", e);
            throw new RuntimeException("启动 HTTP 服务器失败", e);
        }

        // 注意：由于 HttpServletServerLoader 使用异步服务器，
        // 这里返回 null 表示不使用同步服务器
        // 实际的服务器实例存储在 asyncServer 字段中
        return null;
    }

    /**
     * 客户端认证函数
     * 可以通过 meta 配置来自定义认证逻辑
     */
    private boolean authenticateClient(String clientId) {
        log.info("认证客户端: {}", clientId);
        // 简单示例：允许所有客户端
        // 可以在这里添加自定义认证逻辑，例如检查客户端 ID 是否在白名单中
        return true;
    }

    @PostConstruct
    public void init() {
        this.syncServer = start();
    }

    @PreDestroy
    public void stop() {
        if (this.httpServerLoader != null) {
            log.info("正在关闭 HTTP 服务器...");
            this.httpServerLoader.stop();
            log.info("HTTP 服务器已关闭");
        }
        if (this.syncServer != null) {
            this.syncServer.closeGracefully();
        }
        if (this.asyncServer != null) {
            this.asyncServer.closeGracefully();
        }
    }
}
