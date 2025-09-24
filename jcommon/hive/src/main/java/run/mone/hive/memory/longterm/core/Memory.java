package run.mone.hive.memory.longterm.core;

import lombok.extern.slf4j.Slf4j;
import lombok.Data;

import run.mone.hive.memory.longterm.config.MemoryConfig;
import run.mone.hive.memory.longterm.model.MemoryItem;
import run.mone.hive.memory.longterm.model.Message;
import run.mone.hive.memory.longterm.llm.LLMFactory;
import run.mone.hive.memory.longterm.llm.LLMBase;
import run.mone.hive.memory.longterm.embeddings.EmbeddingFactory;
import run.mone.hive.memory.longterm.embeddings.EmbeddingBase;
import run.mone.hive.memory.longterm.vectorstore.VectorStoreFactory;
import run.mone.hive.memory.longterm.vectorstore.VectorStoreBase;
import run.mone.hive.memory.longterm.storage.HistoryManager;
import run.mone.hive.memory.longterm.utils.MessageParser;
import run.mone.hive.memory.longterm.utils.MemoryUtils;
import run.mone.hive.memory.longterm.graph.GraphStoreBase;
import run.mone.hive.memory.longterm.graph.GraphStoreFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Executors;
import java.time.LocalDateTime;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

/**
 * Memory核心实现类
 * 复刻mem0的Memory功能
 */
@Slf4j
@Data
public class Memory implements MemoryBase {

    private MemoryConfig config;
    private LLMBase llm;
    private EmbeddingBase embeddingModel;
    private VectorStoreBase vectorStore;
    private GraphStoreBase graphStore;
    private HistoryManager historyManager;
    private boolean enableGraph;
    private String collectionName;
    private String apiVersion;
    private ThreadPoolExecutor executor;

    /**
     * 构造函数
     */
    public Memory() {
        this(MemoryConfig.getDefault());
    }

    /**
     * 带配置的构造函数
     */
    public Memory(MemoryConfig config) {
        this.config = config;
        this.apiVersion = config.getVersion();
        this.collectionName = config.getVectorStore().getCollectionName();
        this.enableGraph = config.getGraphStore().isEnabled();

        // 初始化组件
        this.llm = LLMFactory.create(config.getLlm());
        this.embeddingModel = EmbeddingFactory.create(config.getEmbedder());
        this.vectorStore = VectorStoreFactory.create(config.getVectorStore());
        this.graphStore = GraphStoreFactory.create(config.getGraphStore());
        this.historyManager = new HistoryManager(config.getHistoryDbPath());

        // 初始化线程池
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        log.info("Memory initialized with provider: {}, vector store: {}, embedder: {}",
                config.getLlm().getProvider(),
                config.getVectorStore().getProvider(),
                config.getEmbedder().getProvider());
    }

    /**
     * 从配置Map创建Memory实例
     */
    public static Memory fromConfig(Map<String, Object> configMap) {
        MemoryConfig config = MemoryConfig.fromMap(configMap);
        return new Memory(config);
    }

    @Override
    public Map<String, Object> add(Object messages, String userId, String agentId, String runId,
                                   Map<String, Object> metadata, boolean infer, String memoryType, String prompt) {

        // 构建过滤条件和元数据
        Map<String, Object> processedMetadata = buildMetadata(userId, agentId, runId, metadata);
        Map<String, Object> effectiveFilters = buildFilters(userId, agentId, runId, null);

        // 验证至少有一个ID
        validateSessionIds(userId, agentId, runId);

        // 解析消息
        List<Message> messageList = MessageParser.parseMessages(messages);

        // 如果是过程记忆且有代理ID
        if ("procedural_memory".equals(memoryType) && agentId != null) {
            return createProceduralMemory(messageList, processedMetadata, prompt);
        }

        // 并行处理向量存储和图存储
        CompletableFuture<List<MemoryItem>> vectorFuture = CompletableFuture.supplyAsync(
                () -> addToVectorStore(messageList, processedMetadata, effectiveFilters, infer), executor);

        CompletableFuture<List<String>> graphFuture = CompletableFuture.supplyAsync(
                () -> addToGraph(messageList, effectiveFilters), executor);

        try {
            List<MemoryItem> vectorResult = vectorFuture.get();
            List<String> graphResult = graphFuture.get();

            Map<String, Object> result = new HashMap<>();
            result.put("results", vectorResult);

            if (enableGraph && !graphResult.isEmpty()) {
                result.put("relations", graphResult);
            }

            return result;

        } catch (Exception e) {
            log.error("Error adding memory", e);
            throw new RuntimeException("Failed to add memory", e);
        }
    }

