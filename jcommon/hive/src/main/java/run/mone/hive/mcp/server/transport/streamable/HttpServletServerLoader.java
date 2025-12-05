package run.mone.hive.mcp.server.transport.streamable;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import run.mone.hive.mcp.server.McpAsyncServer;
import run.mone.hive.mcp.server.McpServer;

import java.time.Duration;
import java.util.function.Function;

/**
 * HTTP Servlet 服务器加载器，用于在 hive 项目中加载和管理 HttpServletStreamableServerTransport
 * 
 * <p>这个类提供了与原始 core 项目相同的加载方式，包括：
 * <ul>
 * <li>创建和配置 HttpServletStreamableServerTransport</li>
 * <li>与 Servlet 容器（如 Tomcat）集成</li>
 * <li>管理服务器生命周期</li>
 * </ul>
 * 
 * <p>使用示例：
 * <pre>{@code
 * HttpServletServerLoader loader = HttpServletServerLoader.builder()
 *     .port(8080)
 *     .mcpEndpoint("/mcp")
 *     .keepAliveInterval(Duration.ofSeconds(30))
 *     .build();
 * 
 * // 配置 MCP 服务器
 * McpAsyncServer server = loader.createAsyncServer()
 *     .serverInfo("my-server", "1.0.0")
 *     .tool(tool, handler)
 *     .build();
 * 
 * // 启动服务器
 * loader.start();
 * }</pre>
 * 
 * @author Adapted for hive MCP framework
 */
@Slf4j
public class HttpServletServerLoader {
    
    private final int port;
    private final String mcpEndpoint;
    private final Duration keepAliveInterval;
    private final boolean disallowDelete;
    private final boolean enableAuth;
    private final ObjectMapper objectMapper;
    private final Function<String, Boolean> authFunction;
    
    private HttpServletStreamableServerTransport transport;
    private Tomcat tomcat;
    private McpAsyncServer mcpServer;
    
    private HttpServletServerLoader(Builder builder) {
        this.port = builder.port;
        this.mcpEndpoint = builder.mcpEndpoint;
        this.keepAliveInterval = builder.keepAliveInterval;
        this.disallowDelete = builder.disallowDelete;
        this.enableAuth = builder.enableAuth;
        this.objectMapper = builder.objectMapper;
        this.authFunction = builder.authFunction;
    }
    
    /**
     * 创建 MCP 服务器构建器
     * @return McpServer.Builder 实例
     */
    public McpServer.Builder createServerBuilder() {
        ensureTransportCreated();
        return McpServer.using(transport);
    }
    
    /**
     * 获取底层的传输实现
     * @return HttpServletStreamableServerTransport 实例
     */
    public HttpServletStreamableServerTransport getTransport() {
        ensureTransportCreated();
        return transport;
    }
    
    /**
     * 启动 HTTP 服务器
     * @throws RuntimeException 如果启动失败
     */
    public void start() {
        try {
            ensureTransportCreated();
            ensureTomcatCreated();
            
            tomcat.start();
            log.info("MCP HTTP 服务器已启动，端口: {}, 端点: {}", port, mcpEndpoint);
            
        } catch (Throwable e) {
            throw new RuntimeException("启动 HTTP 服务器失败", e);
        }
    }
    
    /**
     * 停止 HTTP 服务器
     */
    public void stop() {
        if (tomcat != null) {
            try {
                tomcat.stop();
                tomcat.destroy();
                log.info("MCP HTTP 服务器已停止");
            } catch (LifecycleException e) {
                log.error("停止 HTTP 服务器时出错", e);
            }
        }
        
        if (transport != null) {
            transport.closeGracefully().block();
        }
    }
    
    /**
     * 检查服务器是否正在运行
     * @return true 如果服务器正在运行
     */
    public boolean isRunning() {
        return tomcat != null && tomcat.getServer().getState().isAvailable();
    }
    
    /**
     * 获取服务器访问 URL
     * @return 服务器 URL
     */
    public String getServerUrl() {
        return String.format("http://localhost:%d%s", port, mcpEndpoint);
    }
    
