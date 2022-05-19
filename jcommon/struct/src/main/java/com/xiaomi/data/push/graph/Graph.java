package com.xiaomi.data.push.graph;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Created by  zhangzhiyong
 */
public class Graph<D> {

    public int V;

    private int[] array;

    List<Integer> adj[];//有向边

    private Map<Integer, D> vertexMap = Maps.newHashMap();


    public D getVertexData(int index) {
        return vertexMap.get(index);
    }


    public Graph(int V)// Constructor
    {
        this.V = V;//顶点数量
        adj = new ArrayList[V];//边的集合的数量
        for (int i = 0; i < V; i++) {
            adj[i] = new ArrayList<>();//舒适化
        }
        array = new int[V];//用来存储访问过没访问过
    }

    /**
     * 添加顶点
     *
     * @param vertex
     */
    public void addVertex(Vertex<D> vertex) {
        this.vertexMap.put(vertex.getV(), vertex.getData());
    }


    public List<Integer> dependList(int v) {
        List result = Lists.newArrayList();
        for (int i = 0; i < adj.length; i++) {
            if (i == v) {
                continue;
            }
            boolean match = adj[i].stream().anyMatch(it -> it == v);
            if (match) {
                result.add(i);
            }
        }
        return result;
    }

    //添加边
    // function to add an edge to graph
    public void addEdge(int u, int v)//加入边
    {
        adj[u].add(v);
    }

    private Stack<Integer> stack = new Stack<>();
    private Queue<Integer> queue = new LinkedList<>();

    private int find(int v) {
        List<Integer> list = adj[v];
        for (int i : list) {
            if (array[i] == 0) {
                return i;
            }
        }
        return -1;
    }

    public void dfs() {
        stack.push(0);
        System.out.println(0);
        while (!stack.empty()) {
            int v = stack.peek();
            int v2 = find(v);
            if (v2 != -1) {
                array[v2] = 1;//打标志
                System.out.println(v2);
                stack.push(v2);
            } else {//找不到了
                stack.pop();//弹出去
            }
        }
    }

    public void bfs(int vv, BiFunction<Integer, D, Boolean> consumer) {
        b:
        if (array[vv] == 0) {
            queue.add(vv);
            Boolean r = consumer.apply(vv, this.vertexMap.get(vv));
            if (!r) {
                break b;
            }
            while (!queue.isEmpty()) {
                int v = queue.peek();
                int v2 = -1;
                while ((v2 = find(v)) != -1) {
                    D d = this.vertexMap.get(v2);
                    Boolean r2 = consumer.apply(v2, d);
                    if (!r2) {
                        break b;
                    }
                    array[v2] = 1;//打标志
                    queue.add(v2);
                }
                queue.poll();
            }
        }
    }


    /**
     * bfs 遍历所有
     *
     * @param consumer
     */
    public void bfsAll(BiFunction<Integer, D, Boolean> consumer) {
        //先拓扑排序
        List<Integer> list = this.topologicalSort();
        for (int vv : list) {
            b:
            if (array[vv] == 0) {
                array[vv] = 1;
                queue.add(vv);
                Boolean r = consumer.apply(vv, this.vertexMap.get(vv));
                if (!r) {
                    break b;
                }
                while (!queue.isEmpty()) {
                    int v = queue.peek();
                    int v2 = -1;
                    while ((v2 = find(v)) != -1) {
                        array[v2] = 1;
                        D d = this.vertexMap.get(v2);
                        Boolean r2 = consumer.apply(v2, d);
                        if (!r2) {
                            break b;
                        }
                        //打标志
                        queue.add(v2);
                    }
                    queue.poll();
                }
            }
        }

    }


    /**
     * 访问的节点都重置为0
     */
    public void bfsReset() {
        Arrays.fill(array, 0);
    }


    // prints a Topological Sort of the complete graph
    public List<Integer> topologicalSort()//拓扑排序
    {
        // Create a array to store indegrees of all
        // vertices. Initialize all indegrees as 0.
        int indegree[] = new int[V];

        // Traverse adjacency lists to fill indegrees of
        // vertices. This step takes O(V+E) time
        for (int i = 0; i < V; i++) {
            ArrayList<Integer> temp = (ArrayList<Integer>) adj[i];
            for (int node : temp) {
                indegree[node]++;
            }
        }

        // Create a queue and enqueue all vertices with
        // indegree 0
        Queue<Integer> q = new LinkedList<Integer>();
        //最开始没有依赖的顶点
        for (int i = 0; i < V; i++) {
            if (indegree[i] == 0) {
                q.add(i);
            }
        }

        // Initialize count of visited vertices
        int cnt = 0;

        // Create a vector to store result (A topological
        // ordering of the vertices)
        List<Integer> topOrder = new ArrayList<>();
        while (!q.isEmpty()) {
            // Extract front of queue (or perform dequeue)
            // and add it to topological order
            int u = q.poll();//拿出没有依赖的顶点
            topOrder.add(u);//放入排序中

            // Iterate through all its neighbouring nodes
            // of dequeued node u and decrease their in-degree
            // by 1
            for (int node : adj[u])//遍历顶点是u的边
            {
                // If in-degree becomes zero, add it to queue
                if (--indegree[node] == 0) {//度减去-1 如果发现没有依赖了,则加入没有依赖的集合
                    q.add(node);
                }
            }
            cnt++;//又加入了一个没有依赖的顶点
        }

        // Check if there was a cycle
        //有循环的边
        if (cnt != V) {
            System.out.println("There exists a cycle in the graph");
            throw new RuntimeException("There exists a cycle in the graph");
        }

        // Print topological order
        //按排序输出
        for (int i : topOrder) {
            System.out.print(i + " ");
        }
        return topOrder;
    }

    public Map<Integer, D> getVertexMap() {
        return vertexMap;
    }
}
