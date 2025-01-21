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
        JsonElement res = HttpUtils.postJson("https://X.com/open-apis/ai-plugin-new/chatgpt/query", jo);
        System.out.println(res);
    }


    @Test
    public void test1() {
        String str = "{\"outputList\":{\"output\":\"/**\n * 广告服务\n */\n@Service\npublic class AdvertisementService extends MongoService<Advertisement> {\n\n    public AdvertisementService() {\n        super(Advertisement.class);\n    }\n\n    /**\n     * 根据广告位置获取广告列表\n     *\n     * @param position 广告位置\n     * @return 广告列表\n     */\n    public List<Advertisement> findByPosition(int position) {\n        Filter<Advertisement> filter = Filters.eq(\\\"position\\\", position);\n        return this.findAll(filter);\n    }\n\n    /**\n     * 根据时间范围获取有效广告列表\n     *\n     * @param start 开始时间\n     * @param end 结束时间\n     * @return 有效广告列表\n     */\n    public List<Advertisement> findValidByTime(long start, long end) {\n        Filter<Advertisement> filter = Filters.and(\n                Filters.lte(\\\"startTime\\\", end),\n                Filters.gte(\\\"endTime\\\", start),\n                Filters.eq(\\\"enabled\\\", true)\n        );\n        return this.findAll(filter);\n    }\n\n    /**\n     * 将广告实体转为Document对象\n     *\n     * @param advertisement 广告实体\n     * @return Document对象\n     */\n    public Document toDocument(Advertisement advertisement) {\n        return new Document(\\\"id\\\", advertisement.getId())\n                .append(\\\"title\\\", advertisement.getTitle())\n                .append(\\\"description\\\", advertisement.getDescription())\n                .append(\\\"imageUrl\\\", advertisement.getImageUrl())\n                .append(\\\"landingUrl\\\", advertisement.getLandingUrl())\n                .append(\\\"startTime\\\", advertisement.getStartTime())\n                .append(\\\"endTime\\\", advertisement.getEndTime())\n                .append(\\\"position\\\", advertisement.getPosition())\n                .append(\\\"enabled\\\", advertisement.isEnabled())\n                .append(\\\"state\\\", advertisement.getState())\n                .append(\\\"ctime\\\", advertisement.getCtime())\n                .append(\\\"utime\\\", advertisement.getUtime())\n                .append(\\\"version\\\", advertisement.getVersion());\n    }\n}\"}}";
        System.out.println(str);
    }
}
