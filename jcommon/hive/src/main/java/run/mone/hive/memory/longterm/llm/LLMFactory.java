package run.mone.hive.memory.longterm.llm;

import run.mone.hive.memory.longterm.config.LlmConfig;
import run.mone.hive.memory.longterm.llm.impl.*;

/**
 * LLM工厂类
 * 根据配置创建相应的LLM实例
 */
public class LLMFactory {
    
    /**
     * 创建LLM实例
     * 
     * @param config LLM配置
     * @return LLM实例
     */
    public static LLMBase create(LlmConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("LLM config cannot be null");
        }
        
        switch (config.getProvider()) {
            case OPENAI:
                return new OpenAiLLM(config);
            case CLAUDE:
                return new ClaudeLLM(config);
            case GEMINI:
                return new GeminiLLM(config);
            case OLLAMA:
                return new OllamaLLM(config);
            case GROQ:
                return new GroqLLM(config);
            case AZURE_OPENAI:
                return new AzureOpenAiLLM(config);
            case BEDROCK:
                return new BedrockLLM(config);
            case TOGETHER:
                return new TogetherLLM(config);
            case DEEPSEEK:
                return new DeepSeekLLM(config);
            case XAI:
                return new XaiLLM(config);
            default:
                throw new IllegalArgumentException("Unsupported LLM provider: " + config.getProvider());
        }
    }
    
    /**
     * 创建默认的OpenAI LLM实例
     * 
     * @return OpenAI LLM实例
     */
    public static LLMBase createDefault() {
        return create(LlmConfig.openAiDefault());
    }
    
    /**
     * 检查提供商是否被支持
     * 
     * @param provider 提供商
     * @return 是否支持
     */
    public static boolean isProviderSupported(LlmConfig.Provider provider) {
        try {
            create(LlmConfig.builder().provider(provider).model("test").build());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
