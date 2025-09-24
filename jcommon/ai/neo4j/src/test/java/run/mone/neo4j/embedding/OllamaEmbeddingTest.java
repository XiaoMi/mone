package run.mone.neo4j.embedding;

import org.junit.Before;
import org.junit.Test;
import run.mone.neo4j.embedding.config.EmbedderConfig;
import run.mone.neo4j.embedding.impl.OllamaEmbedding;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Ollama嵌入模型测试类
 */
public class OllamaEmbeddingTest {
    
    private OllamaEmbedding ollamaEmbedding;

    @Before
    public void setUp() {
        EmbedderConfig config = new EmbedderConfig();
        config.setModel("embeddinggemma");
        config.setBaseUrl("http://localhost:11434");
        
        ollamaEmbedding = new OllamaEmbedding(config);
    }
    
    @Test
    public void testEmbed() {
        // 注意：此测试需要本地运行Ollama服务
        String text = "Hello, world!";
        
        try {
            List<Double> embedding = ollamaEmbedding.embed(text);
            
            assertNotNull(embedding);
            assertTrue(embedding.size() > 0);
            System.out.println("Embedding dimensions: " + embedding.size());
            System.out.println("First 5 values: " + embedding.subList(0, Math.min(5, embedding.size())));
        } catch (RuntimeException e) {
            // 如果Ollama服务未运行，跳过测试
            System.out.println("Skipping test - Ollama service not available: " + e.getMessage());
        }
    }
    
    @Test
    public void testEmbedBatch() {
        List<String> texts = Arrays.asList("Hello", "World", "Test");
        
        try {
            List<List<Double>> embeddings = ollamaEmbedding.embedBatch(texts, "add");
            
            assertNotNull(embeddings);
            assertEquals(3, embeddings.size());
            
            for (List<Double> embedding : embeddings) {
                assertNotNull(embedding);
                assertTrue(embedding.size() > 0);
            }
            
            System.out.println("Batch embedding completed for " + texts.size() + " texts");
        } catch (RuntimeException e) {
            System.out.println("Skipping test - Ollama service not available: " + e.getMessage());
        }
    }
    
    @Test
    public void testGetDimensions() {
        int dimensions = ollamaEmbedding.getDimensions();
        assertTrue(dimensions > 0);
        System.out.println("Model dimensions: " + dimensions);
    }
    
    @Test
    public void testGetCommonEmbeddingModels() {
        List<String> models = OllamaEmbedding.getCommonEmbeddingModels();
        
        assertNotNull(models);
        assertFalse(models.isEmpty());
        assertTrue(models.contains("embeddinggemma"));
        
        System.out.println("Common embedding models: " + models);
    }
    

    @Test
    public void testGetAvailableModels() {
        try {
            List<String> models = ollamaEmbedding.getAvailableModels();
            
            assertNotNull(models);
            System.out.println("Available models: " + models);
        } catch (RuntimeException e) {
            System.out.println("Skipping test - Ollama service not available: " + e.getMessage());
        }
    }
}
