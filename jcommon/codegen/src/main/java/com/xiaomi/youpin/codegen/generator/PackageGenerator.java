package com.xiaomi.youpin.codegen.generator;


import com.xiaomi.youpin.codegen.common.FileUtils;

/**
 * @author goodjava@qq.com
 */
public class PackageGenerator {

    private String projectPath;
    private String projectName;
    private String packageName;

    public PackageGenerator(String projectPath, String projectName, String packageName) {
        this.projectPath = projectPath;
        this.projectName = projectName;
        this.packageName = packageName;
    }

    public void generator() {
        FileUtils.createDirectories(projectPath + projectName + "/src/main/java" + packageName);
    }

}
