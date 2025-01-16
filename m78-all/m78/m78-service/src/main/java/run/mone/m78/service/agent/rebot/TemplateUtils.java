package run.mone.m78.service.agent.rebot;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.util.Lists;
import org.beetl.core.Configuration;
import org.beetl.core.Function;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;
import run.mone.m78.service.agent.rebot.function.ConditionalFunction;
import run.mone.m78.service.agent.rebot.function.PromptFunction;

import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2024/2/12 23:00
 */
@Slf4j
public class TemplateUtils {
    public static String renderTemplate(String prompt, Map<String, Object> map) {
        return null;
    }

    public static String renderTemplate2(String template, Map<String, ? extends Object> m) {
        return renderTemplate3(template, m, Lists.newArrayList(
                Pair.of(PromptFunction.name, new PromptFunction()),
                Pair.of(ConditionalFunction.name, new ConditionalFunction())
        ));
    }

    public static String renderTemplate3(String template, Map<String, ? extends Object> m, List<Pair<String, Function>> functionList) {
        try {
            if (template == null || template.isEmpty()) {
                return "";
            }
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
