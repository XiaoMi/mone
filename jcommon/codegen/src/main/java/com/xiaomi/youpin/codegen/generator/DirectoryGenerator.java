package com.xiaomi.youpin.codegen.generator;



import com.xiaomi.youpin.codegen.common.FileUtils;

import java.io.File;

/**
 * @author goodjava@qq.com
 */
public class DirectoryGenerator {

    private String projectPath;
    private String projectName;
    private String path;


    public DirectoryGenerator(String projectPath, String projectName, String path) {
        this.projectPath = projectPath;
        this.projectName = projectName;
        this.path = path;
    }

    public void generator() {
        FileUtils.createDirectories(projectPath + File.separator + projectName + File.separator + path);
    }

}
