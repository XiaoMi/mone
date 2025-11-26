package run.mone.hive.memory.longterm.vectorstore.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.neo4j.driver.exceptions.Neo4jException;
import run.mone.hive.memory.longterm.config.VectorStoreConfig;
import run.mone.hive.memory.longterm.model.MemoryItem;
import run.mone.hive.memory.longterm.vectorstore.VectorStoreBase;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Neo4j向量存储实现
 * 使用Neo4j数据库的向量索引功能存储和搜索向量
 */
@Slf4j
@Data
public class Neo4jVectorStore implements VectorStoreBase {

    private final VectorStoreConfig config;
    private Driver driver;
    private final String indexName;
    private final String nodeLabelName;

    // Neo4j向量搜索相关常量
    private static final String DEFAULT_NODE_LABEL = "MemoryVector";
    private static final String DEFAULT_INDEX_NAME = "memory_vector_index";
    private static final String VECTOR_PROPERTY = "embedding";
    private static final String CONTENT_PROPERTY = "content";
    private static final String ID_PROPERTY = "vectorId";

    public Neo4jVectorStore(VectorStoreConfig config) {
        this.config = config;
        this.nodeLabelName = config.getCollectionName() != null ?
            config.getCollectionName().replace("-", "_") : DEFAULT_NODE_LABEL;
        this.indexName = config.getCollectionName() != null ?
            config.getCollectionName() + "_vector_index" : DEFAULT_INDEX_NAME;
        this.driver = initializeDriver();
        
        // 创建向量索引
        createVectorIndexIfNotExists();

        log.info("Neo4j vector store initialized with database: {}, node label: {}, index: {}",
                config.getDatabase(), nodeLabelName, indexName);
    }

    private Driver initializeDriver() {
        try {
            // 使用 bolt:// 协议进行直连，而不是 neo4j:// 路由协议
            String uri = String.format("bolt://%s:%d", config.getHost(), config.getPort());

            Config.ConfigBuilder configBuilder = Config.builder()
                    .withMaxConnectionLifetime(30, java.util.concurrent.TimeUnit.MINUTES)
                    .withMaxConnectionPoolSize(50)
                    .withConnectionAcquisitionTimeout(2, java.util.concurrent.TimeUnit.MINUTES);

            AuthToken authToken = config.getApiKey() != null ?
                AuthTokens.basic("neo4j", config.getApiKey()) :
                AuthTokens.none();

            Driver driver = GraphDatabase.driver(uri, authToken, configBuilder.build());
            
            // 验证连接
            try {
                driver.verifyConnectivity();
                log.info("Neo4j connection established successfully to {}", uri);
            } catch (Exception e) {
                log.error("Failed to verify Neo4j connectivity: {}", e.getMessage());
                throw e;
            }
            
            return driver;
        } catch (Exception e) {
            log.error("Failed to initialize Neo4j driver: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize Neo4j driver", e);
        }
    }

    private void createVectorIndexIfNotExists() {
        String databaseName = config.getDatabase() != null ? config.getDatabase() : "neo4j";

        try (Session session = driver.session(SessionConfig.forDatabase(databaseName))) {
            // 检查索引是否存在
            String checkIndexQuery = "SHOW INDEXES YIELD name WHERE name = $indexName RETURN count(*) as count";
            Result result = session.run(checkIndexQuery, Map.of("indexName", indexName));

            long indexCount = result.single().get("count").asLong();

            if (indexCount == 0) {
                // 创建向量索引
                String createIndexQuery = String.format(
                    "CREATE VECTOR INDEX %s IF NOT EXISTS FOR (n:%s) ON (n.%s) " +
                    "OPTIONS {indexConfig: {`vector.dimensions`: %d, `vector.similarity_function`: 'cosine'}}",
                    indexName, nodeLabelName, VECTOR_PROPERTY, config.getEmbeddingModelDims()
                );

                session.run(createIndexQuery);
                log.info("Created vector index: {} for node label: {}", indexName, nodeLabelName);
            } else {
                log.info("Vector index already exists: {}", indexName);
            }
        } catch (Exception e) {
            log.error("Failed to create vector index: {}", e.getMessage());
            throw new RuntimeException("Failed to create vector index", e);
        }
    }

