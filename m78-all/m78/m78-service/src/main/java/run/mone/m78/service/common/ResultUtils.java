package run.mone.m78.service.common;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import run.mone.m78.service.bo.ApiResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2024/2/22 17:41
 */
public class ResultUtils {


    public static List<Map<String, Object>> convertApiResultToJsonMaps(JsonObject apiResult) {
        JsonElement obj = apiResult;
        if (obj.isJsonArray()) {
            JsonArray array = obj.getAsJsonArray();
            List<Map<String, Object>> list = new ArrayList<>();
            array.forEach(it -> {
                Map<String, Object> map = Maps.newHashMap();
                if (it.isJsonObject()) {
                    if (it.isJsonObject()) {
                        JsonObject jsonObject = it.getAsJsonObject();
                        jsonObject.keySet().forEach(it2 -> map.put(it2, jsonObject.getAsString()));
                    }

                }
                if (it.isJsonPrimitive()) {
                    String v = it.getAsString();
                    map.put("value",v);
                }
                list.add(map);
            });
            return list;
        } else {
            JsonObject obj1= obj.getAsJsonObject();
            Map<String, Object> map = Maps.newHashMap();
            obj1.keySet().forEach(it -> map.put(it, obj1.get(it).toString()));
            return com.google.common.collect.Lists.newArrayList(map);
        }
    }

}
