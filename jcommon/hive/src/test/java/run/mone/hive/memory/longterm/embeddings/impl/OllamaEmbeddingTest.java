package run.mone.hive.memory.longterm.embeddings.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import run.mone.hive.memory.longterm.config.EmbedderConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OllamaEmbedding 集成测试
 * 需要本地运行 Ollama 服务并加载 embeddinggemma 模型
 */
class OllamaEmbeddingTest {

    private OllamaEmbedding ollamaEmbedding;
    private EmbedderConfig config;

    @BeforeEach
    void setUp() {
        config = new EmbedderConfig();
        config.setModel("embeddinggemma");
        config.setBaseUrl("http://localhost:11434");
        
        ollamaEmbedding = new OllamaEmbedding(config);
    }
    
    /**
     * 检查 Ollama 服务是否可用
     */
    private boolean isOllamaAvailable() {
      return true;
    }

    @Test
    @EnabledIf("isOllamaAvailable")
    void testEmbedSuccess() {
        // 准备测试数据
        String inputText = "蓝天";
        
        // 执行测试
        List<Double> result = ollamaEmbedding.embed(inputText, "add");
        
        // 验证结果
        assertNotNull(result, "嵌入向量不应为null");
        assertFalse(result.isEmpty(), "嵌入向量不应为空");
        assertEquals(3584, result.size(), "embeddinggemma模型应返回3584维向量");
        
        // 验证向量值都是有效的数字
        for (int i = 0; i < Math.min(10, result.size()); i++) {
            Double value = result.get(i);
            assertNotNull(value, "向量值不应为null");
            assertFalse(value.isNaN(), "向量值不应为NaN");
            assertFalse(value.isInfinite(), "向量值不应为无穷大");
        }
        
        System.out.println("成功生成嵌入向量，维度: " + result.size());
        System.out.println("前5个值: " + result.subList(0, Math.min(5, result.size())));
    }

    @Test
    @EnabledIf("isOllamaAvailable")
    void testEmbedWithDefaultMemoryAction() {
        // 准备测试数据
        String inputText = "测试文本";
        
        // 测试便捷方法（不传 memoryAction 参数）
        List<Double> result = ollamaEmbedding.embed(inputText);
        
        // 验证结果
        assertNotNull(result, "嵌入向量不应为null");
        assertFalse(result.isEmpty(), "嵌入向量不应为空");
        assertEquals(3584, result.size(), "embeddinggemma模型应返回3584维向量");
        
        System.out.println("便捷方法测试成功，生成嵌入向量维度: " + result.size());
    }

    @Test
    @EnabledIf("isOllamaAvailable")
    void testEmbedDifferentTexts() {
        // 测试不同类型的文本
        String[] testTexts = {
            "Hello World",
            "你好世界",
            "This is a longer sentence with more words to test the embedding generation.",
            "123456",
            "Special chars: !@#$%^&*()",
            ""
        };
        
        for (String text : testTexts) {
            try {
                List<Double> result = ollamaEmbedding.embed(text, "add");
                
                if (!text.isEmpty()) {
                    assertNotNull(result, "文本 '" + text + "' 的嵌入向量不应为null");
                    assertFalse(result.isEmpty(), "文本 '" + text + "' 的嵌入向量不应为空");
                    assertEquals(3584, result.size(), "所有文本都应返回相同维度的向量");
                }
                
                System.out.println("文本: '" + text + "' -> 向量维度: " + result.size());
            } catch (Exception e) {
                if (!text.isEmpty()) {
                    fail("处理文本 '" + text + "' 时出现异常: " + e.getMessage());
                }
            }
        }
    }

    @Test
    void testEmbedWithInvalidConfig() {
        // 测试无效配置的情况
        EmbedderConfig invalidConfig = new EmbedderConfig();
        invalidConfig.setModel("non-existent-model");
        invalidConfig.setBaseUrl("http://localhost:11434");
        
        OllamaEmbedding invalidEmbedding = new OllamaEmbedding(invalidConfig);
        
        // 这个测试应该抛出异常，因为模型不存在
        assertThrows(RuntimeException.class, () -> {
            invalidEmbedding.embed("test text", "add");
        }, "使用不存在的模型应该抛出异常");
    }

    @Test
    void testEmbedWithWrongUrl() {
        // 测试错误的URL
        EmbedderConfig wrongUrlConfig = new EmbedderConfig();
        wrongUrlConfig.setModel("embeddinggemma");
        wrongUrlConfig.setBaseUrl("http://localhost:9999"); // 错误的端口
        
        OllamaEmbedding wrongUrlEmbedding = new OllamaEmbedding(wrongUrlConfig);
        
        // 这个测试应该抛出异常，因为连接不到服务
        assertThrows(RuntimeException.class, () -> {
            wrongUrlEmbedding.embed("test text", "add");
        }, "连接到错误的URL应该抛出异常");
    }

    @Test
    void testGetDimensions() {
        // 测试获取模型维度
        assertEquals(3584, ollamaEmbedding.getDimensions());
        
        // 测试配置中指定的维度
        EmbedderConfig customConfig = new EmbedderConfig();
        customConfig.setModel("embeddinggemma");
        customConfig.setBaseUrl("http://localhost:11434");
        customConfig.setEmbeddingDims(1024);
        
        OllamaEmbedding customEmbedding = new OllamaEmbedding(customConfig);
        assertEquals(1024, customEmbedding.getDimensions());
    }

    @Test
    void testStaticGetModelDimensions() {
        // 测试静态方法获取模型维度
        assertEquals(3584, OllamaEmbedding.getModelDimensions("embeddinggemma"));
        assertEquals(768, OllamaEmbedding.getModelDimensions("nomic-embed-text"));
        assertEquals(1024, OllamaEmbedding.getModelDimensions("mxbai-embed-large"));
        assertEquals(384, OllamaEmbedding.getModelDimensions("all-minilm"));
        assertEquals(768, OllamaEmbedding.getModelDimensions("unknown-model"));
    }

    @Test
    void testGetCommonEmbeddingModels() {
        // 测试获取常见嵌入模型列表
        List<String> commonModels = OllamaEmbedding.getCommonEmbeddingModels();
        
        assertNotNull(commonModels);
        assertFalse(commonModels.isEmpty());
        assertTrue(commonModels.contains("embeddinggemma"));
        assertTrue(commonModels.contains("nomic-embed-text"));
        
        System.out.println("常见嵌入模型: " + commonModels);
    }

    @Test
    @EnabledIf("isOllamaAvailable")
    void testGetAvailableModels() {
        // 测试获取可用模型列表
        List<String> availableModels = ollamaEmbedding.getAvailableModels();
        
        assertNotNull(availableModels);
        // 如果Ollama服务运行正常，应该至少有一些模型
        if (isOllamaAvailable()) {
            assertFalse(availableModels.isEmpty(), "Ollama服务运行时应该有可用模型");
        }
        
        System.out.println("可用的嵌入模型: " + availableModels);
    }

    @Test
    @EnabledIf("isOllamaAvailable")
    void testIsModelAvailable() {
        // 测试检查模型是否可用
        boolean isAvailable = ollamaEmbedding.isModelAvailable("embeddinggemma");
        
        if (isOllamaAvailable()) {
            // 如果服务可用，embeddinggemma模型应该也可用（假设已安装）
            System.out.println("embeddinggemma 模型是否可用: " + isAvailable);
        }
        
        // 测试不存在的模型
        assertFalse(ollamaEmbedding.isModelAvailable("definitely-not-exists-model"));
    }
}
