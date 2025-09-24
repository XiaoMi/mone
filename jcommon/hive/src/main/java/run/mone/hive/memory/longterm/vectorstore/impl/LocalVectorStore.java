package run.mone.hive.memory.longterm.vectorstore.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.Data;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import run.mone.hive.memory.longterm.config.VectorStoreConfig;
import run.mone.hive.memory.longterm.vectorstore.VectorStoreBase;
import run.mone.hive.memory.longterm.model.MemoryItem;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 本地文件向量存储实现
 * 基于文件系统的向量存储，支持持久化和检索
 * 
 * 特性：
 * - 基于余弦相似度的向量搜索
 * - JSON格式持久化存储
 * - 支持元数据过滤
 * - 线程安全操作
 */
@Slf4j
@Data
public class LocalVectorStore implements VectorStoreBase {
    
    private final VectorStoreConfig config;
    private final Gson gson;
    private final Path dataDir;
    private final Path vectorFile;
    private final Path metadataFile;
    
    // 内存缓存，提高性能
    private final Map<String, List<Double>> vectors = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Object>> metadataCache = new ConcurrentHashMap<>();
    
    public LocalVectorStore(VectorStoreConfig config) {
        this.config = config;
        this.gson = new Gson();
        
        // 创建存储目录
        String baseDir = config.getPath() != null ? config.getPath() : "./data/memory";
        this.dataDir = Paths.get(baseDir, config.getCollectionName());
        this.vectorFile = dataDir.resolve("vectors.json");
        this.metadataFile = dataDir.resolve("metadata.json");
        
        initializeStorage();
        loadFromDisk();
        
        log.info("Local vector store initialized at: {}", dataDir.toAbsolutePath());
    }
    
    private void initializeStorage() {
        try {
            Files.createDirectories(dataDir);
            
            // 如果文件不存在则创建
            if (!Files.exists(vectorFile)) {
                Files.write(vectorFile, "{}".getBytes());
            }
            if (!Files.exists(metadataFile)) {
                Files.write(metadataFile, "{}".getBytes());
            }
            
        } catch (IOException e) {
            log.error("Failed to initialize local storage", e);
            throw new RuntimeException("Failed to initialize local vector storage", e);
        }
    }
    
    private void loadFromDisk() {
        try {
            // 加载向量数据
            String vectorJson = Files.readString(vectorFile);
            Map<String, List<Double>> vectorData = gson.fromJson(vectorJson, 
                new TypeToken<Map<String, List<Double>>>(){}.getType());
            if (vectorData != null) {
                vectors.putAll(vectorData);
            }
            
            // 加载元数据
            String metadataJson = Files.readString(metadataFile);
            Map<String, Map<String, Object>> metadataData = gson.fromJson(metadataJson,
                new TypeToken<Map<String, Map<String, Object>>>(){}.getType());
            if (metadataData != null) {
                metadataCache.putAll(metadataData);
            }
            
            log.info("Loaded {} vectors from disk", vectors.size());
            
        } catch (IOException e) {
            log.error("Failed to load data from disk", e);
            throw new RuntimeException("Failed to load vector data", e);
        }
    }
    
    private synchronized void saveToDisk() {
        try {
            // 保存向量数据
            String vectorJson = gson.toJson(vectors);
            Files.write(vectorFile, vectorJson.getBytes());
            
            // 保存元数据
            String metadataJson = gson.toJson(metadataCache);
            Files.write(metadataFile, metadataJson.getBytes());
            
        } catch (IOException e) {
            log.error("Failed to save data to disk", e);
            throw new RuntimeException("Failed to save vector data", e);
        }
    }
    
