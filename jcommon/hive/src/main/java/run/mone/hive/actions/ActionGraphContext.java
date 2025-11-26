package run.mone.hive.actions;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2025/5/16 16:49
 */
@Data
public class ActionGraphContext {

    private Map<String, Object> map = new HashMap<>();

    public void put(String key, Object value) {
        map.put(key, value);
    }

    public Object get(String key) {
        return map.get(key);
    }


}
