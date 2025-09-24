package run.mone.neo4j;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.*;
import run.mone.neo4j.embedding.EmbeddingBase;
import run.mone.neo4j.embedding.config.EmbedderConfig;
import run.mone.neo4j.embedding.impl.OllamaEmbedding;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * 顶点保存器 - 将List<Map>数据保存为Neo4j顶点
 */
@Slf4j
@Data
public class VertexSaver {

    private String NEO4J_URI = "bolt://localhost:7687";
    private String NEO4J_USER = "neo4j";
    private String password;
    private EmbeddingBase embeddingModel;

    public VertexSaver() {
        // 从环境变量读取密码
        this.password = System.getenv("NEO4J_PASSWORD");
        if (this.password == null || this.password.isEmpty()) {
            log.warn("NEO4J_PASSWORD environment variable is not set or empty");
            this.password = "";
        }
    }

    public VertexSaver setPassword(String password) {
        this.password = password;
        return this;
    }

    public VertexSaver setNeo4jUri(String uri) {
        this.NEO4J_URI = uri;
        return this;
    }

    public VertexSaver setNeo4jUser(String user) {
        this.NEO4J_USER = user;
        return this;
    }

    public VertexSaver setEmbeddingModel(EmbeddingBase embeddingModel) {
        this.embeddingModel = embeddingModel;
        return this;
    }

    /**
     * 使用默认Ollama嵌入模型初始化
     */
    public VertexSaver initDefaultEmbedding() {
        EmbedderConfig config = new EmbedderConfig();
        config.setModel("embeddinggemma");
        config.setBaseUrl("http://localhost:11434");
        this.embeddingModel = new OllamaEmbedding(config);
        return this;
    }

    /**
     * 获取Neo4j会话
     * @return Neo4j Session
     */
    public Session getSession() {
        try {
            Driver driver = GraphDatabase.driver(NEO4J_URI, AuthTokens.basic(NEO4J_USER, password));
            return driver.session();
        } catch (Exception e) {
            log.error("Failed to create Neo4j session. URI: {}, User: {}", NEO4J_URI, NEO4J_USER, e);
            throw new RuntimeException("Failed to connect to Neo4j database", e);
        }
    }
    
    /**
     * 测试数据库连接
     * @return 连接是否成功
     */
    public boolean testConnection() {
        try (Driver driver = GraphDatabase.driver(NEO4J_URI, AuthTokens.basic(NEO4J_USER, password));
             Session session = driver.session()) {
            
            session.run("RETURN 1 as test");
            log.info("Neo4j connection test successful");
            return true;
        } catch (Exception e) {
            log.error("Neo4j connection test failed", e);
            return false;
        }
    }

    /**
     * 保存顶点列表到Neo4j
     * @param vertices 顶点数据列表，每个Map必须包含name字段作为顶点名称
     * @param label 顶点标签，默认为"Vertex"
     */
    public void saveVertices(List<Map<String, Object>> vertices, String label) {
        if (vertices == null || vertices.isEmpty()) {
            log.warn("Vertices list is null or empty, nothing to save");
            return;
        }

        if (label == null || label.isEmpty()) {
            label = "Vertex";
        }

        try (Driver driver = GraphDatabase.driver(NEO4J_URI, AuthTokens.basic(NEO4J_USER, password));
             Session session = driver.session()) {
            
            int savedCount = 0;
            for (Map<String, Object> vertex : vertices) {
                if (vertex == null) {
                    log.warn("Skipping null vertex");
                    continue;
                }

                String name = (String) vertex.get("name");
                if (name == null || name.isEmpty()) {
                    log.warn("Skipping vertex without name: {}", vertex);
                    continue;
                }

                saveVertex(session, vertex, label);
                savedCount++;
                
                if (savedCount % 100 == 0) {
                    log.info("Saved {} vertices so far...", savedCount);
                }
            }
            
            log.info("Successfully saved {} vertices to Neo4j with label '{}'", savedCount, label);
        } catch (Exception e) {
            log.error("Error saving vertices to Neo4j", e);
            throw new RuntimeException("Failed to save vertices to Neo4j", e);
        }
    }

