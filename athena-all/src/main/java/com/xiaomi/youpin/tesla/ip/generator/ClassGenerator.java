/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.tesla.ip.generator;

import com.xiaomi.youpin.tesla.ip.common.FileUtils;

import java.io.File;
import java.util.Map;

/**
 * @author goodjava@qq.com
 */
public class ClassGenerator {

    private String projectPath;
    private String projectName;
    private String packagePath;
    private String className;
    private final String templateName;
    private final String srcPath;


    public ClassGenerator(String projectPath, String projectName, String srcPath, String packagePath, String className, String templateName) {
        this.projectPath = projectPath;
        this.projectName = projectName;
        this.srcPath = srcPath;
        this.packagePath = packagePath;
        this.className = className;
        this.templateName = templateName;
    }

    public void generator(Map<String, Object> m) {
        String template = FileUtils.getTemplate(templateName);
        String str = FileUtils.renderTemplate(template, m);
        FileUtils.writeFile(projectPath + File.separator + projectName + File.separator + srcPath + File.separator + packagePath
                + File.separator
                + className + ".java", str);
    }

}
