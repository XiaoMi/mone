
package run.mone.mcp.neo4j.function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.neo4j.Neo4jMcpBootstrap;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Neo4jMcpBootstrap.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class Neo4jFunctionTest {

    private Neo4jFunction neo4jFunction = new Neo4jFunction("bolt://localhost:7687", "neo4j", System.getenv("PRIVATE_KEY"));

    @BeforeEach
    void setUp() {
        // Create some test data
        createTestNode("Person", Map.of("name", "Alice", "age", 30));
        createTestNode("Person", Map.of("name", "Bob", "age", 25));
        createTestNode("City", Map.of("name", "New York"));
    }

    @Test
    void testGetAllNodes() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("operation", "getNode");
        arguments.put("nodeId", 2);
        McpSchema.CallToolResult result = neo4jFunction.apply(arguments);
        assertFalse(result.isError());
    }

    private void createTestNode(String label, Map<String, Object> properties) {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("operation", "createNode");
        arguments.put("label", label);
        arguments.put("properties", properties);
        neo4jFunction.apply(arguments);
    }

    @Test
    void testGetNodesByLabel() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("operation", "getNodesByLabel");
        arguments.put("label", "Person");
        McpSchema.CallToolResult result = neo4jFunction.apply(arguments);
        assertFalse(result.isError());
    }

}
