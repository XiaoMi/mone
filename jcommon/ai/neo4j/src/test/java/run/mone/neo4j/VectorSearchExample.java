package run.mone.neo4j;

import lombok.extern.slf4j.Slf4j;
import run.mone.neo4j.VertexSaver.VectorSearchResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 向量相似度查询示例
 * 演示如何使用VertexSaver进行向量相似度搜索
 */
@Slf4j
public class VectorSearchExample {

    public static void main(String[] args) {
        // 创建VertexSaver实例并初始化
        VertexSaver vertexSaver = new VertexSaver()
                .setPassword("your_neo4j_password")  // 请设置你的Neo4j密码
                .initDefaultEmbedding();

        // 测试数据库连接
        if (!vertexSaver.testConnection()) {
            log.error("无法连接到Neo4j数据库，请检查配置");
            return;
        }

        try {
            // 示例1: 使用预定义的向量进行搜索
            log.info("=== 示例1: 使用预定义向量进行相似度搜索 ===");
            
            // 这里使用一个示例向量（实际使用时应该是真实的768维向量）
            List<Double> queryVector = createExampleVector(768);
            
            List<VectorSearchResult> results = vertexSaver.vectorSimilaritySearch(
                "document_embeddings",  // 索引名称
                queryVector,           // 查询向量
                5,                    // 返回最相似的5个结果
                "Document"            // 顶点标签
            );
            
            printSearchResults("预定义向量搜索", results);

            // 示例2: 使用文本内容进行搜索
            log.info("\n=== 示例2: 使用文本内容进行相似度搜索 ===");
            
            String queryText = "Java编程语言的特性和优势";
            
            List<VectorSearchResult> textResults = vertexSaver.vectorSimilaritySearchByText(
                "document_embeddings",  // 索引名称
                queryText,             // 查询文本
                3,                     // 返回最相似的3个结果
                "Document"             // 顶点标签
            );
            
            printSearchResults("文本内容搜索: " + queryText, textResults);

            // 示例3: 使用默认参数进行搜索
            log.info("\n=== 示例3: 使用默认参数进行搜索 ===");
            
            List<VectorSearchResult> defaultResults = vertexSaver.vectorSimilaritySearchByText(
                "document_embeddings",
                "机器学习算法"
            );
            
            printSearchResults("默认参数搜索: 机器学习算法", defaultResults);

        } catch (Exception e) {
            log.error("向量搜索过程中发生错误", e);
        }
    }

    /**
     * 创建示例向量（用于演示）
     * 实际使用时应该使用真实的嵌入向量
     */
    private static List<Double> createExampleVector(int dimensions) {
        Double[] vector = new Double[dimensions];
        for (int i = 0; i < dimensions; i++) {
            // 创建一些示例数据
            vector[i] = Math.sin(i * 0.01) * 0.5 + Math.cos(i * 0.02) * 0.3;
        }
        return Arrays.asList(vector);
    }

    /**
     * 打印搜索结果
     */
    private static void printSearchResults(String searchType, List<VectorSearchResult> results) {
        log.info("搜索类型: {}", searchType);
        log.info("找到 {} 个相似结果:", results.size());
        
        if (results.isEmpty()) {
            log.info("  没有找到相似的结果");
            return;
        }
        
        for (int i = 0; i < results.size(); i++) {
            VectorSearchResult result = results.get(i);
            log.info("  第{}名: 名称='{}', 相似度分数={:.4f}", 
                    i + 1, result.getName(), result.getScore());
            
            // 打印顶点的一些关键属性
            Map<String, Object> vertex = result.getVertex();
            if (vertex != null) {
                vertex.forEach((key, value) -> {
                    if (!"embedding".equals(key)) { // 跳过嵌入向量，太长了
                        String valueStr = value != null ? value.toString() : "null";
                        if (valueStr.length() > 100) {
                            valueStr = valueStr.substring(0, 100) + "...";
                        }
                        log.info("    {}: {}", key, valueStr);
                    }
                });
            }
            log.info("");
        }
    }

    /**
     * 创建和保存一些测试文档的示例方法
     * 这个方法演示如何创建带有嵌入向量的文档顶点
     */
    public static void createTestDocuments(VertexSaver vertexSaver) {
        log.info("=== 创建测试文档 ===");
        
        // 创建一些示例文档
        Map<String, Object> doc1 = new HashMap<>();
        doc1.put("name", "Java编程指南");
        doc1.put("content", "Java是一种面向对象的编程语言，具有跨平台、安全性高的特点");
        doc1.put("category", "编程");
        doc1.put("author", "张三");
        
        Map<String, Object> doc2 = new HashMap<>();
        doc2.put("name", "机器学习基础");
        doc2.put("content", "机器学习是人工智能的一个分支，通过算法让计算机从数据中学习");
        doc2.put("category", "AI");
        doc2.put("author", "李四");
        
        Map<String, Object> doc3 = new HashMap<>();
        doc3.put("name", "数据库设计原理");
        doc3.put("content", "数据库设计需要考虑数据的结构化存储和高效查询");
        doc3.put("category", "数据库");
        doc3.put("author", "王五");
        
        // 保存文档到Neo4j
        List<Map<String, Object>> documents = Arrays.asList(doc1, doc2, doc3);
        vertexSaver.saveVertices(documents, "Document");
        
        // 为每个文档计算并存储嵌入向量
        for (Map<String, Object> doc : documents) {
            String name = (String) doc.get("name");
            boolean success = vertexSaver.computeAndStoreEmbedding(name, "content", "Document", "embedding");
            log.info("为文档 '{}' 计算嵌入向量: {}", name, success ? "成功" : "失败");
        }
        
        log.info("测试文档创建完成");
    }
}
