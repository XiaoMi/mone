package run.mone.disruptor;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2022/4/27 13:35
 */
@Data
public class MapEvent implements Serializable {

    public MapEvent() {
        System.out.println("new");
    }

    private Map<String,Object> data = new HashMap<>();

}
