
package run.mone.hive.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.schema.Expr;
import run.mone.hive.schema.Message;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertNotNull;


//本质测试的事一个图的调用
class ActionGraphTest {

    private ActionGraph actionGraph;

    @BeforeEach
    void setUp() {
        actionGraph = new ActionGraph();
    }

    @Test
    void testExecute() {
        boolean debug = false;
        LLM llm = new LLM(LLMConfig.builder().debug(debug).llmProvider(LLMProvider.DOUBAO_DEEPSEEK_V3).build()) {
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
        ActionNode node1 = new ActionNode("node1", String.class, "a=12+22 a=?", null);//34
        node1.setLlm(llm);
        ActionNode node2 = new ActionNode("node2", String.class, "b=a+3 b=?", null);//37
        node2.setLlm(llm);
        ActionNode node3 = new ActionNode("node3", String.class, "c=a+b c=?", null);//71
        node3.getExprs().add(Expr.builder().key("node1").input(false).expr("data").build());
        node3.setLlm(llm);

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

        //check 是不是71
        assertNotNull(results);
    }
}