    private void ensureTransportCreated() {
        if (transport == null) {
            HttpServletStreamableServerTransport.Builder builder = HttpServletStreamableServerTransport.builder()
                    .objectMapper(objectMapper)
                    .mcpEndpoint(mcpEndpoint)
                    .disallowDelete(disallowDelete);
            
            if (keepAliveInterval != null) {
                builder.keepAliveInterval(keepAliveInterval);
            }
            if (enableAuth) {
                builder.tokenValidator(new HttpTokenValidator()); // Reads from TOKEN_VALIDATION_ENDPOINT
                builder.tokenCacheTtl(Duration.ofMinutes(5)); // Cache tokens for 5 minutes
            }
            
            transport = builder.build();
            
            if (authFunction != null) {
                transport.setAuthFunction(authFunction);
            }
        }
    }
    
    @SneakyThrows
    private void ensureTomcatCreated() {
        if (tomcat == null) {
            tomcat = new Tomcat();
            tomcat.setPort(port);
            // 必须调用 getConnector() 来创建 HTTP connector，否则 Tomcat 不会监听端口
            var connector = tomcat.getConnector();

            // 配置连接器以减少 EOFException 日志噪音
            connector.setProperty("connectionTimeout", "60000"); // 60秒连接超时
            connector.setProperty("keepAliveTimeout", "60000");  // Keep-alive 超时
            connector.setProperty("maxKeepAliveRequests", "100"); // 最大 keep-alive 请求数
            connector.setProperty("socket.soTimeout", "60000");  // Socket 读取超时

            // 设置工作目录
            String baseDir = System.getProperty("java.io.tmpdir");
            tomcat.setBaseDir(baseDir);

            // 创建上下文
            Context context = tomcat.addContext("", baseDir);
            
            // 添加 Servlet
            String servletName = "McpServlet";
            tomcat.addServlet(context, servletName, transport);
            context.addServletMappingDecoded(mcpEndpoint + "/*", servletName);
//            tomcat.start();
            
            // 注意：Tomcat Context 没有 setAsyncSupported 方法
            // 异步支持通过 @WebServlet(asyncSupported = true) 注解启用
        }
    }
    
    /**
     * 创建构建器
     * @return Builder 实例
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * HttpServletServerLoader 的构建器
     */
    public static class Builder {
        private int port = 8080;
        private String mcpEndpoint = "/mcp";
        private Duration keepAliveInterval;
        private boolean disallowDelete = false;
        private boolean enableAuth = false;
        private ObjectMapper objectMapper = new ObjectMapper();
        private Function<String, Boolean> authFunction;
        
        /**
         * 设置服务器端口
         * @param port 端口号
         * @return this builder instance
         */
        public Builder port(int port) {
            this.port = port;
            return this;
        }
        
        /**
         * 设置 MCP 端点路径
         * @param mcpEndpoint 端点路径
         * @return this builder instance
         */
        public Builder mcpEndpoint(String mcpEndpoint) {
            this.mcpEndpoint = mcpEndpoint;
            return this;
        }
        
        /**
         * 设置心跳间隔
         * @param keepAliveInterval 心跳间隔
         * @return this builder instance
         */
        public Builder keepAliveInterval(Duration keepAliveInterval) {
            this.keepAliveInterval = keepAliveInterval;
            return this;
        }
        
        /**
         * 设置是否禁止 DELETE 请求
         * @param disallowDelete true 禁止 DELETE 请求
         * @return this builder instance
         */
        public Builder disallowDelete(boolean disallowDelete) {
            this.disallowDelete = disallowDelete;
            return this;
        }

        /**
         * 设置是否启用认证
         * @param enableAuth
         * @return
         */
        public Builder enableAuth(boolean enableAuth) {
            this.enableAuth = enableAuth;
            return this;
        }
        
        /**
         * 设置 JSON 序列化器
         * @param objectMapper ObjectMapper 实例
         * @return this builder instance
         */
        public Builder objectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }
        
        /**
         * 设置认证函数
         * @param authFunction 认证函数
         * @return this builder instance
         */
        public Builder authFunction(Function<String, Boolean> authFunction) {
            this.authFunction = authFunction;
            return this;
        }
        
        /**
         * 构建 HttpServletServerLoader 实例
         * @return HttpServletServerLoader 实例
         */
        public HttpServletServerLoader build() {
            return new HttpServletServerLoader(this);
        }
    }
}
