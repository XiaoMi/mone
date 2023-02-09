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

package com.xiaomi.mione.ms.common;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Properties;

/**
 * @Author goodjava@qq.com
 * @Date 2021/4/16 10:12
 */
@Slf4j
public class FileUtils {


    /**
     * 拷贝jar包中的文件到指定位置，单个文件
     *
     * @param configName
     * @param pathPropertyName
     * @param targetPath
     * @throws IOException
     */
    public static void copyResourceFile(String configName, String pathPropertyName, String targetPath) throws IOException {
        Properties p = new Properties();
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(configName);
        p.load(is);
        String path = p.get(pathPropertyName).toString();
        InputStream is2 = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        File f = new File(targetPath);
        if (!f.exists()) {
            f.mkdirs();
        }
        Files.copy(is2, Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * 拷贝文件夹中的文件到指定位置，多个文件
     *
     * @param configName
     * @param sourcePathPropertyName
     * @param filesPropertyName
     * @param targetPath
     * @throws IOException
     */
    public static void copyResourceDir(String configName, String sourcePathPropertyName, String filesPropertyName, String targetPath) throws IOException {
        Properties p = new Properties();
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(configName);
        p.load(is);

        String sourcePath = p.get(sourcePathPropertyName).toString();
        String finalSourcePath = sourcePath.endsWith("/") ? sourcePath : sourcePath + "/";
        String finalTargetPath = targetPath.endsWith("/") ? targetPath : targetPath + "/";
        String files = p.get(filesPropertyName).toString();
        String[] fileNames = files.split(",");

        Arrays.stream(fileNames).forEach(it -> {
            try {
                InputStream is2 = Thread.currentThread().getContextClassLoader().getResourceAsStream(finalSourcePath + it);
                File f = new File(finalTargetPath + it);
                if (!f.getParentFile().exists()) {
                    f.getParentFile().mkdirs();
                }
                if (!f.exists()) {
                    f.createNewFile();
                }
                Files.copy(is2, Paths.get(finalTargetPath + it), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                log.error("FileUtils.copyResourceDir,finalSourcePath:{}, finalTargetPath:{}, fileName:{}, error msg:{}", finalSourcePath, finalTargetPath, it, e.getMessage(), e);
                //continue
            }
        });


    }

}