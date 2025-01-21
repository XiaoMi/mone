package run.mone.local.docean.util;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.beetl.core.Configuration;
import org.beetl.core.Function;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;
import run.mone.local.docean.util.template.function.JsonValueFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Template utility class for rendering templates with nested placeholders.
 * Supports placeholders like ${a.b}.
 *
 * Author: goodjava@qq.com
 * Date: 2024/3/4 15:44
 */
@Slf4j
public class TemplateUtils {

    public static String renderTemplate(String template, Map<String, ? extends Object> m) {
        return renderTemplate(template, m, Lists.newArrayList(Pair.of(JsonValueFunction.name, new JsonValueFunction())));
    }

    public static String renderTemplate(String template, Map<String, ? extends Object> m, List<Pair<String, Function>> functionList) {
        try {
            StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
            Configuration cfg = Configuration.defaultConfiguration();
            GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
            functionList.forEach(it -> gt.registerFunction(it.getKey(), it.getValue()));
            Template t = gt.getTemplate(template);

            Map<String, Object> result = new HashMap<>();
            m.forEach((k, v) -> result.put(k, preprocessValue(v)));

            result.forEach((k, v) -> t.binding(k, v));
            String str = t.render();
            return str;
        } catch (Throwable ex) {
            log.error("renderTemplate", ex);
        }
        return "";
    }

    private static Object preprocessValue(Object value) {
        if (value instanceof JsonPrimitive) {
            JsonPrimitive jp = (JsonPrimitive) value;
            if (jp.isString()) {
                return jp.getAsString();
            } else if (jp.isNumber()) {
                return jp.getAsNumber();
            } else if (jp.isBoolean()) {
                return jp.getAsBoolean();
            }
        } else if (value instanceof JsonObject) {
            Map<String, Object> map = new HashMap<>();
            ((JsonObject) value).entrySet().forEach(entry -> map.put(entry.getKey(), preprocessValue(entry.getValue())));
            return map;
        } else if (value instanceof JsonArray) {
            List<Object> list = new ArrayList<>();
            ((JsonArray) value).forEach(element -> list.add(preprocessValue(element)));
            return list;
        }
        return value;
    }

}
