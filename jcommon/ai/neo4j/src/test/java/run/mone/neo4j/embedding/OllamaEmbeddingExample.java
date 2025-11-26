package run.mone.neo4j.embedding;

import run.mone.neo4j.embedding.config.EmbedderConfig;
import run.mone.neo4j.embedding.impl.OllamaEmbedding;

import java.util.Arrays;
import java.util.List;

/**
 * Ollama嵌入模型使用示例
 * 展示如何使用OllamaEmbedding类生成文本嵌入向量
 */
public class OllamaEmbeddingExample {
    
    public static void main(String[] args) {
        System.out.println("=== Ollama嵌入模型示例 ===");
        
        // 1. 创建配置
        EmbedderConfig config = new EmbedderConfig();
        config.setModel("embeddinggemma");  // 使用embeddinggemma模型
        config.setBaseUrl("http://localhost:11434");  // 本地Ollama服务地址
        
        // 2. 创建嵌入模型实例
        OllamaEmbedding embedding = new OllamaEmbedding(config);
        
        try {
            // 3. 单个文本嵌入
            System.out.println("\n--- 单个文本嵌入 ---");
            String text = "人工智能是计算机科学的一个分支";
            List<Double> vector = embedding.embed(text);
            
            System.out.println("文本: " + text);
            System.out.println("向量维度: " + vector.size());
            System.out.println("前10个值: " + vector.subList(0, Math.min(10, vector.size())));
            
            // 4. 批量文本嵌入
            System.out.println("\n--- 批量文本嵌入 ---");
            List<String> texts = Arrays.asList(
                "机器学习是人工智能的核心技术",
                "深度学习基于神经网络",
                "自然语言处理让机器理解人类语言",
                "计算机视觉让机器看懂世界"
            );
            
            List<List<Double>> vectors = embedding.embedBatch(texts, "add");
            
            for (int i = 0; i < texts.size(); i++) {
                System.out.printf("文本%d: %s -> 向量维度: %d%n", 
                    i + 1, texts.get(i), vectors.get(i).size());
            }
            
            // 5. 获取模型信息
            System.out.println("\n--- 模型信息 ---");
            System.out.println("模型维度: " + embedding.getDimensions());
            System.out.println("常见嵌入模型: " + OllamaEmbedding.getCommonEmbeddingModels());
            
            // 6. 获取可用模型（需要Ollama服务运行）
            System.out.println("\n--- 可用模型 ---");
            List<String> availableModels = embedding.getAvailableModels();
            System.out.println("可用的嵌入模型: " + availableModels);
            
            // 7. 计算文本相似度示例
            System.out.println("\n--- 文本相似度计算示例 ---");
            String text1 = "机器学习很有趣";
            String text2 = "人工智能很有趣";
            String text3 = "今天天气很好";
            
            List<Double> vec1 = embedding.embed(text1);
            List<Double> vec2 = embedding.embed(text2);
            List<Double> vec3 = embedding.embed(text3);
            
            double similarity12 = cosineSimilarity(vec1, vec2);
            double similarity13 = cosineSimilarity(vec1, vec3);
            
            System.out.printf("'%s' 和 '%s' 的相似度: %.4f%n", text1, text2, similarity12);
            System.out.printf("'%s' 和 '%s' 的相似度: %.4f%n", text1, text3, similarity13);
            
        } catch (Exception e) {
            System.err.println("错误: " + e.getMessage());
            System.err.println("请确保Ollama服务正在运行，并且已安装embeddinggemma模型");
            System.err.println("安装模型命令: ollama pull embeddinggemma");
        }
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
