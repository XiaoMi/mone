package run.mone.hive.memory.longterm.graph.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.Data;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import run.mone.hive.memory.longterm.config.GraphStoreConfig;
import run.mone.hive.memory.longterm.graph.GraphStoreBase;
import run.mone.hive.memory.longterm.graph.GraphStoreBase.GraphEntity;
import run.mone.hive.memory.longterm.model.MemoryItem;
import run.mone.hive.memory.longterm.model.Message;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 本地图存储实现
 * 基于内存和文件系统的图数据存储
 * 
 * 特性：
 * - 实体和关系的存储与查询
 * - JSON格式持久化
 * - 图遍历和路径查找
 * - 实体关系分析
 */
@Slf4j
@Data
public class LocalGraphStore implements GraphStoreBase {
    
    private final GraphStoreConfig config;
    private final Gson gson;
    private final Path dataDir;
    private final Path entitiesFile;
    private final Path relationsFile;
    
    // 内存数据结构
    private final Map<String, Entity> entities = new ConcurrentHashMap<>();
    private final Map<String, Relation> relations = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> entityToRelations = new ConcurrentHashMap<>();
    
    public LocalGraphStore(GraphStoreConfig config) {
        this.config = config;
        this.gson = new Gson();
        
        // 创建存储目录
        String baseDir = config.getUrl() != null ? config.getUrl() : "./data/graph";
        this.dataDir = Paths.get(baseDir);
        this.entitiesFile = dataDir.resolve("entities.json");
        this.relationsFile = dataDir.resolve("relations.json");
        
        initializeStorage();
        loadFromDisk();
        
        log.info("Local graph store initialized at: {}", dataDir.toAbsolutePath());
    }
    
    private void initializeStorage() {
        try {
            Files.createDirectories(dataDir);
            
            if (!Files.exists(entitiesFile)) {
                Files.write(entitiesFile, "{}".getBytes());
            }
            if (!Files.exists(relationsFile)) {
                Files.write(relationsFile, "{}".getBytes());
            }
            
        } catch (IOException e) {
            log.error("Failed to initialize local graph storage", e);
            throw new RuntimeException("Failed to initialize local graph storage", e);
        }
    }
    
    private void loadFromDisk() {
        try {
            // 加载实体数据
            String entitiesJson = Files.readString(entitiesFile);
            Map<String, Entity> entitiesData = gson.fromJson(entitiesJson,
                new TypeToken<Map<String, Entity>>(){}.getType());
            if (entitiesData != null) {
                entities.putAll(entitiesData);
            }
            
            // 加载关系数据
            String relationsJson = Files.readString(relationsFile);
            Map<String, Relation> relationsData = gson.fromJson(relationsJson,
                new TypeToken<Map<String, Relation>>(){}.getType());
            if (relationsData != null) {
                relations.putAll(relationsData);
                
                // 重建实体到关系的映射
                for (Relation relation : relations.values()) {
                    entityToRelations.computeIfAbsent(relation.sourceId, k -> new HashSet<>())
                        .add(relation.id);
                    entityToRelations.computeIfAbsent(relation.targetId, k -> new HashSet<>())
                        .add(relation.id);
                }
            }
            
            log.info("Loaded {} entities and {} relations from disk", entities.size(), relations.size());
            
        } catch (IOException e) {
            log.error("Failed to load graph data from disk", e);
            throw new RuntimeException("Failed to load graph data", e);
        }
    }
    
    private synchronized void saveToDisk() {
        try {
            // 保存实体数据
            String entitiesJson = gson.toJson(entities);
            Files.write(entitiesFile, entitiesJson.getBytes());
            
            // 保存关系数据
            String relationsJson = gson.toJson(relations);
            Files.write(relationsFile, relationsJson.getBytes());
            
        } catch (IOException e) {
            log.error("Failed to save graph data to disk", e);
            throw new RuntimeException("Failed to save graph data", e);
        }
    }
    
