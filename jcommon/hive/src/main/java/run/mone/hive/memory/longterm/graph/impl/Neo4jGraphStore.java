package run.mone.hive.memory.longterm.graph.impl;

import com.google.gson.Gson;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.ServiceUnavailableException;
import run.mone.hive.common.GsonUtils;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.memory.longterm.config.EmbedderConfig;
import run.mone.hive.memory.longterm.config.GraphStoreConfig;
import run.mone.hive.memory.longterm.config.LlmConfig;
import run.mone.hive.memory.longterm.embeddings.EmbeddingBase;
import run.mone.hive.memory.longterm.embeddings.EmbeddingFactory;
import run.mone.hive.memory.longterm.graph.GraphStoreBase;
import run.mone.hive.memory.longterm.graph.utils.BM25Ranker;
import run.mone.hive.memory.longterm.graph.utils.GraphUtils;
import run.mone.hive.memory.longterm.utils.MemoryUtils;
import run.mone.hive.schema.AiMessage;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Neo4j图数据库实现
 * 基于mem0的Neo4j图存储实现
 */
@Slf4j
@Data
public class Neo4jGraphStore implements GraphStoreBase {

    private final GraphStoreConfig config;
    private Driver driver;
    private LLM llm;
    private EmbeddingBase embeddingModel;
    private String nodeLabel;
    private double threshold = 0.7;

    public Neo4jGraphStore(GraphStoreConfig config) {
        this.config = config;
        this.threshold = config.getThreshold();
        this.nodeLabel = config.getConfig().containsKey("base_label") &&
                (Boolean) config.getConfig().get("base_label") ?
                ":`__Entity__`" : "";

        initializeConnection();
        initializeLLM();
        initializeEmbedding();
        createIndices();

        log.info("Neo4j graph store initialized with database: {}", config.getDatabase());
    }

    private void initializeConnection() {
        try {
            Config.ConfigBuilder configBuilder = Config.builder()
                    .withMaxConnectionLifetime(30, TimeUnit.MINUTES)
                    .withMaxConnectionPoolSize(50)
                    .withConnectionAcquisitionTimeout(2, TimeUnit.MINUTES);

            // 禁用通知以减少日志噪音
            configBuilder.withNotificationConfig(NotificationConfig.defaultConfig());

            this.driver = GraphDatabase.driver(
                    config.getUrl(),
                    AuthTokens.basic(config.getUsername(), config.getPassword()),
                    configBuilder.build()
            );

            // 测试连接
            driver.verifyConnectivity();
            log.info("Neo4j connection established successfully");

        } catch (ServiceUnavailableException e) {
            log.error("Failed to connect to Neo4j: {}", e.getMessage());
            throw new RuntimeException("Neo4j service unavailable", e);
        } catch (Exception e) {
            log.error("Failed to initialize Neo4j connection", e);
            throw new RuntimeException("Failed to initialize Neo4j connection", e);
        }
    }

    private void initializeLLM() {
        try {
            LlmConfig llmConfig = config.getLlm();
            this.llm = new LLM(LLMConfig.builder().llmProvider(LLMProvider.valueOf(llmConfig.getProviderName())).build());
            log.info("LLM initialized with provider: {}", llmConfig.getProvider());
        } catch (Exception e) {
            log.error("Failed to initialize LLM", e);
            throw new RuntimeException("Failed to initialize LLM", e);
        }
    }

    private void initializeEmbedding() {
        try {
            EmbedderConfig embedderConfig = config.getEmbedder();
            this.embeddingModel = EmbeddingFactory.create(embedderConfig);
            log.info("Embedding model initialized with provider: {}", embedderConfig.getProvider());
        } catch (Exception e) {
            log.error("Failed to initialize embedding model", e);
            throw new RuntimeException("Failed to initialize embedding model", e);
        }
    }

    private void createIndices() {
        try (Session session = driver.session()) {
            if (!nodeLabel.isEmpty()) {
                // 创建用户ID索引
                try {
                    session.run(String.format("CREATE INDEX entity_single IF NOT EXISTS FOR (n %s) ON (n.user_id)", nodeLabel));
                } catch (Exception e) {
                    log.info("Failed to create entity_single index: {}", e.getMessage());
                }

                // 尝试创建复合索引（企业版功能）
                try {
                    session.run(String.format("CREATE INDEX entity_composite IF NOT EXISTS FOR (n %s) ON (n.name, n.user_id)", nodeLabel));
                } catch (Exception e) {
                    log.info("Failed to create entity_composite index: {}", e.getMessage());
                }
            }
            log.info("Neo4j indices created successfully");
        } catch (Exception e) {
            log.warn("Failed to create indices", e);
        }
    }

    @Override
    public Map<String, Object> addMemory(String source, String destination, String relationship,
                                         String sourceType, String destinationType, String userId) {
        if (!GraphUtils.validateGraphEntity(source, destination, relationship)) {
            throw new IllegalArgumentException("Invalid graph entity parameters");
        }

        if (userId == null || userId.trim().isEmpty()) {
            userId = "default_user";
        }

        try (Session session = driver.session()) {
            Map<String, Object> filters = new HashMap<>();
            filters.put("user_id", userId);

            // 按照mem0的add方法流程实现
            return addMemoryWithFlow(session, source, destination, relationship,
                    sourceType, destinationType, filters);

        } catch (Exception e) {
            log.error("Error adding graph memory to Neo4j", e);
            throw new RuntimeException("Failed to add graph memory", e);
        }
    }

    /**
     * 按照mem0流程实现的添加内存方法
     * 1. 提取实体类型映射 (_retrieve_nodes_from_data)
     * 2. 建立节点关系 (_establish_nodes_relations_from_data)
     * 3. 搜索图数据库 (_search_graph_db)
     * 4. 获取需要删除的实体 (_get_delete_entities_from_search_output)
     * 5. 删除实体 (_delete_entities)
     * 6. 添加实体 (_add_entities)
     */
    private Map<String, Object> addMemoryWithFlow(Session session, String source, String destination,
                                                  String relationship, String sourceType, String destinationType,
                                                  Map<String, Object> filters) {
        // 构建输入数据文本，模拟传入add方法的data参数
        String data = String.format("%s %s %s", source, relationship, destination);

        // 1. 提取实体类型映射 (复用现有方法)
        Map<String, String> entityTypeMap = retrieveNodesFromData(data, filters);

        // 2. 建立节点关系数据 (使用establishRelations方法并转换为兼容格式)
        List<Map<String, Object>> toBeAdded = establishNodesRelationsFromData(data, filters, entityTypeMap);

        // 3. 搜索图数据库中的相似节点
        List<Map<String, Object>> searchOutput = searchGraphDB(session,
                new ArrayList<>(entityTypeMap.keySet()), filters, 100);

        // 4. 获取需要删除的实体(会调用大模型)
        List<Map<String, Object>> toBeDeleted = getDeleteEntitiesFromSearchOutput(searchOutput, data, filters);

        // 5. 删除冲突的实体(调用图数据库)
        List<Map<String, Object>> deletedEntities = deleteEntities(session, toBeDeleted, filters);

        // 6. 添加新的实体(调用图数据库,这里会计算源和目标两个节点的相似度)
        List<Map<String, Object>> addedEntities = addEntities(session, toBeAdded, filters, entityTypeMap);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("deleted_entities", deletedEntities);
        result.put("added_entities", addedEntities);
        result.put("operation", "add_with_flow");
        result.put("success", true);

        log.info("Added graph memory with flow for user {}: {} --[{}]-> {}, deleted: {}, added: {}",
                filters.get("user_id"), source, relationship, destination,
                deletedEntities.size(), addedEntities.size());

        return result;
    }