    /**
     * 保存顶点列表到Neo4j，使用默认标签"Vertex"
     * @param vertices 顶点数据列表
     */
    public void saveVertices(List<Map<String, Object>> vertices) {
        saveVertices(vertices, "Vertex");
    }

    /**
     * 保存单个顶点到Neo4j
     * @param session Neo4j会话
     * @param vertex 顶点数据
     * @param label 顶点标签
     */
    private void saveVertex(Session session, Map<String, Object> vertex, String label) {
        try {
            Map<String, Object> params = new HashMap<>(vertex);
            
            // 构建Cypher查询语句
            StringBuilder cypher = new StringBuilder();
            cypher.append("MERGE (v:").append(label).append(" {name: $name}) ");
            
            // 构建属性设置语句
            StringBuilder setClause = new StringBuilder();
            boolean first = true;
            for (String key : vertex.keySet()) {
                if (!first) {
                    setClause.append(", ");
                }
                setClause.append("v.").append(key).append(" = $").append(key);
                first = false;
            }
            
            // 添加ON CREATE SET和ON MATCH SET子句
            if (setClause.length() > 0) {
                cypher.append("ON CREATE SET ").append(setClause.toString()).append(" ");
                cypher.append("ON MATCH SET ").append(setClause.toString());
            }

            session.run(cypher.toString(), params);
            log.debug("Saved vertex: {}", vertex.get("name"));
            
        } catch (Exception e) {
            log.error("Error saving vertex: {}", vertex, e);
            throw new RuntimeException("Failed to save vertex: " + vertex.get("name"), e);
        }
    }

    /**
     * 删除所有指定标签的顶点
     * @param label 顶点标签
     */
    public void deleteVerticesByLabel(String label) {
        if (label == null || label.isEmpty()) {
            label = "Vertex";
        }

        try (Driver driver = GraphDatabase.driver(NEO4J_URI, AuthTokens.basic(NEO4J_USER, password));
             Session session = driver.session()) {
            
            String cypher = "MATCH (v:" + label + ") DETACH DELETE v";
            Result result = session.run(cypher);
            
            log.info("Deleted all vertices with label '{}'", label);
        } catch (Exception e) {
            log.error("Error deleting vertices with label: {}", label, e);
            throw new RuntimeException("Failed to delete vertices with label: " + label, e);
        }
    }

    /**
     * 删除所有顶点
     */
    public void deleteAllVertices() {
        try (Driver driver = GraphDatabase.driver(NEO4J_URI, AuthTokens.basic(NEO4J_USER, password));
             Session session = driver.session()) {
            
            session.run("MATCH (n) DETACH DELETE n");
            log.info("Deleted all vertices from Neo4j");
        } catch (Exception e) {
            log.error("Error deleting all vertices", e);
            throw new RuntimeException("Failed to delete all vertices", e);
        }
    }

    /**
     * 查询指定标签的所有顶点
     * @param label 顶点标签
     * @return 顶点列表
     */
    public List<Map<String, Object>> getVerticesByLabel(String label) {
        if (label == null || label.isEmpty()) {
            label = "Vertex";
        }

        try (Driver driver = GraphDatabase.driver(NEO4J_URI, AuthTokens.basic(NEO4J_USER, password));
             Session session = driver.session()) {
            
            String cypher = "MATCH (v:" + label + ") RETURN v";
            Result result = session.run(cypher);
            
            return result.list(record -> record.get("v").asMap());
        } catch (Exception e) {
            log.error("Error querying vertices with label: {}", label, e);
            throw new RuntimeException("Failed to query vertices with label: " + label, e);
        }
    }

    /**
     * 根据名称查询顶点
     * @param name 顶点名称
     * @param label 顶点标签
     * @return 顶点数据，如果不存在返回null
     */
    public Map<String, Object> getVertexByName(String name, String label) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        
        if (label == null || label.isEmpty()) {
            label = "Vertex";
        }

