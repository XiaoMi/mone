package run.mone.hive.actions;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.hive.schema.Message;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * ActionGraph: 一个有向图,用于表示动作之间的依赖关系
 */
@Slf4j
@Data
public class ActionGraph {
    private Map<String, ActionNode> nodes;
    private Map<String, List<String>> edges;
    private List<String> executionOrder;
    private Map<String, CompletableFuture<Message>> results;

    private ActionGraphContext context = new ActionGraphContext();

    public ActionGraph() {
        this.nodes = new HashMap<>();
        this.edges = new HashMap<>();
        this.executionOrder = new ArrayList<>();
        this.results = new HashMap<>();
    }

    /**
     * 添加节点到图中
     */
    public void addNode(ActionNode node) {
        nodes.put(node.getKey(), node);
    }

    /**
     * 添加边到图中
     */
    public void addEdge(ActionNode fromNode, ActionNode toNode) {
        String fromKey = fromNode.getKey();
        String toKey = toNode.getKey();

        edges.computeIfAbsent(fromKey, k -> new ArrayList<>()).add(toKey);
        fromNode.addNext(toNode);
        toNode.addPrev(fromNode);
    }

    /**
     * 拓扑排序
     */
    public void topologicalSort() {
        Set<String> visited = new HashSet<>();
        Stack<String> stack = new Stack<>();

        for (String key : nodes.keySet()) {
            if (!visited.contains(key)) {
                topologicalSortUtil(key, visited, stack);
            }
        }

        executionOrder = new ArrayList<>();
        while (!stack.isEmpty()) {
            executionOrder.add(stack.pop());
        }
    }

    private void topologicalSortUtil(String key, Set<String> visited, Stack<String> stack) {
        visited.add(key);

        List<String> neighbors = edges.getOrDefault(key, new ArrayList<>());
        for (String neighbor : neighbors) {
            if (!visited.contains(neighbor)) {
                topologicalSortUtil(neighbor, visited, stack);
            }
        }

        stack.push(key);
    }

    /**
     * 执行图中的所有动作
     */
    public CompletableFuture<Map<String, Message>> execute() {
        if (executionOrder.isEmpty()) {
            topologicalSort();
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Message> finalResults = new HashMap<>();

                for (String nodeKey : executionOrder) {
                    ActionNode node = nodes.get(nodeKey);
                    CompletableFuture<Message> result = executeNode(node);
                    results.put(nodeKey, result);
                    finalResults.put(nodeKey, result.join());
                }
                return finalResults;
            } catch (Exception e) {
                log.error("Error executing action graph: {}", e.getMessage());
                throw new RuntimeException("Action graph execution failed", e);
            }
        });
    }

    private CompletableFuture<Message> executeNode(ActionNode node) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 等待所有前置节点完成
                List<ActionNode> prevNodes = node.getPrevs();
                List<CompletableFuture<Message>> prevResults = prevNodes.stream()
                        .map(prev -> results.get(prev.getKey()))
                        .toList();

                // 等待所有前置结果
                CompletableFuture.allOf(prevResults.toArray(new CompletableFuture[0])).join();

                StringBuilder sb = new StringBuilder();

                sb.append("\n").append("其他上下文 开始").append("\n");
                node.getExprs().forEach(it -> {
                    ActionNode n = this.getNodes().get(it.getKey());
                    //提取出来的值
                    JsonElement je = n.extractValue(it.isInput(), it.getExpr());
                    it.setValue(je);
                    sb.append("\n").append(null != it.getDesc() ? it.getDesc() : it.getExpr()).append(":").append(je.toString());
                });
                sb.append("\n").append("其他上下文 结束").append("\n");

                //依赖节点的结果
                sb.append("\n").append("依赖节点的结果 开始").append("\n");
                prevResults.forEach(it -> {
                    Message msg = it.join();
                    sb.append(msg.getInstructContent()).append("\n").append(Joiner.on(":").join("assistant", msg.getContent())).append("\n");
                });
                sb.append("\n").append("依赖节点的结果 结束").append("\n");


                node.setContext(sb.toString());
                node.setGraphContext(ActionGraph.this.context);

                // 执行当前节点
                return node.run().join();
            } catch (Exception e) {
                log.error("Error executing node {}: {}", node.getKey(), e.getMessage());
                throw new RuntimeException("Node execution failed: " + node.getKey(), e);
            }
        });
    }

    /**
     * 获取节点的所有前置节点
     */
    public List<ActionNode> getPredecessors(String nodeKey) {
        ActionNode node = nodes.get(nodeKey);
        return node != null ? node.getPrevs() : new ArrayList<>();
    }

    /**
     * 获取节点的所有后继节点
     */
    public List<ActionNode> getSuccessors(String nodeKey) {
        ActionNode node = nodes.get(nodeKey);
        return node != null ? node.getNexts() : new ArrayList<>();
    }

    /**
     * 检查是否存在环
     */
    public boolean hasCycle() {
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();

        for (String key : nodes.keySet()) {
            if (hasCycleUtil(key, visited, recursionStack)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasCycleUtil(String key, Set<String> visited, Set<String> recursionStack) {
        if (recursionStack.contains(key)) {
            return true;
        }
        if (visited.contains(key)) {
            return false;
        }

        visited.add(key);
        recursionStack.add(key);

        List<String> neighbors = edges.getOrDefault(key, new ArrayList<>());
        for (String neighbor : neighbors) {
            if (hasCycleUtil(neighbor, visited, recursionStack)) {
                return true;
            }
        }

        recursionStack.remove(key);
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ActionGraph{\n");
        sb.append("  nodes: ").append(nodes.keySet()).append("\n");
        sb.append("  edges: ").append(edges).append("\n");
        sb.append("  executionOrder: ").append(executionOrder).append("\n");
        sb.append("}");
        return sb.toString();
    }
} 