    /**
     * 建立节点关系数据，对应Python中的_establish_nodes_relations_from_data方法
     *
     * @param data          输入数据文本
     * @param filters       过滤器
     * @param entityTypeMap 实体类型映射
     * @return 需要添加的关系列表
     */
    private List<Map<String, Object>> establishNodesRelationsFromData(String data, Map<String, Object> filters,
                                                                      Map<String, String> entityTypeMap) {
        try {
            // 构建用户身份
            String userIdentity = GraphUtils.buildUserIdentity(filters);

            // 准备系统prompt
            String systemPrompt;
            List<Map<String, Object>> messages = new ArrayList<>();

            // 检查自定义prompt配置
            String customPrompt = config.getCustomPrompt();

            if (customPrompt != null && !customPrompt.trim().isEmpty()) {
                systemPrompt = GraphUtils.processPrompt(GraphUtils.EXTRACT_RELATIONS_PROMPT, userIdentity, customPrompt);
                messages.add(Map.of("role", "system", "content", systemPrompt));
                messages.add(Map.of("role", "user", "content", data));
            } else {
                systemPrompt = GraphUtils.processPrompt(GraphUtils.EXTRACT_RELATIONS_PROMPT, userIdentity, null);
                messages.add(Map.of("role", "system", "content", systemPrompt));
                String userContent = String.format("List of entities: %s. \n\nText: %s",
                        new ArrayList<>(entityTypeMap.keySet()), data);
                messages.add(Map.of("role", "user", "content", userContent));
            }

            List<AiMessage> msgList = messages.stream().map(it -> AiMessage.builder().role(it.get("role").toString()).content(it.get("content").toString()).build()).collect(Collectors.toList());

            // 调用LLM进行关系提取
            String str = llm.chat(msgList, LLMConfig.builder().json(true).build());
            str = MemoryUtils.removeCodeBlocks(str.trim());

            Map<String, Object> response = GsonUtils.gson.fromJson(str, Map.class);

            // 解析结果并转换为兼容格式
            List<Map<String, Object>> entities = new ArrayList<>();
            try {
                Map<String, Object> jsonResponse = response;
                if (jsonResponse != null && jsonResponse.get("entities") != null) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> extractedEntities = (List<Map<String, Object>>) jsonResponse.get("entities");
                    entities.addAll(extractedEntities);
                }
            } catch (Exception jsonEx) {
                log.error("Failed to parse JSON response from LLM establishNodesRelationsFromData: {}", jsonEx.getMessage());
            }
            // 清理实体格式，对应Python中的_remove_spaces_from_entities
            entities = removeSpacesFromEntities(entities);
            log.info("Extracted {} relations from data", entities.size());
            return entities;

        } catch (Exception e) {
            log.error("Error establishing nodes relations from data", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取需要删除的实体，对应Python中的_get_delete_entities_from_search_output方法
     *
     * @param searchOutput 搜索输出结果
     * @param data         新数据
     * @param filters      过滤器
     * @return 需要删除的实体列表
     */
    private List<Map<String, Object>> getDeleteEntitiesFromSearchOutput(List<Map<String, Object>> searchOutput,
                                                                        String data, Map<String, Object> filters) {
        try {
            String searchOutputString = GraphUtils.formatEntities(searchOutput);

            // 构建用户身份
            String userIdentity = GraphUtils.buildUserIdentity(filters);

            // 获取删除消息
            String[] deleteMessages = GraphUtils.getDeleteMessages(searchOutputString, data, userIdentity);
            String systemPrompt = deleteMessages[0];
            String userPrompt = deleteMessages[1];


            List<Map<String, String>> messages = Arrays.asList(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", userPrompt)
            );

            List<AiMessage> msgList = messages.stream().map(it -> AiMessage.builder().role(it.get("role").toString()).content(it.get("content").toString()).build()).collect(Collectors.toList());

            // 调用LLM进行关系提取
            String str = llm.chat(msgList, LLMConfig.builder().json(true).build());
            str = MemoryUtils.removeCodeBlocks(str.trim());
            @SuppressWarnings("unchecked")
            Map<String, Object> response = new Gson().fromJson(str, Map.class);

            // 解析结果
            List<Map<String, Object>> toBeDeleted = new ArrayList<>();
            try {
                Map<String, Object> jsonResponse = response;
                if (jsonResponse != null && jsonResponse.get("toBeDeleted") != null) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> extractedRelations = (List<Map<String, Object>>) jsonResponse.get("toBeDeleted");
                    toBeDeleted.addAll(extractedRelations);
                }
            } catch (Exception jsonEx) {
                log.error("Failed to parse JSON response from LLM getDeleteEntitiesFromSearchOutput: {}", jsonEx.getMessage());
            }

            // 清理实体格式
            toBeDeleted = removeSpacesFromEntities(toBeDeleted);
            log.info("Identified {} relations for deletion", toBeDeleted.size());
            return toBeDeleted;

        } catch (Exception e) {
            log.error("Error getting delete entities from search output", e);
            return new ArrayList<>();
        }
    }

    /**
     * 删除实体，对应Python中的_delete_entities方法
     *
     * @param session     Neo4j会话
     * @param toBeDeleted 需要删除的实体列表
     * @param filters     过滤器
     * @return 删除操作结果列表
     */
    private List<Map<String, Object>> deleteEntities(Session session, List<Map<String, Object>> toBeDeleted,
                                                     Map<String, Object> filters) {
        List<Map<String, Object>> results = new ArrayList<>();
        String userId = (String) filters.get("user_id");
        String agentId = (String) filters.get("agent_id");
        String runId = (String) filters.get("run_id");

        for (Map<String, Object> item : toBeDeleted) {
            try {
                String source = (String) item.get("source");
                String destination = (String) item.get("destination");
                String relationship = (String) item.get("relationship");

                if (source == null || destination == null || relationship == null) {
                    continue;
                }

                // 构建查询参数
                Map<String, Object> params = new HashMap<>();
                params.put("source_name", source);
                params.put("dest_name", destination);
                params.put("user_id", userId);

                if (agentId != null) {
                    params.put("agent_id", agentId);
                }
                if (runId != null) {
                    params.put("run_id", runId);
                }

                // 构建节点属性过滤
                List<String> sourceProps = Arrays.asList("name: $source_name", "user_id: $user_id");
                List<String> destProps = Arrays.asList("name: $dest_name", "user_id: $user_id");
                if (agentId != null) {
                    sourceProps.add("agent_id: $agent_id");
                    destProps.add("agent_id: $agent_id");
                }
                if (runId != null) {
                    sourceProps.add("run_id: $run_id");
                    destProps.add("run_id: $run_id");
                }
                String sourcePropsStr = String.join(", ", sourceProps);
                String destPropsStr = String.join(", ", destProps);

                // 执行删除
                String cypher = String.format("""
                        MATCH (n %s {%s})
                        -[r:%s]->
                        (m %s {%s})
                        DELETE r
                        RETURN 
                            n.name AS source,
                            m.name AS target,
                            type(r) AS relationship
                        """, nodeLabel, sourcePropsStr, relationship, nodeLabel, destPropsStr);

                Result result = session.run(cypher, params);
                if (result.hasNext()) {
                    Record record = result.next();
                    Map<String, Object> deleteResult = new HashMap<>();
                    deleteResult.put("source", record.get("source").asString());
                    deleteResult.put("target", record.get("target").asString());
                    deleteResult.put("relationship", record.get("relationship").asString());
                    deleteResult.put("operation", "delete");
                    results.add(deleteResult);
                }

            } catch (Exception e) {
                log.warn("Failed to delete entity relation: {}", item, e);
            }
        }

        return results;
    }

    /**
     * 添加实体，对应Python中的_add_entities方法
     *
     * @param session       Neo4j会话
     * @param toBeAdded     需要添加的实体列表
     * @param filters       过滤器
     * @param entityTypeMap 实体类型映射
     * @return 添加操作结果列表
     */
    private List<Map<String, Object>> addEntities(Session session, List<Map<String, Object>> toBeAdded,
                                                  Map<String, Object> filters, Map<String, String> entityTypeMap) {
        List<Map<String, Object>> results = new ArrayList<>();
        String userId = (String) filters.get("user_id");
        String agentId = (String) filters.get("agent_id");
        String runId = (String) filters.get("run_id");

        for (Map<String, Object> item : toBeAdded) {
            try {
                String source = (String) item.get("source");
                String destination = (String) item.get("destination");
                String relationship = (String) item.get("relationship");

                log.info("{} {} {}", source, relationship, destination);

                if (source == null || destination == null || relationship == null) {
                    continue;
                }

                // 获取实体类型
                String sourceType = entityTypeMap.getOrDefault(source, "__User__");
                String destinationType = entityTypeMap.getOrDefault(destination, "__User__");

                // 构建标签
                String sourceLabel = nodeLabel.isEmpty() ? String.format(":`%s`", sourceType) : nodeLabel;
                String destLabel = nodeLabel.isEmpty() ? String.format(":`%s`", destinationType) : nodeLabel;
                String sourceExtraSet = nodeLabel.isEmpty() ? "" : String.format(", source:`%s`", sourceType);
                String destExtraSet = nodeLabel.isEmpty() ? "" : String.format(", destination:`%s`", destinationType);

                // 生成嵌入向量
                List<Double> sourceEmbedding = embeddingModel.embed(source, "add");
                List<Double> destEmbedding = embeddingModel.embed(destination, "add");

                // 搜索现有节点 (阈值0.9，高相似度匹配)
                List<Map<String, Object>> sourceNodeSearchResult = searchSourceNode(sourceEmbedding, filters, 0.9);
                List<Map<String, Object>> destinationNodeSearchResult = searchDestinationNode(destEmbedding, filters, 0.9);

                // 详细日志输出搜索结果
                log.info("Node search results for adding relation '{}' --[{}]-> '{}':", source, relationship, destination);
                if (!sourceNodeSearchResult.isEmpty()) {
                    Map<String, Object> sourceNode = sourceNodeSearchResult.get(0);
                    log.info("  Source node found: name='{}', similarity={}, elementId={}", 
                        sourceNode.get("name"), sourceNode.get("similarity"), sourceNode.get("elementId"));
                } else {
                    log.info("  Source node: not found (will create new)");
                }
                
                if (!destinationNodeSearchResult.isEmpty()) {
                    Map<String, Object> destNode = destinationNodeSearchResult.get(0);
                    log.info("  Destination node found: name='{}', similarity={}, elementId={}", 
                        destNode.get("name"), destNode.get("similarity"), destNode.get("elementId"));
                } else {
                    log.info("  Destination node: not found (will create new)");
                }

                String cypher;
                Map<String, Object> params = new HashMap<>();
                params.put("user_id", userId);
                if (agentId != null) params.put("agent_id", agentId);
                if (runId != null) params.put("run_id", runId);

                // 根据节点搜索结果选择不同的Cypher查询策略
                if (sourceNodeSearchResult.isEmpty() && destinationNodeSearchResult.isEmpty()) {
                    // 两个节点都不存在，创建新的
                    List<String> sourceProps = Arrays.asList("name: $source_name", "user_id: $user_id");
                    List<String> destProps = Arrays.asList("name: $dest_name", "user_id: $user_id");
                    if (agentId != null) {
                        sourceProps.add("agent_id: $agent_id");
                        destProps.add("agent_id: $agent_id");
                    }
                    if (runId != null) {
                        sourceProps.add("run_id: $run_id");
                        destProps.add("run_id: $run_id");
                    }
                    String sourcePropsStr = String.join(", ", sourceProps);
                    String destPropsStr = String.join(", ", destProps);

                    cypher = String.format("""
                                    MERGE (source %s {%s})
                                    ON CREATE SET source.created = timestamp(),
                                                source.mentions = 1
                                                %s
                                    ON MATCH SET source.mentions = coalesce(source.mentions, 0) + 1
                                    WITH source
                                    CALL db.create.setNodeVectorProperty(source, 'embedding', $source_embedding)
                                    WITH source
                                    MERGE (destination %s {%s})
                                    ON CREATE SET destination.created = timestamp(),
                                                destination.mentions = 1
                                                %s
                                    ON MATCH SET destination.mentions = coalesce(destination.mentions, 0) + 1
                                    WITH source, destination
                                    CALL db.create.setNodeVectorProperty(destination, 'embedding', $dest_embedding)
                                    WITH source, destination
                                    MERGE (source)-[rel:%s]->(destination)
                                    ON CREATE SET rel.created = timestamp(), rel.mentions = 1
                                    ON MATCH SET rel.mentions = coalesce(rel.mentions, 0) + 1
                                    RETURN source.name AS source, type(rel) AS relationship, destination.name AS target
                                    """, sourceLabel, sourcePropsStr, sourceExtraSet,
                            destLabel, destPropsStr, destExtraSet, relationship);

                    params.put("source_name", source);
                    params.put("dest_name", destination);
                    params.put("source_embedding", sourceEmbedding);
                    params.put("dest_embedding", destEmbedding);

                } else if (!sourceNodeSearchResult.isEmpty() && destinationNodeSearchResult.isEmpty()) {
                    // 源节点存在，目标节点不存在
                    List<String> mergeProps = Arrays.asList("name: $destination_name", "user_id: $user_id");
                    if (agentId != null) mergeProps.add("agent_id: $agent_id");
                    if (runId != null) mergeProps.add("run_id: $run_id");
                    String mergePropsStr = String.join(", ", mergeProps);

                    cypher = String.format("""
                            MATCH (source)
                            WHERE elementId(source) = $source_id
                            SET source.mentions = coalesce(source.mentions, 0) + 1
                            WITH source
                            MERGE (destination %s {%s})
                            ON CREATE SET
                                destination.created = timestamp(),
                                destination.mentions = 1
                                %s
                            ON MATCH SET
                                destination.mentions = coalesce(destination.mentions, 0) + 1
                            WITH source, destination
                            CALL db.create.setNodeVectorProperty(destination, 'embedding', $destination_embedding)
                            WITH source, destination
                            MERGE (source)-[r:%s]->(destination)
                            ON CREATE SET 
                                r.created = timestamp(),
                                r.mentions = 1
                            ON MATCH SET
                                r.mentions = coalesce(r.mentions, 0) + 1
                            RETURN source.name AS source, type(r) AS relationship, destination.name AS target
                            """, destLabel, mergePropsStr, destExtraSet, relationship);

                    params.put("source_id", sourceNodeSearchResult.get(0).get("elementId"));
                    params.put("destination_name", destination);
                    params.put("destination_embedding", destEmbedding);

                } else if (sourceNodeSearchResult.isEmpty() && !destinationNodeSearchResult.isEmpty()) {
                    // 源节点不存在，目标节点存在
                    List<String> mergeProps = Arrays.asList("name: $source_name", "user_id: $user_id");
                    if (agentId != null) mergeProps.add("agent_id: $agent_id");
                    if (runId != null) mergeProps.add("run_id: $run_id");
                    String mergePropsStr = String.join(", ", mergeProps);

                    cypher = String.format("""
                            MATCH (destination)
                            WHERE elementId(destination) = $destination_id
                            SET destination.mentions = coalesce(destination.mentions, 0) + 1
                            WITH destination
                            MERGE (source %s {%s})
                            ON CREATE SET
                                source.created = timestamp(),
                                source.mentions = 1
                                %s
                            ON MATCH SET
                                source.mentions = coalesce(source.mentions, 0) + 1
                            WITH source, destination
                            CALL db.create.setNodeVectorProperty(source, 'embedding', $source_embedding)
                            WITH source, destination
                            MERGE (source)-[r:%s]->(destination)
                            ON CREATE SET 
                                r.created = timestamp(),
                                r.mentions = 1
                            ON MATCH SET
                                r.mentions = coalesce(r.mentions, 0) + 1
                            RETURN source.name AS source, type(r) AS relationship, destination.name AS target
                            """, sourceLabel, mergePropsStr, sourceExtraSet, relationship);

                    params.put("destination_id", destinationNodeSearchResult.get(0).get("elementId"));
                    params.put("source_name", source);
                    params.put("source_embedding", sourceEmbedding);

                } else {
                    // 两个节点都存在
                    cypher = String.format("""
                            MATCH (source)
                            WHERE elementId(source) = $source_id
                            SET source.mentions = coalesce(source.mentions, 0) + 1
                            WITH source
                            MATCH (destination)
                            WHERE elementId(destination) = $destination_id
                            SET destination.mentions = coalesce(destination.mentions, 0) + 1
                            MERGE (source)-[r:%s]->(destination)
                            ON CREATE SET 
                                r.created_at = timestamp(),
                                r.updated_at = timestamp(),
                                r.mentions = 1
                            ON MATCH SET r.mentions = coalesce(r.mentions, 0) + 1
                            RETURN source.name AS source, type(r) AS relationship, destination.name AS target
                            """, relationship);

                    params.put("source_id", sourceNodeSearchResult.get(0).get("elementId"));
                    params.put("destination_id", destinationNodeSearchResult.get(0).get("elementId"));
                }

                Result result = session.run(cypher, params);
                if (result.hasNext()) {
                    Record record = result.next();
                    Map<String, Object> addResult = new HashMap<>();
                    addResult.put("source", record.get("source").asString());
                    addResult.put("relationship", record.get("relationship").asString());
                    addResult.put("target", record.get("target").asString());
                    addResult.put("operation", "add");
                    results.add(addResult);
                }

            } catch (Exception e) {
                log.warn("Failed to add entity relation: {}", item, e);
            }
        }

        return results;
    }

    /**
     * 清理实体格式，对应Python中的_remove_spaces_from_entities方法
     *
     * @param entityList 实体列表
     * @return 清理后的实体列表
     */
    private List<Map<String, Object>> removeSpacesFromEntities(List<Map<String, Object>> entityList) {
        for (Map<String, Object> item : entityList) {
            if (item.get("source") != null) {
                item.put("source", GraphUtils.normalizeEntityName(item.get("source").toString()));
            }
            if (item.get("relationship") != null) {
                item.put("relationship", GraphUtils.cleanRelationshipName(item.get("relationship").toString()));
            }
            if (item.get("destination") != null) {
                item.put("destination", GraphUtils.normalizeEntityName(item.get("destination").toString()));
            }
        }
        return entityList;
    }

    /**
     * 搜索源节点，对应Python中的_search_source_node方法
     */
    private List<Map<String, Object>> searchSourceNode(List<Double> sourceEmbedding, Map<String, Object> filters, double threshold) {
        try (Session session = driver.session()) {
            // 构建WHERE条件
            List<String> whereConditions = Arrays.asList(
                    "source_candidate.embedding IS NOT NULL",
                    "source_candidate.user_id = $user_id"
            );
            if (filters.get("agent_id") != null) {
                whereConditions.add("source_candidate.agent_id = $agent_id");
            }
            if (filters.get("run_id") != null) {
                whereConditions.add("source_candidate.run_id = $run_id");
            }
            String whereClause = String.join(" AND ", whereConditions);

            String cypher = String.format("""
                    MATCH (source_candidate %s)
                    WHERE %s
                    WITH source_candidate,
                    round(2 * vector.similarity.cosine(source_candidate.embedding, $source_embedding) - 1, 4) AS source_similarity
                    WHERE source_similarity >= $threshold
                    WITH source_candidate, source_similarity
                    ORDER BY source_similarity DESC
                    LIMIT 1
                    RETURN elementId(source_candidate) as elementId,
                           source_candidate.name as name,
                           source_similarity as similarity
                    """, nodeLabel, whereClause);

            Map<String, Object> params = new HashMap<>();
            params.put("source_embedding", sourceEmbedding);
            params.put("user_id", filters.get("user_id"));
            params.put("threshold", threshold);
            if (filters.get("agent_id") != null) {
                params.put("agent_id", filters.get("agent_id"));
            }
            if (filters.get("run_id") != null) {
                params.put("run_id", filters.get("run_id"));
            }

            Result result = session.run(cypher, params);
            List<Map<String, Object>> results = new ArrayList<>();
            result.stream().forEach(record -> {
                Map<String, Object> node = new HashMap<>();
                node.put("elementId", record.get("elementId").asString());
                node.put("name", record.get("name").asString());
                node.put("similarity", record.get("similarity").asDouble());
                results.add(node);
            });

            // 添加详细的日志输出
            if (!results.isEmpty()) {
                Map<String, Object> foundNode = results.get(0);
                log.info("searchSourceNode - Found matching node: name='{}', similarity={}, threshold={}, sourceEmbedding=[{} elements]", 
                    foundNode.get("name"), 
                    foundNode.get("similarity"), 
                    threshold,
                    sourceEmbedding.size());
            } else {
                log.info("searchSourceNode - No matching nodes found with threshold={}, sourceEmbedding=[{} elements]", 
                    threshold, sourceEmbedding.size());
            }

            return results;
        } catch (Exception e) {
            log.warn("Failed to search source node", e);
            return new ArrayList<>();
        }
    }

    /**
     * 搜索目标节点，对应Python中的_search_destination_node方法
     */
    private List<Map<String, Object>> searchDestinationNode(List<Double> destEmbedding, Map<String, Object> filters, double threshold) {
        try (Session session = driver.session()) {
            // 构建WHERE条件
            List<String> whereConditions = Arrays.asList(
                    "destination_candidate.embedding IS NOT NULL",
                    "destination_candidate.user_id = $user_id"
            );
            if (filters.get("agent_id") != null) {
                whereConditions.add("destination_candidate.agent_id = $agent_id");
            }
            if (filters.get("run_id") != null) {
                whereConditions.add("destination_candidate.run_id = $run_id");
            }
            String whereClause = String.join(" AND ", whereConditions);

            String cypher = String.format("""
                    MATCH (destination_candidate %s)
                    WHERE %s
                    WITH destination_candidate,
                    round(2 * vector.similarity.cosine(destination_candidate.embedding, $destination_embedding) - 1, 4) AS destination_similarity
                    WHERE destination_similarity >= $threshold
                    WITH destination_candidate, destination_similarity
                    ORDER BY destination_similarity DESC
                    LIMIT 1
                    RETURN elementId(destination_candidate) as elementId, 
                           destination_candidate.name as name, 
                           destination_similarity as similarity
                    """, nodeLabel, whereClause);

            Map<String, Object> params = new HashMap<>();
            params.put("destination_embedding", destEmbedding);
            params.put("user_id", filters.get("user_id"));
            params.put("threshold", threshold);
            if (filters.get("agent_id") != null) {
                params.put("agent_id", filters.get("agent_id"));
            }
            if (filters.get("run_id") != null) {
                params.put("run_id", filters.get("run_id"));
            }

            Result result = session.run(cypher, params);
            List<Map<String, Object>> results = new ArrayList<>();
            result.stream().forEach(record -> {
                Map<String, Object> node = new HashMap<>();
                node.put("elementId", record.get("elementId").asString());
                node.put("name", record.get("name").asString());
                node.put("similarity", record.get("similarity").asDouble());
                results.add(node);
            });

            // 添加详细的日志输出
            if (!results.isEmpty()) {
                Map<String, Object> foundNode = results.get(0);
                log.info("searchDestinationNode - Found matching node: name='{}', similarity={}, threshold={}, destEmbedding=[{} elements]", 
                    foundNode.get("name"), 
                    foundNode.get("similarity"), 
                    threshold,
                    destEmbedding.size());
            } else {
                log.info("searchDestinationNode - No matching nodes found with threshold={}, destEmbedding=[{} elements]", 
                    threshold, destEmbedding.size());
            }

            return results;
        } catch (Exception e) {
            log.warn("Failed to search destination node", e);
            return new ArrayList<>();
        }
    }

    private Map<String, Object> addEntity(Session session, String source, String destination,
                                          String relationship, String sourceType, String destinationType,
                                          Map<String, Object> filters) {

        // 生成嵌入向量
        List<Double> sourceEmbedding = embeddingModel.embed(source, "add");
        List<Double> destEmbedding = embeddingModel.embed(destination, "add");

        // 构建标签
        String sourceLabel = nodeLabel.isEmpty() ? String.format(":`%s`", sourceType) : nodeLabel;
        String destLabel = nodeLabel.isEmpty() ? String.format(":`%s`", destinationType) : nodeLabel;

        // 构建Cypher查询
        String cypher = String.format("""
                MERGE (source %s {name: $source_name, user_id: $user_id})
                ON CREATE SET source.created = timestamp(),
                            source.mentions = 1
                ON MATCH SET source.mentions = coalesce(source.mentions, 0) + 1
                WITH source
                CALL db.create.setNodeVectorProperty(source, 'embedding', $source_embedding)
                WITH source
                MERGE (destination %s {name: $dest_name, user_id: $user_id})
                ON CREATE SET destination.created = timestamp(),
                            destination.mentions = 1
                ON MATCH SET destination.mentions = coalesce(destination.mentions, 0) + 1
                WITH source, destination
                CALL db.create.setNodeVectorProperty(destination, 'embedding', $dest_embedding)
                WITH source, destination
                MERGE (source)-[rel:%s]->(destination)
                ON CREATE SET rel.created = timestamp(), rel.mentions = 1
                ON MATCH SET rel.mentions = coalesce(rel.mentions, 0) + 1
                RETURN source.name AS source, type(rel) AS relationship, destination.name AS target
                """, sourceLabel, destLabel, relationship);

        Map<String, Object> params = new HashMap<>();
        params.put("source_name", source);
        params.put("dest_name", destination);
        params.put("source_embedding", sourceEmbedding);
        params.put("dest_embedding", destEmbedding);
        params.put("user_id", filters.get("user_id"));

        Result result = session.run(cypher, params);

        Map<String, Object> response = new HashMap<>();
        if (result.hasNext()) {
            Record record = result.next();
            response.put("source", record.get("source").asString());
            response.put("relationship", record.get("relationship").asString());
            response.put("destination", record.get("target").asString());
        }
        response.put("operation", "add");
        response.put("success", true);

        return response;
    }

    @Override
    public Map<String, Object> updateMemory(String source, String destination, String relationship, String userId) {
        if (!GraphUtils.validateGraphEntity(source, destination, relationship)) {
            throw new IllegalArgumentException("Invalid graph entity parameters");
        }

        if (userId == null || userId.trim().isEmpty()) {
            userId = "default_user";
        }

        try (Session session = driver.session()) {
            String cypher = String.format("""
                    MATCH (source {name: $source_name, user_id: $user_id})-[r]->(dest {name: $dest_name, user_id: $user_id})
                    DELETE r
                    CREATE (source)-[:%s]->(dest)
                    RETURN source.name AS source, dest.name AS destination
                    """, relationship);

            Map<String, Object> params = new HashMap<>();
            params.put("source_name", source);
            params.put("dest_name", destination);
            params.put("user_id", userId);

            session.run(cypher, params);

            log.info("Updated graph memory for user {}: {} --[{}]-> {}", userId, source, relationship, destination);

            Map<String, Object> result = new HashMap<>();
            result.put("source", source);
            result.put("destination", destination);
            result.put("relationship", relationship);
            result.put("operation", "update");
            result.put("success", true);

            return result;

        } catch (Exception e) {
            log.error("Error updating graph memory in Neo4j", e);
            throw new RuntimeException("Failed to update graph memory", e);
        }
    }

    @Override
    public Map<String, Object> deleteMemory(String source, String destination, String relationship, String userId) {
        if (!GraphUtils.validateGraphEntity(source, destination, relationship)) {
            throw new IllegalArgumentException("Invalid graph entity parameters");
        }

        if (userId == null || userId.trim().isEmpty()) {
            userId = "default_user";
        }

        try (Session session = driver.session()) {
            String cypher = String.format("""
                    MATCH (source {name: $source_name, user_id: $user_id})
                    -[r:%s]->
                    (dest {name: $dest_name, user_id: $user_id})
                    DELETE r
                    RETURN source.name AS source, dest.name AS destination
                    """, relationship);

            Map<String, Object> params = new HashMap<>();
            params.put("source_name", source);
            params.put("dest_name", destination);
            params.put("user_id", userId);

            session.run(cypher, params);

            log.info("Deleted graph memory for user {}: {} --[{}]-> {}", userId, source, relationship, destination);

            Map<String, Object> result = new HashMap<>();
            result.put("source", source);
            result.put("destination", destination);
            result.put("relationship", relationship);
            result.put("operation", "delete");
            result.put("success", true);

            return result;

        } catch (Exception e) {
            log.error("Error deleting graph memory from Neo4j", e);
            throw new RuntimeException("Failed to delete graph memory", e);
        }
    }

    @Override
    public List<Map<String, Object>> search(String query, int limit, String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            userId = "default_user";
        }

        try (Session session = driver.session()) {
            Map<String, Object> filters = new HashMap<>();
            filters.put("user_id", userId);

            // 提取查询中的实体
            Map<String, String> entityTypeMap = retrieveNodesFromData(query, filters);

            // 搜索图数据库
            List<Map<String, Object>> searchOutput = searchGraphDB(session, new ArrayList<>(entityTypeMap.keySet()), filters, limit);

            if (searchOutput.isEmpty()) {
                return new ArrayList<>();
            }

            // 使用BM25重新排序
            BM25Ranker bm25 = BM25Ranker.fromGraphMemories(searchOutput);
            List<String> queryTokens = BM25Ranker.tokenize(query);
            List<Integer> topIndices = bm25.getTopN(queryTokens, Math.min(5, searchOutput.size()));

            List<Map<String, Object>> searchResults = topIndices.stream()
                    .map(searchOutput::get)
                    .collect(Collectors.toList());

            log.info("Returned {} search results for user {}", searchResults.size(), userId);
            return searchResults;

        } catch (Exception e) {
            log.error("Error searching graph memories in Neo4j", e);
            return new ArrayList<>();
        }
    }

