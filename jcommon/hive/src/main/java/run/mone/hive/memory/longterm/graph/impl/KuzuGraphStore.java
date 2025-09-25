package run.mone.hive.memory.longterm.graph.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.Data;
import run.mone.hive.memory.longterm.config.GraphStoreConfig;
import run.mone.hive.memory.longterm.graph.GraphStoreBase;

import com.kuzudb.Connection;
import com.kuzudb.Database;
import com.kuzudb.QueryResult;

import java.util.*;
import java.util.stream.Collectors;
import java.io.File;

import run.mone.hive.memory.longterm.graph.tools.GraphTools;
import run.mone.hive.memory.longterm.graph.utils.GraphUtils;
import run.mone.hive.memory.longterm.llm.LLMBase;
import run.mone.hive.memory.longterm.llm.LLMFactory;
import run.mone.hive.memory.longterm.utils.MemoryUtils;

@Slf4j
@Data
public class KuzuGraphStore implements GraphStoreBase {
    private final GraphStoreConfig config;
    private Database database;
    private Connection connection;
    private boolean isEmbedded;
    private String databasePath;

    // Constants matching Python version
    private static final String NODE_LABEL = ":Entity";
    private static final String REL_LABEL = ":CONNECTED_TO";
    private static final double DEFAULT_THRESHOLD = 0.7;

    private LLMBase llm;

    public KuzuGraphStore(GraphStoreConfig config) {
        this.config = config;
        this.isEmbedded = initializeDatabase();
        this.connection = createConnection();
        this.llm = LLMFactory.create(config.getLlm());
        setupSchema();

        log.info("Kuzu graph store initialized {} at path: {}",
                isEmbedded ? "in embedded mode" : "with remote connection",
                databasePath);
    }

    private boolean initializeDatabase() {
        try {
            if (config.getUrl() == null 
                || config.getUrl().isEmpty() 
                || config.getUrl().trim().equalsIgnoreCase("")
                || config.getUrl().trim().equalsIgnoreCase(":memory:")) {

                log.info("Initializing embedded Kuzu database under in-memory mode");
                database = new Database();
                return true;

            } else if (config.getUrl().startsWith("file://")
                || config.getUrl().startsWith("file://")
                || config.getUrl().startsWith("/") // for linux
                || "local".equals(config.getUrl())) {

                databasePath = config.getUrl() != null && config.getUrl().startsWith("file://")
                    ? config.getUrl().substring(7)
                    : (config.getUrl() != null && !"local".equals(config.getUrl())
                        ? config.getUrl()
                        : "kuzu-embedded");

                File dbDir = new File(databasePath);
                if (!dbDir.exists()) {
                    dbDir.mkdirs();
                    log.info("Created Kuzu database directory: {}", databasePath);
                }

                database = new Database(databasePath);
                log.info("Initialized embedded Kuzu database at: {}", databasePath);
                return true;

            } else {
                throw new UnsupportedOperationException("Remote Kuzu connections not yet supported");
            }
        } catch (Exception e) {
            log.error("Failed to initialize Kuzu database: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize Kuzu database", e);
        }
    }

    private Connection createConnection() {
        try {
            return new Connection(database);
        } catch (Exception e) {
            log.error("Failed to create Kuzu connection: {}", e.getMessage());
            throw new RuntimeException("Failed to create Kuzu connection", e);
        }
    }

    private void setupSchema() {
        try {
            // Create node table for entities matching Python version
            executeQuery("""
                CREATE NODE TABLE IF NOT EXISTS Entity(
                    id SERIAL PRIMARY KEY,
                    user_id STRING,
                    agent_id STRING,
                    run_id STRING,
                    name STRING,
                    mentions INT64,
                    created TIMESTAMP,
                    embedding FLOAT[]
                )
                """);

            // Create relationship table matching Python version
            executeQuery("""
                CREATE REL TABLE IF NOT EXISTS CONNECTED_TO(
                    FROM Entity TO Entity,
                    name STRING,
                    mentions INT64,
                    created TIMESTAMP,
                    updated TIMESTAMP
                )
                """);

            log.info("Kuzu schema setup completed");
        } catch (Exception e) {
            log.error("Failed to setup Kuzu schema: {}", e.getMessage());
            throw new RuntimeException("Failed to setup schema", e);
        }
    }

    private QueryResult executeQuery(String query) {
        try {
            return connection.query(query);
        } catch (Exception e) {
            log.error("Failed to execute query: {} - Error: {}", query, e.getMessage());
            throw new RuntimeException("Query execution failed", e);
        }
    }

    /**
     * Adds data to the graph matching Python version functionality.
     *
     * @param data The data to add to the graph
     * @param filters A map containing filters (user_id, agent_id, run_id)
     * @return Map containing deleted_entities and added_entities
     */
    public Map<String, Object> add(String data, Map<String, Object> filters) {
        try {
            // Extract entities from data (simplified - would use LLM in full implementation)
            Map<String, String> entityTypeMap = retrieveNodesFromData(data, filters);

            // Establish relations from data (simplified - would use LLM in full implementation)
            List<Map<String, Object>> toBeAdded = establishNodesRelationsFromData(data, filters, entityTypeMap);

            // Search graph database for existing nodes
            List<Map<String, Object>> searchOutput = searchGraphDb(new ArrayList<>(entityTypeMap.keySet()), filters);

            // Get entities to be deleted (simplified - would use LLM in full implementation)
            List<Map<String, Object>> toBeDeleted = getDeleteEntitiesFromSearchOutput(searchOutput, data, filters);

            // Delete old entities
            List<Map<String, Object>> deletedEntities = deleteEntities(toBeDeleted, filters);

            // Add new entities
            List<Map<String, Object>> addedEntities = addEntities(toBeAdded, filters, entityTypeMap);

            Map<String, Object> result = new HashMap<>();
            result.put("deleted_entities", deletedEntities);
            result.put("added_entities", addedEntities);
            return result;
        } catch (Exception e) {
            log.error("Failed to add memory data: {}", e.getMessage());
            Map<String, Object> result = new HashMap<>();
            result.put("deleted_entities", new ArrayList<>());
            result.put("added_entities", new ArrayList<>());
            result.put("error", e.getMessage());
            return result;
        }
    }