    @Override
    public CompletableFuture<Map<String, Object>> addAsync(Object messages, String userId, String agentId, String runId,
                                                           Map<String, Object> metadata, boolean infer, String memoryType, String prompt) {
        return CompletableFuture.supplyAsync(() ->
                add(messages, userId, agentId, runId, metadata, infer, memoryType, prompt), executor);
    }

    @Override
    public MemoryItem get(String memoryId) {
        try {
            return vectorStore.get(memoryId);
        } catch (Exception e) {
            log.error("Error getting memory with ID: {}", memoryId, e);
            return null;
        }
    }

    @Override
    public CompletableFuture<MemoryItem> getAsync(String memoryId) {
        return CompletableFuture.supplyAsync(() -> get(memoryId), executor);
    }

    @Override
    public Map<String, Object> getAll(String userId, String agentId, String runId,
                                      Map<String, Object> filters, int limit) {
        Map<String, Object> effectiveFilters = buildFilters(userId, agentId, runId, filters);
        validateSessionIds(userId, agentId, runId);
        try {
            List<MemoryItem> memories = vectorStore.list(effectiveFilters, limit);
            Map<String, Object> result = new HashMap<>();
            result.put("results", memories);
            return result;
        } catch (Exception e) {
            log.error("Error getting all memories", e);
            throw new RuntimeException("Failed to get all memories", e);
        }
    }

    @Override
    public CompletableFuture<Map<String, Object>> getAllAsync(String userId, String agentId, String runId,
                                                              Map<String, Object> filters, int limit) {
        return CompletableFuture.supplyAsync(() ->
                getAll(userId, agentId, runId, filters, limit), executor);
    }

    @Override
    public Map<String, Object> search(String query, String userId, String agentId, String runId,
                                      int limit, Map<String, Object> filters, Double threshold) {
        Map<String, Object> effectiveFilters = buildFilters(userId, agentId, runId, filters);
        validateSessionIds(userId, agentId, runId);

        try {
            // 生成查询向量
            List<Double> queryEmbedding = embeddingModel.embed(query, "search");

            // 搜索相似向量
            List<MemoryItem> memories = vectorStore.search(query, queryEmbedding, limit, effectiveFilters);

            // 应用阈值过滤
            if (threshold != null) {
                memories = memories.stream()
                        .filter(m -> m.getScore() == null || m.getScore() >= threshold)
                        .collect(java.util.stream.Collectors.toList());
            }

            Map<String, Object> result = new HashMap<>();
            result.put("results", memories);

            return result;

        } catch (Exception e) {
            log.error("Error searching memories", e);
            throw new RuntimeException("Failed to search memories", e);
        }
    }

    @Override
    public CompletableFuture<Map<String, Object>> searchAsync(String query, String userId, String agentId, String runId,
                                                              int limit, Map<String, Object> filters, Double threshold) {
        return CompletableFuture.supplyAsync(() ->
                search(query, userId, agentId, runId, limit, filters, threshold), executor);
    }

    @Override
    public Map<String, Object> update(String memoryId, String data) {
        try {
            List<Double> embeddings = embeddingModel.embed(data, "update");
            updateMemory(memoryId, data, embeddings);

            Map<String, Object> result = new HashMap<>();
            result.put("message", "Memory updated successfully!");
            return result;

        } catch (Exception e) {
            log.error("Error updating memory with ID: {}", memoryId, e);
            throw new RuntimeException("Failed to update memory", e);
        }
    }

    @Override
    public CompletableFuture<Map<String, Object>> updateAsync(String memoryId, String data) {
        return CompletableFuture.supplyAsync(() -> update(memoryId, data), executor);
    }

