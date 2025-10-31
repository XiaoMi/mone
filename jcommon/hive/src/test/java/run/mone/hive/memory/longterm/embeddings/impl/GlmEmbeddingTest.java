package run.mone.hive.memory.longterm.embeddings.impl;

import org.junit.Before;
import org.junit.Test;
import run.mone.hive.memory.longterm.config.EmbedderConfig;
import run.mone.hive.memory.longterm.embeddings.EmbeddingBase;
import run.mone.hive.memory.longterm.embeddings.EmbeddingFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * GLM嵌入模型测试类
 */
public class GlmEmbeddingTest {
    
    private EmbedderConfig config;
    private GlmEmbedding glmEmbedding;
    
    @Before
    public void setUp() {
        // 创建测试配置
        config = EmbedderConfig.builder()
                .provider(EmbedderConfig.Provider.GLM)
                .model("embedding-3")
                .baseUrl("https://open.bigmodel.cn/api/paas/v4")
                .apiKey(System.getenv("GLM_API_KEY")) // 在实际测试中需要替换为真实的API密钥
                .embeddingDims(1024)
                .build();
        
        glmEmbedding = new GlmEmbedding(config);
    }
    
    @Test
    public void testConfigInitialization() {
        assertNotNull("GLM embedding should be initialized", glmEmbedding);
        assertEquals("Provider should be GLM", EmbedderConfig.Provider.GLM, config.getProvider());
        assertEquals("Model should be embedding-3", "embedding-3", config.getModel());
        assertEquals("Dimensions should be 1024", 1024, config.getEmbeddingDims());
    }
    
    @Test
    public void testGetDimensions() {
        assertEquals("Dimensions should match config", 1024, glmEmbedding.getDimensions());
    }
    
    @Test
    public void testGetConfig() {
        EmbedderConfig retrievedConfig = glmEmbedding.getConfig();
        assertNotNull("Config should not be null", retrievedConfig);
        assertEquals("Config should match", config, retrievedConfig);
    }
    
    @Test
    public void testDefaultValues() {
        // 测试默认值设置
        EmbedderConfig emptyConfig = EmbedderConfig.builder()
                .provider(EmbedderConfig.Provider.GLM)
                .apiKey("test-key")
                .build();
        
        GlmEmbedding embedding = new GlmEmbedding(emptyConfig);
        
        assertEquals("Default model should be embedding-3", "embedding-3", emptyConfig.getModel());
        assertEquals("Default base URL should be set", "https://open.bigmodel.cn/api/paas/v4", emptyConfig.getBaseUrl());
        assertEquals("Default dimensions should be 1024", 1024, emptyConfig.getEmbeddingDims());
    }
    
    @Test
    public void testSupportedModels() {
        List<String> supportedModels = GlmEmbedding.getSupportedModels();
        assertNotNull("Supported models should not be null", supportedModels);
        assertTrue("Should support embedding-3", supportedModels.contains("embedding-3"));
        assertTrue("Should support embedding-2", supportedModels.contains("embedding-2"));
    }
    
    @Test
    public void testModelDimensions() {
        assertEquals("embedding-3 should have 1024 dimensions", 1024, GlmEmbedding.getModelDimensions("embedding-3"));
        assertEquals("embedding-2 should have 1024 dimensions", 1024, GlmEmbedding.getModelDimensions("embedding-2"));
        assertEquals("Unknown model should have default 1024 dimensions", 1024, GlmEmbedding.getModelDimensions("unknown-model"));
    }
    
    @Test
    public void testFactoryCreation() {
        EmbeddingBase embedding = EmbeddingFactory.create(config);
        assertNotNull("Factory should create GLM embedding", embedding);
        assertTrue("Should be instance of GlmEmbedding", embedding instanceof GlmEmbedding);
    }
    
    @Test
    public void testGlmDefaultFactory() {
        EmbeddingBase embedding = EmbeddingFactory.createGlmDefault();
        assertNotNull("Factory should create default GLM embedding", embedding);
        assertTrue("Should be instance of GlmEmbedding", embedding instanceof GlmEmbedding);
    }
    
    @Test
    public void testGlmDefaultConfig() {
        EmbedderConfig defaultConfig = EmbedderConfig.glmDefault();
        assertNotNull("Default config should not be null", defaultConfig);
        assertEquals("Provider should be GLM", EmbedderConfig.Provider.GLM, defaultConfig.getProvider());
        assertEquals("Model should be embedding-3", "embedding-3", defaultConfig.getModel());
        assertEquals("Base URL should be set", "https://open.bigmodel.cn/api/paas/v4", defaultConfig.getBaseUrl());
        assertEquals("Dimensions should be 1024", 1024, defaultConfig.getEmbeddingDims());
    }
    