    @Override
    public void insert(List<List<Double>> vectors, List<String> ids, List<Map<String, Object>> payloads) {
        if (vectors.size() != ids.size() || vectors.size() != payloads.size()) {
            throw new IllegalArgumentException("Vectors, ids, and payloads must have the same size");
        }

        String databaseName = config.getDatabase() != null ? config.getDatabase() : "neo4j";

        try (Session session = driver.session(SessionConfig.forDatabase(databaseName))) {
            for (int i = 0; i < vectors.size(); i++) {
                List<Double> vector = vectors.get(i);
                String id = ids.get(i);
                Map<String, Object> payload = payloads.get(i);

                Map<String, Object> nodeProperties = new HashMap<>(payload);
                nodeProperties.put(ID_PROPERTY, id);
                nodeProperties.put(VECTOR_PROPERTY, vector);
                nodeProperties.put(CONTENT_PROPERTY, payload.getOrDefault("memory", ""));
                nodeProperties.put("timestamp", System.currentTimeMillis());
                nodeProperties.put("collection", config.getCollectionName());
                nodeProperties.put("created_at", LocalDateTime.now().toString());
                nodeProperties.put("updated_at", LocalDateTime.now().toString());

                String query = String.format(
                    "MERGE (n:%s {%s: $id}) " +
                    "SET n += $properties",
                    nodeLabelName, ID_PROPERTY
                );

                session.run(query, Map.of(
                    "id", id,
                    "properties", nodeProperties
                ));
            }

            log.info("Inserted {} vectors into Neo4j collection {}", vectors.size(), nodeLabelName);
        } catch (Neo4jException e) {
            log.error("Failed to insert vectors into Neo4j: {}", e.getMessage());
            throw new RuntimeException("Failed to insert vectors", e);
        }
    }

