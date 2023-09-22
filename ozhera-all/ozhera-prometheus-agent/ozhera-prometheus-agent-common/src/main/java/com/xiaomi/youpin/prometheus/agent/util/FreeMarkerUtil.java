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
     * Get the template file under the specified directory.
     *
     * @param name       The name of the template file.
     * @param pathPrefix The directory of the template file.
     */
    private static Template getTemplate(String name, String pathPrefix) throws IOException {
        Configuration cfg = new Configuration();
        cfg.setClassForTemplateLoading(FreeMarkerUtil.class, pathPrefix); // Set the directory for template files
        cfg.setDefaultEncoding("UTF-8");       //Set the default charset of the template files
        Template temp = cfg.getTemplate(name); //Search for a template file named "name" in the template file directory.
        return temp; //At this time, FreeMarker will search for the template file named "name" in the "pathPrefix" folder in the classpath.
    }


    /**
     * Output the content according to the template file to the console.
     *
     * @param name       The name of the template file
     * @param pathPrefix The directory of the template file
     * @param rootMap    The data model of the template
     */
    public static String getContent(String pathPrefix, String name, Map<String, Object> rootMap) throws TemplateException, IOException {
        StringWriter writer = new StringWriter();
        getTemplate(name, pathPrefix).process(rootMap, writer);
        String jsonStr = writer.toString();
        JsonObject returnData = new JsonParser().parse(jsonStr).getAsJsonObject();//First convert the template file into a JSON object, then convert it into a JSON string.
        return returnData.toString();
    }

    public static String getContentExceptJson(String pathPrefix, String name, Map<String, Object> rootMap) throws TemplateException, IOException {
        StringWriter writer = new StringWriter();
        getTemplate(name, pathPrefix).process(rootMap, writer);
        String str = writer.toString();
        return str;
    }


    /**
     * Output the content according to the template file to the specified file.
     *
     * @param name       The name of the template file
     * @param pathPrefix The directory of the template file
     * @param rootMap    The data model of the template
     * @param file       The output file of the content
     */
    public static void printFile(String pathPrefix, String name, Map<String, Object> rootMap, File file) throws TemplateException, IOException {
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
        getTemplate(name, pathPrefix).process(rootMap, out); //Output the content of the template file to the corresponding stream encoded in UTF-8.
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
            //Get project path
            String content = getContent("/", "grafana.ftl", map);
            System.out.println("返回的json" + "\n" + content + "\n");
        } catch (TemplateException | IOException e) {
            e.printStackTrace();
        }
    }
}