    @Test
    public void testEmbedSingleText() {
        // 需要真实的API密钥
        if (hasValidApiKey()) {
            String text = "你好，今天天气怎么样";
            List<Double> embedding = glmEmbedding.embed(text, "add");
            
            assertNotNull("Embedding should not be null", embedding);
            assertFalse("Embedding should not be empty", embedding.isEmpty());
            assertEquals("Embedding should have correct dimensions", 1024, embedding.size());
            
            // 检查向量值是否合理
            for (Double value : embedding) {
                assertNotNull("Embedding value should not be null", value);
                assertFalse("Embedding value should not be NaN", value.isNaN());
                assertFalse("Embedding value should not be infinite", value.isInfinite());
            }
        }
    }
    
    @Test
    public void testEmbedBatchTexts() {
        // 需要真实的API密钥
        if (hasValidApiKey()) {
            List<String> texts = Arrays.asList(
                "你好，今天天气怎么样",
                "Hello, how are you?",
                "机器学习是人工智能的一个分支"
            );
            
            List<List<Double>> embeddings = glmEmbedding.embedBatch(texts, "add");
            
            assertNotNull("Embeddings should not be null", embeddings);
            assertEquals("Should have same number of embeddings as texts", texts.size(), embeddings.size());
            
            for (List<Double> embedding : embeddings) {
                assertNotNull("Each embedding should not be null", embedding);
                assertEquals("Each embedding should have correct dimensions", 1024, embedding.size());
            }
        }
    }
    
    @Test
    public void testConnectionTest() {
        // 需要真实的API密钥
        if (hasValidApiKey()) {
            boolean connectionOk = glmEmbedding.testConnection();
            assertTrue("Connection test should pass with valid API key", connectionOk);
        }
    }
    
    @Test
    public void testValidateApiKey() {
        // 需要真实的API密钥
        if (hasValidApiKey()) {
            boolean isValid = glmEmbedding.validateApiKey();
            assertTrue("API key validation should pass", isValid);
        }
    }

    @Test
    public void testSimilarityBetweenAppleAndAppleInChinese() {
        // 需要真实的API密钥
        if (hasValidApiKey()) {
            String englishApple = "apple";
            String chineseApple = "苹果";
            
            // 获取两个词的嵌入向量
            List<Double> englishEmbedding = glmEmbedding.embed(englishApple, "add");
            List<Double> chineseEmbedding = glmEmbedding.embed(chineseApple, "add");
            
            assertNotNull("English embedding should not be null", englishEmbedding);
            assertNotNull("Chinese embedding should not be null", chineseEmbedding);
            assertEquals("English embedding should have correct dimensions", 1024, englishEmbedding.size());
            assertEquals("Chinese embedding should have correct dimensions", 1024, chineseEmbedding.size());
            
            // 计算余弦相似度
            double similarity = calculateCosineSimilarity(englishEmbedding, chineseEmbedding);
            
            // 验证相似度结果
            assertTrue("Similarity should be between -1 and 1", similarity >= -1.0 && similarity <= 1.0);
            assertTrue("Apple and 苹果 should have high similarity (> 0.5)", similarity > 0.5);
            
            System.out.println("Similarity between 'apple' and '苹果': " + similarity);
        } else {
            System.out.println("Skipping similarity test - no valid API key available");
        }
    }

    /**
     * 计算两个向量的余弦相似度
     * @param vector1 第一个向量
     * @param vector2 第二个向量
     * @return 余弦相似度值，范围在-1到1之间
     */
    private double calculateCosineSimilarity(List<Double> vector1, List<Double> vector2) {
        if (vector1.size() != vector2.size()) {
            throw new IllegalArgumentException("Vectors must have the same dimensions");
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int i = 0; i < vector1.size(); i++) {
            double v1 = vector1.get(i);
            double v2 = vector2.get(i);
            
            dotProduct += v1 * v2;
            norm1 += v1 * v1;
            norm2 += v2 * v2;
        }
        
        norm1 = Math.sqrt(norm1);
        norm2 = Math.sqrt(norm2);
        
        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }
        
        return dotProduct / (norm1 * norm2);
    }

    /**
     * 检查是否有有效的API密钥用于测试
     * 在实际环境中，可以从环境变量或配置文件中读取
     */
    private boolean hasValidApiKey() {
        String apiKey = config.getApiKey();
        return apiKey != null && !apiKey.equals("test-api-key") && !apiKey.trim().isEmpty();
    }
}
