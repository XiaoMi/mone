package run.mone.hive.actions;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.common.Constants;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.BaseLLM;
import run.mone.hive.memory.Memory;
import run.mone.hive.roles.Role;
import run.mone.hive.schema.Expr;
import run.mone.hive.schema.Message;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Action for breaking down requirements into specific tasks
 */
@Slf4j
public class WriteAction extends Action {


    @Override
    public CompletableFuture<Message> run(Map<String, Object> map) {
        return CompletableFuture.supplyAsync(() -> {
            ActionNode node0 = new ActionNode("node0", String.class, "准备信息", this.getRole());
            node0.setLlm(new BaseLLM(LLMConfig.builder().build()) {
                @Override
                public CompletableFuture<String> ask(String prompt) {
                    StringBuilder instruction = new StringBuilder();
                    if (map.get(Constants.ROLE) instanceof Role role) {
                        instruction.append("你的角色:").append(role.getGoal()).append("\n");
                    }
                    if (map.get(Constants.MEMORY) instanceof Memory memory) {
                        Message message = memory.getStorage().get(memory.getStorage().size() - 1);
                        instruction.append("标题和要求:").append(message.getContent()).append("\n");
                    }
                    return CompletableFuture.completedFuture("[CONTENT]" + instruction + "[/CONTENT]");
                }
            });


            ActionNode node1 = new ActionNode("node1", String.class, "列出大纲,用数字隔开", this.getRole());
            ActionNode node2 = new ActionNode("node2", String.class, "写出文章", this.getRole());
            ActionNode node3 = new ActionNode("node3", String.class, "润色文章", this.getRole());
            node3.getExprs().add(Expr.builder().key("node1").expr("data").desc("文章大纲:").build());

            ActionGraph graph = new ActionGraph();
            graph.addNode(node0);
            graph.addNode(node1);
            graph.addNode(node2);
            graph.addNode(node3);

            graph.addEdge(node0, node1);
            graph.addEdge(node0, node2);
            graph.addEdge(node0, node3);
            graph.addEdge(node1, node2);
            graph.addEdge(node2, node3);

            CompletableFuture<Map<String, Message>> res = graph.execute();

            return res.join().get("node3");
        });
    }

} 