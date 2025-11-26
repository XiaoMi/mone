package run.mone.mcp.minimaxrealtime.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * WebSocket 配置类
 * @author renqingfu
 * @Date 2025/5/22 16:25
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "minimax.realtime")
public class WebSocketConfig {

    /**
     * MiniMax Realtime API WebSocket URL
     */
    private String url = "wss://api.minimax.chat/ws/v1/realtime";

    /**
     * 默认使用的模型
     */
    private String model = "abab6.5s-chat";

    /**
     * 最大重连尝试次数
     */
    private int maxReconnectAttempts = 5;

    /**
     * 连接超时时间（毫秒）
     */
    private long connectionTimeout = 30000;

    /**
     * 最大消息大小（字节）
     */
    private int maxMessageSize = 2 * 1024 * 1024; // 2MB

    /**
     * 重连间隔时间（毫秒）
     */
    private long reconnectInterval = 5000;

    private String apiKey;

    /**
     * 默认语音类型
     */
    private String defaultVoice = "female-yujie";

    /**
     * 默认输入音频格式
     */
    private String defaultInputAudioFormat = "pcm16";

    /**
     * 默认输出音频格式
     */
    private String defaultOutputAudioFormat = "pcm16";

    /**
     * 默认温度参数
     */
    private double defaultTemperature = 0.8;

    /**
     * 默认最大响应token数
     */
    private String defaultMaxResponseOutputTokens = "10000";

    /**
     * 默认系统指令
     */
    private String defaultInstructions = "你是一位优秀的助理，请根据用户的问题给出帮助。";
} 