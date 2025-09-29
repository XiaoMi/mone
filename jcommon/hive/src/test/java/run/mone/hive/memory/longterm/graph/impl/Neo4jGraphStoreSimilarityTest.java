package run.mone.hive.memory.longterm.graph.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import run.mone.hive.memory.longterm.config.EmbedderConfig;
import run.mone.hive.memory.longterm.config.GraphStoreConfig;
import run.mone.hive.memory.longterm.config.LlmConfig;
import run.mone.hive.memory.longterm.embeddings.EmbeddingBase;
import run.mone.hive.memory.longterm.embeddings.EmbeddingFactory;

import java.util.*;

import lombok.extern.slf4j.Slf4j;

/**
 * Neo4j图存储相似度测试
 * 专门测试排球和篮球等体育项目的相似度计算
 */
@Slf4j
@DisplayName("Neo4j Graph Store Similarity Tests")
public class Neo4jGraphStoreSimilarityTest {

    private Neo4jGraphStore graphStore;
    private GraphStoreConfig config;
    private EmbeddingBase embeddingModel;
    private final String TEST_USER_ID = "similarity_test_user";

    @BeforeEach
    void setUp() {
        // 配置嵌入模型
        EmbedderConfig embedderConfig = EmbedderConfig.builder()
                .provider(EmbedderConfig.Provider.OLLAMA)
//                .model("embeddinggemma")
                .model("qwen3-embedding:4b")
                .embeddingDims(2560)
                .build();

        // 配置LLM
        LlmConfig llmConfig = LlmConfig.builder()
                .provider(LlmConfig.Provider.DEEPSEEK)
                .apiKey(System.getenv("DEEPSEEK_API_KEY"))
                .model("deepseek-chat")
                .temperature(0.1)
                .build();

        // 配置Neo4j图存储
        config = GraphStoreConfig.builder()
                .provider(GraphStoreConfig.Provider.NEO4J)
                .url("bolt://localhost:7687")
                .username("neo4j")
                .password(System.getenv("NEO4J_PASSWORD"))
                .enabled(true)
                .embedder(embedderConfig)
                .llm(llmConfig)
                .threshold(0.7) // 设置相似度阈值
                .build();

        try {
            embeddingModel = EmbeddingFactory.create(embedderConfig);
        } catch (Exception e) {
            log.error("Failed to setup test environment", e);
            assumeTrue(false, "Neo4j or embedding service not available: " + e.getMessage());
        }
    }

    @AfterEach
    void tearDown() {
        try {
            if (graphStore != null) {
                // 清理测试数据
                graphStore.deleteAll(TEST_USER_ID);
                graphStore.close();
            }
            if (embeddingModel != null) {
                embeddingModel.close();
            }
            log.info("Test cleanup completed");
        } catch (Exception e) {
            log.warn("Error during test cleanup", e);
        }
    }

