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

package run.mone.m78.ip.generator;

import run.mone.m78.ip.common.FileUtils;

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
