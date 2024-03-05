package run.mone.struct.test;

import com.xiaomi.data.push.graph.Graph;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2024/3/1 10:55
 */
@Data
@Builder
public class VertexData implements Serializable {

    private String data;

    boolean finish;

    private int id;

    private String param;

    @Builder.Default
    private Map<String, String> input = new HashMap<>();

    @Builder.Default
    private Map<String, String> output = new HashMap<>();


    public void execute(Graph<VertexData> graph) {
        if (data.equals("选择器")) {
            List[] listArray = graph.getAdj();
            List<Integer> list = listArray[id];

            if (param.equals("abc")) {
                graph.getVertexData(2).setFinish(true);
            }

            System.out.println(list);

        }

        if (data.equals("开始")) {
            this.output.put("name", "zzy");
        }

        if (data.equals("代码")) {
            this.output.put("name1","aaaa");
        }
    }


}