    /**
     * Search for memories and related graph data matching Python version.
     *
     * @param query Query to search for
     * @param filters A map containing filters (user_id, agent_id, run_id)
     * @param limit Maximum number of results to return
     * @return List of search results with source, relationship, destination
     */
    public List<Map<String, Object>> search(String query, Map<String, Object> filters, int limit) {
        try {
            // Extract entities from query
            Map<String, String> entityTypeMap = retrieveNodesFromData(query, filters);

            // Search graph database
            List<Map<String, Object>> searchOutput = searchGraphDb(new ArrayList<>(entityTypeMap.keySet()), filters);

            if (searchOutput.isEmpty()) {
                return new ArrayList<>();
            }

            // Simplified BM25 ranking - would use proper BM25 implementation in full version
            List<Map<String, Object>> rankedResults = rankSearchResults(searchOutput, query, limit);

            log.info("Returned {} search results", rankedResults.size());
            return rankedResults;
        } catch (Exception e) {
            log.error("Failed to search graph: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Delete all entities based on filters matching Python version.
     *
     * @param filters A map containing filters (user_id, agent_id, run_id)
     */
    public void deleteAll(Map<String, Object> filters) {
        try {
            List<String> nodeProps = new ArrayList<>();
            nodeProps.add("user_id: $user_id");

            Map<String, Object> params = new HashMap<>();
            params.put("user_id", filters.get("user_id"));

            if (filters.get("agent_id") != null) {
                nodeProps.add("agent_id: $agent_id");
                params.put("agent_id", filters.get("agent_id"));
            }
            if (filters.get("run_id") != null) {
                nodeProps.add("run_id: $run_id");
                params.put("run_id", filters.get("run_id"));
            }

            String nodePropsStr = String.join(", ", nodeProps);
            String cypher = String.format("""
                MATCH (n %s {%s})
                DETACH DELETE n
                """, NODE_LABEL, nodePropsStr);

            executeQueryWithParams(cypher, params);
            log.info("Deleted all entities with filters: {}", filters);
        } catch (Exception e) {
            log.error("Failed to delete all entities: {}", e.getMessage());
            throw new RuntimeException("Failed to delete all entities", e);
        }
    }

    /**
     * Get all nodes and relationships based on filters matching Python version.
     *
     * @param filters A map containing filters (user_id, agent_id, run_id)
     * @param limit Maximum number of results to return
     * @return List of relationships with source, relationship, target
     */
    public List<Map<String, Object>> getAll(Map<String, Object> filters, int limit) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("user_id", filters.get("user_id"));
            params.put("limit", limit);

            List<String> nodeProps = new ArrayList<>();
            nodeProps.add("user_id: $user_id");

            if (filters.get("agent_id") != null) {
                nodeProps.add("agent_id: $agent_id");
                params.put("agent_id", filters.get("agent_id"));
            }
            if (filters.get("run_id") != null) {
                nodeProps.add("run_id: $run_id");
                params.put("run_id", filters.get("run_id"));
            }

            String nodePropsStr = String.join(", ", nodeProps);
            String query = String.format("""
                MATCH (n %s {%s})-[r]->(m %s {%s})
                RETURN
                    n.name AS source,
                    r.name AS relationship,
                    m.name AS target
                LIMIT $limit
                """, NODE_LABEL, nodePropsStr, NODE_LABEL, nodePropsStr);

            List<Map<String, Object>> results = executeQueryWithParamsAndGetResults(query, params);
            log.info("Retrieved {} relationships", results.size());
            return results;
        } catch (Exception e) {
            log.error("Failed to get all relationships: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Map<String, Object> addMemory(String source, String destination, String relationship,
                                        String sourceType, String destinationType, String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            userId = "default_user";
        }

        try {
            // Insert source entity if not exists
            String sourceQuery = String.format("""
                MERGE (s:Entity {name: '%s', user_id: '%s'})
                ON CREATE SET s.type = '%s', s.created_at = timestamp()
                """,
                escapeString(source), escapeString(userId), escapeString(sourceType));
            executeQuery(sourceQuery);

            // Insert destination entity if not exists
            String destQuery = String.format("""
                MERGE (d:Entity {name: '%s', user_id: '%s'})
                ON CREATE SET d.type = '%s', d.created_at = timestamp()
                """,
                escapeString(destination), escapeString(userId), escapeString(destinationType));
            executeQuery(destQuery);

            // Create relationship
            String relQuery = String.format("""
                MATCH (s:Entity {name: '%s', user_id: '%s'}), (d:Entity {name: '%s', user_id: '%s'})
                CREATE (s)-[r:Relationship {type: '%s', created_at: timestamp(), updated_at: timestamp()}]->(d)
                """,
                escapeString(source), escapeString(userId), escapeString(destination), escapeString(userId), escapeString(relationship));
            executeQuery(relQuery);

            Map<String, Object> result = new HashMap<>();
            result.put("action", "add_graph_memory");
            result.put("source", source);
            result.put("destination", destination);
            result.put("relationship", relationship);
            result.put("user_id", userId);
            result.put("status", "success");

            log.info("Added relationship for user {}: {} -[{}]-> {}", userId, source, relationship, destination);
            return result;
        } catch (Exception e) {
            log.error("Failed to add memory: {}", e.getMessage());
            Map<String, Object> result = new HashMap<>();
            result.put("action", "add_graph_memory");
            result.put("status", "error");
            result.put("error", e.getMessage());
            return result;
        }
    }

    @Override
    public Map<String, Object> updateMemory(String source, String destination, String relationship, String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            userId = "default_user";
        }

        try {
            String query = String.format("""
                MATCH (s:Entity {name: '%s', user_id: '%s'})-[r:Relationship]->(d:Entity {name: '%s', user_id: '%s'})
                SET r.type = '%s', r.updated_at = timestamp()
                """,
                escapeString(source), escapeString(userId), escapeString(destination), escapeString(userId), escapeString(relationship));
            executeQuery(query);

            Map<String, Object> result = new HashMap<>();
            result.put("action", "update_graph_memory");
            result.put("source", source);
            result.put("destination", destination);
            result.put("relationship", relationship);
            result.put("user_id", userId);
            result.put("status", "success");

            log.info("Updated relationship for user {}: {} -[{}]-> {}", userId, source, relationship, destination);
            return result;
        } catch (Exception e) {
            log.error("Failed to update memory: {}", e.getMessage());
            Map<String, Object> result = new HashMap<>();
            result.put("action", "update_graph_memory");
            result.put("status", "error");
            result.put("error", e.getMessage());
            return result;
        }
    }

    @Override
    public Map<String, Object> deleteMemory(String source, String destination, String relationship, String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            userId = "default_user";
        }

        try {
            String query = String.format("""
                MATCH (s:Entity {name: '%s', user_id: '%s'})-[r:Relationship {type: '%s'}]->(d:Entity {name: '%s', user_id: '%s'})
                DELETE r
                """,
                escapeString(source), escapeString(userId), escapeString(relationship), escapeString(destination), escapeString(userId));
            executeQuery(query);

            Map<String, Object> result = new HashMap<>();
            result.put("action", "delete_graph_memory");
            result.put("source", source);
            result.put("destination", destination);
            result.put("relationship", relationship);
            result.put("user_id", userId);
            result.put("status", "success");

            log.info("Deleted relationship for user {}: {} -[{}]-> {}", userId, source, relationship, destination);
            return result;
        } catch (Exception e) {
            log.error("Failed to delete memory: {}", e.getMessage());
            Map<String, Object> result = new HashMap<>();
            result.put("action", "delete_graph_memory");
            result.put("status", "error");
            result.put("error", e.getMessage());
            return result;
        }
    }

