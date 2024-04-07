package run.mone.local.docean.util;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.beetl.core.Configuration;
import org.beetl.core.Function;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;
import run.mone.local.docean.util.template.function.JsonValueFunction;

import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2024/3/4 15:44
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
            m.forEach((k, v) -> t.binding(k, v));
            String str = t.render();
            return str;
        } catch (Throwable ex) {
            log.error("renderTemplate", ex);
        }
        return "";
    }


}
