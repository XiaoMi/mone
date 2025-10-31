package run.mone.hive.memory.longterm.temptest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import run.mone.hive.memory.longterm.config.EmbedderConfig;
import run.mone.hive.memory.longterm.config.VectorStoreConfig;
import run.mone.hive.memory.longterm.embeddings.impl.OllamaEmbedding;
import run.mone.hive.memory.longterm.vectorstore.VectorStoreFactory;
import run.mone.hive.memory.longterm.vectorstore.VectorStoreBase;
import run.mone.hive.memory.longterm.model.MemoryItem;

import java.util.*;
import java.io.File;

@DisplayName("Chroma Local Embedded Vector Store Tests")
public class ChromaLocalTest {

    private VectorStoreBase vectorStore;
    private VectorStoreConfig config;

    @BeforeEach
    void setUp() {
        config = VectorStoreConfig.builder()
                .provider(VectorStoreConfig.Provider.CHROMA)
                .collectionName("test_collectionb")
                .host("localhost")
                .port(8000)
                .embeddingModelDims(768)
                .build();

        vectorStore = VectorStoreFactory.create(config);
    }

    @AfterEach
    void tearDown() {
        if (vectorStore != null) {
            vectorStore.close();
        }
    }



    @Test
    @DisplayName("Should initialize Chroma embedded store successfully")
    void testInitialization() {
        assertNotNull(vectorStore);
        assertTrue(vectorStore.validateConnection());

        Map<String, Object> stats = vectorStore.getStats();
        assertEquals("chroma", stats.get("provider"));
        assertTrue((Boolean) stats.get("embedded_mode"));
        assertEquals(0L, stats.get("vector_count"));
    }

    @Test
    @DisplayName("Should create collection if it doesn't exist")
    void testCollectionCreation() {
        vectorStore.createCollection();
        assertTrue(vectorStore.collectionExists());
    }

    @Test
    @DisplayName("Should insert and retrieve vectors successfully")
    void testInsertAndRetrieve() {
        String first = "First test memory";
        String second = "Second test memory";

        EmbedderConfig embedderConfig = EmbedderConfig.builder()
                .provider(EmbedderConfig.Provider.OLLAMA)
                .model("embeddinggemma")
                .embeddingDims(768)
                .build();

        List<Double> a = new OllamaEmbedding(embedderConfig).embed(first);
        List<Double> b = new OllamaEmbedding(embedderConfig).embed(second);


        // 准备测试数据
        List<List<Double>> vectors = Arrays.asList(
            a,
            b
        );

        List<String> ids = Arrays.asList("test1", "test2");

        List<Map<String, Object>> payloads = Arrays.asList(
            createPayload("First test memory", "user1", "agent1"),
            createPayload("Second test memory", "user1", "agent1")
        );

        // 插入向量
        vectorStore.insert(vectors, ids, payloads);

        // 验证插入
        assertEquals(2L, vectorStore.getVectorCount());

        // 检索特定向量
        MemoryItem item1 = vectorStore.get("test1");
        assertNotNull(item1);
        assertEquals("test1", item1.getId());
        assertEquals("First test memory", item1.getMemory());
        assertEquals("user1", item1.getUserId());
        assertEquals("agent1", item1.getAgentId());

        MemoryItem item2 = vectorStore.get("test2");
        assertNotNull(item2);
        assertEquals("test2", item2.getId());
        assertEquals("Second test memory", item2.getMemory());
    }

    @Test
    @DisplayName("Should perform vector similarity search")
    void testVectorSearch() {
        // 插入测试数据
        insertTestVectors();

        // 执行相似性搜索
        List<Double> queryVector = Arrays.asList(0.15, 0.25, 0.35, 0.45);
        List<MemoryItem> results = vectorStore.search("test", queryVector, 5, null);

        // 验证搜索结果
        assertFalse(results.isEmpty());
        assertTrue(results.size() <= 5);

        // 验证结果按相似度排序
        for (int i = 0; i < results.size() - 1; i++) {
            assertTrue(results.get(i).getScore() >= results.get(i + 1).getScore());
        }
    }

    @Test
    @DisplayName("Should support filtering in search")
    void testSearchWithFilters() {
        // 插入不同用户的测试数据
        insertTestVectorsWithDifferentUsers();

        // 使用过滤器搜索
        Map<String, Object> filters = Map.of("user_id", "user1");
        List<Double> queryVector = Arrays.asList(0.1, 0.2, 0.3, 0.4);
        List<MemoryItem> results = vectorStore.search("test", queryVector, 10, filters);

        // 验证所有结果都属于指定用户
        for (MemoryItem item : results) {
            assertEquals("user1", item.getUserId());
        }
    }

