package run.mone.hive.memory.longterm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;

/**
 * YAML配置加载器
 * 用于从YAML文件加载内存配置
 */
@Slf4j
public class YamlConfigLoader {

    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    /**
     * 从类路径加载YAML配置
     *
     * @param resourcePath 资源路径，如 "memory-config.yml"
     * @return MemoryConfig 配置对象
     */
    public static MemoryConfig loadFromClasspath(String resourcePath) {
        try (InputStream inputStream = YamlConfigLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                log.warn("配置文件 {} 未找到，使用默认配置", resourcePath);
                return MemoryConfig.getDefault();
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> configMap = yamlMapper.readValue(inputStream, Map.class);

            // 获取memory节点下的配置
            if (configMap.containsKey("memory")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> memoryConfig = (Map<String, Object>) configMap.get("memory");
                return MemoryConfig.fromMap(memoryConfig);
            } else {
                // 直接从根节点读取
                return MemoryConfig.fromMap(configMap);
            }

        } catch (Exception e) {
            log.error("加载配置文件失败: {}", resourcePath, e);
            return MemoryConfig.getDefault();
        }
    }

    /**
     * 从文件路径加载YAML配置
     *
     * @param filePath 文件路径
     * @return MemoryConfig 配置对象
     */
    public static MemoryConfig loadFromFile(String filePath) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> configMap = yamlMapper.readValue(Path.of(filePath).toFile(), Map.class);

            // 获取memory节点下的配置
            if (configMap.containsKey("memory")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> memoryConfig = (Map<String, Object>) configMap.get("memory");
                return MemoryConfig.fromMap(memoryConfig);
            } else {
                // 直接从根节点读取
                return MemoryConfig.fromMap(configMap);
            }

        } catch (Exception e) {
            log.error("加载配置文件失败: {}", filePath, e);
            return MemoryConfig.getDefault();
        }
    }

    /**
     * 从默认配置文件加载配置
     *
     * @return MemoryConfig 配置对象
     */
    public static MemoryConfig loadDefault() {
        return loadFromClasspath("memory-config.yml");
    }

    /**
     * 构建配置对象，支持动态路径替换
     *
     * @param resourcePath 资源路径
     * @param tempDir 临时目录路径，用于替换配置中的路径占位符
     * @return MemoryConfig 配置对象
     */
    public static MemoryConfig loadWithTempDir(String resourcePath, Path tempDir) {
        MemoryConfig config = loadFromClasspath(resourcePath);

        // 动态设置临时目录路径
        if (config.getVectorStore().getPath() != null && config.getVectorStore().getPath().contains("./temp")) {
            VectorStoreConfig updatedVectorStore = VectorStoreConfig.builder()
                    .provider(config.getVectorStore().getProvider())
                    .collectionName(config.getVectorStore().getCollectionName())
                    .path(tempDir.resolve("vector").toString())
                    .embeddingModelDims(config.getVectorStore().getEmbeddingModelDims())
                    .embeddingFunction(config.getVectorStore().getEmbeddingFunction())
                    .apiKey(config.getVectorStore().getApiKey())
                    .baseUrl(config.getVectorStore().getBaseUrl())
                    .host(config.getVectorStore().getHost())
                    .port(config.getVectorStore().getPort())
                    .database(config.getVectorStore().getDatabase())
                    .config(config.getVectorStore().getConfig())
                    .build();

            config = MemoryConfig.builder()
                    .llm(config.getLlm())
                    .embedder(config.getEmbedder())
                    .vectorStore(updatedVectorStore)
                    .graphStore(config.getGraphStore())
                    .historyDbPath(tempDir.resolve("history.db").toString())
                    .version(config.getVersion())
                    .customFactExtractionPrompt(config.getCustomFactExtractionPrompt())
                    .customUpdateMemoryPrompt(config.getCustomUpdateMemoryPrompt())
                    .build();
        }

        return config;
    }
}