    @Override
    public void insert(List<List<Double>> vectors, List<String> ids, List<Map<String, Object>> payloads) {
        if (vectors.size() != ids.size() || vectors.size() != payloads.size()) {
            throw new IllegalArgumentException("Vectors, IDs, and payloads must have the same size");
        }
        
        try {
            for (int i = 0; i < vectors.size(); i++) {
                String id = ids.get(i);
                List<Double> vector = vectors.get(i);
                Map<String, Object> payload = payloads.get(i);
                
                // 存储向量和元数据
                this.vectors.put(id, new ArrayList<>(vector));
                this.metadataCache.put(id, new HashMap<>(payload));
                
                log.debug("Inserted vector with ID: {}", id);
            }
            
            // 保存到磁盘
            saveToDisk();
            log.info("Successfully inserted {} vectors", vectors.size());
            
        } catch (Exception e) {
            log.error("Error inserting vectors", e);
            throw new RuntimeException("Failed to insert vectors", e);
        }
    }
    
    @Override
    public List<MemoryItem> search(String query, List<Double> queryVector, int limit, Map<String, Object> filters) {
        try {
            log.debug("Searching for similar vectors with limit: {}", limit);
            
            List<VectorResult> results = new ArrayList<>();
            
            // 计算与所有向量的相似度
            for (Map.Entry<String, List<Double>> entry : vectors.entrySet()) {
                String id = entry.getKey();
                List<Double> vector = entry.getValue();
                Map<String, Object> metadata = metadataCache.get(id);
                
                // 应用过滤器
                if (!matchesFilters(metadata, filters)) {
                    continue;
                }
                
                // 计算余弦相似度
                double similarity = cosineSimilarity(queryVector, vector);
                results.add(new VectorResult(id, vector, metadata, similarity));
            }
            
            // 按相似度排序并限制结果数量
            List<MemoryItem> memoryItems = results.stream()
                .sorted((a, b) -> Double.compare(b.similarity, a.similarity))
                .limit(limit)
                .map(this::convertToMemoryItem)
                .collect(Collectors.toList());
            
            log.info("Found {} similar vectors", memoryItems.size());
            return memoryItems;
            
        } catch (Exception e) {
            log.error("Error searching vectors", e);
            throw new RuntimeException("Failed to search vectors", e);
        }
    }
    
    @Override
    public MemoryItem get(String id) {
        try {
            List<Double> vector = vectors.get(id);
            Map<String, Object> metadata = metadataCache.get(id);
            
            if (vector == null || metadata == null) {
                log.debug("Vector not found with ID: {}", id);
                return null;
            }
            
            VectorResult result = new VectorResult(id, vector, metadata, 1.0);
            return convertToMemoryItem(result);
            
        } catch (Exception e) {
            log.error("Error getting vector with ID: {}", id, e);
            throw new RuntimeException("Failed to get vector", e);
        }
    }
    
    @Override
    public List<MemoryItem> list(Map<String, Object> filters, int limit) {
        try {
            List<MemoryItem> results = new ArrayList<>();
            int count = 0;
            
            for (Map.Entry<String, List<Double>> entry : vectors.entrySet()) {
                if (count >= limit) break;
                
                String id = entry.getKey();
                List<Double> vector = entry.getValue();
                Map<String, Object> metadata = metadataCache.get(id);
                
                // 应用过滤器
                if (!matchesFilters(metadata, filters)) {
                    continue;
                }
                
                VectorResult result = new VectorResult(id, vector, metadata, 1.0);
                results.add(convertToMemoryItem(result));
                count++;
            }
            
            log.info("Listed {} vectors", results.size());
            return results;
            
        } catch (Exception e) {
            log.error("Error listing vectors", e);
            throw new RuntimeException("Failed to list vectors", e);
        }
    }
    
    @Override
    public void update(String id, List<Double> vector, Map<String, Object> payload) {
        try {
            if (!vectors.containsKey(id)) {
                throw new IllegalArgumentException("Vector with ID " + id + " does not exist");
            }
            
            // 更新向量和元数据
            vectors.put(id, new ArrayList<>(vector));
            metadataCache.put(id, new HashMap<>(payload));
            
            // 保存到磁盘
            saveToDisk();
            log.info("Successfully updated vector with ID: {}", id);
            
        } catch (Exception e) {
            log.error("Error updating vector with ID: {}", id, e);
            throw new RuntimeException("Failed to update vector", e);
        }
    }
    
