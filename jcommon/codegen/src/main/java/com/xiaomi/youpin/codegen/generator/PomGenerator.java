package com.xiaomi.youpin.codegen.generator;



import com.xiaomi.youpin.codegen.common.FileUtils;

import java.io.File;
import java.util.Map;

/**
 * @author goodjava@qq.com
 */
public class PomGenerator {

    private String projectPath;
    private String projectName;
    private final String tmlName;

    public PomGenerator(String projectPath, String projectName, String tmlName) {
        this.projectPath = projectPath;
        this.projectName = projectName;
        this.tmlName = tmlName;
    }

    public void generator(Map<String, Object> m) {
        String template = FileUtils.getTemplate(tmlName);
        String pomStr = FileUtils.renderTemplate(template, m);
        FileUtils.writeFile(projectPath + File.separator + projectName + File.separator + "pom.xml", pomStr);
    }

}
