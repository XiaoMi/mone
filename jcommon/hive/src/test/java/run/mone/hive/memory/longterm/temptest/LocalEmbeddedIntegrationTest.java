package run.mone.hive.memory.longterm.temptest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import run.mone.hive.memory.longterm.config.*;
import run.mone.hive.memory.longterm.core.Memory;
import run.mone.hive.memory.longterm.vectorstore.VectorStoreFactory;
import run.mone.hive.memory.longterm.graph.GraphStoreFactory;
import run.mone.hive.memory.longterm.vectorstore.VectorStoreBase;
import run.mone.hive.memory.longterm.graph.GraphStoreBase;
import run.mone.hive.memory.longterm.model.MemoryItem;

import java.util.*;
import java.io.File;

@DisplayName("Local Embedded Memory Integration Tests")
public class LocalEmbeddedIntegrationTest {

    private Memory memory;
    private VectorStoreBase vectorStore;
    private GraphStoreBase graphStore;
    private String testBaseDir = "./data/test/integration";

    @BeforeEach
    void setUp() {
        // 清理测试目录
        cleanupTestDirectory();

        // 配置本地嵌入式向量存储
        VectorStoreConfig vectorConfig = VectorStoreConfig.builder()
                .provider(VectorStoreConfig.Provider.CHROMA)
                .collectionName("integration_test")
                .host("localhost")
                .path(testBaseDir + "/chroma")
                .embeddingModelDims(384)
                .build();

        // 配置本地嵌入式图存储
        GraphStoreConfig graphConfig = GraphStoreConfig.builder()
                .provider(GraphStoreConfig.Provider.KUZU)
                .url(testBaseDir + "/kuzu")
                .enabled(true)
                .build();

        // 配置LLM
        LlmConfig llmConfig = LlmConfig.builder()
                .provider(LlmConfig.Provider.OPENAI)
                .model("gpt-3.5-turbo")
                .build();

        // 配置嵌入器
        EmbedderConfig embedderConfig = EmbedderConfig.builder()
                .provider(EmbedderConfig.Provider.OPENAI)
                .model("text-embedding-ada-002")
                .build();

        // 创建内存配置
        MemoryConfig memoryConfig = MemoryConfig.builder()
                .vectorStore(vectorConfig)
                .graphStore(graphConfig)
                .llm(llmConfig)
                .embedder(embedderConfig)
                .build();

        // 单独创建存储实例用于测试
        vectorStore = VectorStoreFactory.create(vectorConfig);
        graphStore = GraphStoreFactory.create(graphConfig);

        // 创建内存实例
        memory = new Memory(memoryConfig);
    }

    @AfterEach
    void tearDown() {
        if (vectorStore != null) {
            vectorStore.close();
        }
        if (graphStore != null) {
            graphStore.close();
        }
        if (memory != null) {
            memory.close();
        }
        cleanupTestDirectory();
    }

