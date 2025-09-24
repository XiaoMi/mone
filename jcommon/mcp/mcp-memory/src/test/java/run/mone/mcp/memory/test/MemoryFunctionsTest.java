package run.mone.mcp.memory.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.memory.MemoryMcpBootstrap;
import run.mone.mcp.memory.function.MemoryFunctions;
import run.mone.mcp.memory.graph.Entity;
import run.mone.mcp.memory.graph.Relation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = MemoryMcpBootstrap.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class MemoryFunctionsTest {

    private MemoryFunctions.CreateEntitiesFunction createEntitiesFunction;
    private MemoryFunctions.CreateRelationsFunction createRelationsFunction;
    private MemoryFunctions.AddObservationsFunction addObservationsFunction;
    private MemoryFunctions.DeleteEntitiesFunction deleteEntitiesFunction;
    private MemoryFunctions.DeleteObservationsFunction deleteObservationsFunction;
    private MemoryFunctions.DeleteRelationsFunction deleteRelationsFunction;
    private MemoryFunctions.ReadGraphFunction readGraphFunction;
    private MemoryFunctions.SearchNodesFunction searchNodesFunction;
    private MemoryFunctions.OpenNodesFunction openNodesFunction;

    @BeforeEach
    void setUp() {
        createEntitiesFunction = new MemoryFunctions.CreateEntitiesFunction();
        createRelationsFunction = new MemoryFunctions.CreateRelationsFunction();
        addObservationsFunction = new MemoryFunctions.AddObservationsFunction();
        deleteEntitiesFunction = new MemoryFunctions.DeleteEntitiesFunction();
        deleteObservationsFunction = new MemoryFunctions.DeleteObservationsFunction();
        deleteRelationsFunction = new MemoryFunctions.DeleteRelationsFunction();
        readGraphFunction = new MemoryFunctions.ReadGraphFunction();
        searchNodesFunction = new MemoryFunctions.SearchNodesFunction();
        openNodesFunction = new MemoryFunctions.OpenNodesFunction();
    }

    @Test
    void testCreateEntities() {
        List<Entity> entities = new ArrayList<>();
        Entity entity = new Entity();
        entity.setName("TestEntity");
        entity.setEntityType("TestType");
        entity.setObservations(List.of("Observation1", "Observation2"));
        entities.add(entity);

        Map<String, Object> args = new HashMap<>();
        args.put("entities", entities);

        McpSchema.CallToolResult result = createEntitiesFunction.apply(args);
        assertFalse(result.isError());
    }

    @Test
    void testCreateRelations() {
        // First create some entities
        testCreateEntities();

        List<Relation> relations = new ArrayList<>();
        Relation relation = new Relation();
        relation.setFrom("TestEntity");
        relation.setTo("TestEntity");
        relation.setRelationType("SELF_RELATION");
        relations.add(relation);

        Map<String, Object> args = new HashMap<>();
        args.put("relations", relations);

        McpSchema.CallToolResult result = createRelationsFunction.apply(args);
        assertFalse(result.isError());
    }

    @Test
    void testAddObservations() {
        // First create an entity
        testCreateEntities();

        Map<String, Object> args = new HashMap<>();
        List<Map<String, Object>> observations = new ArrayList<>();
        Map<String, Object> observation = new HashMap<>();
        observation.put("entityName", "TestEntity");
        observation.put("contents", List.of("NewObservation1", "NewObservation2"));
        observations.add(observation);
        args.put("observations", observations);

        McpSchema.CallToolResult result = addObservationsFunction.apply(args);
        assertFalse(result.isError());
    }

    @Test
    void testDeleteEntities() {
        // First create an entity
        testCreateEntities();

        Map<String, Object> args = new HashMap<>();
        args.put("entityNames", List.of("TestEntity"));

        McpSchema.CallToolResult result = deleteEntitiesFunction.apply(args);
        assertFalse(result.isError());
    }

    @Test
    void testDeleteObservations() {
        // First create an entity with observations
        testCreateEntities();

        Map<String, Object> args = new HashMap<>();
        List<Map<String, Object>> deletions = new ArrayList<>();
        Map<String, Object> deletion = new HashMap<>();
        deletion.put("entityName", "TestEntity");
        deletion.put("observations", List.of("Observation1"));
        deletions.add(deletion);
        args.put("deletions", deletions);

        McpSchema.CallToolResult result = deleteObservationsFunction.apply(args);
        assertFalse(result.isError());
    }

    @Test
    void testDeleteRelations() {
        // First create some relations
        testCreateRelations();

        List<Relation> relations = new ArrayList<>();
        Relation relation = new Relation();
        relation.setFrom("TestEntity");
        relation.setTo("TestEntity");
        relation.setRelationType("SELF_RELATION");
        relations.add(relation);

        Map<String, Object> args = new HashMap<>();
        args.put("relations", relations);

        McpSchema.CallToolResult result = deleteRelationsFunction.apply(args);
        assertFalse(result.isError());
    }

    @Test
    void testReadGraph() {
        // First create some data
        testCreateRelations();

        Map<String, Object> args = new HashMap<>();
        McpSchema.CallToolResult result = readGraphFunction.apply(args);
        assertFalse(result.isError());
    }

    @Test
    void testSearchNodes() {
        // First create some data
        testCreateEntities();

        Map<String, Object> args = new HashMap<>();
        args.put("query", "TestEntity");

        McpSchema.CallToolResult result = searchNodesFunction.apply(args);
        assertFalse(result.isError());
    }

    @Test
    void testOpenNodes() {
        // First create some entities
        testCreateEntities();

        Map<String, Object> args = new HashMap<>();
        args.put("names", List.of("TestEntity"));

        McpSchema.CallToolResult result = openNodesFunction.apply(args);
        assertFalse(result.isError());
    }
}
