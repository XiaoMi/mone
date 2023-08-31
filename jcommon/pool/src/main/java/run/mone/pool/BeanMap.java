package run.mone.pool;

import java.util.HashMap;

/**
 * @author goodjava@qq.com
 * @date 2022/5/3 19:13
 */
public class BeanMap {

    private HashMap<String, Object> m = new HashMap<>();

    public <T> T get(String key) {
        return (T) m.get(key);
    }

    public void put(String key, Object value) {
        m.put(key, value);
    }

}
