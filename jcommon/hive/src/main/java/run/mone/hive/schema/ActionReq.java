package run.mone.hive.schema;

import lombok.Data;

import java.util.HashMap;

/**
 * @author goodjava@qq.com
 * @date 2025/1/3 16:41
 */
@Data
public class ActionReq extends HashMap<String, Object> {

    private Message message;

    public <T> T getWithKey(String key) {
        return (T) super.get(key);
    }

}