    private void cleanupTestDirectory() {
        File testDir = new File(testBaseDir);
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
    @DisplayName("Should initialize both stores successfully")
    void testInitialization() {
        assertNotNull(vectorStore);
        assertNotNull(graphStore);
        assertNotNull(memory);

        assertTrue(vectorStore.validateConnection());
        assertTrue(graphStore.validateConnection());

        // 验证向量存储统计
        Map<String, Object> vectorStats = vectorStore.getStats();
        assertEquals("chroma", vectorStats.get("provider"));
        assertTrue((Boolean) vectorStats.get("embedded_mode"));

        // 验证图存储统计
        Map<String, Object> graphStats = graphStore.getStats();
        assertEquals("kuzu", graphStats.get("provider"));
        assertTrue((Boolean) graphStats.get("embedded_mode"));
    }

    @Test
    @DisplayName("Should work together for vector and graph operations")
    void testVectorAndGraphIntegration() {
        // 1. 添加向量数据
        List<List<Double>> vectors = Arrays.asList(
            Arrays.asList(0.1, 0.2, 0.3, 0.4),
            Arrays.asList(0.5, 0.6, 0.7, 0.8),
            Arrays.asList(0.2, 0.3, 0.4, 0.5)
        );

        List<String> ids = Arrays.asList("memory1", "memory2", "memory3");

        List<Map<String, Object>> payloads = Arrays.asList(
            createMemoryPayload("Alice loves programming", "user1"),
            createMemoryPayload("Bob works with Alice on projects", "user1"),
            createMemoryPayload("Charlie manages the team", "user1")
        );

        vectorStore.insert(vectors, ids, payloads);

        // 2. 添加图关系
        graphStore.addMemory("Alice", "Programming", "LOVES", "PERSON", "ACTIVITY");
        graphStore.addMemory("Bob", "Alice", "WORKS_WITH", "PERSON", "PERSON");
        graphStore.addMemory("Alice", "Bob", "COLLABORATES", "PERSON", "PERSON");
        graphStore.addMemory("Charlie", "Team", "MANAGES", "PERSON", "GROUP");

        // 3. 验证向量存储
        assertEquals(3L, vectorStore.getVectorCount());
        List<MemoryItem> vectorResults = vectorStore.search("Alice",
            Arrays.asList(0.15, 0.25, 0.35, 0.45), 5, null);
        assertFalse(vectorResults.isEmpty());

        // 4. 验证图存储
        assertTrue(graphStore.relationshipExists("Alice", "Programming", "LOVES"));
        assertTrue(graphStore.relationshipExists("Bob", "Alice", "WORKS_WITH"));

        List<Map<String, Object>> aliceRelations = graphStore.getNodeRelationships("Alice");
        assertTrue(aliceRelations.size() >= 2);

        // 5. 验证组合查询能力
        List<Map<String, Object>> graphSearchResults = graphStore.search("Alice", 10);
        assertFalse(graphSearchResults.isEmpty());

        Map<String, Object> vectorFilters = Map.of("user_id", "user1");
        List<MemoryItem> filteredVectorResults = vectorStore.search("Bob",
            Arrays.asList(0.5, 0.6, 0.7, 0.8), 5, vectorFilters);
        assertFalse(filteredVectorResults.isEmpty());
    }

    @Test
    @DisplayName("Should handle complex knowledge graph scenarios")
    void testComplexKnowledgeGraph() {
        // 创建复杂的知识图谱
        // 公司结构
        graphStore.addMemory("TechCorp", "Alice", "EMPLOYS", "COMPANY", "PERSON");
        graphStore.addMemory("TechCorp", "Bob", "EMPLOYS", "COMPANY", "PERSON");
        graphStore.addMemory("TechCorp", "Charlie", "EMPLOYS", "COMPANY", "PERSON");

        // 团队关系
        graphStore.addMemory("Alice", "DevTeam", "LEADS", "PERSON", "TEAM");
        graphStore.addMemory("Bob", "DevTeam", "MEMBER_OF", "PERSON", "TEAM");
        graphStore.addMemory("Charlie", "QATeam", "LEADS", "PERSON", "TEAM");

        // 项目关系
        graphStore.addMemory("DevTeam", "ProjectX", "WORKS_ON", "TEAM", "PROJECT");
        graphStore.addMemory("QATeam", "ProjectX", "TESTS", "TEAM", "PROJECT");

        // 技能关系
        graphStore.addMemory("Alice", "Java", "SKILLED_IN", "PERSON", "SKILL");
        graphStore.addMemory("Alice", "Python", "SKILLED_IN", "PERSON", "SKILL");
        graphStore.addMemory("Bob", "JavaScript", "SKILLED_IN", "PERSON", "SKILL");

        // 验证复杂查询
        List<Map<String, Object>> aliceConnections = graphStore.getNodeRelationships("Alice");
        assertTrue(aliceConnections.size() >= 4); // EMPLOYS + LEADS + 2 SKILLED_IN

        List<Map<String, Object>> projectXRelated = graphStore.search("ProjectX", 10);
        assertTrue(projectXRelated.size() >= 2); // WORKS_ON + TESTS

        List<Map<String, Object>> techCorpEmployees = graphStore.search("TechCorp", 10);
        assertTrue(techCorpEmployees.size() >= 3); // 3 EMPLOYS relationships
    }

    @Test
    @DisplayName("Should support real-world memory scenarios")
    void testRealWorldMemoryScenarios() {
        // 场景1: 用户对话记忆
        String[] conversations = {
            "I love playing guitar in my free time",
            "I work as a software engineer at Google",
            "My favorite programming language is Python",
            "I have a cat named Whiskers",
            "I'm planning to visit Japan next year"
        };

        // 为每个对话创建向量和图关系
        for (int i = 0; i < conversations.length; i++) {
            String conversation = conversations[i];
            String memoryId = "conv_" + i;

            // 添加向量记忆
            List<Double> fakeEmbedding = generateFakeEmbedding(conversation);
            vectorStore.insert(
                Arrays.asList(fakeEmbedding),
                Arrays.asList(memoryId),
                Arrays.asList(createMemoryPayload(conversation, "user123"))
            );

            // 提取并添加图关系
            addConversationRelations(conversation);
        }

        // 验证记忆存储
        assertEquals(5L, vectorStore.getVectorCount());

        // 搜索相关记忆
        List<MemoryItem> hobbyMemories = vectorStore.search("hobby",
            generateFakeEmbedding("hobby"), 3, null);
        assertFalse(hobbyMemories.isEmpty());

        List<MemoryItem> workMemories = vectorStore.search("work",
            generateFakeEmbedding("work"), 3, null);
        assertFalse(workMemories.isEmpty());

        // 验证图关系
        assertTrue(graphStore.relationshipExists("User", "Guitar", "PLAYS"));
        assertTrue(graphStore.relationshipExists("User", "Google", "WORKS_AT"));
        assertTrue(graphStore.relationshipExists("User", "Python", "PREFERS"));

        List<Map<String, Object>> userRelations = graphStore.getNodeRelationships("User");
        assertTrue(userRelations.size() >= 5);
    }

    @Test
    @DisplayName("Should maintain data persistence across restarts")
    void testDataPersistence() {
        // 添加初始数据
        vectorStore.insert(
            Arrays.asList(Arrays.asList(0.1, 0.2, 0.3, 0.4)),
            Arrays.asList("persistent_memory"),
            Arrays.asList(createMemoryPayload("This should persist", "user1"))
        );

        graphStore.addMemory("DataA", "DataB", "CONNECTS_TO", "DATA", "DATA");

        // 验证数据存在
        MemoryItem item = vectorStore.get("persistent_memory");
        assertNotNull(item);
        assertTrue(graphStore.relationshipExists("DataA", "DataB", "CONNECTS_TO"));

        // 模拟重启 - 关闭连接
        vectorStore.close();
        graphStore.close();

        // 重新初始化
        VectorStoreConfig vectorConfig = VectorStoreConfig.builder()
                .provider(VectorStoreConfig.Provider.CHROMA)
                .collectionName("integration_test")
                .host("localhost")
                .path(testBaseDir + "/chroma")
                .embeddingModelDims(384)
                .build();

        GraphStoreConfig graphConfig = GraphStoreConfig.builder()
                .provider(GraphStoreConfig.Provider.KUZU)
                .url(testBaseDir + "/kuzu")
                .enabled(true)
                .build();

        vectorStore = VectorStoreFactory.create(vectorConfig);
        graphStore = GraphStoreFactory.create(graphConfig);

        // 验证数据仍然存在
        MemoryItem persistedItem = vectorStore.get("persistent_memory");
        assertNotNull(persistedItem);
        assertEquals("This should persist", persistedItem.getMemory());

        assertTrue(graphStore.relationshipExists("DataA", "DataB", "CONNECTS_TO"));
    }

    @Test
    @DisplayName("Should handle concurrent operations safely")
    void testConcurrentOperations() {
        // 这是一个简化的并发测试
        // 在真实环境中，你可能需要更复杂的并发测试

        // 同时执行多个操作
        List<Thread> threads = new ArrayList<>();

        // 向量插入线程
        for (int i = 0; i < 3; i++) {
            final int threadId = i;
            threads.add(new Thread(() -> {
                for (int j = 0; j < 5; j++) {
                    String id = "thread" + threadId + "_mem" + j;
                    vectorStore.insert(
                        Arrays.asList(generateFakeEmbedding("test " + threadId + " " + j)),
                        Arrays.asList(id),
                        Arrays.asList(createMemoryPayload("Memory from thread " + threadId, "user" + threadId))
                    );
                }
            }));
        }

        // 图关系添加线程
        for (int i = 0; i < 2; i++) {
            final int threadId = i;
            threads.add(new Thread(() -> {
                for (int j = 0; j < 3; j++) {
                    graphStore.addMemory(
                        "Entity" + threadId + "_" + j,
                        "Target" + threadId + "_" + j,
                        "RELATES_TO",
                        "TYPE",
                        "TYPE"
                    );
                }
            }));
        }

        // 启动所有线程
        threads.forEach(Thread::start);

        // 等待所有线程完成
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // 验证所有数据都已正确插入
        assertTrue(vectorStore.getVectorCount() >= 15); // 3 threads * 5 memories

        Map<String, Object> graphStats = graphStore.getStats();
        assertTrue((Long) graphStats.get("relationship_count") >= 6); // 2 threads * 3 relationships
    }

    // 辅助方法
    private Map<String, Object> createMemoryPayload(String memory, String userId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("memory", memory);
        payload.put("user_id", userId);
        payload.put("agent_id", "test_agent");
        payload.put("run_id", "integration_test_run");
        payload.put("role", "user");
        return payload;
    }

    private List<Double> generateFakeEmbedding(String text) {
        // 生成基于文本的伪嵌入向量
        Random random = new Random(text.hashCode());
        List<Double> embedding = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            embedding.add(random.nextDouble());
        }
        return embedding;
    }

    private void addConversationRelations(String conversation) {
        String lowerConv = conversation.toLowerCase();

        if (lowerConv.contains("guitar")) {
            graphStore.addMemory("User", "Guitar", "PLAYS", "PERSON", "INSTRUMENT");
        }
        if (lowerConv.contains("google")) {
            graphStore.addMemory("User", "Google", "WORKS_AT", "PERSON", "COMPANY");
        }
        if (lowerConv.contains("python")) {
            graphStore.addMemory("User", "Python", "PREFERS", "PERSON", "LANGUAGE");
        }
        if (lowerConv.contains("cat")) {
            graphStore.addMemory("User", "Whiskers", "OWNS", "PERSON", "PET");
        }
        if (lowerConv.contains("japan")) {
            graphStore.addMemory("User", "Japan", "PLANS_TO_VISIT", "PERSON", "COUNTRY");
        }
    }
}