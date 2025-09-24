package run.mone.hive.memory.longterm.vectorstore.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.Data;
import run.mone.hive.memory.longterm.config.VectorStoreConfig;
import run.mone.hive.memory.longterm.vectorstore.VectorStoreBase;
import run.mone.hive.memory.longterm.model.MemoryItem;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Slf4j
@Data
public class PgVectorStore implements VectorStoreBase {
    private final VectorStoreConfig config;
    
    public PgVectorStore(VectorStoreConfig config) {
        this.config = config;
    }
    
    @Override
    public void insert(List<List<Double>> vectors, List<String> ids, List<Map<String, Object>> payloads) {
        throw new UnsupportedOperationException("PgVector implementation coming soon");
    }
    
    @Override
    public List<MemoryItem> search(String query, List<Double> vectors, int limit, Map<String, Object> filters) {
        return new ArrayList<>();
    }
    
    @Override
    public MemoryItem get(String vectorId) { return null; }
    @Override
    public List<MemoryItem> list(Map<String, Object> filters, int limit) { return new ArrayList<>(); }
    @Override
    public void update(String vectorId, List<Double> vector, Map<String, Object> payload) {}
    @Override
    public void delete(String vectorId) {}
    @Override
    public void deleteCol() {}
    @Override
    public void reset() {}
}
