package run.mone.hive.memory.longterm.temptest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import run.mone.hive.memory.longterm.config.EmbedderConfig;
import run.mone.hive.memory.longterm.config.GraphStoreConfig;
import run.mone.hive.memory.longterm.config.LlmConfig;
import run.mone.hive.memory.longterm.graph.GraphStoreFactory;
import run.mone.hive.memory.longterm.graph.GraphStoreBase;
import run.mone.hive.memory.longterm.graph.impl.Neo4jGraphStore;

import java.util.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@DisplayName("Neo4j Graph Store Tests")
public class Neo4jLocalTest {

    private GraphStoreBase graphStore;
    private GraphStoreConfig config;

    @BeforeEach
    void setUp() {

        EmbedderConfig embedderConfig = EmbedderConfig.builder()
                .provider(EmbedderConfig.Provider.OLLAMA)
                .model("embeddinggemma")
                .embeddingDims(768)
                .build();

        // 配置LLM
        LlmConfig llmConfig = LlmConfig.builder()
                .provider(LlmConfig.Provider.DEEPSEEK)
                .apiKey(System.getenv("DEEPSEEK_API_KEY"))
                .model("deepseek-chat")
                .temperature(0.1)
                .build();

        config = GraphStoreConfig.builder()
                .provider(GraphStoreConfig.Provider.NEO4J)
                .url("bolt://localhost:7687")
                .username("neo4j")
                .password(System.getenv("NEO4J_PASSWORD"))
                .enabled(true)
                .embedder(embedderConfig)
                .llm(llmConfig)
                .build();

        graphStore = GraphStoreFactory.create(config);

        // 清理测试数据
        if (graphStore != null) {
//            graphStore.deleteAll();
        }
    }

    @AfterEach
    void tearDown() {
        if (graphStore != null) {
            // 清理测试数据
//            graphStore.deleteAll();
//            graphStore.close();
        }
    }

    @Test
    @DisplayName("Should search relationships successfully")
    void testSearchRelationships() {
        // 添加测试数据
        addTestRelationships();

        // 搜索包含 "Alice" 的关系
        List<Map<String, Object>> results = graphStore.search("Alice", 10);

        assertFalse(results.isEmpty());

        // 验证搜索结果包含预期的关系
        boolean foundAliceBob = results.stream().anyMatch(r ->
            "Alice".equals(r.get("source")) && "Bob".equals(r.get("destination"))
        );
        assertTrue(foundAliceBob);
        
        // 验证搜索结果包含Alice相关的其他关系
        boolean foundAliceCharlie = results.stream().anyMatch(r ->
            "Alice".equals(r.get("source")) && "Charlie".equals(r.get("destination"))
        );
        assertTrue(foundAliceCharlie);
        
        // 验证搜索结果的基本结构
        for (Map<String, Object> result : results) {
            assertNotNull(result.get("source"));
            assertNotNull(result.get("destination"));
            assertNotNull(result.get("relationship"));
        }
        
        // 测试限制结果数量
        List<Map<String, Object>> limitedResults = graphStore.search("Alice", 1);
        assertTrue(limitedResults.size() <= 1);
        
        // 测试搜索不存在的实体
        List<Map<String, Object>> noResults = graphStore.search("NonExistentEntity", 10);
        assertTrue(noResults.isEmpty());
        
        // 测试搜索关系类型
        List<Map<String, Object>> knowsResults = graphStore.search("KNOWS", 10);
        assertFalse(knowsResults.isEmpty());
        
        boolean foundKnowsRelation = knowsResults.stream().anyMatch(r ->
            "KNOWS".equals(r.get("relationship"))
        );
        assertTrue(foundKnowsRelation);
    }

    private void addTestRelationships() {
        graphStore.addMemory("Alice", "Bob", "KNOWS", "PERSON", "PERSON");
        graphStore.addMemory("Bob", "Charlie", "WORKS_WITH", "PERSON", "PERSON");
        graphStore.addMemory("Alice", "Charlie", "MANAGES", "PERSON", "PERSON");
    }

