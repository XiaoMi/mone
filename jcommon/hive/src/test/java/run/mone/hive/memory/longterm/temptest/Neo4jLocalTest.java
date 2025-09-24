package run.mone.hive.memory.longterm.temptest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import run.mone.hive.memory.longterm.config.EmbedderConfig;
import run.mone.hive.memory.longterm.config.GraphStoreConfig;
import run.mone.hive.memory.longterm.graph.GraphStoreFactory;
import run.mone.hive.memory.longterm.graph.GraphStoreBase;

import java.util.*;

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

        config = GraphStoreConfig.builder()
                .provider(GraphStoreConfig.Provider.NEO4J)
                .url("bolt://localhost:7687")
                .username("neo4j")
                .password(System.getenv("NEO4J_PASSWORD"))
                .enabled(true)
                .embedder(embedderConfig)
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
}
