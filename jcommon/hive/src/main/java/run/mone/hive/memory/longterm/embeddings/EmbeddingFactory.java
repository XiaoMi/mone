package run.mone.hive.memory.longterm.embeddings;

import run.mone.hive.memory.longterm.config.EmbedderConfig;
import run.mone.hive.memory.longterm.embeddings.impl.*;

/**
 * 嵌入模型工厂类
 * 根据配置创建相应的嵌入模型实例
 */
public class EmbeddingFactory {
    
    /**
     * 创建嵌入模型实例
     * 
     * @param config 嵌入器配置
     * @return 嵌入模型实例
     */
    public static EmbeddingBase create(EmbedderConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Embedder config cannot be null");
        }
        
        switch (config.getProvider()) {
            case OPENAI:
                return new OpenAiEmbedding(config);
            case AZURE_OPENAI:
                return new AzureOpenAiEmbedding(config);
            case HUGGINGFACE:
                return new HuggingFaceEmbedding(config);
            case OLLAMA:
                return new OllamaEmbedding(config);
            case GEMINI:
                return new GeminiEmbedding(config);
            case BEDROCK:
                return new BedrockEmbedding(config);
            case VERTEXAI:
                return new VertexAiEmbedding(config);
            case TOGETHER:
                return new TogetherEmbedding(config);
            default:
                throw new IllegalArgumentException("Unsupported embedding provider: " + config.getProvider());
        }
    }
    
    /**
     * 创建默认的OpenAI嵌入实例
     * 
     * @return OpenAI嵌入实例
     */
    public static EmbeddingBase createDefault() {
        return create(EmbedderConfig.openAiDefault());
    }
    
    /**
     * 检查提供商是否被支持
     * 
     * @param provider 提供商
     * @return 是否支持
     */
    public static boolean isProviderSupported(EmbedderConfig.Provider provider) {
        try {
            create(EmbedderConfig.builder().provider(provider).model("test").build());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
