package com.xiaomi.youpin.prometheus.agent.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FreeMarkerUtil {
    /**
     * 获取指定目录下的模板文件
     *
     * @param name       模板文件的名称
     * @param pathPrefix 模板文件的目录
     */
    private static Template getTemplate(String name, String pathPrefix) throws IOException {
        Configuration cfg = new Configuration();
        cfg.setClassForTemplateLoading(FreeMarkerUtil.class, pathPrefix); //设置模板文件的目录
        cfg.setDefaultEncoding("UTF-8");       //Set the default charset of the template files
        Template temp = cfg.getTemplate(name); //在模板文件目录中寻找名为"name"的模板文件
        return temp; //此时FreeMarker就会到类路径下的"pathPrefix"文件夹中寻找名为"name"的模板文件
    }


    /**
     * 根据模板文件输出内容到控制台
     *
     * @param name       模板文件的名称
     * @param pathPrefix 模板文件的目录
     * @param rootMap    模板的数据模型
     */
    public static String getContent(String pathPrefix, String name, Map<String, Object> rootMap) throws TemplateException, IOException {
        StringWriter writer = new StringWriter();
        getTemplate(name, pathPrefix).process(rootMap, writer);
        String jsonStr = writer.toString();
        JsonObject returnData = new JsonParser().parse(jsonStr).getAsJsonObject();//先将模板文件转为json对象，再转为json字符串
        return returnData.toString();
    }


    /**
     * 根据模板文件输出内容到指定的文件中
     *
     * @param name       模板文件的名称
     * @param pathPrefix 模板文件的目录
     * @param rootMap    模板的数据模型
     * @param file       内容的输出文件
     */
    public static void printFile(String pathPrefix, String name, Map<String, Object> rootMap, File file) throws TemplateException, IOException {
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
        getTemplate(name, pathPrefix).process(rootMap, out); //将模板文件内容以UTF-8编码输出到相应的流中
        if (null != out) {
            out.close();
        }
    }

    public static String freemarkerProcess(Map input, String templateStr) {
        StringTemplateLoader stringLoader = new StringTemplateLoader();
        String template = "content";
        stringLoader.putTemplate(template, templateStr);
        Configuration cfg = new Configuration();
        cfg.setTemplateLoader(stringLoader);
        try {
            Template templateCon = cfg.getTemplate(template);
            StringWriter writer = new StringWriter();
            templateCon.process(input, writer);
            return writer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {

        Map<String, Object> map = new HashMap<>();
       /* map.put("size", 0);
        map.put("gte_val", "1497283200000");
        map.put("lte_val", "1497928996980");
        map.put("min_val", "1497283200000");
        map.put("max_val", "1497928996980");
        map.put("interval", "21526566ms");*/
        map.put("env", "staging");
        map.put("serviceName", "zxw_test");
        map.put("title", "zxw_test");
        map.put("folderId", 851);
        map.put("folderUid", "GUoGPii7k");
        map.put("dataSource", "prometheus-systech");
        try {
            //获取工程路径
            String content = getContent("/", "grafana.ftl", map);
            System.out.println("返回的json" + "\n" + content + "\n");
        } catch (TemplateException | IOException e) {
            e.printStackTrace();
        }
    }
}
