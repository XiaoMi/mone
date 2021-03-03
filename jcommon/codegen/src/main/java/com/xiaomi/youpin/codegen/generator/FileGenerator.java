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
