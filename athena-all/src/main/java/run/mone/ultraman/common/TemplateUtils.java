package run.mone.ultraman.common;

import com.google.common.collect.Maps;
import org.apache.commons.compress.utils.Lists;
import org.beetl.core.Configuration;
import org.beetl.core.Function;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author goodjava@qq.com
 * @date 2023/5/30 13:23
 */
public class TemplateUtils {


    public static String renderTemplate(String template, Map<String, Object> m) {
        return renderTemplate(template, m, Lists.newArrayList());
    }

    public static String renderTemplate(String template, Map<String, Object> m, List<Function> functionList) {
        try {
            StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
            Configuration cfg = Configuration.defaultConfiguration();
            GroupTemplate gt = new GroupTemplate(resourceLoader, cfg, cfg.getClass().getClassLoader());
            functionList.forEach(it -> gt.registerFunction(it.toString(), it));
            Template t = gt.getTemplate(template);
            m.forEach((k, v) -> t.binding(k, v));
            String str = t.render();
            return str;
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public static Map<String, Integer> getParams(String template) {
        try {
            StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
            Configuration cfg = Configuration.defaultConfiguration();
            GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
            Template t = gt.getTemplate(template);
            Map<String, Integer> map = t.program.metaData.globalIndexMap;
            return map;
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return Maps.newHashMap();
    }


}
