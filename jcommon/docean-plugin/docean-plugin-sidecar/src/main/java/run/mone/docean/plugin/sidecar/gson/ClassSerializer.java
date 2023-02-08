package run.mone.docean.plugin.sidecar.gson;

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * @author goodjava@qq.com
 * @date 2022/11/7 17:48
 */
public class ClassSerializer implements JsonSerializer<Class> {
    @Override
    public JsonElement serialize(Class src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jObj = new JsonObject();
        jObj.add("className", new JsonPrimitive(src.getName()));
        return jObj;
    }
}