    private List<Map<String, Object>> searchGraphDB(Session session, List<String> nodeList,
                                                    Map<String, Object> filters, int limit) {
        List<Map<String, Object>> resultRelations = new ArrayList<>();

        for (String node : nodeList) {
            List<Double> nEmbedding = embeddingModel.embed(node, "search");

            String cypher = String.format("""
                    MATCH (n %s {user_id: $user_id})
                    WHERE n.embedding IS NOT NULL
                    WITH n, round(2 * vector.similarity.cosine(n.embedding, $n_embedding) - 1, 4) AS similarity
                    WHERE similarity >= $threshold
                    CALL {
                        WITH n
                        MATCH (n)-[r]->(m %s {user_id: $user_id})
                        RETURN n.name AS source, elementId(n) AS source_id, type(r) AS relationship,
                               elementId(r) AS relation_id, m.name AS destination, elementId(m) AS destination_id
                        UNION
                        WITH n
                        MATCH (n)<-[r]-(m %s {user_id: $user_id})
                        RETURN m.name AS source, elementId(m) AS source_id, type(r) AS relationship,
                               elementId(r) AS relation_id, n.name AS destination, elementId(n) AS destination_id
                    }
                    WITH distinct source, source_id, relationship, relation_id, destination, destination_id, similarity
                    RETURN source, source_id, relationship, relation_id, destination, destination_id, similarity
                    ORDER BY similarity DESC
                    LIMIT $limit
                    """, nodeLabel, nodeLabel, nodeLabel);

            Map<String, Object> params = new HashMap<>();
            params.put("n_embedding", nEmbedding);
            params.put("threshold", threshold);
            params.put("user_id", filters.get("user_id"));
            params.put("limit", limit);

            Result result = session.run(cypher, params);
            result.stream().forEach(record -> {
                Map<String, Object> relation = new HashMap<>();
                relation.put("source", record.get("source").asString());
                relation.put("relationship", record.get("relationship").asString());
                relation.put("destination", record.get("destination").asString());
                relation.put("similarity", record.get("similarity").asDouble());
                resultRelations.add(relation);
            });
        }

        return resultRelations;
    }

