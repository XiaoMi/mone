package run.mone.local.docean.test;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.tuple.Pair;
import org.beetl.core.Context;
import org.beetl.core.Function;
import org.junit.jupiter.api.Test;
import run.mone.local.docean.fsm.JsonElementUtils;
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
        String template = "Hello, ${name}!";
        Map<String, String> map = new HashMap<>();
        map.put("name", "World");

        String result = TemplateUtils.renderTemplate(template, map);
        assertEquals("Hello, World!", result);
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
