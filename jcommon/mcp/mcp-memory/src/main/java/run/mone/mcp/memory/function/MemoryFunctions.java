package run.mone.mcp.memory.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.mcp.spec.McpSchema.CallToolResult;
import run.mone.mcp.memory.graph.KnowledgeGraphManager;
import run.mone.mcp.memory.graph.Entity;
import run.mone.mcp.memory.graph.KnowledgeGraph;
import run.mone.mcp.memory.graph.Relation;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Data
@Slf4j
public class MemoryFunctions {

    private static final KnowledgeGraphManager graphManager = new KnowledgeGraphManager();

    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Data
    public static class CreateEntitiesFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
        private String name = "create_entities";

        private String desc = """
        Create multiple new entities in the knowledge graph, when the user wants to remember something, they can use this function to create an entity
        """;

        private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "entities": {
                    "type": "array",
                    "items": {
                        "type": "object",
                        "properties": {
                            "name": {
                                "type": "string",
                                "description": "The name of the entity"
                            },
                            "entityType": {
                                "type": "string",
                                "description": "The type of the entity"
                            },
                            "observations": {
                                "type": "array",
                                "items": {
                                "type": "string"
                                },
                                "description": "An array of observation contents associated with the entity"
                            }
                            },
                            "required": ["name", "entityType", "observations"]
                        }
                    }
                },
                "required": ["entities"]
            }
            """;

        @Override
        public CallToolResult apply(Map<String, Object> t) {
            List<Entity> entities = parseObject(t.get("entities"), new TypeReference<List<Entity>>() {});
            log.info("Creating entities: {}", entities);
            try {
                List<Entity> newEntities = graphManager.createEntities(entities);
                return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(objectMapper.writeValueAsString(newEntities))), false);
            } catch (Throwable e) {
                log.error("Failed to create entities", e);
                return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Failed to create entities")), true);
            }
        } 
    }

    @Data
    public static class CreateRelationsFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
        private String name = "create_relations";
        
        private String desc = """
        Create multiple new relations between entities in the knowledge graph. Relations should be in active voice, and should be in the past tense.
        when the user wants to remember something, they can use this function to create a relation between the entity and the event that happened.
        """;

        private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "relations": {
                        "type": "array",
                        "items": {
                            "type": "object",
                            "properties": {
                                "from": {
                                    "type": "string",
                                    "description": "The name of the entity where the relation starts"
                                },
                                "to": {
                                    "type": "string", 
                                    "description": "The name of the entity where the relation ends"
                                },
                                "relationType": {
                                    "type": "string",
                                    "description": "The type of the relation"
                                }
                            },
                            "required": ["from", "to", "relationType"]
                        }
                    }
                },
                "required": ["relations"]
            }
            """;

        @Override
        public CallToolResult apply(Map<String, Object> t) {
            List<Relation> relations = parseObject(t.get("relations"), new TypeReference<List<Relation>>() {});
            log.info("Creating relations: {}", relations);
            try {
                List<Relation> newRelations = graphManager.createRelations(relations);
                return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(objectMapper.writeValueAsString(newRelations))), false);
            } catch (Throwable e) {
                log.error("Failed to create relations", e);
                return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Failed to create relations")), true);
            }
        }
    }

    @Data 
    public static class AddObservationsFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
        private String name = "add_observations";

        private String desc = """
        Add new observations to existing entities in the knowledge graph, when the user wants to remember something, they can use this function to add an observation to the entity.
        """;

        private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "observations": {
                        "type": "array",
                        "items": {
                            "type": "object",
                            "properties": {
                                "entityName": {
                                    "type": "string",
                                    "description": "The name of the entity to add the observations to"
                                },
                                "contents": {
                                    "type": "array",
                                    "items": {
                                        "type": "string"
                                    },
                                    "description": "An array of observation contents to add"
                                }
                            },
                            "required": ["entityName", "contents"]
                        }
                    }
                },
                "required": ["observations"]
            }
            """;

        @Override
        public CallToolResult apply(Map<String, Object> t) {
            List<KnowledgeGraphManager.ObservationRequest> observations = parseObject(t.get("observations"), new TypeReference<List<KnowledgeGraphManager.ObservationRequest>>() {});
            log.info("Adding observations: {}", observations);
            try {
                List<KnowledgeGraphManager.ObservationResult> results = graphManager.addObservations(observations);
                return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(objectMapper.writeValueAsString(results))), false);
            } catch (Throwable e) {
                log.error("Failed to add observations", e);
                return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Failed to add observations")), true);
            }
        }
    }

    @Data
    public static class DeleteEntitiesFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
        private String name = "delete_entities";

        private String desc = """
        Delete multiple entities and their associated relations from the knowledge graph, when the user wants to forget something, they can use this function to delete the entity.
        """;

        private String toolScheme = """
            {
                "type": "object", 
                "properties": {
                    "entityNames": {
                        "type": "array",
                        "items": {
                            "type": "string"
                        },
                        "description": "An array of entity names to delete"
                    }
                },
                "required": ["entityNames"]
            }
            """;

        @Override
        public CallToolResult apply(Map<String, Object> t) {
            List<String> entityNames = parseObject(t.get("entityNames"), new TypeReference<List<String>>() {});
            log.info("Deleting entities: {}", entityNames);
            try {
                graphManager.deleteEntities(entityNames);
                return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Entities deleted successfully")), false);
            } catch (Throwable e) {
                log.error("Failed to delete entities", e);
                return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Failed to delete entities")), true);
            }
        }
    }

    @Data
    public static class DeleteObservationsFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
        private String name = "delete_observations";

        private String desc = """
        Delete specific observations from entities in the knowledge graph, when the user wants to forget something, they can use this function to delete the observation.
        """;

        private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "deletions": {
                        "type": "array",
                        "items": {
                            "type": "object",
                            "properties": {
                                "entityName": {
                                    "type": "string",
                                    "description": "The name of the entity containing the observations"
                                },
                                "observations": {
                                    "type": "array",
                                    "items": {
                                        "type": "string"
                                    },
                                    "description": "An array of observations to delete"
                                }
                            },
                            "required": ["entityName", "observations"]
                        }
                    }
                },
                "required": ["deletions"]
            }
            """;

        @Override
        public CallToolResult apply(Map<String, Object> t) {
            List<KnowledgeGraphManager.ObservationDeletion> deletions = parseObject(t.get("deletions"), new TypeReference<List<KnowledgeGraphManager.ObservationDeletion>>() {});
            log.info("Deleting observations: {}", deletions);
            try {
                graphManager.deleteObservations(deletions);
                return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Observations deleted successfully")), false);
            } catch (Throwable e) {
                log.error("Failed to delete observations", e);
                return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Failed to delete observations")), true);
            }
        }
    }

    @Data
    public static class DeleteRelationsFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
        private String name = "delete_relations";

        private String desc = """
        Delete multiple relations from the knowledge graph, when the user wants to forget something, they can use this function to delete the relation.
        """;

        private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "relations": {
                        "type": "array",
                        "items": {
                            "type": "object",
                            "properties": {
                                "from": {
                                    "type": "string",
                                    "description": "The name of the entity where the relation starts"
                                },
                                "to": {
                                    "type": "string",
                                    "description": "The name of the entity where the relation ends"
                                },
                                "relationType": {
                                    "type": "string",
                                    "description": "The type of the relation"
                                }
                            },
                            "required": ["from", "to", "relationType"]
                        }
                    }
                },
                "required": ["relations"]
            }
            """;

        @Override
        public CallToolResult apply(Map<String, Object> t) {
            List<Relation> relations = parseObject(t.get("relations"), new TypeReference<List<Relation>>() {});
            log.info("Deleting relations: {}", relations);
            try {
                graphManager.deleteRelations(relations);
                return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Relations deleted successfully")), false);
            } catch (Throwable e) {
                log.error("Failed to delete relations", e);
                return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Failed to delete relations")), true);
            }
        }
    }

    @Data
    public static class ReadGraphFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
        private String name = "read_graph";

        private String desc = """
        Read the entire knowledge graph, when the user wants to know what they have remembered, they can use this function to read the graph.
        """;

        private String toolScheme = """
            {
                "type": "object",
                "properties": {}
            }
            """;

        @Override
        public CallToolResult apply(Map<String, Object> t) {
            log.info("Reading graph");
            try {
                KnowledgeGraph graph = graphManager.readGraph();
                return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(objectMapper.writeValueAsString(graph))), false);
            } catch (Throwable e) {
                log.error("Failed to read graph", e);
                return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Failed to read graph")), true);
            }
        }
    }

    @Data
    public static class SearchNodesFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
        private String name = "search_nodes";

        private String desc = """
        Search for nodes in the knowledge graph based on a query, when the user wants to know what they have remembered, they can use this function to search the graph.
        """;

        private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "query": {
                        "type": "string",
                        "description": "The search query to match against entity names, types, and observation content"
                    }
                },
                "required": ["query"]
            }
            """;

        @Override
        public CallToolResult apply(Map<String, Object> t) {
            String query = (String) t.get("query");
            log.info("Searching nodes with query: {}", query);
            try {
                KnowledgeGraph results = graphManager.searchNodes(query);
                return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(objectMapper.writeValueAsString(results))), false);
            } catch (Throwable e) {
                log.error("Failed to search nodes", e);
                return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Failed to search nodes")), true);
            }
        }
    }

    @Data
    public static class OpenNodesFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {
        private String name = "open_nodes";

        private String desc = """
        Open specific nodes in the knowledge graph by their names, when the user wants to know more about a specific entity, they can use this function to open the node.
        """;

        private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "names": {
                        "type": "array",
                        "items": {
                            "type": "string"
                        },
                        "description": "An array of entity names to retrieve"
                    }
                },
                "required": ["names"]
            }
            """;

        @Override
        public CallToolResult apply(Map<String, Object> t) {
            List<String> names = parseObject(t.get("names"), new TypeReference<List<String>>() {});
            log.info("Opening nodes: {}", names);
            try {
                KnowledgeGraph results = graphManager.openNodes(names);
                return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(objectMapper.writeValueAsString(results))), false);
            } catch (Throwable e) {
                log.error("Failed to open nodes", e);
                return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Failed to open nodes")), true);
            }
        }
    }

    private static <T> T parseObject(Object obj, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(objectMapper.writeValueAsString(obj), typeReference);
        } catch (Exception e) {
            log.error("Failed to parse JSON: {}", obj, e);
            throw new RuntimeException("Failed to parse JSON", e);
        }
    }
} 