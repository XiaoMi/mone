package run.mone.hive.schema;

import lombok.Data;
import run.mone.hive.Environment;
import run.mone.hive.memory.Memory;
import run.mone.hive.roles.Role;

import java.util.HashMap;

/**
 * @author goodjava@qq.com
 * @date 2025/1/3 16:41
 */
@Data
public class ActionReq extends HashMap<String, Object> {

    private Message message;

    private Role role;

    private Memory memory;

    private Environment env;

    public <T> T getWithKey(String key) {
        return (T) super.get(key);
    }

}
