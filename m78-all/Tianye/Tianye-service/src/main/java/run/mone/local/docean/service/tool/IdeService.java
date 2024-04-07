package run.mone.local.docean.service.tool;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import run.mone.local.docean.util.HttpUtils;

/**
 * @author goodjava@qq.com
 * @date 2024/2/27 14:02
 * <p>
 * 操作本地ide的一些行为
 */
@Slf4j
@Service
public class IdeService implements ToolService {


    /**
     * 执行IDE服务的方法。
     * 支持能力
     * 关闭所有tab标签:{"cmd":"close_all_tab"}
     */
    @SneakyThrows
    public JsonElement execute(JsonElement params) {
        //支持调用ide,比如关闭所有tab,编写代码等
        log.info("execute ide service params:{}", params);
        if (params.isJsonObject()) {
            JsonObject obj = (JsonObject) params;
            HttpUtils.postJson("http://127.0.0.1:3458/tianye", obj);
        }
        JsonObject res = new JsonObject();
        res.addProperty("code", 0);
        return res;
    }

    @Override
    public String author() {
        return "goodjava@qq.com";
    }

    @Override
    public String version() {
        return "0.0.1:2024-02-27";
    }
}
