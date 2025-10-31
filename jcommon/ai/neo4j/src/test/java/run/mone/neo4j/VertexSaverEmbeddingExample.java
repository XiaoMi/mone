package run.mone.neo4j;

import run.mone.neo4j.embedding.config.EmbedderConfig;
import run.mone.neo4j.embedding.impl.OllamaEmbedding;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * VertexSaver嵌入功能使用示例
 * 演示如何为Neo4j顶点计算和存储嵌入向量
 */
public class VertexSaverEmbeddingExample {
    
    public static void main(String[] args) {
        System.out.println("=== VertexSaver嵌入功能示例 ===");
        
        // 1. 创建VertexSaver实例
        VertexSaver vertexSaver = new VertexSaver()
                .initDefaultEmbedding();  // 初始化默认Ollama嵌入模型
        
        try {
            // 2. 测试数据库连接
            if (!vertexSaver.testConnection()) {
                System.err.println("无法连接到Neo4j数据库，请检查连接配置");
                return;
            }
            
            // 3. 创建测试数据
            System.out.println("\n--- 创建测试顶点 ---");
            createTestVertices(vertexSaver);
            
            // 4. 为单个顶点计算嵌入向量
            System.out.println("\n--- 单个顶点嵌入计算 ---");
            boolean success1 = vertexSaver.computeAndStoreEmbedding("文档1", "content", "Document");
            System.out.println("文档1嵌入计算结果: " + (success1 ? "成功" : "失败"));
            
            boolean success2 = vertexSaver.computeAndStoreEmbedding("产品1", "description", "Product");
            System.out.println("产品1嵌入计算结果: " + (success2 ? "成功" : "失败"));
            
            // 5. 批量计算嵌入向量
            System.out.println("\n--- 批量嵌入计算 ---");
            List<String> documentNames = Arrays.asList("文档1", "文档2", "文档3");
            int successCount = vertexSaver.batchComputeAndStoreEmbedding(documentNames, "content", "Document", "embedding");
            System.out.println("批量处理结果: " + successCount + "/" + documentNames.size() + " 成功");
            
            // 6. 查询嵌入向量
            System.out.println("\n--- 查询嵌入向量 ---");
            List<Double> embedding1 = vertexSaver.getVertexEmbedding("文档1", "Document", "embedding");
            if (embedding1 != null) {
                System.out.println("文档1的嵌入向量维度: " + embedding1.size());
                System.out.println("前5个值: " + embedding1.subList(0, Math.min(5, embedding1.size())));
            }
            
            // 7. 计算文档相似度示例
            System.out.println("\n--- 文档相似度计算 ---");
            List<Double> embedding2 = vertexSaver.getVertexEmbedding("文档2", "Document", "embedding");
            List<Double> embedding3 = vertexSaver.getVertexEmbedding("文档3", "Document", "embedding");
            
            if (embedding1 != null && embedding2 != null && embedding3 != null) {
                double similarity12 = cosineSimilarity(embedding1, embedding2);
                double similarity13 = cosineSimilarity(embedding1, embedding3);
                
                System.out.printf("文档1和文档2的相似度: %.4f%n", similarity12);
                System.out.printf("文档1和文档3的相似度: %.4f%n", similarity13);
            }
            
            // 8. 查询所有文档顶点
            System.out.println("\n--- 查询所有文档顶点 ---");
            List<Map<String, Object>> documents = vertexSaver.getVerticesByLabel("Document");
            for (Map<String, Object> doc : documents) {
                System.out.println("文档: " + doc.get("name") + 
                                 ", 嵌入更新时间: " + doc.get("embeddingUpdatedAt") +
                                 ", 嵌入来源: " + doc.get("embeddingSource"));
            }
            
        } catch (Exception e) {
            System.err.println("执行过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 创建测试顶点数据
     */
    private static void createTestVertices(VertexSaver vertexSaver) {
        // 创建文档顶点
        Map<String, Object> doc1 = new HashMap<>();
        doc1.put("name", "文档1");
        doc1.put("title", "人工智能基础");
        doc1.put("content", "人工智能是计算机科学的一个分支，它试图理解智能的实质，并生产出一种新的能以人类智能相似的方式做出反应的智能机器。");
        doc1.put("author", "张三");
        doc1.put("type", "技术文档");
        
        Map<String, Object> doc2 = new HashMap<>();
        doc2.put("name", "文档2");
        doc2.put("title", "机器学习算法");
        doc2.put("content", "机器学习是人工智能的一个重要分支，它通过算法让计算机能够从数据中学习，并做出预测或决策。");
        doc2.put("author", "李四");
        doc2.put("type", "技术文档");
        
        Map<String, Object> doc3 = new HashMap<>();
        doc3.put("name", "文档3");
        doc3.put("title", "今日天气预报");
        doc3.put("content", "今天天气晴朗，温度适宜，是外出游玩的好日子。建议大家多到户外活动。");
        doc3.put("author", "王五");
        doc3.put("type", "生活信息");
        
        List<Map<String, Object>> documents = Arrays.asList(doc1, doc2, doc3);
        vertexSaver.saveVertices(documents, "Document");
        
        // 创建产品顶点
        Map<String, Object> product1 = new HashMap<>();
        product1.put("name", "产品1");
        product1.put("title", "智能手机");
        product1.put("description", "一款功能强大的智能手机，配备高性能处理器和优质摄像头，为用户提供卓越的使用体验。");
        product1.put("price", 2999.0);
        product1.put("category", "电子产品");
        
        Map<String, Object> product2 = new HashMap<>();
        product2.put("name", "产品2");
        product2.put("title", "笔记本电脑");
        product2.put("description", "轻薄便携的笔记本电脑，适合办公和学习使用，续航能力强，性能稳定。");
        product2.put("price", 5999.0);
        product2.put("category", "电子产品");
        
        List<Map<String, Object>> products = Arrays.asList(product1, product2);
        vertexSaver.saveVertices(products, "Product");
        
        System.out.println("测试数据创建完成！");
    }
    
    /**
     * 计算两个向量的余弦相似度
     */
    private static double cosineSimilarity(List<Double> vec1, List<Double> vec2) {
        if (vec1.size() != vec2.size()) {
            throw new IllegalArgumentException("向量维度不匹配");
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int i = 0; i < vec1.size(); i++) {
            dotProduct += vec1.get(i) * vec2.get(i);
            norm1 += Math.pow(vec1.get(i), 2);
            norm2 += Math.pow(vec2.get(i), 2);
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
