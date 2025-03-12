package run.mone.mcp.hammerspoon.function.trigertrade.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
/**
 * @author shanwb
 * @date 2025-03-12
 */
public class TemplateUtils {

    private static final Configuration cfg;

    static {
        // 初始化Freemarker配置
        cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
    }

    /**
     * 使用Freemarker处理模板字符串
     *
     * @param templateContent 模板内容字符串
     * @param dataModel 数据模型对象，可以是POJO
     * @return 处理后的字符串
     * @throws IOException 如果模板处理过程中出错
     * @throws TemplateException 如果模板处理过程中出错
     */
    public static String processTemplateContent(String templateContent, Object dataModel){
        try {
            // 从字符串创建模板
            Template template = new Template("stringTemplate",
                    new StringReader(templateContent), cfg);

            // 处理模板
            StringWriter writer = new StringWriter();
            template.process(dataModel, writer);
            return writer.toString();
        } catch (IOException | TemplateException e) {
            throw new RuntimeException("processTemplateContent error:", e);
        }
    }

}