    @Override
    public Map<String, Object> delete(String memoryId) {
        try {
            deleteMemory(memoryId);

            Map<String, Object> result = new HashMap<>();
            result.put("message", "Memory deleted successfully!");
            return result;

        } catch (Exception e) {
            log.error("Error deleting memory with ID: {}", memoryId, e);
            throw new RuntimeException("Failed to delete memory", e);
        }
    }

    @Override
    public CompletableFuture<Map<String, Object>> deleteAsync(String memoryId) {
        return CompletableFuture.supplyAsync(() -> delete(memoryId), executor);
    }

    @Override
    public Map<String, Object> deleteAll(String userId, String agentId, String runId) {
        Map<String, Object> filters = buildFilters(userId, agentId, runId, null);

        if (filters.isEmpty()) {
            throw new IllegalArgumentException("At least one filter is required to delete all memories");
        }

        try {
            List<MemoryItem> memories = vectorStore.list(filters, Integer.MAX_VALUE);

            for (MemoryItem memory : memories) {
                deleteMemory(memory.getId());
            }

            log.info("Deleted {} memories", memories.size());

            Map<String, Object> result = new HashMap<>();
            result.put("message", "Memories deleted successfully!");
            return result;

        } catch (Exception e) {
            log.error("Error deleting all memories", e);
            throw new RuntimeException("Failed to delete all memories", e);
        }
    }

    @Override
    public CompletableFuture<Map<String, Object>> deleteAllAsync(String userId, String agentId, String runId) {
        return CompletableFuture.supplyAsync(() -> deleteAll(userId, agentId, runId), executor);
    }

    @Override
    public List<Map<String, Object>> history(String memoryId) {
        try {
            return historyManager.getHistory(memoryId);
        } catch (Exception e) {
            log.error("Error getting history for memory ID: {}", memoryId, e);
            throw new RuntimeException("Failed to get memory history", e);
        }
    }

    @Override
    public CompletableFuture<List<Map<String, Object>>> historyAsync(String memoryId) {
        return CompletableFuture.supplyAsync(() -> history(memoryId), executor);
    }

    @Override
    public Map<String, Object> reset() {
        try {
            vectorStore.reset();
            historyManager.reset();

            log.warn("All memories have been reset");

            Map<String, Object> result = new HashMap<>();
            result.put("message", "Memory store reset successfully!");
            return result;

        } catch (Exception e) {
            log.error("Error resetting memory store", e);
            throw new RuntimeException("Failed to reset memory store", e);
        }
    }

    @Override
    public CompletableFuture<Map<String, Object>> resetAsync() {
        return CompletableFuture.supplyAsync(() -> reset(), executor);
    }

    // ========== 私有辅助方法 ==========

    private Map<String, Object> buildMetadata(String userId, String agentId, String runId, Map<String, Object> inputMetadata) {
        Map<String, Object> metadata = inputMetadata != null ? new HashMap<>(inputMetadata) : new HashMap<>();

        if (userId != null) metadata.put("user_id", userId);
        if (agentId != null) metadata.put("agent_id", agentId);
        if (runId != null) metadata.put("run_id", runId);

        return metadata;
    }

    private Map<String, Object> buildFilters(String userId, String agentId, String runId, Map<String, Object> inputFilters) {
        Map<String, Object> filters = inputFilters != null ? new HashMap<>(inputFilters) : new HashMap<>();

        if (userId != null) filters.put("user_id", userId);
        if (agentId != null) filters.put("agent_id", agentId);
        if (runId != null) filters.put("run_id", runId);

        return filters;
    }

    private void validateSessionIds(String userId, String agentId, String runId) {
        if (userId == null && agentId == null && runId == null) {
            throw new IllegalArgumentException("At least one of 'userId', 'agentId', or 'runId' must be provided");
        }
    }

    private List<MemoryItem> addToVectorStore(List<Message> messages, Map<String, Object> metadata,
                                              Map<String, Object> filters, boolean infer) {
        if (!infer) {
            return addDirectly(messages, metadata);
        }

        return addWithInference(messages, metadata, filters);
    }

