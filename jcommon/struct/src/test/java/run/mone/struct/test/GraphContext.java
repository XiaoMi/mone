package run.mone.struct.test;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2024/3/1 14:01
 */
@Data
@Builder
public class GraphContext {


    @Builder.Default
    private Map<Integer, Map<String, String>> input = new HashMap<>();


}