    @Override
    public List<MemoryItem> search(String query, List<Double> queryVector, int limit, Map<String, Object> filters) {
        String databaseName = config.getDatabase() != null ? config.getDatabase() : "neo4j";

        try (Session session = driver.session(SessionConfig.forDatabase(databaseName))) {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append(String.format(
                "CALL db.index.vector.queryNodes('%s', %d, $queryVector) " +
                "YIELD node, score ",
                indexName, limit
            ));

            // 添加过滤条件
            if (filters != null && !filters.isEmpty()) {
                queryBuilder.append("WHERE ");
                List<String> conditions = new ArrayList<>();
                for (String key : filters.keySet()) {
                    conditions.add(String.format("node.%s = $%s", key, key));
                }
                queryBuilder.append(String.join(" AND ", conditions));
                queryBuilder.append(" ");
            }

            queryBuilder.append("RETURN node, score ORDER BY score DESC");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("queryVector", queryVector);
            if (filters != null) {
                parameters.putAll(filters);
            }

            Result result = session.run(queryBuilder.toString(), parameters);

            List<MemoryItem> results = new ArrayList<>();
            while (result.hasNext()) {
                Record record = result.next();
                org.neo4j.driver.types.Node node = record.get("node").asNode();
                double score = record.get("score").asDouble();

                MemoryItem item = parseNodeToMemoryItem(node, score);
                if (item != null) {
                    results.add(item);
                }
            }

            log.info("Found {} similar vectors in Neo4j collection {}", results.size(), nodeLabelName);
            return results;
        } catch (Neo4jException e) {
            log.error("Failed to search vectors in Neo4j: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public MemoryItem get(String vectorId) {
        String databaseName = config.getDatabase() != null ? config.getDatabase() : "neo4j";

        try (Session session = driver.session(SessionConfig.forDatabase(databaseName))) {
            String query = String.format(
                "MATCH (n:%s) WHERE n.%s = $vectorId RETURN n",
                nodeLabelName, ID_PROPERTY
            );

            Result result = session.run(query, Map.of("vectorId", vectorId));

            if (result.hasNext()) {
                org.neo4j.driver.types.Node node = result.next().get("n").asNode();
                return parseNodeToMemoryItem(node, 1.0);
            }

            return null;
        } catch (Neo4jException e) {
            log.error("Failed to get vector {} from Neo4j: {}", vectorId, e.getMessage());
            return null;
        }
    }

    @Override
    public List<MemoryItem> list(Map<String, Object> filters, int limit) {
        String databaseName = config.getDatabase() != null ? config.getDatabase() : "neo4j";

        try (Session session = driver.session(SessionConfig.forDatabase(databaseName))) {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append(String.format("MATCH (n:%s)", nodeLabelName));

            // 添加过滤条件
            if (filters != null && !filters.isEmpty()) {
                queryBuilder.append(" WHERE ");
                List<String> conditions = new ArrayList<>();
                for (String key : filters.keySet()) {
                    conditions.add(String.format("n.%s = $%s", key, key));
                }
                queryBuilder.append(String.join(" AND ", conditions));
            }

            queryBuilder.append(" RETURN n");
            if (limit > 0) {
                queryBuilder.append(" LIMIT ").append(limit);
            }

            Map<String, Object> parameters = filters != null ? new HashMap<>(filters) : new HashMap<>();
            Result result = session.run(queryBuilder.toString(), parameters);

            List<MemoryItem> results = new ArrayList<>();
            while (result.hasNext()) {
                org.neo4j.driver.types.Node node = result.next().get("n").asNode();
                MemoryItem item = parseNodeToMemoryItem(node, 1.0);
                if (item != null) {
                    results.add(item);
                }
            }

            log.info("Listed {} vectors from Neo4j collection {}", results.size(), nodeLabelName);
            return results;
        } catch (Neo4jException e) {
            log.error("Failed to list vectors from Neo4j: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void update(String vectorId, List<Double> vector, Map<String, Object> payload) {
        String databaseName = config.getDatabase() != null ? config.getDatabase() : "neo4j";

        try (Session session = driver.session(SessionConfig.forDatabase(databaseName))) {
            Map<String, Object> nodeProperties = new HashMap<>(payload);
            nodeProperties.put(VECTOR_PROPERTY, vector);
            nodeProperties.put(CONTENT_PROPERTY, payload.getOrDefault("memory", ""));
            nodeProperties.put("updated_at", LocalDateTime.now().toString());
            nodeProperties.put("timestamp", System.currentTimeMillis());

            String query = String.format(
                "MATCH (n:%s) WHERE n.%s = $vectorId " +
                "SET n += $properties",
                nodeLabelName, ID_PROPERTY
            );

            session.run(query, Map.of(
                "vectorId", vectorId,
                "properties", nodeProperties
            ));

            log.info("Updated vector {} in Neo4j collection {}", vectorId, nodeLabelName);
        } catch (Neo4jException e) {
            log.error("Failed to update vector {} in Neo4j: {}", vectorId, e.getMessage());
            throw new RuntimeException("Failed to update vector", e);
        }
    }

    @Override
    public void delete(String vectorId) {
        String databaseName = config.getDatabase() != null ? config.getDatabase() : "neo4j";

        try (Session session = driver.session(SessionConfig.forDatabase(databaseName))) {
            String query = String.format(
                "MATCH (n:%s) WHERE n.%s = $vectorId DELETE n",
                nodeLabelName, ID_PROPERTY
            );

            session.run(query, Map.of("vectorId", vectorId));
            log.info("Deleted vector {} from Neo4j collection {}", vectorId, nodeLabelName);
        } catch (Neo4jException e) {
            log.error("Failed to delete vector {} from Neo4j: {}", vectorId, e.getMessage());
            throw new RuntimeException("Failed to delete vector", e);
        }
    }

    @Override
    public void deleteCol() {
        String databaseName = config.getDatabase() != null ? config.getDatabase() : "neo4j";

        try (Session session = driver.session(SessionConfig.forDatabase(databaseName))) {
            // 删除所有节点
            String deleteNodesQuery = String.format("MATCH (n:%s) DELETE n", nodeLabelName);
            session.run(deleteNodesQuery);

            // 删除索引
            String dropIndexQuery = String.format("DROP INDEX %s IF EXISTS", indexName);
            session.run(dropIndexQuery);

            log.info("Deleted Neo4j collection {} and index {}", nodeLabelName, indexName);
        } catch (Neo4jException e) {
            log.error("Failed to delete Neo4j collection {}: {}", nodeLabelName, e.getMessage());
            throw new RuntimeException("Failed to delete collection", e);
        }
    }

    @Override
    public void reset() {
        try {
            deleteCol();
            createVectorIndexIfNotExists();
            log.info("Reset Neo4j collection {}", nodeLabelName);
        } catch (Exception e) {
            log.error("Failed to reset Neo4j collection {}: {}", nodeLabelName, e.getMessage());
            throw new RuntimeException("Failed to reset collection", e);
        }
    }

    @Override
    public boolean collectionExists() {
        String databaseName = config.getDatabase() != null ? config.getDatabase() : "neo4j";

        try (Session session = driver.session(SessionConfig.forDatabase(databaseName))) {
            String query = "SHOW INDEXES YIELD name WHERE name = $indexName RETURN count(*) as count";
            Result result = session.run(query, Map.of("indexName", indexName));
            return result.single().get("count").asLong() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void createCollection() {
        if (!collectionExists()) {
            createVectorIndexIfNotExists();
        }
    }

    @Override
    public long getVectorCount() {
        String databaseName = config.getDatabase() != null ? config.getDatabase() : "neo4j";

        try (Session session = driver.session(SessionConfig.forDatabase(databaseName))) {
            String query = String.format("MATCH (n:%s) RETURN count(n) as count", nodeLabelName);
            Result result = session.run(query);
            return result.single().get("count").asLong();
        } catch (Neo4jException e) {
            log.error("Failed to get vector count from Neo4j: {}", e.getMessage());
            return 0;
        }
    }

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("vector_count", getVectorCount());
        stats.put("collection_name", nodeLabelName);
        stats.put("index_name", indexName);
        stats.put("provider", "neo4j");
        stats.put("dimensions", config.getEmbeddingModelDims());
        stats.put("host", config.getHost());
        stats.put("port", config.getPort());
        stats.put("database", config.getDatabase());
        return stats;
    }

    @Override
    public void close() {
        try {
            if (driver != null) {
                driver.close();
            }
            log.info("Closed Neo4j vector store for collection {}", nodeLabelName);
        } catch (Exception e) {
            log.warn("Error closing Neo4j driver: {}", e.getMessage());
        }
    }

    @Override
    public VectorStoreConfig getConfig() {
        return config;
    }

    /**
     * 将Neo4j节点转换为MemoryItem对象
     */
    private MemoryItem parseNodeToMemoryItem(org.neo4j.driver.types.Node node, double score) {
        try {
            Map<String, Object> properties = node.asMap();

            return MemoryItem.builder()
                    .id(getStringProperty(properties, ID_PROPERTY))
                    .memory(getStringProperty(properties, CONTENT_PROPERTY))
                    .score(score)
                    .userId(getStringProperty(properties, "user_id"))
                    .agentId(getStringProperty(properties, "agent_id"))
                    .runId(getStringProperty(properties, "run_id"))
                    .actorId(getStringProperty(properties, "actor_id"))
                    .role(getStringProperty(properties, "role"))
                    .metadata(properties)
                    .createdAt(parseDateTime(getStringProperty(properties, "created_at")))
                    .updatedAt(parseDateTime(getStringProperty(properties, "updated_at")))
                    .build();
        } catch (Exception e) {
            log.error("Failed to parse Neo4j node to MemoryItem: {}", e.getMessage());
            return null;
        }
    }

    private String getStringProperty(Map<String, Object> properties, String key) {
        Object value = properties.get(key);
        return value != null ? value.toString() : null;
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return LocalDateTime.now();
        }
        try {
            return LocalDateTime.parse(dateTimeStr);
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
}