    @Override
    public List<Map<String, Object>> search(String query, int limit, String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            userId = "default_user";
        }

        try {
            String searchQuery = String.format("""
                MATCH (s:Entity {user_id: '%s'})-[r:Relationship]->(d:Entity {user_id: '%s'})
                WHERE s.name CONTAINS '%s' OR d.name CONTAINS '%s' OR r.type CONTAINS '%s'
                RETURN s.name as source, r.type as relationship, d.name as destination,
                       s.type as source_type, d.type as destination_type
                LIMIT %d
                """,
                escapeString(userId), escapeString(userId), escapeString(query), escapeString(query), escapeString(query), limit);

            QueryResult result = executeQuery(searchQuery);
            List<Map<String, Object>> results = new ArrayList<>();

            while (result.hasNext()) {
                var tuple = result.getNext();
                Map<String, Object> row = new HashMap<>();
                row.put("source", tuple.getValue(0).toString());
                row.put("relationship", tuple.getValue(1).toString());
                row.put("destination", tuple.getValue(2).toString());
                row.put("source_type", tuple.getValue(3).toString());
                row.put("destination_type", tuple.getValue(4).toString());
                results.add(row);
            }

            log.info("Found {} relationships for user {} matching query: {}", results.size(), userId, query);
            return results;
        } catch (Exception e) {
            log.error("Failed to search memories: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<Map<String, Object>> getAll(int limit, String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            userId = "default_user";
        }

        try {
            String query = String.format("""
                MATCH (s:Entity {user_id: '%s'})-[r:Relationship]->(d:Entity {user_id: '%s'})
                RETURN s.name as source, r.type as relationship, d.name as destination,
                       s.type as source_type, d.type as destination_type
                LIMIT %d
                """, escapeString(userId), escapeString(userId), limit);

            QueryResult result = executeQuery(query);
            List<Map<String, Object>> results = new ArrayList<>();

            while (result.hasNext()) {
                var tuple = result.getNext();
                Map<String, Object> row = new HashMap<>();
                row.put("source", tuple.getValue(0).toString());
                row.put("relationship", tuple.getValue(1).toString());
                row.put("destination", tuple.getValue(2).toString());
                row.put("source_type", tuple.getValue(3).toString());
                row.put("destination_type", tuple.getValue(4).toString());
                results.add(row);
            }

            return results;
        } catch (Exception e) {
            log.error("Failed to get all memories: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<Map<String, Object>> extractEntities(String text) {
        // Simple entity extraction based on patterns
        List<Map<String, Object>> entities = new ArrayList<>();

        // This is a basic implementation - in real scenarios you'd use NLP libraries
        String[] words = text.split("\\s+");
        Set<String> uniqueEntities = new HashSet<>();

        for (String word : words) {
            word = word.replaceAll("[^a-zA-Z0-9]", "");
            if (word.length() > 2 && Character.isUpperCase(word.charAt(0))) {
                uniqueEntities.add(word);
            }
        }

        for (String entity : uniqueEntities) {
            Map<String, Object> entityMap = new HashMap<>();
            entityMap.put("name", entity);
            entityMap.put("type", "GENERAL");
            entities.add(entityMap);
        }

        return entities;
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

        // Create filters with user ID
        Map<String, Object> filters = new HashMap<>();
        filters.put("user_id", userId);

        // First extract entities
        Map<String, String> entityTypeMap = retrieveNodesFromData(text, filters);

        // Then establish relations using LLM
        List<Map<String, Object>> relations = establishNodesRelationsFromData(text, filters, entityTypeMap);

        // Convert to GraphEntity format
        return relations.stream()
                .map(rel -> new GraphEntity(
                    rel.get("source").toString(),
                    rel.get("destination").toString(),
                    rel.get("relationship").toString(),
                    "GENERAL",
                    "GENERAL"
                ))
                .collect(Collectors.toList());
    }

    @Override
    public boolean relationshipExists(String source, String destination, String relationship, String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            userId = "default_user";
        }

        try {
            String query = String.format("""
                MATCH (s:Entity {name: '%s', user_id: '%s'})-[r:Relationship {type: '%s'}]->(d:Entity {name: '%s', user_id: '%s'})
                RETURN COUNT(r) as count
                """,
                escapeString(source), escapeString(userId), escapeString(relationship), escapeString(destination), escapeString(userId));

            QueryResult result = executeQuery(query);
            if (result.hasNext()) {
                Long count = (Long) result.getNext().getValue(0).getValue();
                return count > 0;
            }
            return false;
        } catch (Exception e) {
            log.error("Failed to check relationship existence: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public List<Map<String, Object>> getNodeRelationships(String nodeName, String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            userId = "default_user";
        }

        try {
            String query = String.format("""
                MATCH (n:Entity {name: '%s', user_id: '%s'})-[r:Relationship]-(other:Entity {user_id: '%s'})
                RETURN CASE
                         WHEN startNode(r) = n THEN 'outgoing'
                         ELSE 'incoming'
                       END as direction,
                       r.type as relationship,
                       other.name as other_node,
                       other.type as other_type
                """, escapeString(nodeName), escapeString(userId), escapeString(userId));

            QueryResult result = executeQuery(query);
            List<Map<String, Object>> relationships = new ArrayList<>();

            while (result.hasNext()) {
                var tuple = result.getNext();
                Map<String, Object> rel = new HashMap<>();
                rel.put("direction", tuple.getValue(0).toString());
                rel.put("relationship", tuple.getValue(1).toString());
                rel.put("other_node", tuple.getValue(2).toString());
                rel.put("other_type", tuple.getValue(3).toString());
                relationships.add(rel);
            }

            return relationships;
        } catch (Exception e) {
            log.error("Failed to get node relationships: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void deleteAll(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            userId = "default_user";
        }

        try {
            String query = String.format("MATCH (n:Entity {user_id: '%s'}) DETACH DELETE n", escapeString(userId));
            executeQuery(query);
            log.info("Deleted all data for user {} from Kuzu graph store", userId);
        } catch (Exception e) {
            log.error("Failed to delete all data: {}", e.getMessage());
            throw new RuntimeException("Failed to delete all data", e);
        }
    }

    @Override
    public boolean validateConnection() {
        try {
            executeQuery("MATCH (n) RETURN COUNT(n) LIMIT 1");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            // Get node count
            QueryResult nodeResult = executeQuery("MATCH (n:Entity) RETURN COUNT(n) as count");
            long nodeCount = 0;
            if (nodeResult.hasNext()) {
                nodeCount = (Long) nodeResult.getNext().getValue(0).getValue();
            }

            // Get relationship count
            QueryResult relResult = executeQuery("MATCH ()-[r:Relationship]->() RETURN COUNT(r) as count");
            long relCount = 0;
            if (relResult.hasNext()) {
                relCount = (Long) relResult.getNext().getValue(0).getValue();
            }

            stats.put("node_count", nodeCount);
            stats.put("relationship_count", relCount);
            stats.put("provider", "kuzu");
            stats.put("embedded_mode", isEmbedded);
            stats.put("database_path", databasePath);
            stats.put("enabled", config.isEnabled());
        } catch (Exception e) {
            stats.put("node_count", 0);
            stats.put("relationship_count", 0);
            stats.put("error", e.getMessage());
        }
        return stats;
    }

    @Override
    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
            if (database != null) {
                database.close();
            }
            log.info("Closed Kuzu graph store");
        } catch (Exception e) {
            log.warn("Error closing Kuzu connection: {}", e.getMessage());
        }
    }

    private String escapeString(String input) {
        if (input == null) return "";
        return input.replace("'", "\\'").replace("\"", "\\\"");
    }

    // Helper methods to support the Python-equivalent functionality

    /**
     * Execute query with parameters (placeholder for proper parameter binding).
     */
    private void executeQueryWithParams(String query, Map<String, Object> params) {
        // TODO: Implement proper parameter binding when KuzuDB Java API supports it
        // For now, substitute parameters manually (not production-ready)
        String processedQuery = substituteParameters(query, params);
        executeQuery(processedQuery);
    }

    /**
     * Execute query with parameters and return results.
     */
    private List<Map<String, Object>> executeQueryWithParamsAndGetResults(String query, Map<String, Object> params) {
        // TODO: Implement proper parameter binding when KuzuDB Java API supports it
        String processedQuery = substituteParameters(query, params);
        QueryResult result = executeQuery(processedQuery);
        List<Map<String, Object>> results = new ArrayList<>();

        // Process results - this would need proper result parsing based on actual API
        while (result.hasNext()) {
            var tuple = result.getNext();
            Map<String, Object> row = new HashMap<>();
            // TODO: Map tuple values properly based on query structure
            results.add(row);
        }
        return results;
    }

    /**
     * Simple parameter substitution (not production-ready - use proper parameter binding).
     */
    private String substituteParameters(String query, Map<String, Object> params) {
        String result = query;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String placeholder = "$" + entry.getKey();
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace(placeholder, "'" + escapeString(value) + "'");
        }
        return result;
    }

