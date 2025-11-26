package run.mone.neo4j;

import run.mone.neo4j.VertexSaver.VectorSearchResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 向量相似度搜索完整示例
 * 演示如何使用VertexSaver进行向量相似度查询
 */
public class VectorSimilaritySearchExample {
    
    public static void main(String[] args) {
        System.out.println("=== Neo4j向量相似度搜索示例 ===");
        
        // 1. 创建VertexSaver实例并初始化嵌入模型
        VertexSaver vertexSaver = new VertexSaver()
                .initDefaultEmbedding();  // 使用默认Ollama嵌入模型
        
        try {
            // 2. 测试数据库连接
            System.out.println("\n--- 测试数据库连接 ---");
            if (!vertexSaver.testConnection()) {
                System.err.println("❌ 无法连接到Neo4j数据库，请检查配置");
                System.err.println("请确保:");
                System.err.println("1. Neo4j服务正在运行");
                System.err.println("2. 设置了正确的NEO4J_PASSWORD环境变量");
                System.err.println("3. Neo4j运行在bolt://localhost:7687");
                return;
            }
            System.out.println("✅ Neo4j数据库连接成功");
            
            // 3. 清理并创建测试数据
            System.out.println("\n--- 准备测试数据 ---");
            prepareTestData(vertexSaver);
            
            // 4. 等待一下让嵌入计算完成
            System.out.println("⏳ 等待嵌入向量计算完成...");
            Thread.sleep(2000);
            
            // 5. 执行向量相似度搜索测试
            System.out.println("\n=== 开始向量相似度搜索测试 ===");
            
            // 测试1: 使用文本查询相似文档
            testTextSimilaritySearch(vertexSaver);
            
            // 测试2: 使用预定义向量查询
            testVectorSimilaritySearch(vertexSaver);
            
            // 测试3: 不同标签的搜索
            testMultiLabelSearch(vertexSaver);
            
            // 测试4: 不同topK值的搜索
            testDifferentTopK(vertexSaver);
            
            System.out.println("\n🎉 所有测试完成！");
            
        } catch (Exception e) {
            System.err.println("❌ 执行过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 准备测试数据
     */
    private static void prepareTestData(VertexSaver vertexSaver) {
        // 清理现有数据
        System.out.println("🧹 清理现有测试数据...");
        try {
            vertexSaver.deleteVerticesByLabel("Document");
            vertexSaver.deleteVerticesByLabel("Product");
            vertexSaver.deleteVerticesByLabel("Article");
        } catch (Exception e) {
            System.out.println("清理数据时出现错误（可能是正常的）: " + e.getMessage());
        }
        
        // 创建文档数据
        System.out.println("📄 创建文档数据...");
        createDocuments(vertexSaver);
        
        // 创建产品数据
        System.out.println("🛍️ 创建产品数据...");
        createProducts(vertexSaver);
        
        // 创建文章数据
        System.out.println("📰 创建文章数据...");
        createArticles(vertexSaver);
        
        // 计算所有文档的嵌入向量
        System.out.println("🧠 计算嵌入向量...");
        computeEmbeddings(vertexSaver);
    }
    
    /**
     * 创建文档数据
     */
    private static void createDocuments(VertexSaver vertexSaver) {
        Map<String, Object> doc1 = new HashMap<>();
        doc1.put("name", "Java编程指南");
        doc1.put("content", "Java是一种面向对象的编程语言，具有跨平台、安全性高、性能优秀的特点。它被广泛应用于企业级应用开发。");
        doc1.put("category", "编程技术");
        doc1.put("author", "张三");
        doc1.put("tags", "Java,编程,面向对象");
        
        Map<String, Object> doc2 = new HashMap<>();
        doc2.put("name", "Python数据科学");
        doc2.put("content", "Python是数据科学领域最受欢迎的编程语言之一，拥有丰富的数据分析和机器学习库，如pandas、numpy、scikit-learn等。");
        doc2.put("category", "数据科学");
        doc2.put("author", "李四");
        doc2.put("tags", "Python,数据科学,机器学习");
        
        Map<String, Object> doc3 = new HashMap<>();
        doc3.put("name", "机器学习基础");
        doc3.put("content", "机器学习是人工智能的一个重要分支，通过算法让计算机从数据中学习规律，并做出预测或决策。包括监督学习、无监督学习等。");
        doc3.put("category", "人工智能");
        doc3.put("author", "王五");
        doc3.put("tags", "机器学习,人工智能,算法");
        
        Map<String, Object> doc4 = new HashMap<>();
        doc4.put("name", "深度学习框架");
        doc4.put("content", "TensorFlow和PyTorch是目前最流行的深度学习框架，它们提供了构建和训练神经网络的强大工具和API。");
        doc4.put("category", "人工智能");
        doc4.put("author", "赵六");
        doc4.put("tags", "深度学习,TensorFlow,PyTorch");
        
        Map<String, Object> doc5 = new HashMap<>();
        doc5.put("name", "美食制作指南");
        doc5.put("content", "学会制作美味的家常菜，从选择新鲜食材到掌握烹饪技巧，让每一道菜都充满爱意和营养。");
        doc5.put("category", "生活美食");
        doc5.put("author", "孙七");
        doc5.put("tags", "美食,烹饪,家常菜");
        
        List<Map<String, Object>> documents = Arrays.asList(doc1, doc2, doc3, doc4, doc5);
        vertexSaver.saveVertices(documents, "Document");
        System.out.println("✅ 创建了 " + documents.size() + " 个文档");
    }
    
    /**
     * 创建产品数据
     */
    private static void createProducts(VertexSaver vertexSaver) {
        Map<String, Object> product1 = new HashMap<>();
        product1.put("name", "智能编程键盘");
        product1.put("content", "专为程序员设计的机械键盘，配备RGB背光和可编程按键，提升编程效率和体验。");
        product1.put("price", 599.0);
        product1.put("category", "电子产品");
        product1.put("brand", "TechPro");
        
        Map<String, Object> product2 = new HashMap<>();
        product2.put("name", "AI学习平板");
        product2.put("content", "搭载最新AI芯片的平板电脑，专门优化了机器学习和数据科学应用的性能，适合学习和研究。");
        product2.put("price", 3999.0);
        product2.put("category", "电子产品");
        product2.put("brand", "AITech");
        
        Map<String, Object> product3 = new HashMap<>();
        product3.put("name", "厨房智能助手");
        product3.put("content", "集成语音控制和食谱推荐的智能厨房设备，帮助用户轻松制作美味佳肴，支持多种烹饪模式。");
        product3.put("price", 1299.0);
        product3.put("category", "智能家居");
        product3.put("brand", "SmartHome");
        
        List<Map<String, Object>> products = Arrays.asList(product1, product2, product3);
        vertexSaver.saveVertices(products, "Product");
        System.out.println("✅ 创建了 " + products.size() + " 个产品");
    }
    
    /**
     * 创建文章数据
     */
    private static void createArticles(VertexSaver vertexSaver) {
        Map<String, Object> article1 = new HashMap<>();
        article1.put("name", "编程语言发展史");
        article1.put("content", "从早期的汇编语言到现代的高级编程语言，编程语言的发展经历了多个重要阶段，每种语言都有其独特的特点和应用场景。");
        article1.put("publishDate", "2024-01-15");
        article1.put("category", "技术历史");
        
        Map<String, Object> article2 = new HashMap<>();
        article2.put("name", "人工智能的未来");
        article2.put("content", "人工智能技术正在快速发展，从图像识别到自然语言处理，AI正在改变我们的生活方式和工作模式。");
        article2.put("publishDate", "2024-02-20");
        article2.put("category", "科技前沿");
        
        List<Map<String, Object>> articles = Arrays.asList(article1, article2);
        vertexSaver.saveVertices(articles, "Article");
        System.out.println("✅ 创建了 " + articles.size() + " 个文章");
    }
    
    /**
     * 计算所有数据的嵌入向量
     */
    private static void computeEmbeddings(VertexSaver vertexSaver) {
        // 为文档计算嵌入
        List<String> docNames = Arrays.asList("Java编程指南", "Python数据科学", "机器学习基础", "深度学习框架", "美食制作指南");
        int docSuccess = vertexSaver.batchComputeAndStoreEmbedding(docNames, "content", "Document", "embedding");
        System.out.println("📊 文档嵌入计算完成: " + docSuccess + "/" + docNames.size());
        
        // 为产品计算嵌入
        List<String> productNames = Arrays.asList("智能编程键盘", "AI学习平板", "厨房智能助手");
        int productSuccess = vertexSaver.batchComputeAndStoreEmbedding(productNames, "content", "Product", "embedding");
        System.out.println("📊 产品嵌入计算完成: " + productSuccess + "/" + productNames.size());
        
        // 为文章计算嵌入
        List<String> articleNames = Arrays.asList("编程语言发展史", "人工智能的未来");
        int articleSuccess = vertexSaver.batchComputeAndStoreEmbedding(articleNames, "content", "Article", "embedding");
        System.out.println("📊 文章嵌入计算完成: " + articleSuccess + "/" + articleNames.size());
    }
    
    /**
     * 测试文本相似度搜索
     */
    private static void testTextSimilaritySearch(VertexSaver vertexSaver) {
        System.out.println("\n🔍 测试1: 文本相似度搜索");
        
        String[] queries = {
            "编程语言和开发工具",
            "人工智能和机器学习",
            "美食烹饪技巧"
        };
        
        for (String query : queries) {
            System.out.println("\n📝 查询: \"" + query + "\"");
            
            // 在Document标签中搜索
            List<VectorSearchResult> results = vertexSaver.vectorSimilaritySearchByText(
                "document_embeddings", query, 3, "Document");
            
            printSearchResults("Document", results);
        }
    }
    
    /**
     * 测试向量相似度搜索
     */
    private static void testVectorSimilaritySearch(VertexSaver vertexSaver) {
        System.out.println("\n🔍 测试2: 使用现有向量进行搜索");
        
        // 获取"Java编程指南"的嵌入向量
        List<Double> javaEmbedding = vertexSaver.getVertexEmbedding("Java编程指南", "Document", "embedding");
        
        if (javaEmbedding != null) {
            System.out.println("\n📝 使用'Java编程指南'的向量查找相似文档:");
            
            List<VectorSearchResult> results = vertexSaver.vectorSimilaritySearch(
                "document_embeddings", javaEmbedding, 4, "Document");
            
            printSearchResults("Document", results);
        } else {
            System.out.println("❌ 无法获取Java编程指南的嵌入向量");
        }
    }
    
    /**
     * 测试多标签搜索
     */
    private static void testMultiLabelSearch(VertexSaver vertexSaver) {
        System.out.println("\n🔍 测试3: 多标签搜索");
        
        String query = "智能技术产品";
        
        // 在不同标签中搜索
        String[] labels = {"Document", "Product", "Article"};
        
        for (String label : labels) {
            System.out.println("\n📝 在 " + label + " 中搜索: \"" + query + "\"");
            
            List<VectorSearchResult> results = vertexSaver.vectorSimilaritySearchByText(
                "document_embeddings", query, 2, label);
            
            printSearchResults(label, results);
        }
    }
    
    /**
     * 测试不同topK值
     */
    private static void testDifferentTopK(VertexSaver vertexSaver) {
        System.out.println("\n🔍 测试4: 不同TopK值的影响");
        
        String query = "编程开发技术";
        int[] topKValues = {1, 3, 5};
        
        for (int topK : topKValues) {
            System.out.println("\n📝 TopK=" + topK + ", 查询: \"" + query + "\"");
            
            List<VectorSearchResult> results = vertexSaver.vectorSimilaritySearchByText(
                "document_embeddings", query, topK, "Document");
            
            printSearchResults("Document", results);
        }
    }
    
    /**
     * 打印搜索结果
     */
    private static void printSearchResults(String label, List<VectorSearchResult> results) {
        if (results.isEmpty()) {
            System.out.println("   ❌ 没有找到相似的" + label + "结果");
            return;
        }
        
        System.out.println("   ✅ 找到 " + results.size() + " 个相似的" + label + ":");
        
        for (int i = 0; i < results.size(); i++) {
            VectorSearchResult result = results.get(i);
            System.out.printf("   %d. 📄 %s (相似度: %.4f)%n", 
                i + 1, result.getName(), result.getScore());
            
            // 显示部分内容
            Map<String, Object> vertex = result.getVertex();
            if (vertex != null) {
                String content = (String) vertex.get("content");
                if (content != null) {
                    String preview = content.length() > 60 ? 
                        content.substring(0, 60) + "..." : content;
                    System.out.println("      💭 " + preview);
                }
                
                // 显示其他有用信息
                Object category = vertex.get("category");
                Object author = vertex.get("author");
                Object price = vertex.get("price");
                
                if (category != null) System.out.println("      🏷️ 分类: " + category);
                if (author != null) System.out.println("      👤 作者: " + author);
                if (price != null) System.out.println("      💰 价格: ¥" + price);
            }
            System.out.println();
        }
    }
}
