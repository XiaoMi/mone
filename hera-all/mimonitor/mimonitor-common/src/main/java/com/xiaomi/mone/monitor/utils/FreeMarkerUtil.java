package com.xiaomi.mone.monitor.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gaoxihui
 * @date 2021/7/7 8:11 下午
 */
public class FreeMarkerUtil {


    /**
     * Get template files under the specified directory
     * @param name       The name of the template file
     * @param pathPrefix The directory of the template file
     */
    private static Template getTemplate(String name, String pathPrefix) throws IOException {
        Configuration cfg = new Configuration();
        cfg.setClassForTemplateLoading(FreeMarkerUtil.class, pathPrefix); //Set the directory of the template files.
        cfg.setDefaultEncoding("UTF-8");       //Set the default charset of the template files
        Template temp = cfg.getTemplate(name); //Search for the template file named "name" in the template file directory.

        return temp; //At this point, FreeMarker will look for a template file named "name" in the "pathPrefix" folder under the classpath.

    }


    /**
     * Output content to the console based on the template file.
     * @param name       The name of the template file.
     * @param pathPrefix The directory of the template file.
     * @param rootMap    The data model of the template.
     */
    public static String getContent(String pathPrefix, String name, Map<String,Object> rootMap) throws TemplateException, IOException{
        StringWriter writer = new StringWriter();
        getTemplate(name, pathPrefix).process(rootMap, writer);
        String jsonStr = writer.toString();
        JsonObject returnData = new JsonParser().parse(jsonStr).getAsJsonObject();//先将模板文件转为json对象，再转为json字符串
        return returnData.toString();
    }


    /**
     * Output content to a specified file based on a template file.
     * @param name       The name of the template file.
     * @param pathPrefix The directory of the template file.
     * @param rootMap    The data model of the template.
     * @param file       The output file for the content.
     */
    public static void printFile(String pathPrefix, String name,Map<String,Object> rootMap, File file) throws TemplateException, IOException{
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
        getTemplate(name, pathPrefix).process(rootMap, out); //将模板文件内容以UTF-8编码输出到相应的流中
        if(null != out){
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

}