    @Override
    public List<Map<String, Object>> getAll(int limit, String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            userId = "default_user";
        }

        try (Session session = driver.session()) {
            String cypher = String.format("""
                    MATCH (n %s {user_id: $user_id})-[r]->(m %s {user_id: $user_id})
                    RETURN n.name AS source, type(r) AS relationship, m.name AS target
                    LIMIT $limit
                    """, nodeLabel, nodeLabel);

            Map<String, Object> params = new HashMap<>();
            params.put("user_id", userId);
            params.put("limit", limit);

            Result result = session.run(cypher, params);
            List<Map<String, Object>> results = new ArrayList<>();

            result.stream().forEach(record -> {
                Map<String, Object> memory = new HashMap<>();
                memory.put("source", record.get("source").asString());
                memory.put("relationship", record.get("relationship").asString());
                memory.put("target", record.get("target").asString());
                results.add(memory);
            });

            log.info("Retrieved {} relationships for user {}", results.size(), userId);
            return results;

        } catch (Exception e) {
            log.error("Error getting all graph memories from Neo4j", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Map<String, Object>> extractEntities(String text) {
        return extractEntities(text, new HashMap<>());
    }

    /**
     * 从文本中提取实体，支持过滤器
     *
     * @param text    输入文本
     * @param filters 过滤器，包含用户身份信息
     * @return 实体列表
     */
    public List<Map<String, Object>> extractEntities(String text, Map<String, Object> filters) {
        try {
            // 构建用户身份
            String userIdentity = GraphUtils.buildUserIdentity(filters);

            // 处理系统prompt，替换USER_ID占位符
            String systemPrompt = GraphUtils.EXTRACT_ENTITIES_PROMPT.replace("USER_ID", userIdentity);

            // 构建提取实体的提示词
            List<Map<String, Object>> messages = Arrays.asList(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", text)
            );


            // 调用LLM进行实体提取
            List<AiMessage> msgList = messages.stream().map(it -> AiMessage.builder().role(it.get("role").toString()).content(it.get("content").toString()).build()).collect(Collectors.toList());

            // 调用LLM进行关系提取
            String str = llm.chat(msgList, LLMConfig.builder().json(true).build());
            str = MemoryUtils.removeCodeBlocks(str.trim());
            Map<String, Object> response = new Gson().fromJson(str, Map.class);

            // 解析工具调用结果
            List<Map<String, Object>> entities = new ArrayList<>();
            try {
                Map<String, Object> jsonResponse = response;
                if (jsonResponse != null && jsonResponse.get("entities") != null) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> extractedEntities = (List<Map<String, Object>>) jsonResponse.get("entities");
                    for (Map<String, Object> entity : extractedEntities) {
                        String entityName = entity.get("entity") != null ? entity.get("entity").toString() : "";
                        String entityType = entity.get("entity_type") != null ? entity.get("entity_type").toString() : "general";
                        if (!entityName.isEmpty()) {
                            entities.add(Map.of("entity", GraphUtils.normalizeEntityName(entityName), "entity_type", GraphUtils.normalizeEntityName(entityType)));
                        }
                    }
                }
            } catch (Exception jsonEx) {
                log.error("Failed to parse JSON response from LLM extractEntities: {}", jsonEx.getMessage());
            }

            log.info("Extracted {} entities from text", entities.size());
            return entities;

        } catch (Exception e) {
            log.error("Error extracting entities", e);
            return new ArrayList<>();
        }
    }

    private Map<String, String> retrieveNodesFromData(String data, Map<String, Object> filters) {
        try {
            // 使用extractEntities方法来智能提取实体，传递用户身份过滤器
            List<Map<String, Object>> entities = extractEntities(data, filters);
            Map<String, String> entityTypeMap = new HashMap<>();

            for (Map<String, Object> entity : entities) {
                String entityName = (String) entity.get("entity");
                String entityType = (String) entity.get("entity_type");

                if (entityName != null && entityType != null) {
                    // 清理实体名称
                    String cleanedName = GraphUtils.cleanEntityName(entityName);
                    String cleanedType = GraphUtils.cleanRelationshipName(entityType);

                    if (!cleanedName.isEmpty()) {
                        entityTypeMap.put(cleanedName.toLowerCase().replace(" ", "_"), cleanedType);
                    }
                }
            }

            // 如果LLM提取失败，回退到简单分词
            if (entityTypeMap.isEmpty()) {
                log.warn("LLM entity extraction returned no results, falling back to simple tokenization");
                String[] words = data.toLowerCase().split("\\s+");
                for (String word : words) {
                    if (word.length() > 2) {
                        entityTypeMap.put(word, "__Entity__");
                    }
                }
            }

            log.info("Retrieved {} entities from data: {}", entityTypeMap.size(), entityTypeMap.keySet());
            return entityTypeMap;

        } catch (Exception e) {
            log.error("Error in retrieveNodesFromData", e);
            // 回退到简单分词
            Map<String, String> entityTypeMap = new HashMap<>();
            String[] words = data.toLowerCase().split("\\s+");
            for (String word : words) {
                if (word.length() > 2) {
                    entityTypeMap.put(word, "__Entity__");
                }
            }
            return entityTypeMap;
        }
    }

    @Override
    public List<GraphEntity> establishRelations(String text) {
        return establishRelations(text, "default_user");
    }

    /**
     * 从文本中建立实体关系，支持指定用户ID
     *
     * @param text   输入文本
     * @param userId 用户ID
     * @return 关系列表
     */
    @Override
    public List<GraphEntity> establishRelations(String text, String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            userId = "default_user";
        }

        try {
            // 创建包含用户ID的过滤器
            Map<String, Object> filters = new HashMap<>();
            filters.put("user_id", userId);

            // 首先提取实体
            Map<String, String> entityTypeMap = retrieveNodesFromData(text, filters);

            // 构建用户身份
            String userIdentity = GraphUtils.buildUserIdentity(filters);

            // 检查自定义prompt配置
            String customPrompt = config.getCustomPrompt();

            // 准备系统prompt
            String systemPrompt;
            List<Map<String, Object>> messages = new ArrayList<>();

            if (customPrompt != null && !customPrompt.trim().isEmpty()) {
                systemPrompt = GraphUtils.processPrompt(GraphUtils.EXTRACT_RELATIONS_PROMPT, userIdentity, customPrompt);
                messages.add(Map.of("role", "system", "content", systemPrompt));
                messages.add(Map.of("role", "user", "content", text));
            } else {
                systemPrompt = GraphUtils.processPrompt(GraphUtils.EXTRACT_RELATIONS_PROMPT, userIdentity, null);
                messages.add(Map.of("role", "system", "content", systemPrompt));
                String userContent = String.format("List of entities: %s. \n\nText: %s",
                        new ArrayList<>(entityTypeMap.keySet()), text);
                messages.add(Map.of("role", "user", "content", userContent));
            }

            // 调用LLM进行关系提取
            List<AiMessage> msgList = messages.stream().map(it -> AiMessage.builder().role(it.get("role").toString()).content(it.get("content").toString()).build()).collect(Collectors.toList());
            // 调用LLM进行关系提取
            String str = llm.chat(msgList, LLMConfig.builder().json(true).build());
            str = MemoryUtils.removeCodeBlocks(str.trim());
            Map<String, Object> response = new Gson().fromJson(str, Map.class);

            // 解析工具调用结果
            List<GraphEntity> relations = new ArrayList<>();
            try {
                Map<String, Object> jsonResponse = response;
                if (jsonResponse != null && jsonResponse.get("entities") != null) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> jsonEntities = (List<Map<String, Object>>) jsonResponse.get("entities");
                    for (Map<String, Object> entity : jsonEntities) {
                        String source = entity.get("source") != null ? entity.get("source").toString() : "";
                        String relationship = entity.get("relationship") != null ? entity.get("relationship").toString() : "general";
                        String destination = entity.get("destination") != null ? entity.get("destination").toString() : "general";
                        if (!source.isEmpty() && !relationship.isEmpty() && !destination.isEmpty()) {
                            GraphEntity graphEntity = new GraphEntity(
                                    GraphUtils.cleanEntityName(source),
                                    GraphUtils.cleanEntityName(destination),
                                    GraphUtils.cleanRelationshipName(relationship),
                                    "Entity",  // 默认源节点类型
                                    "Entity"   // 默认目标节点类型
                            );
                            relations.add(graphEntity);
                        }
                    }
                }
            } catch (Exception jsonEx) {
                log.error("Failed to parse JSON response from LLM establishRelations: {}", jsonEx.getMessage());
            }

            log.info("Extracted {} relations from text", relations.size());
            return relations;

        } catch (Exception e) {
            log.error("Error establishing relations", e);
            return new ArrayList<>();
        }
    }

    @Override
    public boolean relationshipExists(String source, String destination, String relationship, String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            userId = "default_user";
        }

        try (Session session = driver.session()) {
            String cypher = String.format("""
                    MATCH (n {name: $source_name, user_id: $user_id})-[r:%s]->(m {name: $dest_name, user_id: $user_id})
                    RETURN count(r) as count
                    """, relationship);

            Map<String, Object> params = new HashMap<>();
            params.put("source_name", source);
            params.put("dest_name", destination);
            params.put("user_id", userId);

            Result result = session.run(cypher, params);
            return result.hasNext() && result.next().get("count").asInt() > 0;

        } catch (Exception e) {
            log.error("Error checking relationship existence in Neo4j", e);
            return false;
        }
    }

    @Override
    public List<Map<String, Object>> getNodeRelationships(String nodeName, String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            userId = "default_user";
        }

        try (Session session = driver.session()) {
            String cypher = String.format("""
                    MATCH (n {name: $node_name, user_id: $user_id})-[r]-(m {user_id: $user_id})
                    RETURN n.name as source, type(r) as relationship, m.name as destination
                    """);

            Map<String, Object> params = new HashMap<>();
            params.put("node_name", nodeName);
            params.put("user_id", userId);

            Result result = session.run(cypher, params);
            List<Map<String, Object>> relationships = new ArrayList<>();

            result.stream().forEach(record -> {
                Map<String, Object> rel = new HashMap<>();
                rel.put("source", record.get("source").asString());
                rel.put("relationship", record.get("relationship").asString());
                rel.put("destination", record.get("destination").asString());
                relationships.add(rel);
            });

            return relationships;

        } catch (Exception e) {
            log.error("Error getting node relationships from Neo4j", e);
            return new ArrayList<>();
        }
    }

    @Override
    public void deleteAll(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            userId = "default_user";
        }

        try (Session session = driver.session()) {
            session.run("MATCH (n {user_id: $user_id}) DETACH DELETE n",
                    Map.of("user_id", userId));
            log.info("Deleted all graph data for user {} from Neo4j", userId);

        } catch (Exception e) {
            log.error("Error deleting all graph data from Neo4j", e);
            throw new RuntimeException("Failed to delete all graph data", e);
        }
    }

    @Override
    public void reset(final String userId) {
        String finalUserId = (userId == null || userId.trim().isEmpty()) ? "default_user" : userId;

        try (Session session = driver.session()) {
            // 使用事务确保操作的原子性
            session.executeWrite(tx -> {
                // 首先删除所有关系
                tx.run("MATCH (n {user_id: $user_id})-[r]-() DELETE r",
                        Map.of("user_id", finalUserId));

                // 然后删除所有节点
                tx.run("MATCH (n {user_id: $user_id}) DELETE n",
                        Map.of("user_id", finalUserId));

                return null;
            });

            log.info("Reset completed - cleared all vertices and edges for user {} from Neo4j", finalUserId);

        } catch (Exception e) {
            log.error("Error resetting graph database for user {}", finalUserId, e);
            throw new RuntimeException("Failed to reset graph database", e);
        }
    }

    @Override
    public void resetAll() {
        try (Session session = driver.session()) {
            // 使用事务确保操作的原子性
            session.executeWrite(tx -> {
                // 首先删除所有带有user_id属性的关系
                tx.run("MATCH (n)-[r]-(m) WHERE n.user_id IS NOT NULL AND m.user_id IS NOT NULL DELETE r");

                // 然后删除所有带有user_id属性的节点
                tx.run("MATCH (n) WHERE n.user_id IS NOT NULL DELETE n");

                return null;
            });

            log.info("ResetAll completed - cleared all vertices and edges with user_id property from Neo4j");

        } catch (Exception e) {
            log.error("Error resetting entire graph database", e);
            throw new RuntimeException("Failed to reset entire graph database", e);
        }
    }

    @Override
    public GraphStoreConfig getConfig() {
        return config;
    }

    @Override
    public void close() {
        try {
            if (driver != null) {
                driver.close();
            }
            if (embeddingModel != null) {
                embeddingModel.close();
            }
            log.info("Neo4j connection closed");

        } catch (Exception e) {
            log.error("Error closing Neo4j connection", e);
        }
    }
}