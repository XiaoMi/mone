package run.mone.hive.spring.starter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * WebSocket 配置属性
 * prefix: mcp.websocket
 */
@Data
@ConfigurationProperties(prefix = "mcp.websocket")
public class WebSocketProperties {
    /** 是否启用 WebSocket */
    private Boolean enabled = Boolean.FALSE;

    /** 文本消息最大大小（字节），默认 10MB */
    private Integer maxTextMessageSize = 10 * 1024 * 1024;

    /** 二进制消息最大大小（字节），默认 10MB */
    private Integer maxBinaryMessageSize = 10 * 1024 * 1024;

    /** 异步发送超时时间（毫秒），可选 */
    private Long asyncSendTimeout;

    /** 会话最大空闲时间（毫秒），可选 */
    private Long maxSessionIdleTimeout;
}