    @Override
    public void delete(String id) {
        try {
            boolean removed = vectors.remove(id) != null;
            metadataCache.remove(id);
            
            if (removed) {
                // 保存到磁盘
                saveToDisk();
                log.info("Successfully deleted vector with ID: {}", id);
            } else {
                log.warn("Vector with ID {} not found for deletion", id);
            }
            
        } catch (Exception e) {
            log.error("Error deleting vector with ID: {}", id, e);
            throw new RuntimeException("Failed to delete vector", e);
        }
    }
    
    @Override
    public void deleteCol() {
        try {
            vectors.clear();
            metadataCache.clear();
            
            // 清空文件
            Files.write(vectorFile, "{}".getBytes());
            Files.write(metadataFile, "{}".getBytes());
            
            log.info("Successfully deleted collection: {}", config.getCollectionName());
            
        } catch (Exception e) {
            log.error("Error deleting collection", e);
            throw new RuntimeException("Failed to delete collection", e);
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
    public void reset() {
        deleteCol();
    }

    @Override
    public VectorStoreConfig getConfig() {
        return this.config;
    }
    
    // 工具方法
    
    private boolean matchesFilters(Map<String, Object> metadata, Map<String, Object> filters) {
        if (filters == null || filters.isEmpty()) {
            return true;
        }
        
        if (metadata == null) {
            return false;
        }
        
        for (Map.Entry<String, Object> filter : filters.entrySet()) {
            Object metadataValue = metadata.get(filter.getKey());
            Object filterValue = filter.getValue();
            
            if (!Objects.equals(metadataValue, filterValue)) {
                return false;
            }
        }
        
        return true;
    }
    
    private double cosineSimilarity(List<Double> vec1, List<Double> vec2) {
        if (vec1.size() != vec2.size()) {
            throw new IllegalArgumentException("Vectors must have the same dimension");
        }
        
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (int i = 0; i < vec1.size(); i++) {
            double v1 = vec1.get(i);
            double v2 = vec2.get(i);
            
            dotProduct += v1 * v2;
            norm1 += v1 * v1;
            norm2 += v2 * v2;
        }
        
        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
    
    private MemoryItem convertToMemoryItem(VectorResult result) {
        Map<String, Object> metadata = result.metadata;
        
        return MemoryItem.builder()
            .id(result.id)
            .memory((String) metadata.get("memory"))
            .userId((String) metadata.get("user_id"))
            .agentId((String) metadata.get("agent_id"))
            .runId((String) metadata.get("run_id"))
            .score(result.similarity)
            .metadata(metadata)
            .createdAt(parseTimestamp((String) metadata.get("created_at")))
            .updatedAt(parseTimestamp((String) metadata.get("updated_at")))
            .build();
    }
    
    // 内部类
    @Data
    private static class VectorResult {
        private final String id;
        private final List<Double> vector;
        private final Map<String, Object> metadata;
        private final double similarity;
    }
    
    // 获取统计信息
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_vectors", vectors.size());
        stats.put("collection_name", config.getCollectionName());
        stats.put("storage_path", dataDir.toString());
        stats.put("vector_dimension", vectors.isEmpty() ? 0 : vectors.values().iterator().next().size());
        return stats;
    }
    
    @Override
    public void close() {
        log.info("Closing local vector store");
        // 最后保存一次数据
        saveToDisk();
    }

    private java.time.LocalDateTime parseTimestamp(String ts) {
        try {
            if (ts == null || ts.isEmpty()) return java.time.LocalDateTime.now();
            return java.time.LocalDateTime.parse(ts);
        } catch (Exception e) {
            return java.time.LocalDateTime.now();
        }
    }
}
