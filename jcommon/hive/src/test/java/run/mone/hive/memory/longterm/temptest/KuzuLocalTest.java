package run.mone.hive.memory.longterm.temptest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import run.mone.hive.memory.longterm.config.GraphStoreConfig;
import run.mone.hive.memory.longterm.graph.GraphStoreFactory;
import run.mone.hive.memory.longterm.graph.GraphStoreBase;
import run.mone.hive.memory.longterm.graph.GraphStoreBase.GraphEntity;

import java.util.*;
import java.io.File;

@DisplayName("Kuzu Local Embedded Graph Store Tests")
public class KuzuLocalTest {

    private GraphStoreBase graphStore;
    private GraphStoreConfig config;
    private String testPath = "./data/test/kuzu_test";

    @BeforeEach
    void setUp() {
        // 清理测试目录
        cleanupTestDirectory();

        config = GraphStoreConfig.builder()
                .provider(GraphStoreConfig.Provider.KUZU)
                .url(testPath)
                .enabled(true)
                .build();

        graphStore = GraphStoreFactory.create(config);
    }

    @AfterEach
    void tearDown() {
        if (graphStore != null) {
            graphStore.close();
        }
        cleanupTestDirectory();
    }

    private void cleanupTestDirectory() {
        File testDir = new File(testPath);
        if (testDir.exists()) {
            deleteDirectory(testDir);
        }
    }

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

    @Test
    @DisplayName("Should initialize Kuzu embedded store successfully")
    void testInitialization() {
        assertNotNull(graphStore);
        assertTrue(graphStore.validateConnection());

        Map<String, Object> stats = graphStore.getStats();
        assertEquals("kuzu", stats.get("provider"));
        assertTrue((Boolean) stats.get("embedded_mode"));
        assertEquals(0L, stats.get("node_count"));
        assertEquals(0L, stats.get("relationship_count"));
    }

    @Test
    @DisplayName("Should add graph memory successfully")
    void testAddMemory() {
        Map<String, Object> result = graphStore.addMemory(
            "Alice", "Bob", "KNOWS", "PERSON", "PERSON"
        );

        assertEquals("add_graph_memory", result.get("action"));
        assertEquals("success", result.get("status"));
        assertEquals("Alice", result.get("source"));
        assertEquals("Bob", result.get("destination"));
        assertEquals("KNOWS", result.get("relationship"));

        // 验证关系存在
        assertTrue(graphStore.relationshipExists("Alice", "Bob", "KNOWS"));
    }

    @Test
    @DisplayName("Should update graph memory successfully")
    void testUpdateMemory() {
        // 先添加关系
        graphStore.addMemory("Alice", "Bob", "KNOWS", "PERSON", "PERSON");

        // 更新关系
        Map<String, Object> result = graphStore.updateMemory("Alice", "Bob", "FRIEND_OF");

        assertEquals("update_graph_memory", result.get("action"));
        assertEquals("success", result.get("status"));
        assertEquals("FRIEND_OF", result.get("relationship"));

        // 验证关系已更新（原关系不存在，新关系存在）
        assertFalse(graphStore.relationshipExists("Alice", "Bob", "KNOWS"));
        assertTrue(graphStore.relationshipExists("Alice", "Bob", "FRIEND_OF"));
    }

    @Test
    @DisplayName("Should delete graph memory successfully")
    void testDeleteMemory() {
        // 先添加关系
        graphStore.addMemory("Alice", "Bob", "KNOWS", "PERSON", "PERSON");
        assertTrue(graphStore.relationshipExists("Alice", "Bob", "KNOWS"));

        // 删除关系
        Map<String, Object> result = graphStore.deleteMemory("Alice", "Bob", "KNOWS");

        assertEquals("delete_graph_memory", result.get("action"));
        assertEquals("success", result.get("status"));

        // 验证关系已删除
        assertFalse(graphStore.relationshipExists("Alice", "Bob", "KNOWS"));
    }

    @Test
    @DisplayName("Should search relationships successfully")
    void testSearchRelationships() {
        // 添加测试数据
//        addTestRelationships();

        // 搜索包含 "Alice" 的关系
        List<Map<String, Object>> results = graphStore.search("Alice", 10);

        assertFalse(results.isEmpty());

        // 验证搜索结果包含预期的关系
        boolean foundAliceBob = results.stream().anyMatch(r ->
            "Alice".equals(r.get("source")) && "Bob".equals(r.get("destination"))
        );
        assertTrue(foundAliceBob);
    }

    @Test
    @DisplayName("Should get all relationships with limit")
    void testGetAllRelationships() {
        addTestRelationships();

        List<Map<String, Object>> results = graphStore.getAll(5);

        assertFalse(results.isEmpty());
        assertTrue(results.size() <= 5);

        // 验证结果包含必要字段
        for (Map<String, Object> result : results) {
            assertNotNull(result.get("source"));
            assertNotNull(result.get("destination"));
            assertNotNull(result.get("relationship"));
        }
    }

    @Test
    @DisplayName("Should extract entities from text")
    void testExtractEntities() {
        String text = "Alice works at Google and knows Bob from Microsoft";
        List<Map<String, Object>> entities = graphStore.extractEntities(text);

        assertFalse(entities.isEmpty());

        // 验证提取的实体
        Set<String> entityNames = new HashSet<>();
        for (Map<String, Object> entity : entities) {
            entityNames.add((String) entity.get("name"));
            assertEquals("GENERAL", entity.get("type"));
        }

        assertTrue(entityNames.contains("Alice"));
        assertTrue(entityNames.contains("Google"));
        assertTrue(entityNames.contains("Bob"));
        assertTrue(entityNames.contains("Microsoft"));
    }

