package run.mone.hive.memory.longterm.graph.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.Data;
import run.mone.hive.memory.longterm.config.GraphStoreConfig;
import run.mone.hive.memory.longterm.config.LlmConfig;
import run.mone.hive.memory.longterm.config.EmbedderConfig;
import run.mone.hive.memory.longterm.graph.GraphStoreBase;
import run.mone.hive.memory.longterm.graph.tools.GraphTools;
import run.mone.hive.memory.longterm.graph.utils.GraphUtils;
import run.mone.hive.memory.longterm.graph.utils.BM25Ranker;
import run.mone.hive.memory.longterm.llm.LLMBase;
import run.mone.hive.memory.longterm.llm.LLMFactory;
import run.mone.hive.memory.longterm.utils.MemoryUtils;
import run.mone.hive.memory.longterm.embeddings.EmbeddingBase;
import run.mone.hive.memory.longterm.embeddings.EmbeddingFactory;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.ServiceUnavailableException;

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
    private LLMBase llm;
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
            if (llmConfig == null) {
                // 使用默认配置
                llmConfig = LlmConfig.deepseekDefault();
            }
            this.llm = LLMFactory.create(llmConfig);
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
                    log.debug("Failed to create entity_single index: {}", e.getMessage());
                }

                // 尝试创建复合索引（企业版功能）
                try {
                    session.run(String.format("CREATE INDEX entity_composite IF NOT EXISTS FOR (n %s) ON (n.name, n.user_id)", nodeLabel));
                } catch (Exception e) {
                    log.debug("Failed to create entity_composite index: {}", e.getMessage());
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

            Map<String, Object> result = addEntity(session, source, destination, relationship,
                                                 sourceType, destinationType, filters);

            log.info("Added graph memory for user {}: {} --[{}]-> {}", userId, source, relationship, destination);
            return result;

        } catch (Exception e) {
            log.error("Error adding graph memory to Neo4j", e);
            throw new RuntimeException("Failed to add graph memory", e);
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
     * @param text 输入文本
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

            // 获取对应的工具定义
            List<Map<String, Object>> tools = Arrays.asList(Map.of("tool", GraphTools.EXTRACT_ENTITIES_TOOL));

            // 调用LLM进行实体提取
            Map<String, Object> response = llm.generateResponseWithTools(messages, tools);

            // 解析工具调用结果
            List<Map<String, Object>> entities = new ArrayList<>();
            if (response != null && response.get("tool_calls") instanceof List && !((List<?>) response.get("tool_calls")).isEmpty()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> toolCalls = (List<Map<String, Object>>) response.get("tool_calls");

                for (Map<String, Object> toolCall : toolCalls) {
                    if ("extract_entities".equals(toolCall.get("name"))) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> arguments = (Map<String, Object>) toolCall.get("arguments");
                        if (arguments != null && arguments.get("entities") instanceof List) {
                            @SuppressWarnings("unchecked")
                            List<Map<String, Object>> extractedEntities = (List<Map<String, Object>>) arguments.get("entities");
                            entities.addAll(extractedEntities);
                        }
                    }
                }
            } else {
                // when tool calls is empty, use json extraction
                String content = response != null && response.get("content") != null ? response.get("content").toString() : "";
                if (!content.isEmpty()) {
                    try {
                        
                        @SuppressWarnings("unchecked")
                        Map<String, Object> jsonResponse = (Map<String, Object>) new com.google.gson.Gson().fromJson(content, Map.class);
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
                        log.error("Failed to parse JSON response from LLM: {}", jsonEx.getMessage());
                        // 如果JSON解析失败，可以在这里添加额外的处理逻辑
                    }
                }
 
            }

            log.debug("Extracted {} entities from text", entities.size());
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

            log.debug("Retrieved {} entities from data: {}", entityTypeMap.size(), entityTypeMap.keySet());
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
     * @param text 输入文本
     * @param userId 用户ID
     * @return 关系列表
     */
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

            // 准备工具 - 根据LLM提供商选择结构化工具
            List<Map<String, Object>> tools = new ArrayList<>();
            String llmProvider = config.getLlm() != null ? config.getLlm().getProvider().getValue() : "openai";

            if ("azure_openai_structured".equals(llmProvider) || "openai_structured".equals(llmProvider)) {
                tools.add(Map.of("tool", GraphTools.RELATIONS_STRUCT_TOOL));
            } else {
                tools.add(Map.of("tool", GraphTools.RELATIONS_TOOL));
            }

            // 调用LLM进行关系提取
            Map<String, Object> response = llm.generateResponseWithTools(messages, tools);

            // 解析工具调用结果
            List<GraphEntity> relations = new ArrayList<>();
            if (response != null && response.get("tool_calls") instanceof List && !((List<?>) response.get("tool_calls")).isEmpty()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> toolCalls = (List<Map<String, Object>>) response.get("tool_calls");

                for (Map<String, Object> toolCall : toolCalls) {
                    if ("establish_relations".equals(toolCall.get("name"))) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> arguments = (Map<String, Object>) toolCall.get("arguments");
                        if (arguments != null && arguments.get("entities") instanceof List) {
                            @SuppressWarnings("unchecked")
                            List<Map<String, Object>> extractedRelations = (List<Map<String, Object>>) arguments.get("entities");

                            for (Map<String, Object> rel : extractedRelations) {
                                String source = (String) rel.get("source");
                                String destination = (String) rel.get("destination");
                                String relationship = (String) rel.get("relationship");

                                if (GraphUtils.validateGraphEntity(source, destination, relationship)) {
                                    GraphEntity entity = new GraphEntity(
                                        GraphUtils.cleanEntityName(source),
                                        GraphUtils.cleanEntityName(destination),
                                        GraphUtils.cleanRelationshipName(relationship),
                                        "Entity",  // 默认源节点类型
                                        "Entity"   // 默认目标节点类型
                                    );
                                    relations.add(entity);
                                }
                            }
                        }
                    }
                }
            } else {
                // when tool calls is empty, use json extraction
                String content = MemoryUtils.removeCodeBlocks(response != null && response.get("content") != null ? response.get("content").toString() : "");
                if (!content.isEmpty()) {
                    try {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> jsonResponse = (Map<String, Object>) new com.google.gson.Gson().fromJson(content, Map.class);
                        if (jsonResponse != null && jsonResponse.get("entities") != null) {
                            @SuppressWarnings("unchecked")
                            List<Map<String, Object>> jsonEntities = (List<Map<String, Object>>) jsonResponse.get("entities");
                            for (Map<String, Object> entity : jsonEntities) {
                                Map<String, Object> normalizedRel = new HashMap<>();
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
                        log.error("Failed to parse JSON response from LLM: {}", jsonEx.getMessage());
                        // 如果JSON解析失败，可以在这里添加额外的处理逻辑
                    }
                } else {
                    log.warn("Empty content from LLM response!");
                }
            }

            log.debug("Extracted {} relations from text", relations.size());
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
    public GraphStoreConfig getConfig() {
        return config;
    }

    @Override
    public void close() {
        try {
            if (driver != null) {
                driver.close();
            }
            if (llm != null) {
                llm.close();
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