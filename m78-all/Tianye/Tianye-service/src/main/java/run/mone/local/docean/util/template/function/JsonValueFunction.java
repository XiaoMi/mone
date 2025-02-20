package run.mone.local.docean.util.template.function;

import com.google.gson.JsonElement;
import org.beetl.core.Context;
import org.beetl.core.Function;
import run.mone.local.docean.util.JsonElementUtils;

import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2024/3/22 15:08
 */
public class JsonValueFunction implements Function {


    public static final String name = "json_value";

    @Override
    public Object call(Object[] paras, Context ctx) {
        String key = paras[0].toString();
        Map<String, Object> map = ctx.globalVar;
        Object value = map.get(key);
        String name = paras[1].toString();
        JsonElement element = (JsonElement) value;
        JsonElement jsonValue = JsonElementUtils.queryFieldValue(element, name);
        return JsonElementUtils.getValue(jsonValue);
    }
}
