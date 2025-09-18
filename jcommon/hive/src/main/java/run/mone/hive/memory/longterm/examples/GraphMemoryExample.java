package run.mone.hive.memory.longterm.examples;

import run.mone.hive.memory.longterm.core.Memory;
import run.mone.hive.memory.longterm.config.MemoryConfig;
import run.mone.hive.memory.longterm.config.LlmConfig;
import run.mone.hive.memory.longterm.config.EmbedderConfig;
import run.mone.hive.memory.longterm.config.VectorStoreConfig;
import run.mone.hive.memory.longterm.config.GraphStoreConfig;
import run.mone.hive.memory.longterm.graph.GraphStoreBase;
import run.mone.hive.memory.longterm.graph.GraphStoreFactory;
import run.mone.hive.memory.longterm.graph.GraphUtils;

import java.util.List;
import java.util.Map;
import java.util.Arrays;

/**
 * 图存储Memory使用示例
 * 展示如何使用图数据库功能进行知识图谱构建
 */
public class GraphMemoryExample {
    
    public static void main(String[] args) {
        // 基础图存储示例
        basicGraphExample();
        
        // 集成Memory的图存储示例
        integratedGraphMemoryExample();
        
        // 实体关系处理示例
        entityRelationshipExample();
    }
    
    /**
     * 基础图存储示例
     */
    public static void basicGraphExample() {
        System.out.println("=== 基础图存储示例 ===");
        
        try {
            // 创建Neo4j图存储配置
            GraphStoreConfig graphConfig = GraphStoreConfig.builder()
                .provider(GraphStoreConfig.Provider.NEO4J)
                .url("bolt://localhost:7687")
                .username("neo4j")
                .password("password")
                .database("neo4j")
                .enabled(true)
                .build();
            
            // 创建图存储实例
            GraphStoreBase graphStore = GraphStoreFactory.create(graphConfig);
            
            if (graphStore != null) {
                // 添加图记忆
                Map<String, Object> result1 = graphStore.addMemory(
                    "张三", "北京大学", "works_at", "Person", "University"
                );
                System.out.println("添加图记忆1: " + result1);
                
                Map<String, Object> result2 = graphStore.addMemory(
                    "张三", "海淀区", "lives_in", "Person", "District"
                );
                System.out.println("添加图记忆2: " + result2);
                
                Map<String, Object> result3 = graphStore.addMemory(
                    "李四", "张三", "student_of", "Person", "Person"
                );
                System.out.println("添加图记忆3: " + result3);
                
                // 检查关系是否存在
                boolean exists = graphStore.relationshipExists("张三", "北京大学", "works_at");
                System.out.println("关系是否存在: " + exists);
                
                // 获取所有图记忆
                List<Map<String, Object>> allMemories = graphStore.getAll(10);
                System.out.println("所有图记忆: " + allMemories);
                
                // 获取统计信息
                Map<String, Object> stats = graphStore.getStats();
                System.out.println("图存储统计: " + stats);
                
                // 关闭连接
                graphStore.close();
            } else {
                System.out.println("图存储未启用或创建失败");
            }
            
        } catch (Exception e) {
            System.err.println("基础图存储示例执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 集成Memory的图存储示例
     */
    public static void integratedGraphMemoryExample() {
        System.out.println("\n=== 集成Memory的图存储示例 ===");
        
        try {
            // 创建带图存储的完整配置
            MemoryConfig memoryConfig = MemoryConfig.builder()
                .llm(LlmConfig.builder()
                    .provider(LlmConfig.Provider.OPENAI)
                    .model("gpt-4o-mini")
                    .temperature(0.1)
                    .build())
                .embedder(EmbedderConfig.builder()
                    .provider(EmbedderConfig.Provider.OPENAI)
                    .model("text-embedding-3-small")
                    .build())
                .vectorStore(VectorStoreConfig.builder()
                    .provider(VectorStoreConfig.Provider.QDRANT)
                    .collectionName("graph_memory")
                    .build())
                .graphStore(GraphStoreConfig.builder()
                    .provider(GraphStoreConfig.Provider.NEO4J)
                    .url("bolt://localhost:7687")
                    .username("neo4j")
                    .password("password")
                    .enabled(true)
                    .build())
                .build();
            
            // 创建Memory实例
            Memory memory = new Memory(memoryConfig);
            
            String userId = "graph_user";
            
            // 添加包含实体关系的记忆
            String complexText = "张三是北京大学的计算机科学教授，他住在海淀区。李四是他的博士生，专门研究人工智能。" +
                               "王五是清华大学的教授，与张三经常合作研究。";
            
            Map<String, Object> result = memory.add(complexText, userId, null, null, 
                Map.of("topic", "学术关系"), true, null, null);
            System.out.println("添加复杂记忆结果: " + result);
            
            // 搜索相关记忆
            Map<String, Object> searchResult = memory.search("张三的学生", userId, null, null, 5, null, null);
            System.out.println("搜索结果: " + searchResult);
            
            // 添加更多关系信息
            String additionalText = "张三发表了一篇关于机器学习的论文，李四是第二作者。这篇论文发表在AAAI会议上。";
            
            Map<String, Object> result2 = memory.add(additionalText, userId, null, null, 
                Map.of("topic", "学术成果"), true, null, null);
            System.out.println("添加学术成果记忆: " + result2);
            
            // 获取所有相关记忆
            Map<String, Object> allMemories = memory.getAll(userId, null, null, null, 20);
            System.out.println("所有相关记忆: " + allMemories);
            
            // 关闭资源
            memory.close();
            
        } catch (Exception e) {
            System.err.println("集成图存储示例执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 实体关系处理示例
     */
    public static void entityRelationshipExample() {
        System.out.println("\n=== 实体关系处理示例 ===");
        
        try {
            // 创建图存储配置
            GraphStoreConfig graphConfig = GraphStoreConfig.builder()
                .provider(GraphStoreConfig.Provider.NEO4J)
                .url("bolt://localhost:7687")
                .username("neo4j")
                .password("password")
                .enabled(true)
                .build();
            
            GraphStoreBase graphStore = GraphStoreFactory.create(graphConfig);
            
            if (graphStore != null) {
                // 使用工具类处理图记忆
                String existingMemoriesText = "张三 -- works_at -- 北京大学\n李四 -- student_of -- 张三";
                String newMemoryText = "张三 -- lives_in -- 海淀区";
                
                // 生成更新图记忆的提示词
                String updatePrompt = GraphUtils.getUpdateGraphPrompt(existingMemoriesText, newMemoryText);
                System.out.println("更新提示词: " + updatePrompt);
                
                // 解析图记忆字符串
                List<Map<String, Object>> memories = GraphUtils.parseMemoriesFromString(existingMemoriesText);
                System.out.println("解析的图记忆: " + memories);
                
                // 格式化图记忆
                String formattedMemories = GraphUtils.formatMemoriesToString(memories);
                System.out.println("格式化的图记忆: " + formattedMemories);
                
                // 获取图统计信息
                Map<String, Object> graphStats = GraphUtils.getGraphStats(memories);
                System.out.println("图统计信息: " + graphStats);
                
                // 验证图实体
                boolean valid1 = GraphUtils.validateGraphEntity("张三", "北京大学", "works_at");
                boolean valid2 = GraphUtils.validateGraphEntity("", "北京大学", "works_at");
                System.out.println("实体验证1: " + valid1);
                System.out.println("实体验证2: " + valid2);
                
                // 清理实体名称
                String cleanEntity = GraphUtils.cleanEntityName("  \"张三\"  ");
                String cleanRelation = GraphUtils.cleanRelationshipName("Works At");
                System.out.println("清理后的实体: " + cleanEntity);
                System.out.println("清理后的关系: " + cleanRelation);
                
                // 去重测试
                List<Map<String, Object>> duplicatedMemories = Arrays.asList(
                    Map.of("source", "张三", "relationship", "works_at", "destination", "北京大学"),
                    Map.of("source", "张三", "relationship", "works_at", "destination", "北京大学"),
                    Map.of("source", "李四", "relationship", "student_of", "destination", "张三")
                );
                
                List<Map<String, Object>> deduplicatedMemories = GraphUtils.deduplicateMemories(duplicatedMemories);
                System.out.println("原始记忆数量: " + duplicatedMemories.size());
                System.out.println("去重后记忆数量: " + deduplicatedMemories.size());
                
                graphStore.close();
            }
            
        } catch (Exception e) {
            System.err.println("实体关系处理示例执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 图记忆管理示例
     */
    public static void graphMemoryManagementExample() {
        System.out.println("\n=== 图记忆管理示例 ===");
        
        try {
            GraphStoreConfig graphConfig = GraphStoreConfig.builder()
                .provider(GraphStoreConfig.Provider.NEO4J)
                .url("bolt://localhost:7687")
                .username("neo4j")
                .password("password")
                .enabled(true)
                .build();
            
            GraphStoreBase graphStore = GraphStoreFactory.create(graphConfig);
            
            if (graphStore != null) {
                // 批量添加图记忆
                List<GraphStoreBase.GraphEntity> entities = Arrays.asList(
                    new GraphStoreBase.GraphEntity("Alice", "Company A", "works_at", "Person", "Company"),
                    new GraphStoreBase.GraphEntity("Bob", "Alice", "reports_to", "Person", "Person"),
                    new GraphStoreBase.GraphEntity("Alice", "Project X", "manages", "Person", "Project")
                );
                
                List<Map<String, Object>> batchResults = graphStore.addMemories(entities);
                System.out.println("批量添加结果: " + batchResults);
                
                // 更新图记忆
                Map<String, Object> updateResult = graphStore.updateMemory("Alice", "Company A", "senior_works_at");
                System.out.println("更新结果: " + updateResult);
                
                // 获取节点关系
                List<Map<String, Object>> aliceRelations = graphStore.getNodeRelationships("Alice");
                System.out.println("Alice的关系: " + aliceRelations);
                
                // 搜索图记忆
                List<Map<String, Object>> searchResults = graphStore.search("Alice", 10);
                System.out.println("搜索Alice的结果: " + searchResults);
                
                // 删除特定关系
                Map<String, Object> deleteResult = graphStore.deleteMemory("Bob", "Alice", "reports_to");
                System.out.println("删除结果: " + deleteResult);
                
                graphStore.close();
            }
            
        } catch (Exception e) {
            System.err.println("图记忆管理示例执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}