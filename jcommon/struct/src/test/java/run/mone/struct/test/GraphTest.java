package run.mone.struct.test;

import com.xiaomi.data.push.graph.Graph;
import com.xiaomi.data.push.graph.Graph2;
import com.xiaomi.data.push.graph.Vertex;
import org.junit.Test;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2024/3/1 10:53
 */
public class GraphTest {


    @Test
    public void test1() {
        Graph2<VertexData> graph = new Graph2<>();

        graph.addVertex(new Vertex<>(44, VertexData.builder().data("执行").id(0).build()));
        graph.addVertex(new Vertex<>(88, VertexData.builder().data("aaa").id(0).build()));
        graph.addVertex(new Vertex<>(33, VertexData.builder().data("开始").id(0).build()));
        graph.addVertex(new Vertex<>(22, VertexData.builder().data("结束").id(0).build()));

        graph.addEdge(33,44);
        graph.addEdge(44,22);
        graph.addEdge(22,88);


//        graph.removeVertex(44);

        List<Integer> list = graph.topologicalSort();
        System.out.println(list);
    }

    @Test
    public void initializeAndTopologicallySortGraph() {
        Graph<VertexData> graph = new Graph<>(5);
        graph.addVertex(new Vertex<>(0, VertexData.builder().data("开始").id(0).build()));
        graph.addVertex(new Vertex<>(1, VertexData.builder().data("代码").id(1).build()));
        graph.addVertex(new Vertex<>(2, VertexData.builder().data("大模型").id(2).build()));
        graph.addVertex(new Vertex<>(3, VertexData.builder().data("选择器").id(3).param("abc").build()));
        graph.addVertex(new Vertex<>(4, VertexData.builder().data("结束").id(4).build()));

        graph.addEdge(0, 1);
        graph.addEdge(1, 3);
        graph.addEdge(3, 2);
        graph.addEdge(3, 4);

        List<Integer> list = graph.topologicalSort();
        System.out.println(list);

        GraphContext context = GraphContext.builder().build();
        list.stream().forEach(it -> {
            VertexData data = graph.getVertexData(it);
            if (!data.isFinish()) {
                data.execute(graph);
                context.getInput().put(data.getId(),data.getOutput());
                System.out.println(data.getData());
                data.setFinish(true);
            }
        });

        System.out.println(context);


    }

}