    private List<MemoryItem> addDirectly(List<Message> messages, Map<String, Object> metadata) {
        List<MemoryItem> returnedMemories = new ArrayList<>();

        for (Message message : messages) {
            if (message.isSystemMessage()) {
                continue;
            }

            Map<String, Object> msgMetadata = new HashMap<>(metadata);
            msgMetadata.put("role", message.getRole().getValue());

            if (message.getName() != null) {
                msgMetadata.put("actor_id", message.getName());
            }

            String memoryId = createMemory(message.getContent(), null, msgMetadata);

            MemoryItem memoryItem = MemoryItem.builder()
                    .id(memoryId)
                    .memory(message.getContent())
                    .event("ADD")
                    .actorId(message.getName())
                    .role(message.getRole().getValue())
                    .build();

            returnedMemories.add(memoryItem);
        }

        return returnedMemories;
    }

    private List<MemoryItem> addWithInference(List<Message> messages, Map<String, Object> metadata,
                                              Map<String, Object> filters) {
        // 解析消息
        String parsedMessages = MessageParser.parseMessagesToString(messages);

        // 使用LLM提取事实
        List<String> facts = extractFacts(parsedMessages);

        if (facts.isEmpty()) {
            log.info("No facts extracted from input, skipping memory update");
            return new ArrayList<>();
        }

        // 查找相关的现有记忆
        List<Map<String, Object>> existingMemories = findRelevantMemories(facts, filters);

        // 使用LLM决定如何更新记忆
        List<Map<String, Object>> memoryActions = determineMemoryActions(existingMemories, facts);

        // 执行记忆操作
        return executeMemoryActions(memoryActions, metadata);
    }

    private List<String> extractFacts(String parsedMessages) {
        try {
            String prompt = config.getCustomFactExtractionPrompt() != null
                    ? config.getCustomFactExtractionPrompt() + "\n\nInput:\n" + parsedMessages
                    : getDefaultFactExtractionPrompt(parsedMessages);

            String response = llm.generateResponse(Arrays.asList(
                    Map.of("role", "system", "content", getFactExtractionSystemPrompt()),
                    Map.of("role", "user", "content", prompt)
            ), "json_object");

            return MemoryUtils.parseFactsFromJson(response);

        } catch (Exception e) {
            log.error("Error extracting facts", e);
            return new ArrayList<>();
        }
    }

    private List<Map<String, Object>> findRelevantMemories(List<String> facts, Map<String, Object> filters) {
        List<Map<String, Object>> allMemories = new ArrayList<>();

        for (String fact : facts) {
            try {
                List<Double> embedding = embeddingModel.embed(fact, "add");
                List<MemoryItem> memories = vectorStore.search(fact, embedding, 5, filters);

                for (MemoryItem memory : memories) {
                    Map<String, Object> memoryMap = new HashMap<>();
                    memoryMap.put("id", memory.getId());
                    memoryMap.put("text", memory.getMemory());
                    allMemories.add(memoryMap);
                }
            } catch (Exception e) {
                log.error("Error finding relevant memories for fact: {}", fact, e);
            }
        }

        // 去重
        Map<String, Map<String, Object>> uniqueMemories = new HashMap<>();
        for (Map<String, Object> memory : allMemories) {
            String id = (String) memory.get("id");
            uniqueMemories.put(id, memory);
        }

        return new ArrayList<>(uniqueMemories.values());
    }

    private List<Map<String, Object>> determineMemoryActions(List<Map<String, Object>> existingMemories,
                                                             List<String> newFacts) {
        try {
            String prompt = getUpdateMemoryPrompt(existingMemories, newFacts);
            String response = llm.generateResponse(Arrays.asList(
                    Map.of("role", "user", "content", prompt)
            ), "json_object");

            return MemoryUtils.parseMemoryActionsFromJson(response);

        } catch (Exception e) {
            log.error("Error determining memory actions", e);
            return new ArrayList<>();
        }
    }