    @Override
    public Map<String, Object> addMemory(String source, String destination, String relationship, 
                                        String sourceType, String destinationType) {
        try {
            // 创建或更新源实体
            String sourceEntityId = findOrCreateEntity(source, sourceType);
            
            // 创建或更新目标实体
            String targetEntityId = findOrCreateEntity(destination, destinationType);
            
            // 创建关系
            String relationId = generateRelationId();
            Relation relation = new Relation(relationId, relationship,
                sourceEntityId, targetEntityId, getCurrentTimestamp(), new HashMap<>());
            
            relations.put(relationId, relation);
            
            // 更新实体到关系的映射
            entityToRelations.computeIfAbsent(sourceEntityId, k -> new HashSet<>()).add(relationId);
            entityToRelations.computeIfAbsent(targetEntityId, k -> new HashSet<>()).add(relationId);
            
            saveToDisk();
            
            Map<String, Object> result = new HashMap<>();
            result.put("message", "Memory added successfully");
            result.put("source", source);
            result.put("destination", destination);
            result.put("relationship", relationship);
            result.put("relation_id", relationId);
            return result;
            
        } catch (Exception e) {
            log.error("Error adding memory: {} -> {} ({})", source, destination, relationship, e);
            throw new RuntimeException("Failed to add memory", e);
        }
    }
    
    public void addMemories(List<Map<String, Object>> memories, Map<String, Object> filters) {
        try {
            for (Map<String, Object> memory : memories) {
                // 提取实体信息
                String entityId = generateEntityId();
                String entityName = (String) memory.get("name");
                String entityType = (String) memory.get("type");
                String userId = (String) (filters != null ? filters.get("user_id") : null);
                
                if (entityName == null || entityName.trim().isEmpty()) {
                    continue;
                }
                
                Entity entity = new Entity(entityId, entityName, entityType, userId, 
                    getCurrentTimestamp(), memory);
                entities.put(entityId, entity);
                
                log.debug("Added entity: {} ({})", entityName, entityType);
            }
            
            saveToDisk();
            log.info("Added {} entities to graph", memories.size());
            
        } catch (Exception e) {
            log.error("Error adding memories to graph", e);
            throw new RuntimeException("Failed to add memories to graph", e);
        }
    }
    
    @Override
    public Map<String, Object> updateMemory(String source, String destination, String relationship) {
        try {
            // 查找现有关系并更新
            for (Relation relation : relations.values()) {
                if (relation.sourceId.equals(source) && relation.targetId.equals(destination)) {
                    relation.type = relationship;
                    relation.properties.put("updated_at", getCurrentTimestamp());
                    saveToDisk();
                    
                    Map<String, Object> result = new HashMap<>();
                    result.put("message", "Memory updated successfully");
                    result.put("source", source);
                    result.put("destination", destination);
                    result.put("relationship", relationship);
                    return result;
                }
            }
            
            throw new IllegalArgumentException("Relationship not found: " + source + " -> " + destination);
            
        } catch (Exception e) {
            log.error("Error updating memory: {} -> {}", source, destination, e);
            throw new RuntimeException("Failed to update memory", e);
        }
    }
    
    @Override
    public Map<String, Object> deleteMemory(String source, String destination, String relationship) {
        try {
            String removedRelationId = null;
            
            // 查找并删除匹配的关系
            for (Map.Entry<String, Relation> entry : relations.entrySet()) {
                Relation relation = entry.getValue();
                if (relation.sourceId.equals(source) && 
                    relation.targetId.equals(destination) && 
                    relation.type.equals(relationship)) {
                    
                    removedRelationId = entry.getKey();
                    relations.remove(removedRelationId);
                    final String rid = removedRelationId;
                    
                    // 从实体映射中删除关系引用
                    entityToRelations.computeIfPresent(source, (k, v) -> {
                        v.remove(rid);
                        return v.isEmpty() ? null : v;
                    });
                    entityToRelations.computeIfPresent(destination, (k, v) -> {
                        v.remove(rid);
                        return v.isEmpty() ? null : v;
                    });
                    
                    break;
                }
            }
            
            saveToDisk();
            
            Map<String, Object> result = new HashMap<>();
            result.put("message", removedRelationId != null ? "Memory deleted successfully" : "Memory not found");
            result.put("source", source);
            result.put("destination", destination);
            result.put("relationship", relationship);
            return result;
            
        } catch (Exception e) {
            log.error("Error deleting memory: {} -> {} ({})", source, destination, relationship, e);
            throw new RuntimeException("Failed to delete memory", e);
        }
    }
    
