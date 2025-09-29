package run.mone.hive.memory.longterm.core;

import lombok.extern.slf4j.Slf4j;
import lombok.Data;

import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
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
import run.mone.hive.memory.longterm.utils.MemoryUtils;
import run.mone.hive.memory.longterm.utils.MessageParser;
import run.mone.hive.memory.longterm.graph.GraphStoreBase;
import run.mone.hive.memory.longterm.graph.GraphStoreFactory;
import run.mone.hive.memory.longterm.graph.utils.PromptUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import run.mone.hive.schema.AiMessage;

import java.util.*;
import java.util.stream.Collectors;

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
    private LLM llm;
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
        this.llm = new LLM(LLMConfig.builder().llmProvider(LLMProvider.valueOf(config.getLlm().getProviderName())).build());
        this.embeddingModel = EmbeddingFactory.create(config.getEmbedder());
        this.vectorStore = VectorStoreFactory.create(config.getVectorStore());
        this.graphStore = GraphStoreFactory.create(config.getGraphStore());
        this.historyManager = new HistoryManager(config.getHistoryDbPath());

        // 初始化线程池
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        log.info("Memory initialized with provider: {}, vector store: {}, embedder: {}",
                this.llm.getLlmProvider(),
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
                () -> addToGraph(messageList, effectiveFilters, processedMetadata), executor);

        try {
            List<MemoryItem> vectorResult = vectorFuture.get();
            List<String> graphResult = graphFuture.get();

            // 转换为与Python版本一致的Map格式
            List<Map<String, Object>> results = vectorResult.stream()
                    .map(item -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", item.getId());
                        map.put("memory", item.getMemory());
                        map.put("event", item.getEvent() != null ? item.getEvent() : "ADD");
                        if (item.getScore() != null) {
                            map.put("score", item.getScore());
                        }
                        if (item.getHash() != null) {
                            map.put("hash", item.getHash());
                        }
                        if (item.getCreatedAt() != null) {
                            map.put("created_at", item.getCreatedAt().toString());
                        }
                        if (item.getUpdatedAt() != null) {
                            map.put("updated_at", item.getUpdatedAt().toString());
                        }
                        return map;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> result = new HashMap<>();
            result.put("results", results);

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
            // 并行执行向量存储和图存储查询
            CompletableFuture<List<MemoryItem>> vectorFuture = CompletableFuture.supplyAsync(() ->
                    vectorStore.list(effectiveFilters, limit), executor);

            CompletableFuture<List<Map<String, Object>>> graphFuture = CompletableFuture.supplyAsync(() -> {
                if (enableGraph && graphStore != null) {
                    return graphStore.getAll(limit);
                }
                return new ArrayList<>();
            }, executor);

            List<MemoryItem> vectorResults = vectorFuture.get();
            List<Map<String, Object>> graphResults = graphFuture.get();

            // 转换为与Python版本一致的Map格式
            List<Map<String, Object>> results = vectorResults.stream()
                    .map(item -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", item.getId());
                        map.put("memory", item.getMemory());
                        if (item.getScore() != null) {
                            map.put("score", item.getScore());
                        }
                        if (item.getHash() != null) {
                            map.put("hash", item.getHash());
                        }
                        if (item.getCreatedAt() != null) {
                            map.put("created_at", item.getCreatedAt().toString());
                        }
                        if (item.getUpdatedAt() != null) {
                            map.put("updated_at", item.getUpdatedAt().toString());
                        }
                        if (item.getUserId() != null) {
                            map.put("user_id", item.getUserId());
                        }
                        if (item.getAgentId() != null) {
                            map.put("agent_id", item.getAgentId());
                        }
                        if (item.getRunId() != null) {
                            map.put("run_id", item.getRunId());
                        }
                        return map;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> result = new HashMap<>();
            result.put("results", results);

            if (enableGraph && !graphResults.isEmpty()) {
                result.put("relations", graphResults);
            }

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
            // 并行执行向量搜索和图搜索
            CompletableFuture<List<MemoryItem>> vectorFuture = CompletableFuture.supplyAsync(() -> {
                List<Double> queryEmbedding = embeddingModel.embed(query, "search");
                List<MemoryItem> memories = vectorStore.search(query, queryEmbedding, limit, effectiveFilters);

                if (threshold != null) {
                    return memories.stream()
                            .filter(m -> m.getScore() == null || m.getScore() >= threshold)
                            .collect(java.util.stream.Collectors.toList());
                }
                return memories;
            }, executor);

            CompletableFuture<List<Map<String, Object>>> graphFuture = CompletableFuture.supplyAsync(() -> {
                if (enableGraph && graphStore != null) {
                    return graphStore.search(query, limit, userId);
                }
                return new ArrayList<>();
            }, executor);

            List<MemoryItem> vectorResults = vectorFuture.get();
            List<Map<String, Object>> graphResults = graphFuture.get();

            // 转换为与Python版本一致的Map格式
            List<Map<String, Object>> results = vectorResults.stream()
                    .map(item -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", item.getId());
                        map.put("memory", item.getMemory());
                        if (item.getScore() != null) {
                            map.put("score", item.getScore());
                        }
                        if (item.getHash() != null) {
                            map.put("hash", item.getHash());
                        }
                        if (item.getCreatedAt() != null) {
                            map.put("created_at", item.getCreatedAt().toString());
                        }
                        if (item.getUpdatedAt() != null) {
                            map.put("updated_at", item.getUpdatedAt().toString());
                        }
                        if (item.getUserId() != null) {
                            map.put("user_id", item.getUserId());
                        }
                        if (item.getAgentId() != null) {
                            map.put("agent_id", item.getAgentId());
                        }
                        if (item.getRunId() != null) {
                            map.put("run_id", item.getRunId());
                        }
                        return map;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> result = new HashMap<>();
            result.put("results", results);

            if (enableGraph && !graphResults.isEmpty()) {
                result.put("relations", graphResults);
            }

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
            graphStore.resetAll();
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
        // 解析消息，与Python版本一致
        String parsedMessages = MessageParser.parseMessagesToString(messages);
        log.info("Parsed messages: {}", parsedMessages);

        // 第一步：使用LLM提取事实 (类似Python的FACT_RETRIEVAL_PROMPT)
        List<String> newRetrievedFacts = extractFactsFromMessages(parsedMessages);
        log.info("Extracted facts: {}", newRetrievedFacts);

        if (newRetrievedFacts.isEmpty()) {
            log.info("No facts extracted from input, skipping memory update");
            return new ArrayList<>();
        }

        // 第二步：为每个事实搜索相关的现有记忆
        List<Map<String, Object>> retrievedOldMemory = searchRelevantMemoriesForFacts(newRetrievedFacts, filters);
        log.info("Retrieved {} existing memories", retrievedOldMemory.size());

        // 建立临时ID映射（类似Python版本的temp_uuid_mapping）
        Map<String, String> tempUuidMapping = new HashMap<>();
        for (int i = 0; i < retrievedOldMemory.size(); i++) {
            Map<String, Object> memory = retrievedOldMemory.get(i);
            String originalId = (String) memory.get("id");
            tempUuidMapping.put(String.valueOf(i), originalId);
            memory.put("id", String.valueOf(i)); // 临时替换为索引
        }

        // 第三步：使用LLM决定如何更新记忆 (类似Python的UPDATE_MEMORY_PROMPT)
        List<Map<String, Object>> newMemoriesWithActions = determineMemoryActionsWithPrompt(
                retrievedOldMemory, newRetrievedFacts);
        log.info("Determined {} memory actions", newMemoriesWithActions.size());

        // 第四步：执行记忆操作
        return executeMemoryActionsWithMapping(newMemoriesWithActions, tempUuidMapping, metadata);
    }

    /**
     * 从消息中提取事实，类似Python版本的FACT_RETRIEVAL_PROMPT
     */
    private List<String> extractFactsFromMessages(String parsedMessages) {
        try {
            // 使用PromptUtils中的标准事实提取提示词
            String factExtractionPrompt = PromptUtils.DEFAULT_FACT_RETRIEVAL_PROMPT +
                    "\n\nFollowing is a conversation between the user and the assistant. You have to extract the relevant facts and preferences about the user, if any, from the conversation and return them in the json format as shown above.\n\n" +
                    parsedMessages;

            List<Map<String, Object>> messages = Arrays.asList(
                    Map.of("role", "user", "content", factExtractionPrompt)
            );

            List<AiMessage> msgList = messages.stream().map(it -> AiMessage.builder()
                    .role(it.get("role").toString())
                    .content(it.get("content").toString())
                    .build()).toList();

            String response = llm.chat(msgList, LLMConfig.builder().json(true).build());
            log.info("Fact extraction LLM response: {}", response);

            return parseFactsFromJsonResponse(response);

        } catch (Exception e) {
            log.error("Error extracting facts from messages", e);
            return new ArrayList<>();
        }
    }

    /**
     * 从 JSON 响应中解析事实列表
     */
    private List<String> parseFactsFromJsonResponse(String response) {
        try {
            JsonElement element = JsonParser.parseString(MemoryUtils.removeCodeBlocks(response.trim()));
            if (element.isJsonObject()) {
                JsonObject jsonObject = element.getAsJsonObject();
                if (jsonObject.has("facts")) {
                    JsonArray factsArray = jsonObject.getAsJsonArray("facts");
                    List<String> facts = new ArrayList<>();
                    for (JsonElement factElement : factsArray) {
                        if (factElement.isJsonPrimitive()) {
                            facts.add(factElement.getAsString());
                        }
                    }
                    return facts;
                }
            }
        } catch (Exception e) {
            log.error("Error parsing facts from JSON response: {}", response, e);
        }
        return new ArrayList<>();
    }

    /**
     * 为每个事实搜索相关的现有记忆，类似Python版本的向量搜索逻辑
     */
    private List<Map<String, Object>> searchRelevantMemoriesForFacts(List<String> facts, Map<String, Object> filters) {
        Map<String, Map<String, Object>> uniqueMemories = new HashMap<>();

        // 为每个事实分别搜索相关记忆
        for (String fact : facts) {
            try {
                List<Double> embedding = embeddingModel.embed(fact, "add");
                // 搜索最相关的5个记忆，与Python版本一致
                List<MemoryItem> memories = vectorStore.search(fact, embedding, 5, filters);

                for (MemoryItem memory : memories) {
                    String memoryId = memory.getId();
                    if (!uniqueMemories.containsKey(memoryId)) {
                        Map<String, Object> memoryMap = new HashMap<>();
                        memoryMap.put("id", memoryId);
                        memoryMap.put("text", memory.getMemory());
                        // 添加其他可能有用的元数据
                        if (memory.getScore() != null) {
                            memoryMap.put("score", memory.getScore());
                        }
                        uniqueMemories.put(memoryId, memoryMap);
                    }
                }
            } catch (Exception e) {
                log.error("Error searching relevant memories for fact: {}", fact, e);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>(uniqueMemories.values());
        log.info("Found {} unique relevant memories for {} facts", result.size(), facts.size());
        return result;
    }

    /**
     * 使用LLM决定如何更新记忆，类似Python版本的UPDATE_MEMORY_PROMPT
     */
    private List<Map<String, Object>> determineMemoryActionsWithPrompt(List<Map<String, Object>> retrievedOldMemory,
                                                                       List<String> newRetrievedFacts) {
        try {
            // 使用PromptUtils中的标准更新提示词
            String customUpdateMemoryPrompt = config.getCustomUpdateMemoryPrompt();
            String updatePrompt = PromptUtils.getUpdateMemoryMessages(
                    retrievedOldMemory,
                    newRetrievedFacts,
                    customUpdateMemoryPrompt
            );

            log.info("Update memory prompt: {}", updatePrompt);

            List<Map<String, Object>> messages = Arrays.asList(
                    Map.of("role", "user", "content", updatePrompt)
            );

            List<AiMessage> msgList = messages.stream().map(it ->
                    AiMessage.builder().role(it.get("role").toString()).content(it.get("content").toString()).build()).collect(Collectors.toList());

            String response = llm.chat(msgList, LLMConfig.builder().json(true).build());
            log.info("Memory decision LLM response: {}", response);

            return parseMemoryActionsFromJsonResponse(response);

        } catch (Exception e) {
            log.error("Error determining memory actions with prompt", e);
            return new ArrayList<>();
        }
    }

    /**
     * 从 JSON 响应中解析记忆操作列表
     */
    private List<Map<String, Object>> parseMemoryActionsFromJsonResponse(String response) {
        try {
            JsonElement element = JsonParser.parseString(MemoryUtils.removeCodeBlocks(response.trim()));
            if (element.isJsonObject()) {
                JsonObject jsonObject = element.getAsJsonObject();
                if (jsonObject.has("memory")) {
                    JsonArray memoryArray = jsonObject.getAsJsonArray("memory");
                    List<Map<String, Object>> actions = new ArrayList<>();

                    for (JsonElement memoryElement : memoryArray) {
                        if (memoryElement.isJsonObject()) {
                            JsonObject memoryObj = memoryElement.getAsJsonObject();
                            Map<String, Object> action = new HashMap<>();

                            if (memoryObj.has("id")) {
                                action.put("id", memoryObj.get("id").getAsString());
                            }
                            if (memoryObj.has("text")) {
                                action.put("text", memoryObj.get("text").getAsString());
                            }
                            if (memoryObj.has("event")) {
                                action.put("event", memoryObj.get("event").getAsString());
                            }
                            if (memoryObj.has("old_memory")) {
                                action.put("old_memory", memoryObj.get("old_memory").getAsString());
                            }

                            actions.add(action);
                        }
                    }
                    return actions;
                }
            }
        } catch (Exception e) {
            log.error("Error parsing memory actions from JSON response: {}", response, e);
        }
        return new ArrayList<>();
    }

    /**
     * 执行记忆操作，带有ID映射，类似Python版本的temp_uuid_mapping逻辑
     */
    private List<MemoryItem> executeMemoryActionsWithMapping(List<Map<String, Object>> newMemoriesWithActions,
                                                             Map<String, String> tempUuidMapping,
                                                             Map<String, Object> metadata) {
        List<MemoryItem> returnedMemories = new ArrayList<>();

        // 预处理所有新消息的嵌入向量，类似Python的new_message_embeddings
        Map<String, List<Double>> newMessageEmbeddings = new HashMap<>();

        if (newMemoriesWithActions == null || newMemoriesWithActions.isEmpty()) {
            log.info("No memory actions to be executed!");
            return returnedMemories;
        }

        // 遍历所有记忆操作
        for (Map<String, Object> action : newMemoriesWithActions) {
            try {
                log.info("Processing memory action: {}", action);

                String actionText = (String) action.get("text");
                String eventType = (String) action.get("event");

                if (actionText == null || actionText.trim().isEmpty()) {
                    log.info("Skipping memory entry because of empty 'text' field.");
                    continue;
                }

                switch (eventType) {
                    case "ADD":
                        String memoryId = createMemoryWithEmbeddings(actionText, newMessageEmbeddings, new HashMap<>(metadata));
                        returnedMemories.add(MemoryItem.builder()
                                .id(memoryId)
                                .memory(actionText)
                                .event(eventType)
                                .build());
                        break;

                    case "UPDATE":
                        String tempId = (String) action.get("id");
                        String realMemoryId = tempUuidMapping.get(tempId);
                        if (realMemoryId != null) {
                            updateMemoryWithEmbeddings(realMemoryId, actionText, newMessageEmbeddings, new HashMap<>(metadata));
                            String previousMemory = (String) action.get("old_memory");
                            returnedMemories.add(MemoryItem.builder()
                                    .id(realMemoryId)
                                    .memory(actionText)
                                    .event(eventType)
                                    .previousMemory(previousMemory)
                                    .build());
                        }
                        break;

                    case "DELETE":
                        String deleteTempId = (String) action.get("id");
                        String realDeleteId = tempUuidMapping.get(deleteTempId);
                        if (realDeleteId != null) {
                            deleteMemory(realDeleteId);
                            returnedMemories.add(MemoryItem.builder()
                                    .id(realDeleteId)
                                    .memory(actionText)
                                    .event(eventType)
                                    .build());
                        }
                        break;

                    case "NONE":
                        log.info("NOOP for Memory.");
                        break;

                    default:
                        log.warn("Unknown event type: {}", eventType);
                        break;
                }
            } catch (Exception e) {
                log.error("Error processing memory action: {}", action, e);
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
            metadata.put("memory", data);
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

    /**
     * 带有嵌入向量缓存的创建记忆方法
     */
    private String createMemoryWithEmbeddings(String data, Map<String, List<Double>> existingEmbeddings, Map<String, Object> metadata) {
        log.info("Creating memory with data: {}", data);

        try {
            List<Double> embeddings;
            if (existingEmbeddings.containsKey(data)) {
                embeddings = existingEmbeddings.get(data);
            } else {
                embeddings = embeddingModel.embed(data, "add");
                existingEmbeddings.put(data, embeddings); // 缓存嵌入向量
            }

            String memoryId = UUID.randomUUID().toString();
            metadata.put("memory", data);
            metadata.put("hash", calculateHash(data));
            metadata.put("created_at", LocalDateTime.now().toString());

            vectorStore.insert(Arrays.asList(embeddings), Arrays.asList(memoryId), Arrays.asList(metadata));

            historyManager.addHistory(memoryId, null, data, "ADD",
                    (String) metadata.get("created_at"),
                    (String) metadata.get("actor_id"),
                    (String) metadata.get("role"));

            return memoryId;

        } catch (Exception e) {
            log.error("Error creating memory with embeddings", e);
            throw new RuntimeException("Failed to create memory", e);
        }
    }

    private void updateMemory(String memoryId, String data, List<Double> embeddings) {
        try {
            MemoryItem existingMemory = vectorStore.get(memoryId);
            if (existingMemory == null) {
                throw new IllegalArgumentException("Memory with ID " + memoryId + " not found");
            }

            String prevValue = existingMemory.getMemory();
            Map<String, Object> newMetadata = new HashMap<>(existingMemory.getMetadata());
            newMetadata.put("memory", data);
            newMetadata.put("hash", calculateHash(data));

            // 保持创建时间
            if (existingMemory.getCreatedAt() != null) {
                newMetadata.put("created_at", existingMemory.getCreatedAt().toString());
            }
            newMetadata.put("updated_at", LocalDateTime.now().toString());

            // 保持原有的用户ID等信息
            if (existingMemory.getUserId() != null) {
                newMetadata.put("user_id", existingMemory.getUserId());
            }
            if (existingMemory.getAgentId() != null) {
                newMetadata.put("agent_id", existingMemory.getAgentId());
            }
            if (existingMemory.getRunId() != null) {
                newMetadata.put("run_id", existingMemory.getRunId());
            }

            // 更新向量存储
            vectorStore.update(memoryId, embeddings, newMetadata);
            log.info("Updated memory with ID: {} with data: {}", memoryId, data);

            // 更新图记忆（如果启用）
            updateGraphMemoryOnUpdate(data, newMetadata);

            historyManager.addHistory(memoryId, prevValue, data, "UPDATE",
                    (String) newMetadata.get("created_at"),
                    (String) newMetadata.get("updated_at"),
                    (String) newMetadata.get("actor_id"),
                    (String) newMetadata.get("role"));

        } catch (Exception e) {
            log.error("Error updating memory with ID: {}", memoryId, e);
            throw new RuntimeException("Failed to update memory", e);
        }
    }

    /**
     * 带有嵌入向量缓存的更新记忆方法
     */
    private void updateMemoryWithEmbeddings(String memoryId, String data, Map<String, List<Double>> existingEmbeddings, Map<String, Object> metadata) {
        log.info("Updating memory with ID: {} with data: {}", memoryId, data);

        try {
            MemoryItem existingMemory = vectorStore.get(memoryId);
            if (existingMemory == null) {
                log.error("Error getting memory with ID {} during update.", memoryId);
                throw new IllegalArgumentException("Error getting memory with ID " + memoryId + ". Please provide a valid 'memory_id'");
            }

            String prevValue = existingMemory.getMemory();
            Map<String, Object> newMetadata = new HashMap<>(metadata);

            newMetadata.put("memory", data);
            newMetadata.put("hash", calculateHash(data));
            if (existingMemory.getCreatedAt() != null) {
                newMetadata.put("created_at", existingMemory.getCreatedAt().toString());
            }
            newMetadata.put("updated_at", LocalDateTime.now().toString());

            // 保持原有的标识符
            if (existingMemory.getUserId() != null) {
                newMetadata.put("user_id", existingMemory.getUserId());
            }
            if (existingMemory.getAgentId() != null) {
                newMetadata.put("agent_id", existingMemory.getAgentId());
            }
            if (existingMemory.getRunId() != null) {
                newMetadata.put("run_id", existingMemory.getRunId());
            }
            if (existingMemory.getActorId() != null) {
                newMetadata.put("actor_id", existingMemory.getActorId());
            }
            if (existingMemory.getRole() != null) {
                newMetadata.put("role", existingMemory.getRole());
            }

            List<Double> embeddings;
            if (existingEmbeddings.containsKey(data)) {
                embeddings = existingEmbeddings.get(data);
            } else {
                embeddings = embeddingModel.embed(data, "update");
                existingEmbeddings.put(data, embeddings);
            }

            vectorStore.update(memoryId, embeddings, newMetadata);
            log.info("Updated memory with ID: {} with data: {}", memoryId, data);

            historyManager.addHistory(memoryId, prevValue, data, "UPDATE",
                    (String) newMetadata.get("created_at"),
                    (String) newMetadata.get("updated_at"),
                    (String) newMetadata.get("actor_id"),
                    (String) newMetadata.get("role"));

        } catch (Exception e) {
            log.error("Error updating memory with embeddings for ID: {}", memoryId, e);
            throw new RuntimeException("Failed to update memory", e);
        }
    }

    private void deleteMemory(String memoryId) {
        log.info("Deleting memory with ID: {}", memoryId);

        try {
            MemoryItem existingMemory = vectorStore.get(memoryId);
            if (existingMemory == null) {
                log.warn("Memory with ID {} not found", memoryId);
                return;
            }

            String prevValue = existingMemory.getMemory();

            // 删除向量存储中的记忆
            vectorStore.delete(memoryId);

            // 删除相关的图记忆（如果启用）
            deleteRelatedGraphMemories(existingMemory);

            historyManager.addHistory(memoryId, prevValue, null, "DELETE",
                    existingMemory.getCreatedAt() != null ? existingMemory.getCreatedAt().toString() : null,
                    null, // updated_at 为 null
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


            List<AiMessage> msgList = parsedMessages.stream().map(it -> AiMessage.builder().role(it.get("role").toString()).content(it.get("content").toString()).build()).collect(Collectors.toList());

            String proceduralMemory = llm.chat(msgList, LLMConfig.builder().build());

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

    private List<String> addToGraph(List<Message> messages, Map<String, Object> filters, Map<String, Object> metadata) {
        if (!enableGraph || graphStore == null) {
            return new ArrayList<>();
        }

        try {
            // 解析消息为字符串
            String parsedMessages = MessageParser.parseMessagesToString(messages);

            // 设置用户ID过滤器
            if (filters.get("user_id") == null) {
                filters.put("user_id", metadata.get("user_id"));
            }

            // 从文本中提取实体关系
            List<GraphStoreBase.GraphEntity> entities = graphStore.establishRelations(parsedMessages, (String) filters.get("user_id"));

            // 添加关系到图存储
            List<Map<String, Object>> addResults = graphStore.addMemories(entities, metadata);
            log.info("{}", addResults);

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
     * 在记忆更新时同时更新图记忆
     * 从新的文本数据中提取关系并更新到图存储中
     */
    private void updateGraphMemoryOnUpdate(String data, Map<String, Object> metadata) {
        if (!enableGraph || graphStore == null) {
            return;
        }

        try {
            // 构建过滤器，包含用户身份信息
            Map<String, Object> filters = new HashMap<>();
            filters.put("user_id", metadata.getOrDefault("user_id", "default_user"));
            if (metadata.containsKey("agent_id")) {
                filters.put("agent_id", metadata.get("agent_id"));
            }
            if (metadata.containsKey("run_id")) {
                filters.put("run_id", metadata.get("run_id"));
            }

            // 从更新的文本中提取新的实体关系
            List<GraphStoreBase.GraphEntity> newEntities = graphStore.establishRelations(data, (String) metadata.get("user_id"));

            if (!newEntities.isEmpty()) {
                // 添加新的关系到图存储
                // 注意：这里使用addMemories方法，图存储会自动处理重复关系
                List<Map<String, Object>> addResults = graphStore.addMemories(newEntities, metadata);

                log.info("Updated graph memory with {} new relationships during memory update", newEntities.size());
                log.info("Graph update results: {}", addResults);
            }

        } catch (Exception e) {
            // 图记忆更新失败不应该影响主要的记忆更新操作
            log.warn("Failed to update graph memory during memory update, but vector store update succeeded", e);
        }
    }

    /**
     * 删除与已删除记忆相关的图记忆
     * 当删除一个记忆时，也应该删除包含该记忆内容的图关系
     */
    private void deleteRelatedGraphMemories(MemoryItem memory) {
        if (!enableGraph || graphStore == null) {
            return;
        }

        try {
            String data = memory.getMemory();
            Map<String, Object> metadata = memory.getMetadata();

            // 构建过滤器
            Map<String, Object> filters = new HashMap<>();
            filters.put("user_id", metadata.getOrDefault("user_id", "default_user"));
            if (metadata.containsKey("agent_id")) {
                filters.put("agent_id", metadata.get("agent_id"));
            }
            if (metadata.containsKey("run_id")) {
                filters.put("run_id", metadata.get("run_id"));
            }

            // 从已删除的记忆文本中提取可能的实体关系
            List<GraphStoreBase.GraphEntity> extractedEntities = graphStore.establishRelations(data, (String) metadata.get("user_id"));

            if (!extractedEntities.isEmpty()) {
                // 对于每个提取的关系，尝试从图存储中删除
                for (GraphStoreBase.GraphEntity entity : extractedEntities) {
                    try {
                        // 检查关系是否存在，如果存在则删除
                        String userId = (String) filters.getOrDefault("user_id", "default_user");
                        if (graphStore.relationshipExists(entity.getSource(), entity.getDestination(), entity.getRelationship(), userId)) {
                            Map<String, Object> deleteResult = graphStore.deleteMemory(
                                    entity.getSource(),
                                    entity.getDestination(),
                                    entity.getRelationship(),
                                    userId
                            );
                            log.info("Deleted graph relationship: {} -> {} -> {} (result: {})",
                                    entity.getSource(), entity.getRelationship(), entity.getDestination(), deleteResult);
                        }
                    } catch (Exception e) {
                        log.warn("Failed to delete specific graph relationship: {} -> {} -> {}",
                                entity.getSource(), entity.getRelationship(), entity.getDestination(), e);
                    }
                }

                log.info("Processed {} potential graph relationships for deletion during memory delete", extractedEntities.size());
            }

        } catch (Exception e) {
            // 图记忆删除失败不应该影响主要的记忆删除操作
            log.warn("Failed to delete related graph memories during memory deletion, but vector store deletion succeeded", e);
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

