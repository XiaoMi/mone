
package run.mone.hive.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.schema.Expr;
import run.mone.hive.schema.Message;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class ActionGraphTest {

    private ActionGraph actionGraph;

    @BeforeEach
    void setUp() {
        actionGraph = new ActionGraph();
    }

    @Test
    void testExecute() {
        boolean debug = false;
        // Create mock LLM
        LLM mockLLM = new LLM(LLMConfig.builder().debug(debug).build()) {
            @Override
            public CompletableFuture<String> ask(String prompt) {
                if (debug) {
                    return CompletableFuture.completedFuture("[CONTENT]Mock response[/CONTENT]");
                } else {
                    return super.ask(prompt);
                }
            }
        };

        // Create action nodes
        ActionNode node1 = new ActionNode("node1", String.class, "a=12+22 a=?", null);
        node1.setLlm(mockLLM);
        ActionNode node2 = new ActionNode("node2", String.class, "b=a+3 b=?", null);
        node2.setLlm(mockLLM);
        ActionNode node3 = new ActionNode("node3", String.class, "c=a+b c=?", null);
        node3.getExprs().add(Expr.builder().key("node1").input(false).expr("data").build());
        node3.setLlm(mockLLM);

        // Add nodes to the graph
        actionGraph.addNode(node1);
        actionGraph.addNode(node2);
        actionGraph.addNode(node3);

        // Add edges
        actionGraph.addEdge(node1, node2);
        actionGraph.addEdge(node2, node3);

        // Execute the graph
        CompletableFuture<Map<String, Message>> resultFuture = actionGraph.execute();

        // Wait for the execution to complete
        Map<String, Message> results = resultFuture.join();

        // Assertions
        assertNotNull(results);

        if (debug) {
            assertEquals(3, results.size());
            assertTrue(results.containsKey("node1"));
            assertTrue(results.containsKey("node2"));
            assertTrue(results.containsKey("node3"));

            // Check that each result is a Message
            results.values().forEach(result -> assertTrue(result instanceof Message));

            // Check the content of each Message
            results.values().forEach(result -> {
                Message message = (Message) result;
                assertEquals("Mock response", message.getContent());
            });

            // Check execution order
            assertEquals(3, actionGraph.getExecutionOrder().size());
            assertEquals("node1", actionGraph.getExecutionOrder().get(0));
            assertEquals("node2", actionGraph.getExecutionOrder().get(1));
            assertEquals("node3", actionGraph.getExecutionOrder().get(2));
        }
    }
}
