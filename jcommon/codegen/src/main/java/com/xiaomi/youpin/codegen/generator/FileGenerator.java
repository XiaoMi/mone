package com.xiaomi.youpin.codegen.generator;



import com.xiaomi.youpin.codegen.common.FileUtils;

import java.io.File;
import java.util.Map;

/**
 * @author goodjava@qq.com
 */
public class FileGenerator {

    private String projectPath;
    private String projectName;
    private String path;
    private final String templateName;


    public FileGenerator(String projectPath, String projectName, String path, String templateName) {
        this.projectPath = projectPath;
        this.projectName = projectName;
        this.path = path;
        this.templateName = templateName;
    }

    public void generator(Map<String, Object> m) {
        String template = FileUtils.getTemplate(templateName);
        String str = FileUtils.renderTemplate(template, m);
        FileUtils.writeFile(projectPath + File.separator + projectName + File.separator + path, str);
    }

}