    @Test
    @DisplayName("Should list vectors with filters")
    void testListWithFilters() {
        insertTestVectorsWithDifferentUsers();

        // 列出特定用户的向量
        Map<String, Object> filters = Map.of("user_id", "user2");
        List<MemoryItem> results = vectorStore.list(filters, 10);

        assertFalse(results.isEmpty());
        for (MemoryItem item : results) {
            assertEquals("user2", item.getUserId());
        }
    }

    @Test
    @DisplayName("Should update vectors successfully")
    void testUpdateVector() {
        insertTestVectors();

        // 更新向量
        List<Double> newVector = Arrays.asList(0.9, 0.8, 0.7, 0.6);
        Map<String, Object> newPayload = createPayload("Updated memory", "user1", "agent1");

        vectorStore.update("test1", newVector, newPayload);

        // 验证更新
        MemoryItem updatedItem = vectorStore.get("test1");
        assertNotNull(updatedItem);
        assertEquals("Updated memory", updatedItem.getMemory());
    }

    @Test
    @DisplayName("Should delete vectors successfully")
    void testDeleteVector() {
        insertTestVectors();

        // 删除向量
        vectorStore.delete("test1");

        // 验证删除
        MemoryItem deletedItem = vectorStore.get("test1");
        assertNull(deletedItem);
        assertEquals(1L, vectorStore.getVectorCount());
    }

    @Test
    @DisplayName("Should reset collection successfully")
    void testResetCollection() {
        insertTestVectors();
        assertEquals(2L, vectorStore.getVectorCount());

        // 重置集合
        vectorStore.reset();

        // 验证重置
        assertEquals(0L, vectorStore.getVectorCount());
        assertTrue(vectorStore.collectionExists());
    }

    @Test
    @DisplayName("Should delete collection successfully")
    void testDeleteCollection() {
        insertTestVectors();
        assertTrue(vectorStore.collectionExists());

        // 删除集合
        vectorStore.deleteCol();

        // 验证删除
        assertFalse(vectorStore.collectionExists());
    }

    @Test
    @DisplayName("Should handle empty searches gracefully")
    void testEmptySearch() {
        // 在空集合中搜索
        List<Double> queryVector = Arrays.asList(0.1, 0.2, 0.3, 0.4);
        List<MemoryItem> results = vectorStore.search("test", queryVector, 5, null);

        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should provide accurate statistics")
    void testGetStats() {
        insertTestVectors();

        Map<String, Object> stats = vectorStore.getStats();

        assertEquals(2L, stats.get("vector_count"));
        assertEquals("test_collection", stats.get("collection_name"));
        assertEquals("chroma", stats.get("provider"));
        assertTrue((Boolean) stats.get("embedded_mode"));
        assertEquals(384, stats.get("dimensions"));
    }

    private void insertTestVectors() {
        List<List<Double>> vectors = Arrays.asList(
            Arrays.asList(0.1, 0.2, 0.3, 0.4),
            Arrays.asList(0.5, 0.6, 0.7, 0.8)
        );

        List<String> ids = Arrays.asList("test1", "test2");

        List<Map<String, Object>> payloads = Arrays.asList(
            createPayload("First test memory", "user1", "agent1"),
            createPayload("Second test memory", "user1", "agent1")
        );

        vectorStore.insert(vectors, ids, payloads);
    }

    private void insertTestVectorsWithDifferentUsers() {
        List<List<Double>> vectors = Arrays.asList(
            Arrays.asList(0.1, 0.2, 0.3, 0.4),
            Arrays.asList(0.5, 0.6, 0.7, 0.8),
            Arrays.asList(0.2, 0.3, 0.4, 0.5),
            Arrays.asList(0.6, 0.7, 0.8, 0.9)
        );

        List<String> ids = Arrays.asList("test1", "test2", "test3", "test4");

        List<Map<String, Object>> payloads = Arrays.asList(
            createPayload("User1 memory 1", "user1", "agent1"),
            createPayload("User1 memory 2", "user1", "agent1"),
            createPayload("User2 memory 1", "user2", "agent1"),
            createPayload("User2 memory 2", "user2", "agent1")
        );

        vectorStore.insert(vectors, ids, payloads);
    }

    private Map<String, Object> createPayload(String memory, String userId, String agentId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("memory", memory);
        payload.put("user_id", userId);
        payload.put("agent_id", agentId);
        payload.put("run_id", "test_run");
        payload.put("role", "user");
        return payload;
    }
}