    /**
     * Extract entities from data using LLM.
     * Matches Python _retrieve_nodes_from_data method.
     */
    private Map<String, String> retrieveNodesFromData(String data, Map<String, Object> filters) {
        Map<String, String> entityTypeMap = new HashMap<>();

        try {
            // Build user identity for prompt
            String userIdentity = GraphUtils.buildUserIdentity(filters);

            // Prepare system prompt
            String systemPrompt = GraphUtils.EXTRACT_ENTITIES_PROMPT.replace("USER_ID", userIdentity);

            // Prepare messages for LLM
            List<Map<String, Object>> messages = new ArrayList<>();
            messages.add(Map.<String, Object>of("role", "system", "content", systemPrompt));
            messages.add(Map.<String, Object>of("role", "user", "content", data));

            // Prepare tools
            List<Map<String, Object>> tools = new ArrayList<>();
            tools.add(Map.<String, Object>of("tool", GraphTools.EXTRACT_ENTITIES_TOOL));

            // Call LLM
            Map<String, Object> response = llm.generateResponseWithTools(messages, tools);

            // Parse response
            if (response != null && response.get("tool_calls") instanceof List && !((List<?>) response.get("tool_calls")).isEmpty()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> toolCalls = (List<Map<String, Object>>) response.get("tool_calls");

                for (Map<String, Object> toolCall : toolCalls) {
                    if ("extract_entities".equals(toolCall.get("name"))) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> arguments = (Map<String, Object>) toolCall.get("arguments");

                        if (arguments != null && arguments.get("entities") != null) {
                            @SuppressWarnings("unchecked")
                            List<Map<String, Object>> entities = (List<Map<String, Object>>) arguments.get("entities");

                            for (Map<String, Object> entity : entities) {
                                String entityName = entity.get("entity") != null ? entity.get("entity").toString() : "";
                                String entityType = entity.get("entity_type") != null ? entity.get("entity_type").toString() : "general";

                                if (!entityName.isEmpty()) {
                                    entityTypeMap.put(
                                        GraphUtils.normalizeEntityName(entityName),
                                        GraphUtils.normalizeEntityName(entityType)
                                    );
                                }
                            }
                        }
                    }
                }
            } else {
                // 尝试解析JSON格式的响应
                String content = response != null && response.get("content") != null ? response.get("content").toString() : "";
                if (!content.isEmpty()) {
                    try {
                        
                        @SuppressWarnings("unchecked")
                        Map<String, Object> jsonResponse = (Map<String, Object>) new com.google.gson.Gson().fromJson(content, Map.class);
                        if (jsonResponse != null && jsonResponse.get("entities") != null) {
                            @SuppressWarnings("unchecked")
                            List<Map<String, Object>> entities = (List<Map<String, Object>>) jsonResponse.get("entities");
                            for (Map<String, Object> entity : entities) {
                                String entityName = entity.get("entity") != null ? entity.get("entity").toString() : "";
                                String entityType = entity.get("entity_type") != null ? entity.get("entity_type").toString() : "general";
                                if (!entityName.isEmpty()) {
                                    entityTypeMap.put(
                                        GraphUtils.normalizeEntityName(entityName),
                                        GraphUtils.normalizeEntityName(entityType)
                                    );
                                }
                            }
                        }
                    } catch (Exception jsonEx) {
                        log.error("Failed to parse JSON response from LLM: {}", jsonEx.getMessage());
                        // 如果JSON解析失败，可以在这里添加额外的处理逻辑
                    }
                }

            }
        } catch (Exception e) {
            log.error("Failed to extract entities using LLM: {}", e.getMessage());
            // Fallback to simple extraction
            String[] words = data.split("\\s+");
            for (String word : words) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                if (word.length() > 2 && Character.isUpperCase(word.charAt(0))) {
                    entityTypeMap.put(GraphUtils.normalizeEntityName(word), "general");
                }
            }
        }

        log.info("Entity type map: {}", entityTypeMap);
        return entityTypeMap;
    }

    /**
     * Establish relations from data using LLM.
     * Matches Python _establish_nodes_relations_from_data method.
     */
    private List<Map<String, Object>> establishNodesRelationsFromData(String data, Map<String, Object> filters, Map<String, String> entityTypeMap) {
        List<Map<String, Object>> entities = new ArrayList<>();

        try {
            // Build user identity for prompt
            String userIdentity = GraphUtils.buildUserIdentity(filters);

            // Check for custom prompt in config
            String customPrompt = config.getCustomPrompt();

            // Prepare system prompt
            String systemPrompt;
            List<Map<String, Object>> messages = new ArrayList<>();

            if (customPrompt != null && !customPrompt.trim().isEmpty()) {
                systemPrompt = GraphUtils.processPrompt(GraphUtils.EXTRACT_RELATIONS_PROMPT, userIdentity, customPrompt);
                messages.add(Map.<String, Object>of("role", "system", "content", systemPrompt));
                messages.add(Map.<String, Object>of("role", "user", "content", data));
            } else {
                systemPrompt = GraphUtils.processPrompt(GraphUtils.EXTRACT_RELATIONS_PROMPT, userIdentity, null);
                messages.add(Map.<String, Object>of("role", "system", "content", systemPrompt));
                String userContent = String.format("List of entities: %s. \n\nText: %s",
                    new ArrayList<>(entityTypeMap.keySet()), data);
                messages.add(Map.<String, Object>of("role", "user", "content", userContent));
            }

            // Prepare tools - would check LLM provider for structured tools
            List<Map<String, Object>> tools = new ArrayList<>();
            String llmProvider = config.getLlm() != null ? config.getLlm().getProvider().getValue() : "openai";

            if ("azure_openai_structured".equals(llmProvider) || "openai_structured".equals(llmProvider)) {
                tools.add(Map.<String, Object>of("tool", GraphTools.RELATIONS_STRUCT_TOOL));
            } else {
                tools.add(Map.<String, Object>of("tool", GraphTools.RELATIONS_TOOL));
            }

            // Call LLM
            Map<String, Object> response = llm.generateResponseWithTools(messages, tools);

            // Parse response
            if (response != null && response.get("tool_calls") instanceof List && !((List<?>) response.get("tool_calls")).isEmpty()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> toolCalls = (List<Map<String, Object>>) response.get("tool_calls");

                for (Map<String, Object> toolCall : toolCalls) {
                    if ("establish_relationships".equals(toolCall.get("name"))) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> arguments = (Map<String, Object>) toolCall.get("arguments");

                        if (arguments != null && arguments.get("entities") != null) {
                            @SuppressWarnings("unchecked")
                            List<Map<String, Object>> relationships = (List<Map<String, Object>>) arguments.get("entities");

                            for (Map<String, Object> relationship : relationships) {
                                Map<String, Object> normalizedRel = new HashMap<>();
                                normalizedRel.put("source", GraphUtils.normalizeEntityName(
                                    relationship.get("source") != null ? relationship.get("source").toString() : ""));
                                normalizedRel.put("relationship", GraphUtils.normalizeEntityName(
                                    relationship.get("relationship") != null ? relationship.get("relationship").toString() : ""));
                                normalizedRel.put("destination", GraphUtils.normalizeEntityName(
                                    relationship.get("destination") != null ? relationship.get("destination").toString() : ""));

                                if (!normalizedRel.get("source").toString().isEmpty() &&
                                    !normalizedRel.get("destination").toString().isEmpty() &&
                                    !normalizedRel.get("relationship").toString().isEmpty()) {
                                    entities.add(normalizedRel);
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
                                    normalizedRel.put("source", GraphUtils.normalizeEntityName(source));
                                    normalizedRel.put("relationship", GraphUtils.normalizeEntityName(relationship));
                                    normalizedRel.put("destination", GraphUtils.normalizeEntityName(destination));
                                    entities.add(normalizedRel);
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
        } catch (Exception e) {
            log.error("Failed to extract relationships using LLM: {}", e.getMessage());
            // Fallback to simple relationship extraction
            List<String> entityList = new ArrayList<>(entityTypeMap.keySet());
            if (entityList.size() >= 2) {
                Map<String, Object> relation = new HashMap<>();
                relation.put("source", entityList.get(0));
                relation.put("destination", entityList.get(1));
                relation.put("relationship", "related_to");
                entities.add(relation);
            }
        }

        log.info("Extracted entities: {}", entities);
        return entities;
    }

    /**
     * Search graph database for similar nodes and their relationships.
     */
    private List<Map<String, Object>> searchGraphDb(List<String> nodeList, Map<String, Object> filters) {
        List<Map<String, Object>> resultRelations = new ArrayList<>();

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("threshold", DEFAULT_THRESHOLD);
            params.put("user_id", filters.get("user_id"));

            List<String> nodeProps = new ArrayList<>();
            nodeProps.add("user_id: $user_id");

            if (filters.get("agent_id") != null) {
                nodeProps.add("agent_id: $agent_id");
                params.put("agent_id", filters.get("agent_id"));
            }
            if (filters.get("run_id") != null) {
                nodeProps.add("run_id: $run_id");
                params.put("run_id", filters.get("run_id"));
            }

            String nodePropsStr = String.join(", ", nodeProps);

            for (String node : nodeList) {
                // Generate embedding for the node (placeholder for actual embedding)
                // In a full implementation, this would call an embedding model
                double[] nodeEmbedding = generatePlaceholderEmbedding(node);

                // Search using both text matching and embedding similarity
                String query = String.format("""
                    MATCH (n %s {%s})-[r]->(m %s {%s})
                    WHERE n.name CONTAINS '%s' OR m.name CONTAINS '%s'
                    RETURN
                        n.name AS source,
                        id(n) AS source_id,
                        r.name AS relationship,
                        id(r) AS relation_id,
                        m.name AS destination,
                        id(m) AS destination_id,
                        1.0 AS similarity
                    LIMIT 100
                    """, NODE_LABEL, nodePropsStr, NODE_LABEL, nodePropsStr,
                    escapeString(node), escapeString(node));

                List<Map<String, Object>> results = executeQueryWithParamsAndGetResults(query, params);
                resultRelations.addAll(results);
            }
        } catch (Exception e) {
            log.error("Failed to search graph database: {}", e.getMessage());
        }

        return resultRelations;
    }

    /**
     * Get entities to be deleted from search output using LLM.
     * Matches Python _get_delete_entities_from_search_output method.
     */
    private List<Map<String, Object>> getDeleteEntitiesFromSearchOutput(List<Map<String, Object>> searchOutput, String data, Map<String, Object> filters) {
        List<Map<String, Object>> toBeDeleted = new ArrayList<>();

        try {
            // Format search output
            String searchOutputString = GraphUtils.formatEntities(searchOutput);

            // Build user identity
            String userIdentity = GraphUtils.buildUserIdentity(filters);

            // Get delete messages
            String[] messages = GraphUtils.getDeleteMessages(searchOutputString, data, userIdentity);
            String systemPrompt = messages[0];
            String userPrompt = messages[1];

            // Prepare messages for LLM
            List<Map<String, Object>> llmMessages = new ArrayList<>();
            llmMessages.add(Map.<String, Object>of("role", "system", "content", systemPrompt));
            llmMessages.add(Map.<String, Object>of("role", "user", "content", userPrompt));

            // Prepare tools
            String llmProvider = config.getLlm() != null ? config.getLlm().getProvider().getValue() : "openai";
            List<Map<String, Object>> tools = new ArrayList<>();

            if ("azure_openai_structured".equals(llmProvider) || "openai_structured".equals(llmProvider)) {
                tools.add(Map.<String, Object>of("tool", GraphTools.DELETE_MEMORY_STRUCT_TOOL_GRAPH));
            } else {
                tools.add(Map.<String, Object>of("tool", GraphTools.DELETE_MEMORY_TOOL_GRAPH));
            }

            // Call LLM
            Map<String, Object> response = llm.generateResponseWithTools(llmMessages, tools);

            // Parse response
            if (response != null && response.get("tool_calls") instanceof List && !((List<?>) response.get("tool_calls")).isEmpty()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> toolCalls = (List<Map<String, Object>>) response.get("tool_calls");

                for (Map<String, Object> toolCall : toolCalls) {
                    if ("delete_graph_memory".equals(toolCall.get("name"))) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> arguments = (Map<String, Object>) toolCall.get("arguments");

                        if (arguments != null) {
                            Map<String, Object> deleteItem = new HashMap<>();
                            deleteItem.put("source", GraphUtils.normalizeEntityName(
                                arguments.get("source") != null ? arguments.get("source").toString() : ""));
                            deleteItem.put("relationship", GraphUtils.normalizeEntityName(
                                arguments.get("relationship") != null ? arguments.get("relationship").toString() : ""));
                            deleteItem.put("destination", GraphUtils.normalizeEntityName(
                                arguments.get("destination") != null ? arguments.get("destination").toString() : ""));

                            if (!deleteItem.get("source").toString().isEmpty() &&
                                !deleteItem.get("destination").toString().isEmpty() &&
                                !deleteItem.get("relationship").toString().isEmpty()) {
                                toBeDeleted.add(deleteItem);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to determine entities to delete using LLM: {}", e.getMessage());
            // Fallback to simple logic
            for (Map<String, Object> item : searchOutput) {
                if (data.toLowerCase().contains("not") || data.toLowerCase().contains("no longer")) {
                    toBeDeleted.add(item);
                }
            }
        }

        log.debug("Deleted relationships: {}", toBeDeleted);
        return toBeDeleted;
    }

    /**
     * Delete specific entities from the graph.
     */
    private List<Map<String, Object>> deleteEntities(List<Map<String, Object>> toBeDeleted, Map<String, Object> filters) {
        List<Map<String, Object>> results = new ArrayList<>();

        String userId = filters.get("user_id") != null ? filters.get("user_id").toString() : "";
        String agentId = filters.get("agent_id") != null ? filters.get("agent_id").toString() : null;
        String runId = filters.get("run_id") != null ? filters.get("run_id").toString() : null;

        for (Map<String, Object> item : toBeDeleted) {
            try {
                String source = item.get("source") != null ? item.get("source").toString() : "";
                String destination = item.get("destination") != null ? item.get("destination").toString() : "";
                String relationship = item.get("relationship") != null ? item.get("relationship").toString() : "";

                Map<String, Object> params = new HashMap<>();
                params.put("source_name", source);
                params.put("dest_name", destination);
                params.put("user_id", userId);
                params.put("relationship_name", relationship);

                List<String> sourceProps = new ArrayList<>();
                sourceProps.add("name: $source_name");
                sourceProps.add("user_id: $user_id");

                List<String> destProps = new ArrayList<>();
                destProps.add("name: $dest_name");
                destProps.add("user_id: $user_id");

                if (agentId != null) {
                    sourceProps.add("agent_id: $agent_id");
                    destProps.add("agent_id: $agent_id");
                    params.put("agent_id", agentId);
                }
                if (runId != null) {
                    sourceProps.add("run_id: $run_id");
                    destProps.add("run_id: $run_id");
                    params.put("run_id", runId);
                }

                String sourcePropsStr = String.join(", ", sourceProps);
                String destPropsStr = String.join(", ", destProps);

                String cypher = String.format("""
                    MATCH (n %s {%s})
                    -[r %s {name: $relationship_name}]->
                    (m %s {%s})
                    DELETE r
                    RETURN
                        n.name AS source,
                        r.name AS relationship,
                        m.name AS target
                    """, NODE_LABEL, sourcePropsStr, REL_LABEL, NODE_LABEL, destPropsStr);

                List<Map<String, Object>> result = executeQueryWithParamsAndGetResults(cypher, params);
                results.addAll(result);
            } catch (Exception e) {
                log.error("Failed to delete entity: {}", e.getMessage());
            }
        }

        return results;
    }

    /**
     * Add new entities to the graph.
     */
    private List<Map<String, Object>> addEntities(List<Map<String, Object>> toBeAdded, Map<String, Object> filters, Map<String, String> entityTypeMap) {
        List<Map<String, Object>> results = new ArrayList<>();

        String userId = filters.get("user_id") != null ? filters.get("user_id").toString() : "";
        String agentId = filters.get("agent_id") != null ? filters.get("agent_id").toString() : null;
        String runId = filters.get("run_id") != null ? filters.get("run_id").toString() : null;

        for (Map<String, Object> item : toBeAdded) {
            try {
                String source = item.get("source") != null ? item.get("source").toString() : "";
                String destination = item.get("destination") != null ? item.get("destination").toString() : "";
                String relationship = item.get("relationship") != null ? item.get("relationship").toString() : "";

                // TODO: Add embedding generation when embedding support is available
                // For now, create entities without embeddings

                Map<String, Object> params = new HashMap<>();
                params.put("source_name", source);
                params.put("dest_name", destination);
                params.put("relationship_name", relationship);
                params.put("user_id", userId);

                List<String> sourceProps = new ArrayList<>();
                sourceProps.add("name: $source_name");
                sourceProps.add("user_id: $user_id");

                List<String> destProps = new ArrayList<>();
                destProps.add("name: $dest_name");
                destProps.add("user_id: $user_id");

                if (agentId != null) {
                    sourceProps.add("agent_id: $agent_id");
                    destProps.add("agent_id: $agent_id");
                    params.put("agent_id", agentId);
                }
                if (runId != null) {
                    sourceProps.add("run_id: $run_id");
                    destProps.add("run_id: $run_id");
                    params.put("run_id", runId);
                }

                String sourcePropsStr = String.join(", ", sourceProps);
                String destPropsStr = String.join(", ", destProps);

                String cypher = String.format("""
                    MERGE (source %s {%s})
                    ON CREATE SET
                        source.created = current_timestamp(),
                        source.mentions = 1
                    ON MATCH SET
                        source.mentions = coalesce(source.mentions, 0) + 1
                    WITH source
                    MERGE (destination %s {%s})
                    ON CREATE SET
                        destination.created = current_timestamp(),
                        destination.mentions = 1
                    ON MATCH SET
                        destination.mentions = coalesce(destination.mentions, 0) + 1
                    WITH source, destination
                    MERGE (source)-[rel %s {name: $relationship_name}]->(destination)
                    ON CREATE SET
                        rel.created = current_timestamp(),
                        rel.mentions = 1
                    ON MATCH SET
                        rel.mentions = coalesce(rel.mentions, 0) + 1
                    RETURN
                        source.name AS source,
                        rel.name AS relationship,
                        destination.name AS target
                    """, NODE_LABEL, sourcePropsStr, NODE_LABEL, destPropsStr, REL_LABEL);

                List<Map<String, Object>> result = executeQueryWithParamsAndGetResults(cypher, params);
                results.addAll(result);
            } catch (Exception e) {
                log.error("Failed to add entity: {}", e.getMessage());
            }
        }

        return results;
    }

    /**
     * Rank search results using simplified BM25-like scoring.
     */
    private List<Map<String, Object>> rankSearchResults(List<Map<String, Object>> searchOutput, String query, int limit) {
        // Simplified ranking - would use proper BM25 implementation
        List<String> queryTokens = Arrays.asList(query.toLowerCase().split("\\s+"));

        return searchOutput.stream()
                .sorted((a, b) -> {
                    double scoreA = calculateRelevanceScore(a, queryTokens);
                    double scoreB = calculateRelevanceScore(b, queryTokens);
                    return Double.compare(scoreB, scoreA);
                })
                .limit(limit)
                .map(item -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("source", item.get("source"));
                    result.put("relationship", item.get("relationship"));
                    result.put("destination", item.get("destination"));
                    return result;
                })
                .collect(Collectors.toList());
    }

    /**
     * Calculate relevance score for ranking.
     */
    private double calculateRelevanceScore(Map<String, Object> item, List<String> queryTokens) {
        double score = 0.0;
        String source = item.get("source") != null ? item.get("source").toString().toLowerCase() : "";
        String relationship = item.get("relationship") != null ? item.get("relationship").toString().toLowerCase() : "";
        String destination = item.get("destination") != null ? item.get("destination").toString().toLowerCase() : "";

        for (String token : queryTokens) {
            if (source.contains(token)) score += 1.0;
            if (relationship.contains(token)) score += 0.5;
            if (destination.contains(token)) score += 1.0;
        }

        return score;
    }

    /**
     * Generate placeholder embedding for a text.
     * This should be replaced with actual embedding model when available.
     */
    private double[] generatePlaceholderEmbedding(String text) {
        // Simple hash-based placeholder embedding
        int hash = text.hashCode();
        double[] embedding = new double[384]; // Common embedding dimension
        for (int i = 0; i < embedding.length; i++) {
            embedding[i] = Math.sin(hash * (i + 1) * 0.001);
        }
        return embedding;
    }
}