package run.mone.hive.memory.config;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.service.RoleMemoryConfig;
import run.mone.hive.memory.longterm.config.LlmConfig;
import run.mone.hive.memory.longterm.config.EmbedderConfig;
import run.mone.hive.memory.longterm.config.VectorStoreConfig;
import run.mone.hive.memory.longterm.config.GraphStoreConfig;

/**
 * MemoryConfig转换器
 * 将mcp.service.MemoryConfig转换为longterm.config.MemoryConfig
 * 
 * @author goodjava@qq.com
 * @date 2025/1/1 00:00
 */
@Slf4j
public class MemoryConfigConverter {

    /**
     * 将MCP服务的MemoryConfig转换为长期记忆的MemoryConfig
     * 
     * @param mcpConfig MCP服务的记忆配置
     * @return 长期记忆的配置对象
     */
    public static run.mone.hive.memory.longterm.config.MemoryConfig convert(RoleMemoryConfig mcpConfig) {
        if (mcpConfig == null) {
            log.warn("输入的MemoryConfig为null，返回默认配置");
            return run.mone.hive.memory.longterm.config.MemoryConfig.getDefault();
        }

        try {
            run.mone.hive.memory.longterm.config.MemoryConfig.MemoryConfigBuilder builder = 
                run.mone.hive.memory.longterm.config.MemoryConfig.builder();

            // 转换LLM配置
            if (mcpConfig.getLlm() != null) {
                LlmConfig llmConfig = convertLlmConfig(mcpConfig.getLlm());
                builder.llm(llmConfig);
            }

            // 转换嵌入模型配置
            if (mcpConfig.getEmbedder() != null) {
                EmbedderConfig embedderConfig = convertEmbedderConfig(mcpConfig.getEmbedder());
                builder.embedder(embedderConfig);
            }

            // 转换向量存储配置
            if (mcpConfig.getVectorStore() != null) {
                VectorStoreConfig vectorStoreConfig = convertVectorStoreConfig(mcpConfig.getVectorStore());
                builder.vectorStore(vectorStoreConfig);
            }

            // 转换图存储配置
            if (mcpConfig.getGraphStore() != null) {
                GraphStoreConfig graphStoreConfig = convertGraphStoreConfig(mcpConfig.getGraphStore());
                builder.graphStore(graphStoreConfig);
            }

            // 设置历史数据库路径
            if (mcpConfig.getHistoryDbPath() != null) {
                builder.historyDbPath(mcpConfig.getHistoryDbPath());
            }

            // 设置版本
            if (mcpConfig.getVersion() != null) {
                builder.version(mcpConfig.getVersion());
            }

            run.mone.hive.memory.longterm.config.MemoryConfig result = builder.build();
            log.info("MemoryConfig转换成功");
            return result;

        } catch (Exception e) {
            log.error("MemoryConfig转换失败，返回默认配置", e);
            return run.mone.hive.memory.longterm.config.MemoryConfig.getDefault();
        }
    }

    /**
     * 转换LLM配置
     */
    private static LlmConfig convertLlmConfig(RoleMemoryConfig.LlmConfig mcpLlmConfig) {
        if (mcpLlmConfig == null) {
            return null;
        }

        LlmConfig.LlmConfigBuilder builder = LlmConfig.builder();

        if (mcpLlmConfig.getProviderName() != null) {
            builder.providerName(mcpLlmConfig.getProviderName());
        }

        if (mcpLlmConfig.getModel() != null) {
            builder.model(mcpLlmConfig.getModel());
        }

        // 设置响应格式
        builder.responseJsonFormat(mcpLlmConfig.isResponseJsonFormat() ? "json" : "text");

        return builder.build();
    }