    private List<MemoryItem> executeMemoryActions(List<Map<String, Object>> memoryActions,
                                                  Map<String, Object> metadata) {
        List<MemoryItem> returnedMemories = new ArrayList<>();

        for (Map<String, Object> action : memoryActions) {
            try {
                String text = (String) action.get("text");
                String event = (String) action.get("event");

                if (text == null || text.trim().isEmpty()) {
                    continue;
                }

                switch (event) {
                    case "ADD":
                        String memoryId = createMemory(text, null, new HashMap<>(metadata));
                        returnedMemories.add(MemoryItem.builder()
                                .id(memoryId)
                                .memory(text)
                                .event(event)
                                .build());
                        break;

                    case "UPDATE":
                        String updateId = (String) action.get("id");
                        List<Double> embeddings = embeddingModel.embed(text, "update");
                        updateMemory(updateId, text, embeddings);
                        returnedMemories.add(MemoryItem.builder()
                                .id(updateId)
                                .memory(text)
                                .event(event)
                                .build());
                        break;

                    case "DELETE":
                        String deleteId = (String) action.get("id");
                        deleteMemory(deleteId);
                        returnedMemories.add(MemoryItem.builder()
                                .id(deleteId)
                                .memory(text)
                                .event(event)
                                .build());
                        break;

                    default:
                        log.info("No operation for memory action: {}", event);
                        break;
                }
            } catch (Exception e) {
                log.error("Error executing memory action: {}", action, e);
            }
        }

        return returnedMemories;
    }

    private String createMemory(String data, List<Double> existingEmbeddings, Map<String, Object> metadata) {
        log.info("Creating memory with data: {}", data);

        try {
            List<Double> embeddings = existingEmbeddings != null
                    ? existingEmbeddings
                    : embeddingModel.embed(data, "add");

            String memoryId = UUID.randomUUID().toString();
            metadata.put("data", data);
            metadata.put("hash", calculateHash(data));
            metadata.put("created_at", LocalDateTime.now().toString());

            vectorStore.insert(Arrays.asList(embeddings), Arrays.asList(memoryId), Arrays.asList(metadata));

            historyManager.addHistory(memoryId, null, data, "ADD",
                    (String) metadata.get("created_at"),
                    (String) metadata.get("actor_id"),
                    (String) metadata.get("role"));

            return memoryId;

        } catch (Exception e) {
            log.error("Error creating memory", e);
            throw new RuntimeException("Failed to create memory", e);
        }
    }

    private void updateMemory(String memoryId, String data, List<Double> embeddings) {
        try {
            MemoryItem existingMemory = vectorStore.get(memoryId);
            if (existingMemory == null) {
                throw new IllegalArgumentException("Memory with ID " + memoryId + " not found");
            }

            Map<String, Object> newMetadata = new HashMap<>(existingMemory.getMetadata());
            newMetadata.put("data", data);
            newMetadata.put("hash", calculateHash(data));
            newMetadata.put("updated_at", LocalDateTime.now().toString());

            vectorStore.update(memoryId, embeddings, newMetadata);

            historyManager.addHistory(memoryId, existingMemory.getMemory(), data, "UPDATE",
                    (String) newMetadata.get("created_at"),
                    (String) newMetadata.get("updated_at"),
                    (String) newMetadata.get("actor_id"),
                    (String) newMetadata.get("role"));

        } catch (Exception e) {
            log.error("Error updating memory with ID: {}", memoryId, e);
            throw new RuntimeException("Failed to update memory", e);
        }
    }

    private void deleteMemory(String memoryId) {
        try {
            MemoryItem existingMemory = vectorStore.get(memoryId);
            if (existingMemory == null) {
                log.warn("Memory with ID {} not found", memoryId);
                return;
            }

            vectorStore.delete(memoryId);

            historyManager.addHistory(memoryId, existingMemory.getMemory(), null, "DELETE",
                    null, null,
                    (String) existingMemory.getMetadata().get("actor_id"),
                    (String) existingMemory.getMetadata().get("role"));

        } catch (Exception e) {
            log.error("Error deleting memory with ID: {}", memoryId, e);
            throw new RuntimeException("Failed to delete memory", e);
        }
    }