    @Override
    public List<Map<String, Object>> search(String query, int limit) {
        try {
            List<Entity> matchingEntities = entities.values().stream()
                .filter(entity -> entity.name.toLowerCase().contains(query.toLowerCase()))
                .limit(limit)
                .collect(Collectors.toList());
            
            List<Map<String, Object>> results = new ArrayList<>();
            for (Entity entity : matchingEntities) {
                Map<String, Object> entityMap = new HashMap<>();
                entityMap.put("id", entity.id);
                entityMap.put("name", entity.name);
                entityMap.put("type", entity.type);
                entityMap.put("user_id", entity.userId);
                entityMap.put("created_at", entity.createdAt);
                entityMap.put("updated_at", entity.updatedAt);
                entityMap.putAll(entity.properties);
                results.add(entityMap);
            }
            
            return results;
                
        } catch (Exception e) {
            log.error("Error searching graph", e);
            throw new RuntimeException("Failed to search graph", e);
        }
    }
    
    @Override
    public List<Map<String, Object>> getAll(int limit) {
        try {
            List<Entity> limitedEntities = entities.values().stream()
                .limit(limit)
                .collect(Collectors.toList());
            
            List<Map<String, Object>> results = new ArrayList<>();
            for (Entity entity : limitedEntities) {
                Map<String, Object> entityMap = new HashMap<>();
                entityMap.put("id", entity.id);
                entityMap.put("name", entity.name);
                entityMap.put("type", entity.type);
                entityMap.put("user_id", entity.userId);
                entityMap.put("created_at", entity.createdAt);
                entityMap.put("updated_at", entity.updatedAt);
                entityMap.putAll(entity.properties);
                results.add(entityMap);
            }
            
            return results;
                
        } catch (Exception e) {
            log.error("Error getting all memories", e);
            throw new RuntimeException("Failed to get all memories", e);
        }
    }
    
    @Override
    public List<Map<String, Object>> getNodeRelationships(String nodeId) {
        try {
            List<Map<String, Object>> relationships = new ArrayList<>();
            Set<String> relationIds = entityToRelations.get(nodeId);
            
            if (relationIds != null) {
                for (String relationId : relationIds) {
                    Relation relation = relations.get(relationId);
                    if (relation != null) {
                        Map<String, Object> relationData = new HashMap<>();
                        relationData.put("id", relation.id);
                        relationData.put("type", relation.type);
                        relationData.put("source", relation.sourceId);
                        relationData.put("target", relation.targetId);
                        relationData.put("properties", relation.properties);
                        relationships.add(relationData);
                    }
                }
            }
            
            return relationships;
            
        } catch (Exception e) {
            log.error("Error getting node relationships for: {}", nodeId, e);
            throw new RuntimeException("Failed to get node relationships", e);
        }
    }
    
    @Override
    public List<GraphEntity> establishRelations(String text) {
        try {
            List<GraphEntity> graphEntities = new ArrayList<>();
            
            // 简单的文本实体关系提取（实际应用中应使用NLP）
            // 这里实现基本的关键词提取和关系建立
            String[] sentences = text.split("[.!?]");
            
            for (String sentence : sentences) {
                if (sentence.trim().isEmpty()) continue;
                
                // 简单的实体提取逻辑
                String[] words = sentence.trim().split("\\s+");
                List<String> entities = new ArrayList<>();
                
                for (String word : words) {
                    if (word.length() > 2 && Character.isUpperCase(word.charAt(0))) {
                        entities.add(word);
                    }
                }
                
                // 建立实体间的关系
                for (int i = 0; i < entities.size() - 1; i++) {
                    String source = entities.get(i);
                    String target = entities.get(i + 1);
                    
                    GraphEntity entity = new GraphEntity(source, target, "related_to", "ENTITY", "ENTITY");
                    graphEntities.add(entity);
                    
                    // 存储关系到本地
                    String relationId = generateRelationId();
                    Relation relation = new Relation(relationId, "related_to",
                        source, target, getCurrentTimestamp(), new HashMap<>());
                    
                    relations.put(relationId, relation);
                    
                    // 更新实体到关系的映射
                    entityToRelations.computeIfAbsent(source, k -> new HashSet<>()).add(relationId);
                    entityToRelations.computeIfAbsent(target, k -> new HashSet<>()).add(relationId);
                }
            }
            
            saveToDisk();
            log.info("Established {} relations from text", graphEntities.size());
            return graphEntities;
            
        } catch (Exception e) {
            log.error("Error establishing relations", e);
            throw new RuntimeException("Failed to establish relations", e);
        }
    }
    
