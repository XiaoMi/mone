package run.mone.local.docean.controller.test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import org.junit.Test;
import run.mone.local.docean.util.HttpUtils;

/**
 * @author goodjava@qq.com
 * @date 2024/2/28 11:37
 */
public class UtilsTest {


    @SneakyThrows
    @Test
    public void testChatGptApiInteraction() {
        JsonObject jo = new JsonObject();
        jo.addProperty("promptName", "minzai");
        JsonObject params = new JsonObject();
        params.addProperty("list", "");
        params.addProperty("question", "1+1=?");
        jo.add("params", params);
        JsonArray array = new JsonArray();
        array.add("content");
        jo.add("keys", array);
        JsonElement res = HttpUtils.postJson("https://mone.test.mi.com/open-apis/ai-plugin-new/chatgpt/query", jo);
        System.out.println(res);
    }
}