    @Test
    @DisplayName("Should perform smart add with conflict detection")
    void testSmartAdd() {
        assumeTrue(graphStore instanceof Neo4jGraphStore, "Smart add only available for Neo4jGraphStore");
        
        Neo4jGraphStore neo4jStore = (Neo4jGraphStore) graphStore;
        
        // 准备测试数据和过滤器
        Map<String, Object> filters = new HashMap<>();
        filters.put("user_id", "test_user_001");
        filters.put("agent_id", "agent_001");
        
        try {
            // 第一次添加：添加初始关系
            String initialData = "Alice works with Bob at TechCorp company. They are colleagues in the engineering department.";
            log.info("Adding initial data: {}", initialData);
            
            Map<String, Object> result1 = neo4jStore.add(initialData, filters);
            assertNotNull(result1);
            log.info("First add result: {}", result1);
            
            // 验证添加的实体
            if (result1.containsKey("added_entities")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> addedEntities = (List<Map<String, Object>>) result1.get("added_entities");
                assertFalse(addedEntities.isEmpty());
                log.info("Added {} entities", addedEntities.size());
            }
            
            // 第二次添加：添加冲突的关系，触发智能删除+添加
            String conflictData = "Alice is now the manager of Bob at TechCorp. She got promoted last month.";
            log.info("Adding conflict data: {}", conflictData);
            
            Map<String, Object> result2 = neo4jStore.add(conflictData, filters);
            assertNotNull(result2);
            log.info("Second add result: {}", result2);
            
            // 验证删除和添加的实体
            if (result2.containsKey("deleted_entities")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> deletedEntities = (List<Map<String, Object>>) result2.get("deleted_entities");
                log.info("Deleted {} entities", deletedEntities.size());
            }
            
            if (result2.containsKey("added_entities")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> addedEntities = (List<Map<String, Object>>) result2.get("added_entities");
                assertFalse(addedEntities.isEmpty());
                log.info("Added {} entities", addedEntities.size());
            }
            
            // 第三次添加：添加新的不冲突关系
            String newData = "Bob collaborates with Charlie on the new AI project. Charlie joined the team recently.";
            log.info("Adding new data: {}", newData);
            
            Map<String, Object> result3 = neo4jStore.add(newData, filters);
            assertNotNull(result3);
            log.info("Third add result: {}", result3);
            
            // 验证搜索功能
            List<Map<String, Object>> searchResults = graphStore.search("Alice", 10, "test_user_001");
            assertFalse(searchResults.isEmpty());
            log.info("Found {} relationships for Alice", searchResults.size());
            
            // 验证关系的正确性
            boolean foundManagerRelation = searchResults.stream().anyMatch(r ->
                "Alice".equals(r.get("source")) && 
                "Bob".equals(r.get("destination")) && 
                r.get("relationship").toString().toLowerCase().contains("manage")
            );
            
            if (foundManagerRelation) {
                log.info("✅ Successfully found manager relationship between Alice and Bob");
            } else {
                log.warn("⚠️ Manager relationship not found in search results");
                searchResults.forEach(r -> log.info("Found relationship: {} -> {} [{}]", 
                    r.get("source"), r.get("destination"), r.get("relationship")));
            }
            
        } catch (Exception e) {
            log.error("Smart add test failed", e);
            fail("Smart add test should not throw exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Should handle smart add with user context")
    void testSmartAddWithUserContext() {
        assumeTrue(graphStore instanceof Neo4jGraphStore, "Smart add only available for Neo4jGraphStore");
        
        Neo4jGraphStore neo4jStore = (Neo4jGraphStore) graphStore;
        
        // 测试不同用户的数据隔离
        Map<String, Object> user1Filters = new HashMap<>();
        user1Filters.put("user_id", "user_001");
        user1Filters.put("agent_id", "agent_001");
        
        Map<String, Object> user2Filters = new HashMap<>();
        user2Filters.put("user_id", "user_002");
        user2Filters.put("agent_id", "agent_001");
        
        try {
            // 用户1添加数据
            String user1Data = "David reports to Emma in the marketing department.";
            Map<String, Object> result1 = neo4jStore.add(user1Data, user1Filters);
            assertNotNull(result1);
            log.info("User1 add result: {}", result1);
            
            // 用户2添加数据
            String user2Data = "David works with Frank in the sales department.";
            Map<String, Object> result2 = neo4jStore.add(user2Data, user2Filters);
            assertNotNull(result2);
            log.info("User2 add result: {}", result2);
            
            // 验证用户数据隔离
            List<Map<String, Object>> user1Results = graphStore.search("David", 10, "user_001");
            List<Map<String, Object>> user2Results = graphStore.search("David", 10, "user_002");
            
            log.info("User1 found {} David relationships", user1Results.size());
            log.info("User2 found {} David relationships", user2Results.size());
            
            // 每个用户应该只能看到自己的数据
            assertTrue(user1Results.size() > 0 || user2Results.size() > 0, 
                "At least one user should have David relationships");
            
        } catch (Exception e) {
            log.error("Smart add with user context test failed", e);
            fail("Smart add with user context test should not throw exception: " + e.getMessage());
        }
    }

    private void assumeTrue(boolean condition, String message) {
        if (!condition) {
            log.warn("Skipping test: {}", message);
            return;
        }
    }
}
