package run.mone.hive.schema;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.FluxSink;

import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2025/1/7 11:02
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActionContext {

    private JsonObject ctx = new JsonObject();

    private RoleContext.ReactMode reactMode;

    private Map<String, String> data;

    //和外部通信的
    private FluxSink sink;

}