        try (Driver driver = GraphDatabase.driver(NEO4J_URI, AuthTokens.basic(NEO4J_USER, password));
             Session session = driver.session()) {
            
            String cypher = "MATCH (v:" + label + " {name: $name}) RETURN v";
            Map<String, Object> params = new HashMap<>();
            params.put("name", name);
            
            Result result = session.run(cypher, params);
            
            if (result.hasNext()) {
                return result.next().get("v").asMap();
            }
            return null;
        } catch (Exception e) {
            log.error("Error querying vertex by name: {} with label: {}", name, label, e);
            throw new RuntimeException("Failed to query vertex by name: " + name, e);
        }
    }

    /**
     * 为指定顶点的指定属性计算嵌入向量并存储
     * 
     * @param vertexName 顶点名称
     * @param propertyName 要计算嵌入的属性名称
     * @param label 顶点标签，默认为"Vertex"
     * @param embeddingPropertyName 存储嵌入向量的属性名称，默认为"embedding"
     * @return 是否成功计算和存储嵌入向量
     */
    public boolean computeAndStoreEmbedding(String vertexName, String propertyName, String label, String embeddingPropertyName) {
        if (vertexName == null || vertexName.isEmpty()) {
            log.error("Vertex name cannot be null or empty");
            return false;
        }
        
        if (propertyName == null || propertyName.isEmpty()) {
            log.error("Property name cannot be null or empty");
            return false;
        }
        
        if (embeddingModel == null) {
            log.error("Embedding model is not initialized. Please call setEmbeddingModel() or initDefaultEmbedding() first");
            return false;
        }
        
        if (label == null || label.isEmpty()) {
            label = "Vertex";
        }
        
        if (embeddingPropertyName == null || embeddingPropertyName.isEmpty()) {
            embeddingPropertyName = "embedding";
        }
        
        try (Driver driver = GraphDatabase.driver(NEO4J_URI, AuthTokens.basic(NEO4J_USER, password));
             Session session = driver.session()) {
            
            // 1. 查询顶点并获取指定属性的值
            String queryProperty = String.format("MATCH (v:%s {name: $name}) RETURN v.%s as propertyValue", label, propertyName);
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("name", vertexName);
            
            Result result = session.run(queryProperty, queryParams);
            
            if (!result.hasNext()) {
                log.error("Vertex with name '{}' and label '{}' not found", vertexName, label);
                return false;
            }
            
            Object propertyValue = result.next().get("propertyValue").asObject();
            if (propertyValue == null) {
                log.error("Property '{}' not found or is null in vertex '{}'", propertyName, vertexName);
                return false;
            }
            
            String textToEmbed = propertyValue.toString();
            if (textToEmbed.isEmpty()) {
                log.warn("Property '{}' in vertex '{}' is empty, skipping embedding", propertyName, vertexName);
                return false;
            }
            
            log.info("Computing embedding for vertex '{}', property '{}' with text: '{}'", 
                    vertexName, propertyName, textToEmbed.length() > 100 ? 
                    textToEmbed.substring(0, 100) + "..." : textToEmbed);
            
            // 2. 计算嵌入向量
            List<Double> embedding = embeddingModel.embed(textToEmbed, "add");
            
            if (embedding == null || embedding.isEmpty()) {
                log.error("Failed to compute embedding for vertex '{}', property '{}'", vertexName, propertyName);
                return false;
            }
            
            log.info("Successfully computed embedding with {} dimensions for vertex '{}'", 
                    embedding.size(), vertexName);
            
            // 3. 更新顶点，存储嵌入向量和更新时间
            String updateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            String updateCypher = String.format(
                "MATCH (v:%s {name: $name}) " +
                "SET v.%s = $embedding, v.embeddingUpdatedAt = $updateTime, v.embeddingSource = $source " +
                "RETURN v", 
                label, embeddingPropertyName);
            
            Map<String, Object> updateParams = new HashMap<>();
            updateParams.put("name", vertexName);
            updateParams.put("embedding", embedding);
            updateParams.put("updateTime", updateTime);
            updateParams.put("source", propertyName);
            
            Result updateResult = session.run(updateCypher, updateParams);
            
            if (updateResult.hasNext()) {
                log.info("Successfully stored embedding for vertex '{}' in property '{}'. " +
                        "Embedding dimensions: {}, Updated at: {}", 
                        vertexName, embeddingPropertyName, embedding.size(), updateTime);
                return true;
            } else {
                log.error("Failed to update vertex '{}' with embedding", vertexName);
                return false;
            }
            
        } catch (Exception e) {
            log.error("Error computing and storing embedding for vertex '{}', property '{}'", 
                    vertexName, propertyName, e);
            return false;
        }
    }

    /**
     * 为指定顶点的指定属性计算嵌入向量并存储（使用默认参数）
     * 
     * @param vertexName 顶点名称
     * @param propertyName 要计算嵌入的属性名称
     * @return 是否成功计算和存储嵌入向量
     */
    public boolean computeAndStoreEmbedding(String vertexName, String propertyName) {
        return computeAndStoreEmbedding(vertexName, propertyName, "Vertex", "embedding");
    }

    /**
     * 为指定顶点的指定属性计算嵌入向量并存储（指定标签）
     * 
     * @param vertexName 顶点名称
     * @param propertyName 要计算嵌入的属性名称
     * @param label 顶点标签
     * @return 是否成功计算和存储嵌入向量
     */
    public boolean computeAndStoreEmbedding(String vertexName, String propertyName, String label) {
        return computeAndStoreEmbedding(vertexName, propertyName, label, "embedding");
    }

    /**
     * 批量为多个顶点的指定属性计算嵌入向量并存储
     * 
     * @param vertexNames 顶点名称列表
     * @param propertyName 要计算嵌入的属性名称
     * @param label 顶点标签
     * @param embeddingPropertyName 存储嵌入向量的属性名称
     * @return 成功处理的顶点数量
     */
    public int batchComputeAndStoreEmbedding(List<String> vertexNames, String propertyName, 
                                           String label, String embeddingPropertyName) {
        if (vertexNames == null || vertexNames.isEmpty()) {
            log.warn("Vertex names list is null or empty");
            return 0;
        }
        
        int successCount = 0;
        int totalCount = vertexNames.size();
        
        log.info("Starting batch embedding computation for {} vertices", totalCount);
        
        for (int i = 0; i < vertexNames.size(); i++) {
            String vertexName = vertexNames.get(i);
            
            if (computeAndStoreEmbedding(vertexName, propertyName, label, embeddingPropertyName)) {
                successCount++;
            }
            
            // 每处理10个顶点记录一次进度
            if ((i + 1) % 10 == 0) {
                log.info("Processed {}/{} vertices, {} successful", i + 1, totalCount, successCount);
            }
        }
        
        log.info("Batch embedding computation completed. {}/{} vertices processed successfully", 
                successCount, totalCount);
        
        return successCount;
    }

    /**
     * 批量为多个顶点的指定属性计算嵌入向量并存储（使用默认参数）
     * 
     * @param vertexNames 顶点名称列表
     * @param propertyName 要计算嵌入的属性名称
     * @return 成功处理的顶点数量
     */
    public int batchComputeAndStoreEmbedding(List<String> vertexNames, String propertyName) {
        return batchComputeAndStoreEmbedding(vertexNames, propertyName, "Vertex", "embedding");
    }

    /**
     * 查询顶点的嵌入向量
     * 
     * @param vertexName 顶点名称
     * @param label 顶点标签
     * @param embeddingPropertyName 嵌入向量属性名称
     * @return 嵌入向量，如果不存在返回null
     */
    @SuppressWarnings("unchecked")
    public List<Double> getVertexEmbedding(String vertexName, String label, String embeddingPropertyName) {
        if (vertexName == null || vertexName.isEmpty()) {
            return null;
        }
        
        if (label == null || label.isEmpty()) {
            label = "Vertex";
        }
        
        if (embeddingPropertyName == null || embeddingPropertyName.isEmpty()) {
            embeddingPropertyName = "embedding";
        }
        
        try (Driver driver = GraphDatabase.driver(NEO4J_URI, AuthTokens.basic(NEO4J_USER, password));
             Session session = driver.session()) {
            
            String cypher = String.format("MATCH (v:%s {name: $name}) RETURN v.%s as embedding, v.embeddingUpdatedAt as updatedAt", 
                                        label, embeddingPropertyName);
            Map<String, Object> params = new HashMap<>();
            params.put("name", vertexName);
            
            Result result = session.run(cypher, params);
            
            if (result.hasNext()) {
                var record = result.next();
                Object embeddingObj = record.get("embedding").asObject();
                Object updatedAtObj = record.get("updatedAt").asObject();
                
                if (embeddingObj != null) {
                    log.info("Found embedding for vertex '{}', updated at: {}", vertexName, updatedAtObj);
                    return (List<Double>) embeddingObj;
                }
            }
            
            log.info("No embedding found for vertex '{}' with label '{}'", vertexName, label);
            return null;
            
        } catch (Exception e) {
            log.error("Error querying embedding for vertex '{}' with label '{}'", vertexName, label, e);
            return null;
        }
    }

    /**
     * 查询顶点的嵌入向量（使用默认参数）
     * 
     * @param vertexName 顶点名称
     * @return 嵌入向量，如果不存在返回null
     */
    public List<Double> getVertexEmbedding(String vertexName) {
        return getVertexEmbedding(vertexName, "Vertex", "embedding");
    }

    /**
     * 向量查询结果类，包含顶点数据和相似度分数
     */
    @Data
    public static class VectorSearchResult {
        private Map<String, Object> vertex;
        private double score;
        private String name;

        public VectorSearchResult(Map<String, Object> vertex, double score) {
            this.vertex = vertex;
            this.score = score;
            this.name = vertex != null ? (String) vertex.get("name") : null;
        }
    }

    /**
     * 使用向量索引查询最相似的顶点
     * 
     * @param indexName 向量索引名称
     * @param queryVector 查询向量
     * @param topK 返回最相似的K个结果，默认5
     * @param label 顶点标签，用于过滤查询范围
     * @return 相似度查询结果列表，按分数降序排列
     */
    public List<VectorSearchResult> vectorSimilaritySearch(String indexName, List<Double> queryVector, 
                                                          int topK, String label) {
        if (indexName == null || indexName.isEmpty()) {
            log.error("Index name cannot be null or empty");
            return new ArrayList<>();
        }
        
        if (queryVector == null || queryVector.isEmpty()) {
            log.error("Query vector cannot be null or empty");
            return new ArrayList<>();
        }
        
        if (topK <= 0) {
            topK = 5;
        }
        
        if (label == null || label.isEmpty()) {
            label = "Document"; // 默认使用Document标签，根据你的索引定义
        }
        
        List<VectorSearchResult> results = new ArrayList<>();
        
        try (Driver driver = GraphDatabase.driver(NEO4J_URI, AuthTokens.basic(NEO4J_USER, password));
             Session session = driver.session()) {
            
            // 构建向量相似度查询的Cypher语句
            String cypher = String.format(
                "CALL db.index.vector.queryNodes('%s', %d, $queryVector) " +
                "YIELD node, score " +
                "WHERE node:%s " +
                "RETURN node, score " +
                "ORDER BY score DESC",
                indexName, topK, label
            );
            
            Map<String, Object> params = new HashMap<>();
            params.put("queryVector", queryVector);
            
            log.info("Executing vector similarity search with index '{}', topK={}, label='{}'", 
                    indexName, topK, label);
            log.debug("Query vector dimensions: {}", queryVector.size());
            
            Result result = session.run(cypher, params);
            
            while (result.hasNext()) {
                var record = result.next();
                var node = record.get("node").asMap();
                double score = record.get("score").asDouble();
                
                VectorSearchResult searchResult = new VectorSearchResult(node, score);
                results.add(searchResult);
                
                log.debug("Found similar vertex: name='{}', score={}", 
                         searchResult.getName(), score);
            }
            
            log.info("Vector similarity search completed. Found {} results", results.size());
            
        } catch (Exception e) {
            log.error("Error executing vector similarity search with index '{}', topK={}, label='{}'", 
                     indexName, topK, label, e);
            throw new RuntimeException("Failed to execute vector similarity search", e);
        }
        
        return results;
    }

    /**
     * 使用向量索引查询最相似的顶点（使用默认参数）
     * 
     * @param indexName 向量索引名称
     * @param queryVector 查询向量
     * @return 相似度查询结果列表，返回最相似的5个结果
     */
    public List<VectorSearchResult> vectorSimilaritySearch(String indexName, List<Double> queryVector) {
        return vectorSimilaritySearch(indexName, queryVector, 5, "Document");
    }

    /**
     * 使用向量索引查询最相似的顶点（指定topK）
     * 
     * @param indexName 向量索引名称
     * @param queryVector 查询向量
     * @param topK 返回最相似的K个结果
     * @return 相似度查询结果列表，按分数降序排列
     */
    public List<VectorSearchResult> vectorSimilaritySearch(String indexName, List<Double> queryVector, int topK) {
        return vectorSimilaritySearch(indexName, queryVector, topK, "Document");
    }

    /**
     * 根据文本内容计算嵌入向量并执行相似度查询
     * 
     * @param indexName 向量索引名称
     * @param queryText 查询文本
     * @param topK 返回最相似的K个结果
     * @param label 顶点标签
     * @return 相似度查询结果列表
     */
    public List<VectorSearchResult> vectorSimilaritySearchByText(String indexName, String queryText, 
                                                               int topK, String label) {
        if (embeddingModel == null) {
            log.error("Embedding model is not initialized. Please call setEmbeddingModel() or initDefaultEmbedding() first");
            return new ArrayList<>();
        }
        
        if (queryText == null || queryText.isEmpty()) {
            log.error("Query text cannot be null or empty");
            return new ArrayList<>();
        }
        
        try {
            log.info("Computing embedding for query text: '{}'", 
                    queryText.length() > 100 ? queryText.substring(0, 100) + "..." : queryText);
            
            // 计算查询文本的嵌入向量
            List<Double> queryVector = embeddingModel.embed(queryText, "search");
            
            if (queryVector == null || queryVector.isEmpty()) {
                log.error("Failed to compute embedding for query text");
                return new ArrayList<>();
            }
            
            log.info("Successfully computed query embedding with {} dimensions", queryVector.size());
            
            // 执行向量相似度查询
            return vectorSimilaritySearch(indexName, queryVector, topK, label);
            
        } catch (Exception e) {
            log.error("Error computing embedding and executing similarity search for text: '{}'", queryText, e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据文本内容计算嵌入向量并执行相似度查询（使用默认参数）
     * 
     * @param indexName 向量索引名称
     * @param queryText 查询文本
     * @return 相似度查询结果列表，返回最相似的5个结果
     */
    public List<VectorSearchResult> vectorSimilaritySearchByText(String indexName, String queryText) {
        return vectorSimilaritySearchByText(indexName, queryText, 5, "Document");
    }

    /**
     * 在两个顶点之间创建边
     * 
     * @param fromVertexName 起始顶点名称
     * @param toVertexName 目标顶点名称
     * @param relationshipType 关系类型
     * @param fromLabel 起始顶点标签，默认为"Vertex"
     * @param toLabel 目标顶点标签，默认为"Vertex"
     * @param properties 边的属性，可以为null
     * @return 是否成功创建边
     */
    public boolean createEdge(String fromVertexName, String toVertexName, String relationshipType,
                             String fromLabel, String toLabel, Map<String, Object> properties) {
        if (fromVertexName == null || fromVertexName.isEmpty()) {
            log.error("From vertex name cannot be null or empty");
            return false;
        }
        
        if (toVertexName == null || toVertexName.isEmpty()) {
            log.error("To vertex name cannot be null or empty");
            return false;
        }
        
        if (relationshipType == null || relationshipType.isEmpty()) {
            log.error("Relationship type cannot be null or empty");
            return false;
        }
        
        if (fromLabel == null || fromLabel.isEmpty()) {
            fromLabel = "Vertex";
        }
        
        if (toLabel == null || toLabel.isEmpty()) {
            toLabel = "Vertex";
        }
        
        try (Driver driver = GraphDatabase.driver(NEO4J_URI, AuthTokens.basic(NEO4J_USER, password));
             Session session = driver.session()) {
            
            // 构建Cypher查询语句
            StringBuilder cypher = new StringBuilder();
            cypher.append("MATCH (from:").append(fromLabel).append(" {name: $fromName}), ");
            cypher.append("(to:").append(toLabel).append(" {name: $toName}) ");
            cypher.append("MERGE (from)-[r:").append(relationshipType).append("]->(to) ");
            
            // 如果有属性，则设置属性
            if (properties != null && !properties.isEmpty()) {
                cypher.append("ON CREATE SET ");
                boolean first = true;
                for (String key : properties.keySet()) {
                    if (!first) {
                        cypher.append(", ");
                    }
                    cypher.append("r.").append(key).append(" = $").append(key);
                    first = false;
                }
                cypher.append(" ON MATCH SET ");
                first = true;
                for (String key : properties.keySet()) {
                    if (!first) {
                        cypher.append(", ");
                    }
                    cypher.append("r.").append(key).append(" = $").append(key);
                    first = false;
                }
            }
            
            cypher.append(" RETURN r");
            
            // 准备参数
            Map<String, Object> params = new HashMap<>();
            params.put("fromName", fromVertexName);
            params.put("toName", toVertexName);
            if (properties != null) {
                params.putAll(properties);
            }
            
            Result result = session.run(cypher.toString(), params);
            
            if (result.hasNext()) {
                log.info("Successfully created edge '{}' from vertex '{}' to vertex '{}'", 
                        relationshipType, fromVertexName, toVertexName);
                return true;
            } else {
                log.error("Failed to create edge. One or both vertices may not exist: '{}' -> '{}'", 
                         fromVertexName, toVertexName);
                return false;
            }
            
        } catch (Exception e) {
            log.error("Error creating edge '{}' from vertex '{}' to vertex '{}'", 
                     relationshipType, fromVertexName, toVertexName, e);
            return false;
        }
    }
    
    /**
     * 在两个顶点之间创建边（使用默认标签）
     * 
     * @param fromVertexName 起始顶点名称
     * @param toVertexName 目标顶点名称
     * @param relationshipType 关系类型
     * @return 是否成功创建边
     */
    public boolean createEdge(String fromVertexName, String toVertexName, String relationshipType) {
        return createEdge(fromVertexName, toVertexName, relationshipType, "Vertex", "Vertex", null);
    }
    
    /**
     * 在两个顶点之间创建边（指定边的属性）
     * 
     * @param fromVertexName 起始顶点名称
     * @param toVertexName 目标顶点名称
     * @param relationshipType 关系类型
     * @param properties 边的属性
     * @return 是否成功创建边
     */
    public boolean createEdge(String fromVertexName, String toVertexName, String relationshipType,
                             Map<String, Object> properties) {
        return createEdge(fromVertexName, toVertexName, relationshipType, "Vertex", "Vertex", properties);
    }

    /**
     * 查询一个顶点连接的所有相邻顶点（只查一层关系）
     * 
     * @param vertexName 顶点名称
     * @param label 顶点标签，默认为"Vertex"
     * @param direction 关系方向："outgoing"(出边)、"incoming"(入边)、"both"(双向)，默认为"both"
     * @param relationshipType 关系类型过滤，可以为null表示不过滤
     * @return 相邻顶点列表，每个元素包含顶点数据和关系信息
     */
    public List<Map<String, Object>> getNeighborVertices(String vertexName, String label, 
                                                         String direction, String relationshipType) {
        if (vertexName == null || vertexName.isEmpty()) {
            log.error("Vertex name cannot be null or empty");
            return new ArrayList<>();
        }
        
        if (label == null || label.isEmpty()) {
            label = "Vertex";
        }
        
        if (direction == null || direction.isEmpty()) {
            direction = "both";
        }
        
        List<Map<String, Object>> neighbors = new ArrayList<>();
        
        try (Driver driver = GraphDatabase.driver(NEO4J_URI, AuthTokens.basic(NEO4J_USER, password));
             Session session = driver.session()) {
            
            // 构建Cypher查询语句
            StringBuilder cypher = new StringBuilder();
            cypher.append("MATCH (v:").append(label).append(" {name: $vertexName})");
            
            // 根据方向构建关系模式
            String relationPattern;
            if ("outgoing".equalsIgnoreCase(direction)) {
                relationPattern = relationshipType != null ? 
                    String.format("-[r:%s]->(neighbor)", relationshipType) : "-[r]->(neighbor)";
            } else if ("incoming".equalsIgnoreCase(direction)) {
                relationPattern = relationshipType != null ? 
                    String.format("<-[r:%s]-(neighbor)", relationshipType) : "<-[r]-(neighbor)";
            } else { // both
                relationPattern = relationshipType != null ? 
                    String.format("-[r:%s]-(neighbor)", relationshipType) : "-[r]-(neighbor)";
            }
            
            cypher.append(relationPattern);
            cypher.append(" RETURN neighbor, r, type(r) as relationshipType");
            
            Map<String, Object> params = new HashMap<>();
            params.put("vertexName", vertexName);
            
            log.info("Querying neighbors for vertex '{}' with direction '{}' and relationship type '{}'", 
                    vertexName, direction, relationshipType != null ? relationshipType : "any");
            
            Result result = session.run(cypher.toString(), params);
            
            while (result.hasNext()) {
                var record = result.next();
                Map<String, Object> neighbor = record.get("neighbor").asMap();
                Map<String, Object> relationship = record.get("r").asMap();
                String relType = record.get("relationshipType").asString();
                
                // 构建结果对象，包含邻居顶点和关系信息
                Map<String, Object> neighborInfo = new HashMap<>();
                neighborInfo.put("vertex", neighbor);
                neighborInfo.put("relationship", relationship);
                neighborInfo.put("relationshipType", relType);
                neighborInfo.put("neighborName", neighbor.get("name"));
                
                neighbors.add(neighborInfo);
                
                log.debug("Found neighbor: '{}' with relationship '{}'", 
                         neighbor.get("name"), relType);
            }
            
            log.info("Found {} neighbors for vertex '{}'", neighbors.size(), vertexName);
            
        } catch (Exception e) {
            log.error("Error querying neighbors for vertex '{}' with label '{}'", vertexName, label, e);
            throw new RuntimeException("Failed to query neighbor vertices", e);
        }
        
        return neighbors;
    }
    
    /**
     * 查询一个顶点连接的所有相邻顶点（使用默认参数）
     * 
     * @param vertexName 顶点名称
     * @return 相邻顶点列表
     */
    public List<Map<String, Object>> getNeighborVertices(String vertexName) {
        return getNeighborVertices(vertexName, "Vertex", "both", null);
    }
    
    /**
     * 查询一个顶点的出边相邻顶点
     * 
     * @param vertexName 顶点名称
     * @param label 顶点标签
     * @return 出边相邻顶点列表
     */
    public List<Map<String, Object>> getOutgoingNeighbors(String vertexName, String label) {
        return getNeighborVertices(vertexName, label, "outgoing", null);
    }
    
    /**
     * 查询一个顶点的入边相邻顶点
     * 
     * @param vertexName 顶点名称
     * @param label 顶点标签
     * @return 入边相邻顶点列表
     */
    public List<Map<String, Object>> getIncomingNeighbors(String vertexName, String label) {
        return getNeighborVertices(vertexName, label, "incoming", null);
    }
    
    /**
     * 查询指定关系类型的相邻顶点
     * 
     * @param vertexName 顶点名称
     * @param relationshipType 关系类型
     * @return 指定关系类型的相邻顶点列表
     */
    public List<Map<String, Object>> getNeighborsByRelationType(String vertexName, String relationshipType) {
        return getNeighborVertices(vertexName, "Vertex", "both", relationshipType);
    }
}
