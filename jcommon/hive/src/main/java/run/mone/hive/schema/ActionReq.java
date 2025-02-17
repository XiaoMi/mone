package run.mone.hive.schema;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import run.mone.hive.Environment;
import run.mone.hive.memory.Memory;
import run.mone.hive.roles.Role;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2025/1/3 16:41
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActionReq {

    private Message message;

    private Role role;

    private Memory memory;

    private Environment env;

    private List<Message> history;

    //用来传递一些上下文
    private JsonObject context = new JsonObject();

    private ActionContext ac;


}
