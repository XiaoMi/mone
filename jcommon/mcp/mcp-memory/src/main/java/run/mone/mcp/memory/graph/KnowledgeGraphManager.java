package run.mone.mcp.memory.graph;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class KnowledgeGraphManager {
    private static final String MEMORY_FILE_PATH = System.getProperty("user.dir") + File.separator + ".memory.jsonl";
    private final ObjectMapper objectMapper = new ObjectMapper();

    private void ensureFileExists() throws IOException {
        Path path = Paths.get(MEMORY_FILE_PATH);
        if (!Files.exists(path)) {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
        }
    }

    private KnowledgeGraph loadGraph() throws IOException {
        ensureFileExists();
        List<String> lines = Files.readAllLines(Paths.get(MEMORY_FILE_PATH));
        
        KnowledgeGraph graph = new KnowledgeGraph();
        
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            
            Map<String, Object> item = objectMapper.readValue(line, new TypeReference<Map<String, Object>>() {});
            String type = (String) item.get("type");
            
            if ("entity".equals(type)) {
                Entity entity = objectMapper.convertValue(item, Entity.class);
                graph.getEntities().add(entity);
            } else if ("relation".equals(type)) {
                Relation relation = objectMapper.convertValue(item, Relation.class);
                graph.getRelations().add(relation);
            }
        }
        return graph;
    }

    private void saveGraph(KnowledgeGraph graph) throws IOException {
        List<String> lines = new ArrayList<>();
        
        for (Entity entity : graph.getEntities()) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", "entity");
            item.put("name", entity.getName());
            item.put("entityType", entity.getEntityType());
            item.put("observations", entity.getObservations());
            lines.add(objectMapper.writeValueAsString(item));
        }
        
        for (Relation relation : graph.getRelations()) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", "relation");
            item.put("from", relation.getFrom());
            item.put("to", relation.getTo());
            item.put("relationType", relation.getRelationType());
            lines.add(objectMapper.writeValueAsString(item));
        }
        
        Files.write(Paths.get(MEMORY_FILE_PATH), lines);
    }

    public List<Entity> createEntities(List<Entity> entities) throws IOException {
        KnowledgeGraph graph = loadGraph();
        List<Entity> newEntities = entities.stream()
            .filter(e -> !graph.getEntities().contains(e.getName()))
            .collect(Collectors.toList());
            
        graph.getEntities().addAll(newEntities);
        saveGraph(graph);
        return newEntities;
    }

    public List<Relation> createRelations(List<Relation> relations) throws IOException {
        KnowledgeGraph graph = loadGraph();
        List<Relation> newRelations = relations.stream()
            .filter(r -> !graph.getRelations().stream()
                .anyMatch(existing -> 
                    existing.getFrom().equals(r.getFrom()) &&
                    existing.getTo().equals(r.getTo()) &&
                    existing.getRelationType().equals(r.getRelationType())))
            .collect(Collectors.toList());
            
        graph.getRelations().addAll(newRelations);
        saveGraph(graph);
        return newRelations;
    }

    public List<ObservationResult> addObservations(List<ObservationRequest> requests) throws IOException {
        KnowledgeGraph graph = loadGraph();
        List<ObservationResult> results = new ArrayList<>();
        
        for (ObservationRequest request : requests) {
            Entity entity = graph.getEntities().stream()
                .filter(e -> e.getName().equals(request.getEntityName()))
                .findFirst()
                .orElse(null);
            if (entity == null) {
                throw new IllegalArgumentException("Entity not found: " + request.getEntityName());
            }
            
            List<String> newObservations = request.getContents().stream()
                .filter(content -> !entity.getObservations().contains(content))
                .collect(Collectors.toList());
                
            entity.getObservations().addAll(newObservations);
            results.add(new ObservationResult(request.getEntityName(), newObservations));
        }
        
        saveGraph(graph);
        return results;
    }

    public void deleteEntities(List<String> entityNames) throws IOException {
        KnowledgeGraph graph = loadGraph();
        graph.getEntities().removeIf(e -> entityNames.contains(e.getName()));
        saveGraph(graph);
    }

    public void deleteObservations(List<ObservationDeletion> deletions) throws IOException {
        KnowledgeGraph graph = loadGraph();
        
        for (ObservationDeletion deletion : deletions) {
            Entity entity = graph.getEntities().stream()
                .filter(e -> e.getName().equals(deletion.getEntityName()))
                .findFirst()
                .orElse(null);
            if (entity != null) {
                entity.getObservations().removeAll(deletion.getObservations());
            }
        }
        
        saveGraph(graph);
    }

    public void deleteRelations(List<Relation> relations) throws IOException {
        KnowledgeGraph graph = loadGraph();
        graph.getRelations().removeIf(r -> 
            relations.stream().anyMatch(delRelation ->
                r.getFrom().equals(delRelation.getFrom()) &&
                r.getTo().equals(delRelation.getTo()) &&
                r.getRelationType().equals(delRelation.getRelationType())
            )
        );
        saveGraph(graph);
    }

    public KnowledgeGraph readGraph() throws IOException {
        return loadGraph();
    }

    public KnowledgeGraph searchNodes(String query) throws IOException {
        KnowledgeGraph graph = loadGraph();
        String lowercaseQuery = query.toLowerCase();
        
        List<Entity> filteredEntities = graph.getEntities().stream()
            .filter(e -> 
                e.getName().toLowerCase().contains(lowercaseQuery) ||
                e.getEntityType().toLowerCase().contains(lowercaseQuery) ||
                e.getObservations().stream()
                    .anyMatch(o -> o.toLowerCase().contains(lowercaseQuery)))
            .collect(Collectors.toList());
            
        List<Relation> filteredRelations = graph.getRelations().stream()
            .filter(r -> 
                filteredEntities.contains(r.getFrom()) &&
                filteredEntities.contains(r.getTo()))
            .collect(Collectors.toList());
            
        KnowledgeGraph filteredGraph = new KnowledgeGraph();
        filteredGraph.getEntities().addAll(filteredEntities);
        filteredGraph.getRelations().addAll(filteredRelations);
        
        return filteredGraph;
    }

    public KnowledgeGraph openNodes(List<String> names) throws IOException {
        KnowledgeGraph graph = loadGraph();
        
        List<Entity> filteredEntities = graph.getEntities().stream()
            .filter(e -> names.contains(e.getName()))
            .collect(Collectors.toList());
            
        List<Relation> filteredRelations = graph.getRelations().stream()
            .filter(r -> 
                filteredEntities.contains(r.getFrom()) &&
                filteredEntities.contains(r.getTo()))
            .collect(Collectors.toList());
            
        KnowledgeGraph filteredGraph = new KnowledgeGraph();
        filteredGraph.getEntities().addAll(filteredEntities);
        filteredGraph.getRelations().addAll(filteredRelations);
        
        return filteredGraph;
    }

    // 辅助类
    @Data
    public static class ObservationRequest {
        private String entityName;
        private List<String> contents;
    }

    @Data
    public static class ObservationResult {
        private String entityName;
        private List<String> addedObservations;
        
        public ObservationResult(String entityName, List<String> addedObservations) {
            this.entityName = entityName;
            this.addedObservations = addedObservations;
        }
    }

    @Data
    public static class ObservationDeletion {
        private String entityName;
        private List<String> observations;
    }
}