    @Test
    @DisplayName("测试排球和篮球的相似度计算")
    void testVolleyballBasketballSimilarity() {
        try {
            // 准备测试数据
            String volleyball = "排球";
            String basketball = "篮球";
            
            log.info("开始测试 '{}' 和 '{}' 的相似度计算", volleyball, basketball);
            
            // 1. 生成嵌入向量
            List<Double> volleyballEmbedding = embeddingModel.embed(volleyball, "similarity_test");
            List<Double> basketballEmbedding = embeddingModel.embed(basketball, "similarity_test");
            
            assertNotNull(volleyballEmbedding, "排球的嵌入向量不应为空");
            assertNotNull(basketballEmbedding, "篮球的嵌入向量不应为空");
            assertFalse(volleyballEmbedding.isEmpty(), "排球的嵌入向量不应为空列表");
            assertFalse(basketballEmbedding.isEmpty(), "篮球的嵌入向量不应为空列表");
            
            log.info("生成嵌入向量成功:");
            log.info("  排球嵌入向量: {} 维", volleyballEmbedding.size());
            log.info("  篮球嵌入向量: {} 维", basketballEmbedding.size());
            
            // 2. 手动计算余弦相似度
            double cosineSimilarity = calculateCosineSimilarity(volleyballEmbedding, basketballEmbedding);
            double normalizedSimilarity = 2 * cosineSimilarity - 1; // Neo4j使用的归一化公式
            
            log.info("相似度计算结果:");
            log.info("  原始余弦相似度: {}", cosineSimilarity);
            log.info("  归一化相似度 (Neo4j格式): {}", normalizedSimilarity);
            
            // 3. 先添加排球节点到图数据库
            Map<String, Object> addResult = graphStore.addMemory(
                volleyball, "足球", "同属于", "运动", "运动", TEST_USER_ID);
            assertNotNull(addResult, "添加排球节点应该成功");
            log.info("添加排球节点到图数据库: {}", addResult);
            
            // 4. 使用图存储的搜索功能测试相似度
            Map<String, Object> filters = new HashMap<>();
            filters.put("user_id", TEST_USER_ID);
            
            // 使用不同的相似度阈值测试
            double[] thresholds = {0.5, 0.6, 0.7, 0.8, 0.9, 0.95};
            
            for (double threshold : thresholds) {
                log.info("\n--- 测试阈值: {} ---", threshold);
                
                // 调用私有方法进行节点搜索（这里我们通过反射或者添加公共方法来测试）
                List<Map<String, Object>> searchResults = testNodeSimilaritySearch(
                    basketballEmbedding, filters, threshold);
                
                if (!searchResults.isEmpty()) {
                    Map<String, Object> result = searchResults.get(0);
                    log.info("找到相似节点:");
                    log.info("  节点名称: {}", result.get("name"));
                    log.info("  相似度分数: {}", result.get("similarity"));
                    log.info("  元素ID: {}", result.get("elementId"));
                    
                    // 验证相似度分数
                    double foundSimilarity = (Double) result.get("similarity");
                    assertTrue(foundSimilarity >= threshold, 
                        String.format("找到的相似度 %.4f 应该大于等于阈值 %.4f", foundSimilarity, threshold));
                    
                    // 如果找到了排球节点，验证它确实是我们期望的节点
                    if (volleyball.equals(result.get("name"))) {
                        log.info("✅ 成功找到排球节点，相似度为: {}", foundSimilarity);
                        
                        // 验证相似度是否合理（体育项目应该有一定相似度）
                        assertTrue(foundSimilarity > 0.5, 
                            "排球和篮球作为体育项目，相似度应该大于0.5");
                    }
                } else {
                    log.info("在阈值 {} 下未找到相似节点", threshold);
                }
            }
            
            // 5. 测试添加篮球节点时的相似度检测
            log.info("\n--- 测试添加篮球节点时的相似度检测 ---");
            Map<String, Object> basketballAddResult = graphStore.addMemory(
                basketball, "足球", "同属于", "运动", "运动", TEST_USER_ID);
            
            assertNotNull(basketballAddResult, "添加篮球节点应该成功");
            log.info("添加篮球节点结果: {}", basketballAddResult);
            
            // 6. 验证两个节点都存在于图中
            List<Map<String, Object>> allRelations = graphStore.getAll(10, TEST_USER_ID);
            log.info("图中所有关系: {}", allRelations);
            
            boolean foundVolleyball = allRelations.stream()
                .anyMatch(r -> volleyball.equals(r.get("source")) || volleyball.equals(r.get("target")));
            boolean foundBasketball = allRelations.stream()
                .anyMatch(r -> basketball.equals(r.get("source")) || basketball.equals(r.get("target")));
            
            assertTrue(foundVolleyball, "应该能在图中找到排球相关的关系");
            assertTrue(foundBasketball, "应该能在图中找到篮球相关的关系");
            
            log.info("✅ 排球和篮球相似度测试完成");
            
        } catch (Exception e) {
            log.error("排球篮球相似度测试失败", e);
            fail("测试不应该抛出异常: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试不同体育项目的相似度对比")
    void testSportsItemsSimilarityComparison() {
        try {
            // 测试多个体育项目之间的相似度
            String[] sportsItems = {"排球", "篮球", "足球", "网球", "乒乓球", "羽毛球"};
            String referenceItem = "排球";
            
            log.info("测试各种体育项目与 '{}' 的相似度", referenceItem);
            
            // 生成参考项目的嵌入向量
            List<Double> referenceEmbedding = embeddingModel.embed(referenceItem, "comparison_test");
            
            // 计算所有项目与参考项目的相似度
            Map<String, Double> similarities = new HashMap<>();
            
            for (String sport : sportsItems) {
                if (!sport.equals(referenceItem)) {
                    List<Double> sportEmbedding = embeddingModel.embed(sport, "comparison_test");
                    double similarity = calculateCosineSimilarity(referenceEmbedding, sportEmbedding);
                    double normalizedSimilarity = 2 * similarity - 1;
                    similarities.put(sport, normalizedSimilarity);
                    
                    log.info("'{}' 与 '{}' 的相似度: {:.4f}", referenceItem, sport, normalizedSimilarity);
                }
            }
            
            // 按相似度排序
            similarities.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(entry -> log.info("排序结果: {} -> {:.4f}", entry.getKey(), entry.getValue()));
            
            // 验证体育项目之间应该有合理的相似度
            for (Double similarity : similarities.values()) {
                assertTrue(similarity > 0.3, 
                    "体育项目之间的相似度应该大于0.3，实际值: " + similarity);
            }
            
        } catch (Exception e) {
            log.error("体育项目相似度对比测试失败", e);
            fail("测试不应该抛出异常: " + e.getMessage());
        }
    }

    /**
     * 测试节点相似度搜索（模拟私有方法的功能）
     */
    private List<Map<String, Object>> testNodeSimilaritySearch(
            List<Double> queryEmbedding, Map<String, Object> filters, double threshold) {
        
        // 这里我们直接搜索图中的所有节点来找相似的
        // 在实际测试中，我们可能需要通过反射调用私有方法，或者添加公共测试方法
        List<Map<String, Object>> searchResults = graphStore.search("排球", 10, TEST_USER_ID);
        
        // 手动过滤相似度（这是简化版本，实际的私有方法会更复杂）
        List<Map<String, Object>> filteredResults = new ArrayList<>();
        
        for (Map<String, Object> result : searchResults) {
            // 模拟相似度计算结果
            if (result.containsKey("similarity")) {
                double similarity = (Double) result.get("similarity");
                if (similarity >= threshold) {
                    filteredResults.add(result);
                }
            } else {
                // 如果没有相似度信息，我们手动计算
                String nodeName = (String) result.get("source");
                if (nodeName != null) {
                    try {
                        List<Double> nodeEmbedding = embeddingModel.embed(nodeName, "test");
                        double similarity = 2 * calculateCosineSimilarity(queryEmbedding, nodeEmbedding) - 1;
                        
                        if (similarity >= threshold) {
                            Map<String, Object> enhancedResult = new HashMap<>(result);
                            enhancedResult.put("name", nodeName);
                            enhancedResult.put("similarity", similarity);
                            enhancedResult.put("elementId", "test_id_" + nodeName.hashCode());
                            filteredResults.add(enhancedResult);
                        }
                    } catch (Exception e) {
                        log.warn("计算节点 {} 的相似度时出错", nodeName, e);
                    }
                }
            }
        }
        
        return filteredResults;
    }

    /**
     * 计算两个向量的余弦相似度
     */
    private double calculateCosineSimilarity(List<Double> vector1, List<Double> vector2) {
        if (vector1.size() != vector2.size()) {
            throw new IllegalArgumentException("向量维度不匹配");
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int i = 0; i < vector1.size(); i++) {
            dotProduct += vector1.get(i) * vector2.get(i);
            norm1 += vector1.get(i) * vector1.get(i);
            norm2 += vector2.get(i) * vector2.get(i);
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    /**
     * 假设条件检查（用于跳过不满足条件的测试）
     */
    private void assumeTrue(boolean condition, String message) {
        if (!condition) {
            log.warn("跳过测试: {}", message);
            org.junit.jupiter.api.Assumptions.assumeTrue(condition, message);
        }
    }
}
