package run.mone.local.docean.controller.test;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;
import run.mone.local.docean.util.JsonElementUtils;
import run.mone.local.docean.util.GsonUtils;


/**
 * @author wmin
 * @date 2024/3/14
 */
public class JsonElementParseTest {

    @Test
    public void testParseJson() {
        String jsonData =
                "{ \"name\": \"tom\", \"courses\": [ { \"courseName\": \"math\", \"courseGrade\": \"89\" } ], \"site\": { \"sites\": [ { \"id\": \"1\", \"name\": \"名称\", \"url\": \"www.XX.com\" }, { \"id\": \"2\", \"name\": \"名称2\", \"url\": \"www.yy.com\" } ] } }";
        JsonParser parser = new JsonParser();
        JsonElement data = parser.parse(jsonData);

        System.out.println(JsonElementUtils.getValue(data));

        try {
            JsonElement nameValue = JsonElementUtils.queryFieldValue(data, "name");
            System.out.println("[Name:] " + JsonElementUtils.getValue(nameValue));

            JsonElement courseNameValue = JsonElementUtils.queryFieldValue(data, "courses");
            System.out.println("[course:] " + JsonElementUtils.getValue(courseNameValue));

            JsonElement sitesValue = JsonElementUtils.queryFieldValue(data, "site");
            System.out.println("[site:] " + JsonElementUtils.getValue(sitesValue));

            JsonElement siteIdValue = JsonElementUtils.queryFieldValue(data, "site.sites.0.id");
            System.out.println("[siteId:] " + JsonElementUtils.getValue(siteIdValue));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testGetFieldType() {
        String jsonString = "[{\"name\":\"a\",\"valueType\":\"Object\",\"children\":[{\"name\":\"a1\",\"valueType\":\"String\",\"children\":[],\"id\":\"075c\"}],\"id\":\"f543\"}]";
        //将s转成JsonElement，然后调用getFieldType,获取a1的类型
        System.out.println(JsonElementUtils.getFieldType(jsonString, "a1"));

    }


    @Test
    public void test2() {
        JsonObject obj = new JsonObject();
        obj.add("list", GsonUtils.gson.toJsonTree(Lists.newArrayList("1","2","3")));
        System.out.println(JsonElementUtils.queryFieldValue(obj, "list"));

    }
}

