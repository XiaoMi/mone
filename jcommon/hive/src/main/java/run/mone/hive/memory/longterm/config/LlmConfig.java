package run.mone.hive.memory.longterm.config;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.HashMap;

/**
 * LLM配置类
 * 支持多种LLM提供商：OpenAI、Claude、Gemini、Ollama等
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LlmConfig {
    
    /**
     * LLM提供商类型
     */
    public enum Provider {
        OPENAI("openai"),
        CLAUDE("claude"),
        GEMINI("gemini"),
        OLLAMA("ollama"),
        GROQ("groq"),
        AZURE_OPENAI("azure_openai"),
        BEDROCK("bedrock"),
        TOGETHER("together"),
        DEEPSEEK("deepseek"),
        XAI("xai");
        
        private final String value;
        
        Provider(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static Provider fromString(String value) {
            for (Provider provider : Provider.values()) {
                if (provider.value.equalsIgnoreCase(value)) {
                    return provider;
                }
            }
            throw new IllegalArgumentException("Unknown LLM provider: " + value);
        }
    }
    
    /**
     * 提供商
     */
    @Builder.Default
    private Provider provider = Provider.OPENAI;
    
    /**
     * 模型名称
     */
    @Builder.Default
    private String model = "gpt-4o-mini";
    
    /**
     * API密钥
     */
    private String apiKey;
    
    /**
     * 基础URL
     */
    private String baseUrl;
    
    /**
     * 温度参数
     */
    @Builder.Default
    private double temperature = 0.1;
    
    /**
     * 最大令牌数
     */
    @Builder.Default
    private int maxTokens = 4000;
    
    /**
     * top_p参数
     */
    @Builder.Default
    private double topP = 1.0;
    
    /**
     * 是否启用视觉功能
     */
    @Builder.Default
    private boolean enableVision = false;
    
    /**
     * 视觉详细级别
     */
    @Builder.Default
    private String visionDetails = "low";
    
    /**
     * 额外配置
     */
    @Builder.Default
    private Map<String, Object> config = new HashMap<>();
    
    /**
     * 从Map创建配置
     */
    public static LlmConfig fromMap(Map<String, Object> configMap) {
        LlmConfig.LlmConfigBuilder builder = LlmConfig.builder();
        
        if (configMap.containsKey("provider")) {
            String providerStr = (String) configMap.get("provider");
            builder.provider(Provider.fromString(providerStr));
        }
        
        if (configMap.containsKey("model")) {
            builder.model((String) configMap.get("model"));
        }
        
        if (configMap.containsKey("apiKey")) {
            builder.apiKey((String) configMap.get("apiKey"));
        }
        
        if (configMap.containsKey("baseUrl")) {
            builder.baseUrl((String) configMap.get("baseUrl"));
        }
        
        if (configMap.containsKey("temperature")) {
            Object temp = configMap.get("temperature");
            if (temp instanceof Number) {
                builder.temperature(((Number) temp).doubleValue());
            }
        }
        
        if (configMap.containsKey("maxTokens")) {
            Object maxTokens = configMap.get("maxTokens");
            if (maxTokens instanceof Number) {
                builder.maxTokens(((Number) maxTokens).intValue());
            }
        }
        
        if (configMap.containsKey("topP")) {
            Object topP = configMap.get("topP");
            if (topP instanceof Number) {
                builder.topP(((Number) topP).doubleValue());
            }
        }
        
        if (configMap.containsKey("enableVision")) {
            builder.enableVision((Boolean) configMap.get("enableVision"));
        }
        
        if (configMap.containsKey("visionDetails")) {
            builder.visionDetails((String) configMap.get("visionDetails"));
        }
        
        if (configMap.containsKey("config")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> config = (Map<String, Object>) configMap.get("config");
            builder.config(config);
        }
        
        return builder.build();
    }
    
    /**
     * 获取OpenAI默认配置
     */
    public static LlmConfig openAiDefault() {
        return LlmConfig.builder()
                .provider(Provider.OPENAI)
                .model("gpt-4o-mini")
                .temperature(0.1)
                .maxTokens(4000)
                .topP(1.0)
                .build();
    }
    
    /**
     * 获取Claude默认配置
     */
    public static LlmConfig claudeDefault() {
        return LlmConfig.builder()
                .provider(Provider.CLAUDE)
                .model("claude-3-haiku-20240307")
                .temperature(0.1)
                .maxTokens(4000)
                .topP(1.0)
                .build();
    }
    
    /**
     * 获取Ollama默认配置
     */
    public static LlmConfig ollamaDefault() {
        return LlmConfig.builder()
                .provider(Provider.OLLAMA)
                .model("llama2")
                .baseUrl("http://localhost:11434")
                .temperature(0.1)
                .maxTokens(4000)
                .topP(1.0)
                .build();
    }
}
