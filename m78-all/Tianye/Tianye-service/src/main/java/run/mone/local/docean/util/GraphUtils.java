package run.mone.local.docean.util;

import com.xiaomi.data.push.graph.Graph2;
import run.mone.local.docean.fsm.BotFlow;

import java.util.*;

/**
 * @author goodjava@qq.com
 * @date 2024/9/6 08:07
 */
public class GraphUtils {

    //判断图是否有环
    public static boolean hasCycle(Graph2<BotFlow> graph) {
        Set<Integer> visited = new HashSet<>();
        Set<Integer> recursionStack = new HashSet<>();

        for (Integer vertex : graph.getVertexMap().keySet()) {
            if (hasCycleUtil(vertex, visited, recursionStack, graph)) {
                return true;
            }
        }

        return false;
    }

    private static boolean hasCycleUtil(Integer vertex, Set<Integer> visited, Set<Integer> recursionStack, Graph2<BotFlow> graph) {
        if (recursionStack.contains(vertex)) {
            return true;
        }

        if (visited.contains(vertex)) {
            return false;
        }

        visited.add(vertex);
        recursionStack.add(vertex);

        List<Integer> neighbors = graph.getSuccessors(vertex);
        for (Integer neighbor : neighbors) {
            if (hasCycleUtil(neighbor, visited, recursionStack, graph)) {
                return true;
            }
        }

        recursionStack.remove(vertex);
        return false;
    }

    //给你两个顶点,有先后顺序,帮我返回联通经过的节点
    public static List<Integer> getPath(Graph2<BotFlow> graph, Integer startVertex, Integer endVertex) {
        List<Integer> path = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        if (findPathUtil(graph, startVertex, endVertex, visited, path)) {
            return path;
        }
        return Collections.emptyList();
    }

    private static boolean findPathUtil(Graph2<BotFlow> graph, Integer currentVertex, Integer endVertex, Set<Integer> visited, List<Integer> path) {
        visited.add(currentVertex);
        path.add(currentVertex);

        if (currentVertex.equals(endVertex)) {
            return true;
        }

        List<Integer> neighbors = graph.getSuccessors(currentVertex);
        for (Integer neighbor : neighbors) {
            if (!visited.contains(neighbor)) {
                if (findPathUtil(graph, neighbor, endVertex, visited, path)) {
                    return true;
                }
            }
        }

        path.remove(currentVertex);
        return false;
    }

    // 获取图中的开始节点
    public static List<Integer> getStartVertices(Graph2<BotFlow> graph) {
        List<Integer> startVertices = new ArrayList<>();
        Set<Integer> allVertices = new HashSet<>(graph.getVertexMap().keySet());

        for (Integer vertex : allVertices) {
            boolean hasIncomingEdges = false;
            for (Integer potentialParent : allVertices) {
                if (graph.getSuccessors(potentialParent).contains(vertex)) {
                    hasIncomingEdges = true;
                    break;
                }
            }
            if (!hasIncomingEdges) {
                startVertices.add(vertex);
            }
        }

        return startVertices;
    }

    // 根据给出的图的开始节点startVertex,和目标节点targetVertex，获取从startIndex到targetVertex的路径中，没有分叉的以targetVertex为终点的最长子路径
    public static List<Integer> getLongestNoBranchSubPath(Graph2<BotFlow> graph, Integer startVertex, Integer targetVertex) {
        List<Integer> fullPath = getPath(graph, startVertex, targetVertex);
        if (fullPath.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> longestSubPath = new ArrayList<>();
        for (int i = fullPath.size() - 1; i >= 0; i--) {
            Integer currentVertex = fullPath.get(i);

            if (currentVertex.equals(targetVertex)) {
                longestSubPath.addFirst(currentVertex);
                continue;
            }

            List<Integer> successors = graph.getSuccessors(currentVertex);
            if (successors.size() != 1 || !successors.getFirst().equals(fullPath.get(i + 1))) {
                break;
            }
            longestSubPath.addFirst(currentVertex);
        }

        return longestSubPath;
    }

}
