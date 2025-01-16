package run.mone.m78.service.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiaomi.hera.trace.context.TraceIdUtil;

import lombok.extern.slf4j.Slf4j;
import run.mone.m78.common.WebsocketMessageType;
import run.mone.m78.service.database.UUIDUtil;

/**
 * @author goodjava@qq.com
 * @date 2024/1/9 17:11
 */
@Slf4j
public class GsonUtils {


    public static Gson gson = new Gson();

    public static Map<String, JsonElement> convertToJsonElementMap(Map<String, Object> originalMap) {
        if (MapUtils.isEmpty(originalMap)) {
            return new HashMap<>();
        }
        Gson gson = new Gson();
        Map<String, JsonElement> jsonMap = new HashMap<>();

        for (Map.Entry<String, Object> entry : originalMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            JsonElement jsonElement = gson.toJsonTree(value); // 将对象转换为JsonElement
            jsonMap.put(key, jsonElement);
        }

        return jsonMap;
    }

    /**
     * 在现有json结构中添加message type，为了兼容老的逻辑，不改动现有的返回结构
     */
    public static String addMessageType(String json, String messageType) {
        if (StringUtils.isEmpty(json)) {
            return json;
        }
        try {
            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
            jsonObject.addProperty(WebsocketMessageType.MESSAGE_TYPE_KEY, messageType);
            // add traceId
            jsonObject.addProperty("traceId", TraceIdUtil.traceId());
            return gson.toJson(jsonObject);
        } catch (Exception e) {
            log.error("GsonUtils addMessageType error, json : " + json + ", messageType : " + messageType, e);
        }
        return json;
    }

    /**
     * 在现有json结构中添加message type与msgId，为了兼容老的逻辑，不改动现有的返回结构
     */
    public static String addMessageTypeAndMsgId(String json, String messageType, String msgId) {
        if (StringUtils.isEmpty(json)) {
            return json;
        }
        try {
            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
            jsonObject.addProperty(WebsocketMessageType.MESSAGE_TYPE_KEY, messageType);
            if (jsonObject.get("msgId") == null && StringUtils.isNotBlank(msgId)) {
                jsonObject.addProperty("msgId", Joiner.on("$").join(UUIDUtil.randomNanoId(), msgId));
            }
            // add traceId
            jsonObject.addProperty("traceId", TraceIdUtil.traceId());
            return gson.toJson(jsonObject);
        } catch (Exception e) {
            log.error("GsonUtils addMessageType error, json : " + json + ", messageType : " + messageType, e);
        }
        return json;
    }

    public static String addMessageType(Object json, String messageType) {
        return addMessageType(gson.toJson(json), messageType);
    }

    public static String getStringFromJson(String json, String key) {
        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
        return  jsonObject.has(key) ? jsonObject.get(key).getAsString() : "";
    }

}
