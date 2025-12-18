package run.mone.hive.spring.starter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import java.util.Map;

/**
 * WebSocket 配置类
 *
 * 配置项: mcp.websocket.enabled=true 启用
 */
@Slf4j
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
@EnableConfigurationProperties(WebSocketProperties.class)
@ConditionalOnProperty(name = "mcp.websocket.enabled", havingValue = "true")
public class WebSocketConfig implements WebSocketConfigurer {

    private final run.mone.hive.spring.starter.WebSocketHandler webSocketHandler;
    private final WebSocketProperties webSocketProperties;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        log.info("Registering WebSocket handler at /mcp/ws");
        registry.addHandler(webSocketHandler, "/mcp/ws")
                .addInterceptors(new CorsHandshakeInterceptor())
                .setAllowedOrigins("*")
                .setAllowedOriginPatterns("*");
    }

    /**
     * 跨域握手拦截器，允许所有来源
     */
    private static class CorsHandshakeInterceptor implements HandshakeInterceptor {
        @Override
        public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                       WebSocketHandler wsHandler, Map<String, Object> attributes) {
            // 设置 CORS 响应头
            response.getHeaders().add("Access-Control-Allow-Origin", "*");
            response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.getHeaders().add("Access-Control-Allow-Headers", "*");
            response.getHeaders().add("Access-Control-Allow-Credentials", "true");
            return true;
        }

        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Exception exception) {
            // 握手完成后的处理（如有需要）
        }
    }

    /**
     * 调整 WebSocket 容器的消息缓冲区大小，避免 1009（消息过大）导致的连接关闭。
     */
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        int textSize = webSocketProperties.getMaxTextMessageSize() != null
                ? webSocketProperties.getMaxTextMessageSize()
                : 10 * 1024 * 1024;
        int binarySize = webSocketProperties.getMaxBinaryMessageSize() != null
                ? webSocketProperties.getMaxBinaryMessageSize()
                : 10 * 1024 * 1024;

        container.setMaxTextMessageBufferSize(textSize);
        container.setMaxBinaryMessageBufferSize(binarySize);
        if (webSocketProperties.getAsyncSendTimeout() != null) {
            container.setAsyncSendTimeout(webSocketProperties.getAsyncSendTimeout());
        }
        if (webSocketProperties.getMaxSessionIdleTimeout() != null) {
            container.setMaxSessionIdleTimeout(webSocketProperties.getMaxSessionIdleTimeout());
        }
        return container;
    }
}