    /**
     * 转换嵌入模型配置
     */
    private static EmbedderConfig convertEmbedderConfig(RoleMemoryConfig.EmbedderConfig mcpEmbedderConfig) {
        if (mcpEmbedderConfig == null) {
            return null;
        }

        EmbedderConfig.EmbedderConfigBuilder builder = EmbedderConfig.builder();

        if (mcpEmbedderConfig.getProvider() != null) {
            // 转换提供商名称
            try {
                EmbedderConfig.Provider provider = EmbedderConfig.Provider.fromString(mcpEmbedderConfig.getProvider());
                builder.provider(provider);
            } catch (IllegalArgumentException e) {
                log.warn("未知的嵌入模型提供商: {}, 使用默认值", mcpEmbedderConfig.getProvider());
                builder.provider(EmbedderConfig.Provider.OLLAMA);
            }
        }

        if (mcpEmbedderConfig.getModel() != null) {
            builder.model(mcpEmbedderConfig.getModel());
        }

        if (mcpEmbedderConfig.getEmbeddingDims() > 0) {
            builder.embeddingDims(mcpEmbedderConfig.getEmbeddingDims());
        }

        return builder.build();
    }

    /**
     * 转换向量存储配置
     */
    private static VectorStoreConfig convertVectorStoreConfig(RoleMemoryConfig.VectorStoreConfig mcpVectorConfig) {
        if (mcpVectorConfig == null) {
            return null;
        }

        VectorStoreConfig.VectorStoreConfigBuilder builder = VectorStoreConfig.builder();

        if (mcpVectorConfig.getProvider() != null) {
            // 转换提供商名称
            try {
                VectorStoreConfig.Provider provider = VectorStoreConfig.Provider.fromString(mcpVectorConfig.getProvider());
                builder.provider(provider);
            } catch (IllegalArgumentException e) {
                log.warn("未知的向量存储提供商: {}, 使用默认值", mcpVectorConfig.getProvider());
                builder.provider(VectorStoreConfig.Provider.CHROMA);
            }
        }

        if (mcpVectorConfig.getPort() > 0) {
            builder.port(mcpVectorConfig.getPort());
        }

        if (mcpVectorConfig.getCollectionName() != null) {
            builder.collectionName(mcpVectorConfig.getCollectionName());
        }

        if (mcpVectorConfig.getEmbeddingModelDims() > 0) {
            builder.embeddingModelDims(mcpVectorConfig.getEmbeddingModelDims());
        }

        if (mcpVectorConfig.getEmbeddingFunction() != null) {
            builder.embeddingFunction(mcpVectorConfig.getEmbeddingFunction());
        }

        if (mcpVectorConfig.getModel() != null) {
            builder.model(mcpVectorConfig.getModel());
        }

        builder.enable(mcpVectorConfig.isEnable());

        return builder.build();
    }

    /**
     * 转换图存储配置
     */
    private static GraphStoreConfig convertGraphStoreConfig(RoleMemoryConfig.GraphStoreConfig mcpGraphConfig) {
        if (mcpGraphConfig == null) {
            return null;
        }

        GraphStoreConfig.GraphStoreConfigBuilder builder = GraphStoreConfig.builder();

        if (mcpGraphConfig.getProvider() != null) {
            // 转换提供商名称
            try {
                GraphStoreConfig.Provider provider = GraphStoreConfig.Provider.fromString(mcpGraphConfig.getProvider());
                builder.provider(provider);
            } catch (IllegalArgumentException e) {
                log.warn("未知的图存储提供商: {}, 使用默认值", mcpGraphConfig.getProvider());
                builder.provider(GraphStoreConfig.Provider.NEO4J);
            }
        }

        builder.enabled(mcpGraphConfig.isEnabled());

        if (mcpGraphConfig.getUrl() != null) {
            builder.url(mcpGraphConfig.getUrl());
        }

        // 转换图存储的LLM配置
        if (mcpGraphConfig.getLlm() != null) {
            LlmConfig llmConfig = convertLlmConfig(mcpGraphConfig.getLlm());
            builder.llm(llmConfig);
        }

        // 转换图存储的嵌入模型配置
        if (mcpGraphConfig.getEmbedder() != null) {
            EmbedderConfig embedderConfig = convertEmbedderConfig(mcpGraphConfig.getEmbedder());
            builder.embedder(embedderConfig);
        }

        return builder.build();
    }
}