    private Map<String, Object> createProceduralMemory(List<Message> messages, Map<String, Object> metadata, String prompt) {
        try {
            log.info("Creating procedural memory");

            List<Map<String, Object>> parsedMessages = new ArrayList<>();
            parsedMessages.add(Map.of("role", "system", "content",
                    prompt != null ? prompt : getProceduralMemorySystemPrompt()));

            for (Message msg : messages) {
                parsedMessages.add(msg.toMap());
            }

            parsedMessages.add(Map.of("role", "user", "content",
                    "Create procedural memory of the above conversation."));

            String proceduralMemory = llm.generateResponse(parsedMessages, null);

            metadata.put("memory_type", "procedural_memory");
            List<Double> embeddings = embeddingModel.embed(proceduralMemory, "add");
            String memoryId = createMemory(proceduralMemory, embeddings, metadata);

            Map<String, Object> result = new HashMap<>();
            result.put("results", Arrays.asList(MemoryItem.builder()
                    .id(memoryId)
                    .memory(proceduralMemory)
                    .event("ADD")
                    .build()));

            return result;

        } catch (Exception e) {
            log.error("Error creating procedural memory", e);
            throw new RuntimeException("Failed to create procedural memory", e);
        }
    }

    private List<String> addToGraph(List<Message> messages, Map<String, Object> filters) {
        if (!enableGraph || graphStore == null) {
            return new ArrayList<>();
        }

        try {
            // 解析消息为字符串
            String parsedMessages = MessageParser.parseMessagesToString(messages);

            // 设置用户ID过滤器
            if (filters.get("user_id") == null) {
                filters.put("user_id", "user");
            }

            // 从文本中提取实体关系
            List<GraphStoreBase.GraphEntity> entities = graphStore.establishRelations(parsedMessages);

            // 添加关系到图存储
            List<Map<String, Object>> addResults = graphStore.addMemories(entities);
            log.debug("{}", addResults);

            // 格式化返回的关系
            List<String> relations = new ArrayList<>();
            for (GraphStoreBase.GraphEntity entity : entities) {
                if (entity.getSource() != null && entity.getRelationship() != null && entity.getDestination() != null) {
                    relations.add(String.format("%s -- %s -- %s",
                            entity.getSource(), entity.getRelationship(), entity.getDestination()));
                }
            }

            log.info("Added {} relations to graph", relations.size());
            return relations;

        } catch (Exception e) {
            log.error("Error adding to graph store", e);
            return new ArrayList<>();
        }
    }