    @Test
    @DisplayName("Should establish relations from text")
    void testEstablishRelations() {
        String text = "Alice is a developer and Bob is a manager";
        List<GraphEntity> relations = graphStore.establishRelations(text);

        assertFalse(relations.isEmpty());

        // 验证建立的关系
        GraphEntity relation = relations.get(0);
        assertEquals("Alice", relation.getSource());
        assertEquals("IS", relation.getRelationship());
        assertEquals("GENERAL", relation.getSourceType());
        assertEquals("GENERAL", relation.getDestinationType());
    }

    @Test
    @DisplayName("Should get node relationships")
    void testGetNodeRelationships() {
        // 添加多个关系
        graphStore.addMemory("Alice", "Bob", "KNOWS", "PERSON", "PERSON");
        graphStore.addMemory("Alice", "Charlie", "WORKS_WITH", "PERSON", "PERSON");
        graphStore.addMemory("David", "Alice", "MANAGES", "PERSON", "PERSON");

        List<Map<String, Object>> relationships = graphStore.getNodeRelationships("Alice");

        assertFalse(relationships.isEmpty());
        assertEquals(3, relationships.size());

        // 验证关系方向
        Map<String, Integer> directionCount = new HashMap<>();
        for (Map<String, Object> rel : relationships) {
            String direction = (String) rel.get("direction");
            directionCount.merge(direction, 1, Integer::sum);
        }

        assertTrue(directionCount.containsKey("outgoing"));
        assertTrue(directionCount.containsKey("incoming"));
    }

    @Test
    @DisplayName("Should handle batch memory addition")
    void testBatchAddMemories() {
        List<GraphEntity> entities = Arrays.asList(
            new GraphEntity("Alice", "Bob", "KNOWS", "PERSON", "PERSON"),
            new GraphEntity("Bob", "Charlie", "WORKS_WITH", "PERSON", "PERSON"),
            new GraphEntity("Charlie", "Alice", "REPORTS_TO", "PERSON", "PERSON")
        );

        List<Map<String, Object>> results = graphStore.addMemories(entities);

        assertEquals(3, results.size());
        for (Map<String, Object> result : results) {
            assertEquals("success", result.get("status"));
        }

        // 验证所有关系都已添加
        assertTrue(graphStore.relationshipExists("Alice", "Bob", "KNOWS"));
        assertTrue(graphStore.relationshipExists("Bob", "Charlie", "WORKS_WITH"));
        assertTrue(graphStore.relationshipExists("Charlie", "Alice", "REPORTS_TO"));
    }

    @Test
    @DisplayName("Should delete all data successfully")
    void testDeleteAll() {
        addTestRelationships();

        // 验证数据存在
        Map<String, Object> statsBefore = graphStore.getStats();
        assertTrue((Long) statsBefore.get("node_count") > 0);
        assertTrue((Long) statsBefore.get("relationship_count") > 0);

        // 删除所有数据
        graphStore.deleteAll();

        // 验证数据已清空
        Map<String, Object> statsAfter = graphStore.getStats();
        assertEquals(0L, statsAfter.get("node_count"));
        assertEquals(0L, statsAfter.get("relationship_count"));
    }

    @Test
    @DisplayName("Should provide accurate statistics")
    void testGetStats() {
        addTestRelationships();

        Map<String, Object> stats = graphStore.getStats();

        assertTrue((Long) stats.get("node_count") >= 3); // At least Alice, Bob, Charlie
        assertTrue((Long) stats.get("relationship_count") >= 2); // At least 2 relationships
        assertEquals("kuzu", stats.get("provider"));
        assertTrue((Boolean) stats.get("embedded_mode"));
        assertTrue((Boolean) stats.get("enabled"));
        assertNotNull(stats.get("database_path"));
    }

    @Test
    @DisplayName("Should handle errors gracefully")
    void testErrorHandling() {
        // 尝试删除不存在的关系
        Map<String, Object> result = graphStore.deleteMemory("NonExistent", "Also", "NOTHING");

        // 应该返回成功状态（即使关系不存在）
        assertEquals("delete_graph_memory", result.get("action"));
        assertEquals("success", result.get("status"));
    }

    @Test
    @DisplayName("Should handle complex relationship patterns")
    void testComplexRelationships() {
        // 创建复杂的关系网络
        graphStore.addMemory("Company", "Alice", "EMPLOYS", "ORGANIZATION", "PERSON");
        graphStore.addMemory("Company", "Bob", "EMPLOYS", "ORGANIZATION", "PERSON");
        graphStore.addMemory("Alice", "Project1", "WORKS_ON", "PERSON", "PROJECT");
        graphStore.addMemory("Bob", "Project1", "WORKS_ON", "PERSON", "PROJECT");
        graphStore.addMemory("Alice", "Bob", "COLLABORATES_WITH", "PERSON", "PERSON");

        // 验证网络结构
        List<Map<String, Object>> aliceRels = graphStore.getNodeRelationships("Alice");
        assertTrue(aliceRels.size() >= 3);

        List<Map<String, Object>> companyRels = graphStore.getNodeRelationships("Company");
        assertTrue(companyRels.size() >= 2);

        List<Map<String, Object>> project1Rels = graphStore.getNodeRelationships("Project1");
        assertTrue(project1Rels.size() >= 2);
    }

    private void addTestRelationships() {
        graphStore.addMemory("Alice", "Bob", "KNOWS", "PERSON", "PERSON");
        graphStore.addMemory("Bob", "Charlie", "WORKS_WITH", "PERSON", "PERSON");
        graphStore.addMemory("Alice", "Charlie", "MANAGES", "PERSON", "PERSON");
    }
}