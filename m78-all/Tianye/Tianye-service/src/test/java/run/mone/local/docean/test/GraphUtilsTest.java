package run.mone.local.docean.test;

import com.xiaomi.data.push.graph.Graph2;
import com.xiaomi.data.push.graph.Vertex;
import org.junit.jupiter.api.Test;
import run.mone.local.docean.fsm.BotFlow;
import run.mone.local.docean.util.GraphUtils;

/**
 * @author goodjava@qq.com
 * @date 2024/9/6 08:09
 */
public class GraphUtilsTest {


    @Test
    public void test1() {
        Graph2<BotFlow> graph2 = new Graph2<>();
        graph2.addVertex(new Vertex<>(0, null));
        graph2.addVertex(new Vertex<>(1, null));
        graph2.addVertex(new Vertex<>(2, null));

        graph2.addEdge(0, 1);
        graph2.addEdge(1, 2);
        graph2.addEdge(2, 0);

        System.out.println(GraphUtils.hasCycle(graph2));
    }

    @Test
    public void test2() {
        Graph2<BotFlow> graph2 = new Graph2<>();
        graph2.addVertex(new Vertex<>(0, null));
        graph2.addVertex(new Vertex<>(1, null));
        graph2.addVertex(new Vertex<>(2, null));
        graph2.addVertex(new Vertex<>(3, null));

        graph2.addEdge(0, 3);
        graph2.addEdge(0, 1);
        graph2.addEdge(1, 2);

        System.out.println(GraphUtils.getPath(graph2, 0, 2));
    }

    @Test
    public void testGetLongestNoBranchSubPath() {
        Graph2<BotFlow> graph2 = new Graph2<>();
        graph2.addVertex(new Vertex<>(0, null));
        graph2.addVertex(new Vertex<>(1, null));
        graph2.addVertex(new Vertex<>(2, null));
        graph2.addVertex(new Vertex<>(3, null));
        graph2.addVertex(new Vertex<>(4, null));

        graph2.addEdge(0, 3);
        graph2.addEdge(0, 1);
        graph2.addEdge(1, 2);
        graph2.addEdge(2, 4);
        System.out.println(GraphUtils.getLongestNoBranchSubPath(graph2, 0, 4));
    }

}