    private String calculateHash(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            log.error("Error calculating hash", e);
            return "";
        }
    }

    // ========== 提示词模板 ==========

    private String getFactExtractionSystemPrompt() {
        return "You are a memory extraction assistant. Extract key facts and information from conversations.";
    }

    private String getDefaultFactExtractionPrompt(String messages) {
        return String.format("Extract key facts from the following conversation:\n\n%s\n\n" +
                "Return a JSON object with a 'facts' array containing the extracted facts.", messages);
    }

    private String getUpdateMemoryPrompt(List<Map<String, Object>> existingMemories, List<String> newFacts) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Existing memories:\n");

        for (int i = 0; i < existingMemories.size(); i++) {
            Map<String, Object> memory = existingMemories.get(i);
            prompt.append(String.format("%d. %s\n", i, memory.get("text")));
        }

        prompt.append("\nNew facts:\n");
        for (String fact : newFacts) {
            prompt.append("- ").append(fact).append("\n");
        }

        prompt.append("\nReturn a JSON object with a 'memory' array containing objects with 'text', 'event' (ADD/UPDATE/DELETE), and 'id' (for UPDATE/DELETE) fields.");

        return prompt.toString();
    }

    private String getProceduralMemorySystemPrompt() {
        return "You are a procedural memory assistant. Create a concise summary of the conversation that captures the process or procedure discussed.";
    }

    /**
     * 关闭资源
     */
    public void close() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }

        if (historyManager != null) {
            historyManager.close();
        }

        if (vectorStore != null) {
            vectorStore.close();
        }

        if (graphStore != null) {
            graphStore.close();
        }
    }

    // ==================== 图存储相关方法 ====================

    /**
     * 获取图存储统计信息
     */
    public Map<String, Object> getGraphStats() {
        if (!enableGraph || graphStore == null) {
            Map<String, Object> stats = new HashMap<>();
            stats.put("enabled", false);
            stats.put("message", "Graph store is not enabled");
            return stats;
        }

        return graphStore.getStats();
    }

    /**
     * 搜索图记忆
     */
    public List<Map<String, Object>> searchGraph(String query, int limit) {
        if (!enableGraph || graphStore == null) {
            return new ArrayList<>();
        }

        try {
            return graphStore.search(query, limit);
        } catch (Exception e) {
            log.error("Error searching graph memories", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取所有图记忆
     */
    public List<Map<String, Object>> getAllGraphMemories(int limit) {
        if (!enableGraph || graphStore == null) {
            return new ArrayList<>();
        }

        try {
            return graphStore.getAll(limit);
        } catch (Exception e) {
            log.error("Error getting all graph memories", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取节点的所有关系
     */
    public List<Map<String, Object>> getNodeRelationships(String nodeName) {
        if (!enableGraph || graphStore == null) {
            return new ArrayList<>();
        }

        try {
            return graphStore.getNodeRelationships(nodeName);
        } catch (Exception e) {
            log.error("Error getting node relationships", e);
            return new ArrayList<>();
        }
    }

    /**
     * 删除特定的图记忆关系
     */
    public Map<String, Object> deleteGraphMemory(String source, String destination, String relationship) {
        if (!enableGraph || graphStore == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "Graph store is not enabled");
            return result;
        }

        try {
            return graphStore.deleteMemory(source, destination, relationship);
        } catch (Exception e) {
            log.error("Error deleting graph memory", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", e.getMessage());
            return result;
        }
    }

    /**
     * 更新图记忆关系
     */
    public Map<String, Object> updateGraphMemory(String source, String destination, String relationship) {
        if (!enableGraph || graphStore == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "Graph store is not enabled");
            return result;
        }

        try {
            return graphStore.updateMemory(source, destination, relationship);
        } catch (Exception e) {
            log.error("Error updating graph memory", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", e.getMessage());
            return result;
        }
    }

    /**
     * 添加单个图记忆关系
     */
    public Map<String, Object> addGraphMemory(String source, String destination, String relationship,
                                              String sourceType, String destinationType) {
        if (!enableGraph || graphStore == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "Graph store is not enabled");
            return result;
        }

        try {
            return graphStore.addMemory(source, destination, relationship, sourceType, destinationType);
        } catch (Exception e) {
            log.error("Error adding graph memory", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", e.getMessage());
            return result;
        }
    }

    /**
     * 检查关系是否存在
     */
    public boolean relationshipExists(String source, String destination, String relationship) {
        if (!enableGraph || graphStore == null) {
            return false;
        }

        try {
            return graphStore.relationshipExists(source, destination, relationship);
        } catch (Exception e) {
            log.error("Error checking relationship existence", e);
            return false;
        }
    }

    /**
     * 从文本提取实体
     */
    public List<Map<String, Object>> extractEntities(String text) {
        if (!enableGraph || graphStore == null) {
            return new ArrayList<>();
        }

        try {
            return graphStore.extractEntities(text);
        } catch (Exception e) {
            log.error("Error extracting entities", e);
            return new ArrayList<>();
        }
    }

    /**
     * 删除所有图数据
     */
    public void deleteAllGraphData() {
        if (!enableGraph || graphStore == null) {
            log.warn("Graph store is not enabled, cannot delete graph data");
            return;
        }

        try {
            graphStore.deleteAll();
            log.info("All graph data deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting all graph data", e);
            throw new RuntimeException("Failed to delete all graph data", e);
        }
    }

    /**
     * 验证图存储连接
     */
    public boolean validateGraphConnection() {
        if (!enableGraph || graphStore == null) {
            return false;
        }

        return graphStore.validateConnection();
    }

    /**
     * 是否启用图存储
     */
    public boolean isGraphEnabled() {
        return enableGraph && graphStore != null;
    }
}
