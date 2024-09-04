package run.mone.ai.codegen.util;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.beetl.core.Configuration;
import org.beetl.core.Function;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com, HawickMason@xiaomi.com
 * @date 7/12/24 11:05 AM
 */
@Slf4j
public class TemplateUtils {

    public static String renderTemplate(String template, Map<String, ? extends Object> m) {
        return renderTemplate(template, m, Lists.newArrayList());
    }

    public static String renderTemplate(String template, Map<String, ? extends Object> m, List<Pair<String, Function>> functionList) {
        try {
            if (template == null || template.isEmpty()) {
                return "";
            }
            StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
            Configuration cfg = Configuration.defaultConfiguration();
            GroupTemplate gt = new GroupTemplate(resourceLoader, cfg, cfg.getClass().getClassLoader());
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

    //读取resources下的模板文件,然后渲染成String(class)
    @SneakyThrows
    public static String renderTemplateFromFile(String templateFileName, Map<String, ? extends Object> m) {
        try {
            InputStream is = TemplateUtils.class.getClassLoader().getResourceAsStream(templateFileName);
            //读取is成String
            String template = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return renderTemplate(template, m);
        } catch (IOException ex) {
            log.error("Error reading template file", ex);
        }
        return "";
    }

    @SneakyThrows
    public static String renderTemplateFromFileV2(String templateFileName, Map<String, ? extends Object> m) {
        try {
            InputStream is = TemplateUtils.class.getClassLoader().getResourceAsStream(templateFileName);
            if (is == null) {
                throw new IOException("Resource not found: " + templateFileName);
            }

            // 使用Channels和ByteBuffer读取所有字节
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int bytesRead;
            while ((bytesRead = Channels.newChannel(is).read(byteBuffer)) != -1) {
                if (byteBuffer.remaining() == 0) {
                    ByteBuffer newBuffer = ByteBuffer.allocate(byteBuffer.capacity() * 2);
                    byteBuffer.flip();
                    newBuffer.put(byteBuffer);
                    byteBuffer = newBuffer;
                }
            }
            byteBuffer.flip();
            byte[] bytes = new byte[byteBuffer.limit()];
            byteBuffer.get(bytes);

            // 将字节数组转换为字符串
            String template = new String(bytes, StandardCharsets.UTF_8);

            return renderTemplate(template, m);
        } catch (IOException ex) {
            log.error("Error reading template file", ex);
        }
        return "";
    }

    //把String写到指定文件中(class)
    public static void writeStringToFile(String content, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
        } catch (IOException ex) {
            log.error("Error writing to file", ex);
        }
    }
}
