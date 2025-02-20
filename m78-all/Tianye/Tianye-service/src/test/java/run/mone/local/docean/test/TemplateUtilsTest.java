package run.mone.local.docean.test;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.commons.lang3.tuple.Pair;
import org.beetl.core.Function;
import org.junit.jupiter.api.Test;
import run.mone.local.docean.util.GsonUtils;
import run.mone.local.docean.util.TemplateUtils;
import run.mone.local.docean.util.template.function.JsonValueFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author goodjava@qq.com
 * @date 2024/3/4 15:49
 */
public class TemplateUtilsTest {


    @Test
    public void testRenderTemplate() {
        String template = "Hello \\${who}. hi, ${name}!";
        Map<String, String> map = new HashMap<>();
        map.put("name", "World");

        String result = TemplateUtils.renderTemplate(template, map);
        assertEquals("Hello, World!", result);
    }

    @Test
    public void test1(){
        // Example usage
        Map<String, Object> data = new HashMap<>();
        JsonObject obj = new JsonObject();
        obj.addProperty("a1", "2");
        data.put("a", obj);
        data.put("c", new JsonPrimitive("nestedMap"));
        JsonArray array = new JsonArray();
        array.add(obj);
        array.add(obj);

        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> item1 = new HashMap<>();
        item1.put("a1", "2");
        list.add(item1);
        data.put("b", list);

        data.put("j", new JsonPrimitive(""));

        System.out.println(data);
        String template = "The value is: ${a.a1}, ${c}, ${j}";
        String result = TemplateUtils.renderTemplate(template, data);
        System.out.println(result);
    }


    @Test
    public void testRenderTemplate2() {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", "zzy");
        obj.add("list", GsonUtils.gson.toJsonTree(Lists.newArrayList("1", "2")));

        String template = "Hello, ${name}!  ${json_value('v','name')} ${json_value('v','list.1')}";
        Map<String, Object> map = new HashMap<>();
        map.put("name", "World");
        map.put("v", obj);

        List<Pair<String, Function>> functionList = new ArrayList<>();
        functionList.add(Pair.of(JsonValueFunction.name, new JsonValueFunction()));

        String result = TemplateUtils.renderTemplate(template, map, functionList);
        System.out.println(result);
    }


}
