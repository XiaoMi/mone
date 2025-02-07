
package run.mone.mcp.neo4j.function;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.*;
import run.mone.hive.mcp.spec.McpSchema;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Data
@Slf4j
public class Neo4jFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    private String name = "neo4jOperation";

    private String desc = "Neo4j operations including node and relationship management";

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "operation": {
                        "type": "string",
                        "enum": ["createNode", "getNode", "updateNode", "deleteNode", "createRelationship", "getRelationship", "deleteRelationship", "executeQuery"],
                        "description":"The operation to perform on Neo4j"
                    },
                    "label": {
                        "type": "string",
                        "description":"The label of the node"
                    },
                    "nodeId": {
                        "type": "integer",
                        "description":"The ID of the node"
                    },
                    "properties": {
                        "type": "object",
                        "description":"The properties of the node or relationship"
                    },
                    "startNodeId": {
                        "type": "integer",
                        "description":"The ID of the start node for a relationship"
                    },
                    "endNodeId": {
                        "type": "integer",
                        "description":"The ID of the end node for a relationship"
                    },
                    "relationshipType": {
                        "type": "string",
                        "description":"The type of the relationship"
                    },
                    "relationshipId": {
                        "type": "integer",
                        "description":"The ID of the relationship"
                    },
                    "query": {
                        "type": "string",
                        "description":"The Cypher query to execute"
                    }
                },
                "required": ["operation"]
            }
            """;

    private Driver driver;

    public Neo4jFunction(String uri, String user, String password) {
        this.driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        String operation = (String) arguments.get("operation");

        log.info("operation: {}", operation);

        try {
            String result = switch (operation) {
                case "createNode" -> createNode((String) arguments.get("label"), (Map<String, Object>) arguments.get("properties"));
                case "getNode" -> getNode((Integer) arguments.get("nodeId"));
                case "updateNode" -> updateNode((Integer) arguments.get("nodeId"), (Map<String, Object>) arguments.get("properties"));
                case "deleteNode" -> deleteNode((Integer) arguments.get("nodeId"));
                case "createRelationship" -> createRelationship((Integer) arguments.get("startNodeId"), (Integer) arguments.get("endNodeId"), (String) arguments.get("relationshipType"), (Map<String, Object>) arguments.get("properties"));
                case "getRelationship" -> getRelationship((Integer) arguments.get("relationshipId"));
                case "deleteRelationship" -> deleteRelationship((Integer) arguments.get("relationshipId"));
                case "getNodesByLabel" -> getNodesByLabel((String) arguments.get("label"));
                case "executeQuery" -> executeQuery((String) arguments.get("query"));
                default -> throw new IllegalArgumentException("Unknown operation: " + operation);
            };

            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent(result)), false);
        } catch (Exception e) {
            return new McpSchema.CallToolResult(List.of(new McpSchema.TextContent("Error: " + e.getMessage())), true);
        }
    }

    private String createNode(String label, Map<String, Object> properties) {
        try (Session session = driver.session()) {
            return session.writeTransaction(tx -> {
                Result result = tx.run("CREATE (n:" + label + ") SET n = $props RETURN id(n)", Values.parameters("props", properties));
                return "Node created with ID: " + result.single().get(0).asInt();
            });
        }
    }



    private String getNode(Integer nodeId) {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                Result result = tx.run("MATCH (n) WHERE id(n) = $id RETURN n", Values.parameters("id", nodeId));
                return result.single().get("n").asMap().toString();
            });
        }
    }

    private String getNodesByLabel(String label) {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                Result result = tx.run("MATCH (n:" + label + ") RETURN n");
                List<Map<String, Object>> nodes = result.list(record -> record.get("n").asMap());
                return nodes.toString();
            });
        }
    }


    private String updateNode(Integer nodeId, Map<String, Object> properties) {
        try (Session session = driver.session()) {
            return session.writeTransaction(tx -> {
                tx.run("MATCH (n) WHERE id(n) = $id SET n += $props", Values.parameters("id", nodeId, "props", properties));
                return "Node updated: " + nodeId;
            });
        }
    }

    private String deleteNode(Integer nodeId) {
        try (Session session = driver.session()) {
            return session.writeTransaction(tx -> {
                tx.run("MATCH (n) WHERE id(n) = $id DETACH DELETE n", Values.parameters("id", nodeId));
                return "Node deleted: " + nodeId;
            });
        }
    }

    private String createRelationship(Integer startNodeId, Integer endNodeId, String relationshipType, Map<String, Object> properties) {
        try (Session session = driver.session()) {
            return session.writeTransaction(tx -> {
                Result result = tx.run(
                        "MATCH (a), (b) WHERE id(a) = $startId AND id(b) = $endId " +
                                "CREATE (a)-[r:" + relationshipType + "]->(b) SET r = $props " +
                                "RETURN id(r)",
                        Values.parameters("startId", startNodeId, "endId", endNodeId, "props", properties)
                );
                return "Relationship created with ID: " + result.single().get(0).asInt();
            });
        }
    }

    private String getRelationship(Integer relationshipId) {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                Result result = tx.run("MATCH ()-[r]->() WHERE id(r) = $id RETURN r", Values.parameters("id", relationshipId));
                return result.single().get("r").asMap().toString();
            });
        }
    }

    private String deleteRelationship(Integer relationshipId) {
        try (Session session = driver.session()) {
            return session.writeTransaction(tx -> {
                tx.run("MATCH ()-[r]->() WHERE id(r) = $id DELETE r", Values.parameters("id", relationshipId));
                return "Relationship deleted: " + relationshipId;
            });
        }
    }

    private String executeQuery(String query) {
        try (Session session = driver.session()) {
            return session.readTransaction(tx -> {
                Result result = tx.run(query);
                return result.list().toString();
            });
        }
    }
}
