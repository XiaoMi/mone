package run.mone.hive.actions.writer;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.actions.Action;
import run.mone.hive.actions.ActionGraph;
import run.mone.hive.actions.ActionNode;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Expr;
import run.mone.hive.schema.Message;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author goodjava@qq.com
 * Action for breaking down requirements into specific tasks
 * 本质上是允许Action 里边执行 Dag ActionNode
 * 一个Action 底层可以是一个复杂的Dag
 */
@Slf4j
public class WriteAction extends Action {

    public WriteAction() {
        super("作家", "写出好的文章");
    }

    @Override
    public CompletableFuture<Message> run(ActionReq map, ActionContext context) {
        return CompletableFuture.supplyAsync(() -> {
            ActionNode node0 = new ActionNode("node0", String.class, "准备信息", this.getRole());
            node0.setLlm(new LLM(LLMConfig.builder().llmProvider(LLMProvider.DOUBAO_DEEPSEEK_V3).build()) {
                @Override
                public CompletableFuture<String> ask(String prompt) {
                    StringBuilder instruction = new StringBuilder();
                    instruction.append("你的角色:").append(map.getRole().getProfile()).append("\n");
                    Message message = map.getMessage();
                    instruction.append("标题和要求:").append(message.getContent()).append("\n");
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