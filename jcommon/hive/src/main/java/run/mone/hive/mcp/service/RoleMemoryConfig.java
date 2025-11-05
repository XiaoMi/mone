package run.mone.hive.mcp.service;

import java.util.Map;
import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Memory configuration class corresponding to memory-config.yml
 * @author goodjava@qq.com
 * @date 2025/1/1 00:00
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleMemoryConfig {

    @Builder.Default
    private LlmConfig llm = LlmConfig.builder().build();

    @Builder.Default
    private EmbedderConfig embedder = EmbedderConfig.builder().build();

    @Builder.Default
    private VectorStoreConfig vectorStore = VectorStoreConfig.builder().build();

    @Builder.Default
    private GraphStoreConfig graphStore = GraphStoreConfig.builder().build();

    @Builder.Default
    private String historyDbPath = "./temp/history.db";

    @Builder.Default
    private String version = "v1.1";

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class LlmConfig {
        @Builder.Default
        private String providerName = "QWEN";

        @Builder.Default
        private String model = "";

        @Builder.Default
        private String baseUrl = "";

        @Builder.Default
        private String apiKey = "";

        @Builder.Default
        private Map<String, String> customHeaders = new HashMap<>();

        @Builder.Default
        private boolean responseJsonFormat = true;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class EmbedderConfig {
        @Builder.Default
        private String provider = "ollama";

        @Builder.Default
        private String model = "qwen3-embedding:4b";

        @Builder.Default
        private String baseUrl = "";

        @Builder.Default
        private String apiKey = "";

        @Builder.Default
        private Map<String, String> customHeaders = new HashMap<>();

        @Builder.Default
        private int embeddingDims = 2560;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class VectorStoreConfig {
        @Builder.Default
        private String provider = "chroma";

        @Builder.Default
        private int port = 8000;

        @Builder.Default
        private String collectionName = "test_collection1";

        @Builder.Default
        private int embeddingModelDims = 2560;

        @Builder.Default
        private String embeddingFunction = "ollama";

        @Builder.Default
        private String model = "qwen3-embedding:4b";

        @Builder.Default
        private boolean enable = false;
    }

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class GraphStoreConfig {
        @Builder.Default
        private String provider = "neo4j";

        @Builder.Default
        private boolean enabled = true;

        @Builder.Default
        private String url = "bolt://localhost:7687";

        @Builder.Default
        private LlmConfig llm = LlmConfig.builder().build();

        @Builder.Default
        private String username = "neo4j";

        @Builder.Default
        private String password = "password";

        @Builder.Default
        private EmbedderConfig embedder = EmbedderConfig.builder().build();
    }
}
