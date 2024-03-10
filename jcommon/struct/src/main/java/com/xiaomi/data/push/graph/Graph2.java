package com.xiaomi.data.push.graph;

import com.google.common.collect.Maps;

import java.util.*;

/**
 * @author goodjava@qq.com
 * @date 2024/3/12 14:49
 */
public class Graph2<D> {


    //存储顶点的map
    private Map<Integer, D> vertexMap = Maps.newHashMap();

    //存储边
    private Map<Integer, List<Integer>> adjMap = Maps.newHashMap();


    /**
     * 添加顶点
     *
     * @param vertex
     */
    public void addVertex(Vertex<D> vertex) {
        this.vertexMap.put(vertex.getV(), vertex.getData());
    }

    public void addEdge(int u, int v) {
        //添加边
        if (!adjMap.containsKey(u)) {
            adjMap.put(u, new ArrayList<>());
        }
        adjMap.get(u).add(v);
    }

    //删除这个顶点,且删除和它相关的所有边(class)
    public void removeVertex(int vertex) {
        // Remove the vertex from vertexMap
        if (!vertexMap.containsKey(vertex)) {
            throw new IllegalArgumentException("Vertex does not exist.");
        }
        vertexMap.remove(vertex);

        // Remove all edges associated with this vertex from adjMap
        if (adjMap.containsKey(vertex)) {
            adjMap.remove(vertex);
        }

        // Remove the vertex from all adjacency lists
        for (List<Integer> edges : adjMap.values()) {
            edges.removeIf(edge -> edge.equals(vertex));
        }
    }

    public D getVertexData(int id) {
        return vertexMap.get(id);
    }


    //帮我实现下拓扑排序(class)
    public List<Integer> topologicalSort() {
        List<Integer> result = new ArrayList<>();
        Map<Integer, Integer> inDegree = new HashMap<>();
        Queue<Integer> queue = new LinkedList<>();

        // Initialize in-degree of all vertices
        for (Integer v : vertexMap.keySet()) {
            inDegree.put(v, 0);
        }

        // Calculate in-degree of each vertex
        for (List<Integer> edges : adjMap.values()) {
            for (Integer edge : edges) {
                inDegree.put(edge, inDegree.get(edge) + 1);
            }
        }

        // Find all vertices with in-degree 0
        for (Map.Entry<Integer, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        // Process vertices with in-degree 0
        while (!queue.isEmpty()) {
            Integer vertex = queue.poll();
            result.add(vertex);

            // Decrease in-degree by 1 for all adjacent vertices
            if (adjMap.containsKey(vertex)) {
                for (Integer adjVertex : adjMap.get(vertex)) {
                    inDegree.put(adjVertex, inDegree.get(adjVertex) - 1);

                    // If in-degree becomes 0, add it to the queue
                    if (inDegree.get(adjVertex) == 0) {
                        queue.add(adjVertex);
                    }
                }
            }
        }

        // Check if there was a cycle
        if (result.size() != vertexMap.size()) {
            throw new IllegalStateException("Graph has a cycle, topological sort not possible");
        }

        return result;
    }


}