    @Override
    public List<Map<String, Object>> extractEntities(String text) {
        try {
            List<Map<String, Object>> extractedEntities = new ArrayList<>();
            
            if (text == null || text.trim().isEmpty()) {
                return extractedEntities;
            }
            
            // 简单的实体提取逻辑（在实际应用中应该使用NLP库）
            String[] words = text.split("\\s+");
            for (String word : words) {
                if (word.length() > 2 && Character.isUpperCase(word.charAt(0))) {
                    Map<String, Object> entity = new HashMap<>();
                    entity.put("name", word);
                    entity.put("type", "UNKNOWN");
                    entity.put("source_text", text);
                    extractedEntities.add(entity);
                }
            }
            
            log.debug("Extracted {} entities from text", extractedEntities.size());
            return extractedEntities;
            
        } catch (Exception e) {
            log.error("Error extracting entities", e);
            throw new RuntimeException("Failed to extract entities", e);
        }
    }
    
    @Override
    public void deleteAll() {
        try {
            entities.clear();
            relations.clear();
            entityToRelations.clear();
            
            Files.write(entitiesFile, "{}".getBytes());
            Files.write(relationsFile, "{}".getBytes());
            
            log.info("Deleted all graph data");
            
        } catch (Exception e) {
            log.error("Error deleting all graph data", e);
            throw new RuntimeException("Failed to delete all graph data", e);
        }
    }
    
    @Override
    public boolean validateConnection() {
        try {
            return Files.exists(dataDir) && Files.isWritable(dataDir);
        } catch (Exception e) {
            log.error("Error validating connection", e);
            return false;
        }
    }
    
    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_entities", entities.size());
        stats.put("total_relations", relations.size());
        stats.put("storage_path", dataDir.toString());
        stats.put("enabled", config.isEnabled());
        return stats;
    }
    
    @Override
    public boolean relationshipExists(String sourceId, String targetId, String relationType) {
        return relations.values().stream()
            .anyMatch(relation -> 
                relation.sourceId.equals(sourceId) && 
                relation.targetId.equals(targetId) &&
                (relationType == null || relationType.equals(relation.type)));
    }
    
    // 工具方法
    
    private String generateEntityId() {
        return "entity_" + UUID.randomUUID().toString().replace("-", "");
    }
    
    private String generateRelationId() {
        return "relation_" + UUID.randomUUID().toString().replace("-", "");
    }
    
    private String findOrCreateEntity(String name, String type) {
        // 查找现有实体
        for (Entity entity : entities.values()) {
            if (entity.name.equals(name) && (type == null || entity.type.equals(type))) {
                return entity.id;
            }
        }
        
        // 创建新实体
        String entityId = generateEntityId();
        Entity entity = new Entity(entityId, name, type != null ? type : "UNKNOWN", 
            null, getCurrentTimestamp(), new HashMap<>());
        entities.put(entityId, entity);
        return entityId;
    }
    
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
    
    private LocalDateTime parseTimestamp(String timestamp) {
        try {
            if (timestamp == null || timestamp.isEmpty()) {
                return LocalDateTime.now();
            }
            return LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
    
    private MemoryItem convertToMemoryItem(Entity entity) {
        return MemoryItem.builder()
            .id(entity.id)
            .memory(entity.name)
            .userId(entity.userId)
            .score(1.0)
            .metadata(entity.properties)
            .createdAt(parseTimestamp(entity.createdAt))
            .updatedAt(parseTimestamp(entity.updatedAt))
            .build();
    }
    
    // 内部数据结构
    @Data
    public static class Entity {
        private String id;
        private String name;
        private String type;
        private String userId;
        private String createdAt;
        private String updatedAt;
        private Map<String, Object> properties;
        
        public Entity(String id, String name, String type, String userId, 
                     String timestamp, Map<String, Object> properties) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.userId = userId;
            this.createdAt = timestamp;
            this.updatedAt = timestamp;
            this.properties = new HashMap<>(properties != null ? properties : new HashMap<>());
        }
        
        // 默认构造函数（Gson需要）
        public Entity() {}
    }
    
    @Data
    public static class Relation {
        private String id;
        private String type;
        private String sourceId;
        private String targetId;
        private String createdAt;
        private Map<String, Object> properties;
        
        public Relation(String id, String type, String sourceId, String targetId,
                       String timestamp, Map<String, Object> properties) {
            this.id = id;
            this.type = type;
            this.sourceId = sourceId;
            this.targetId = targetId;
            this.createdAt = timestamp;
            this.properties = new HashMap<>(properties != null ? properties : new HashMap<>());
        }
        
        // 默认构造函数（Gson需要）
        public Relation() {}
    }
    
    @Override
    public void close() {
        log.info("Closing local graph store");
        saveToDisk();
    }
}
