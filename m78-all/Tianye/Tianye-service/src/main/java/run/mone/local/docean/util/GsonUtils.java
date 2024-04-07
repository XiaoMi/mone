package run.mone.local.docean.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author goodjava@qq.com
 * @date 2024/3/4 11:34
 */
public class GsonUtils {

    public static Gson gson = new Gson();

    public static JsonObject objectToJsonObject(Object object) {
        Gson gson = new GsonBuilder().create();
        JsonElement jsonElement = gson.toJsonTree(object);
        if (jsonElement.isJsonObject()) {
            return jsonElement.getAsJsonObject();
        } else if (jsonElement.isJsonArray()) {
            JsonObject res = new JsonObject();
            res.add("outputList", jsonElement);
            res.addProperty("output", jsonElement.toString());
            return res;
        } else {
            JsonObject res = new JsonObject();
            res.add("data", jsonElement);
            return res;
        }
    }


}
