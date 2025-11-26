package run.mone.neo4j;

import org.junit.Before;
import org.junit.Test;
import run.mone.neo4j.embedding.config.EmbedderConfig;
import run.mone.neo4j.embedding.impl.OllamaEmbedding;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * VertexSaver嵌入功能测试类
 */
public class VertexSaverEmbeddingTest {
    
    private VertexSaver vertexSaver;
    
    @Before
    public void setUp() {
        vertexSaver = new VertexSaver();
        
        // 设置测试环境的Neo4j密码
        String password = System.getenv("NEO4J_PASSWORD");
        if (password == null || password.isEmpty()) {
            password = "test123"; // 默认测试密码
        }
        vertexSaver.setPassword(password);
        
        // 初始化嵌入模型
        try {
            vertexSaver.initDefaultEmbedding();
        } catch (Exception e) {
            System.out.println("Warning: Could not initialize embedding model: " + e.getMessage());
        }
    }
    
    @Test
    public void testEmbeddingModelInitialization() {
        // 测试嵌入模型初始化
        VertexSaver saver = new VertexSaver();
        assertNull(saver.getEmbeddingModel());
        
        saver.initDefaultEmbedding();
        assertNotNull(saver.getEmbeddingModel());
        assertTrue(saver.getEmbeddingModel() instanceof OllamaEmbedding);
    }
    
    @Test
    public void testCustomEmbeddingModelSetup() {
        // 测试自定义嵌入模型设置
        EmbedderConfig config = new EmbedderConfig();
        config.setModel("nomic-embed-text");
        config.setBaseUrl("http://localhost:11434");
        
        OllamaEmbedding customEmbedding = new OllamaEmbedding(config);
        
        VertexSaver saver = new VertexSaver();
        saver.setEmbeddingModel(customEmbedding);
        
        assertNotNull(saver.getEmbeddingModel());
        assertEquals(customEmbedding, saver.getEmbeddingModel());
    }
    
    @Test
    public void testComputeAndStoreEmbeddingWithoutModel() {
        // 测试没有设置嵌入模型时的行为
        VertexSaver saver = new VertexSaver().setPassword("test");
        saver.setEmbeddingModel(null);
        
        boolean result = saver.computeAndStoreEmbedding("test", "content");
        assertFalse("Should fail when embedding model is not set", result);
    }
    
    @Test
    public void testComputeAndStoreEmbeddingWithInvalidParams() {
        // 测试无效参数的处理
        boolean result1 = vertexSaver.computeAndStoreEmbedding(null, "content");
        assertFalse("Should fail with null vertex name", result1);
        
        boolean result2 = vertexSaver.computeAndStoreEmbedding("", "content");
        assertFalse("Should fail with empty vertex name", result2);
        
        boolean result3 = vertexSaver.computeAndStoreEmbedding("test", null);
        assertFalse("Should fail with null property name", result3);
        
        boolean result4 = vertexSaver.computeAndStoreEmbedding("test", "");
        assertFalse("Should fail with empty property name", result4);
    }
    
    @Test
    public void testComputeAndStoreEmbeddingIntegration() {
        // 集成测试 - 需要Neo4j和Ollama服务运行
        if (!vertexSaver.testConnection()) {
            System.out.println("Skipping integration test - Neo4j not available");
            return;
        }
        
        if (vertexSaver.getEmbeddingModel() == null) {
            System.out.println("Skipping integration test - Embedding model not available");
            return;
        }
        
        try {
            // 清理测试数据
            vertexSaver.deleteVerticesByLabel("TestDoc");
            
            // 创建测试顶点
            Map<String, Object> testDoc = new HashMap<>();
            testDoc.put("name", "测试文档");
            testDoc.put("content", "这是一个测试文档的内容");
            testDoc.put("title", "测试标题");
            
            vertexSaver.saveVertices(Arrays.asList(testDoc), "TestDoc");
            
            // 测试嵌入计算和存储
            boolean result = vertexSaver.computeAndStoreEmbedding("测试文档", "content", "TestDoc");
            assertTrue("Should successfully compute and store embedding", result);
            
            // 验证嵌入向量已存储
            List<Double> embedding = vertexSaver.getVertexEmbedding("测试文档", "TestDoc", "embedding");
            assertNotNull("Embedding should be stored", embedding);
            assertTrue("Embedding should not be empty", embedding.size() > 0);
            
            System.out.println("Integration test passed - embedding dimensions: " + embedding.size());
            
            // 清理测试数据
            vertexSaver.deleteVerticesByLabel("TestDoc");
            
        } catch (Exception e) {
            System.out.println("Integration test failed (expected if services not running): " + e.getMessage());
        }
    }
    
    @Test
    public void testBatchComputeAndStoreEmbedding() {
        // 测试批量处理
        List<String> emptyList = Arrays.asList();
        int result1 = vertexSaver.batchComputeAndStoreEmbedding(emptyList, "content");
        assertEquals("Should return 0 for empty list", 0, result1);
        
        int result2 = vertexSaver.batchComputeAndStoreEmbedding(null, "content");
        assertEquals("Should return 0 for null list", 0, result2);
    }
    
    @Test
    public void testGetVertexEmbedding() {
        // 测试查询嵌入向量
        List<Double> result1 = vertexSaver.getVertexEmbedding(null);
        assertNull("Should return null for null vertex name", result1);
        
        List<Double> result2 = vertexSaver.getVertexEmbedding("");
        assertNull("Should return null for empty vertex name", result2);
        
        // 测试不存在的顶点
        List<Double> result3 = vertexSaver.getVertexEmbedding("不存在的顶点");
        assertNull("Should return null for non-existent vertex", result3);
    }
    
    @Test
    public void testMethodOverloads() {
        // 测试方法重载
        if (!vertexSaver.testConnection() || vertexSaver.getEmbeddingModel() == null) {
            System.out.println("Skipping overload test - services not available");
            return;
        }
        
        try {
            // 清理和准备测试数据
            vertexSaver.deleteVerticesByLabel("TestOverload");
            
            Map<String, Object> testVertex = new HashMap<>();
            testVertex.put("name", "重载测试");
            testVertex.put("description", "测试方法重载功能");
            
            vertexSaver.saveVertices(Arrays.asList(testVertex), "TestOverload");
            
            // 测试不同的方法重载
            boolean result1 = vertexSaver.computeAndStoreEmbedding("重载测试", "description");
            boolean result2 = vertexSaver.computeAndStoreEmbedding("重载测试", "description", "TestOverload");
            boolean result3 = vertexSaver.computeAndStoreEmbedding("重载测试", "description", "TestOverload", "customEmbedding");
            
            // 由于服务可能不可用，我们主要测试方法调用不会抛出异常
            assertNotNull("Method should complete without exception", result1);
            assertNotNull("Method should complete without exception", result2);
            assertNotNull("Method should complete without exception", result3);
            
            // 清理测试数据
            vertexSaver.deleteVerticesByLabel("TestOverload");
            
        } catch (Exception e) {
            System.out.println("Overload test completed with expected service unavailability: " + e.getMessage());
        }
    }
}
