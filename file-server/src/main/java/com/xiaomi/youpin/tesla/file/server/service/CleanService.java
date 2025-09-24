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

package com.xiaomi.youpin.tesla.file.server.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;

/**
 * @author 丁海洋
 */
@Slf4j
public class CleanService implements Runnable {


    private int cleanNum;

    private final String path;

    public CleanService(String path, int cleanNum) {
        this.path = path;
        this.cleanNum = cleanNum;
        log.info("path:{} cleanNum:{}", this.path, this.cleanNum);
    }


    //appName是jar包前缀
    public void cleanJar(String path) {
        try {
            if (!StringUtils.isEmpty(path)) {
                Map<String, ArrayList<File>> appNames = this.listAllFiles(path);
                if (appNames == null || appNames.size() == 0) {
                    return;
                }

                appNames.entrySet().stream().forEach(it -> {
                    if (it.getValue().size() > cleanNum) {
                        it.getValue().subList(cleanNum, it.getValue().size()).stream().forEach(it2 -> {
                                    try {
                                        FileUtils.forceDelete(it2);
                                    } catch (Exception e) {
                                        log.error("CleanService:cleanJar error, {}", e.getMessage());
                                    }
                                }
                        );
                    }
                });
            }
        } catch (Exception e) {
            log.error("CleanService:cleanJar, error: {}", e.getMessage());
        }

    }

    public Map<String, ArrayList<File>> listAllFiles(String dirName) {
        //如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dirName.endsWith(File.separator)) {
            dirName = dirName + File.separator;
        }
        File dirFile = new File(dirName);
        //如果dir对应的文件不存在，或者不是一个文件夹则退出
        if (!dirFile.exists() || (!dirFile.isDirectory())) {
            return null;
        }

        File[] files = dirFile.listFiles();
        //按时间递减排序
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                long diff = f1.lastModified() - f2.lastModified();
                if (diff > 0) {
                    return -1;
                } else if (diff == 0) {
                    return 0;
                } else {
                    return 1;
                }
            }

            public boolean equals(Object obj) {
                return true;
            }
        });


        Map<String, ArrayList<File>> res = new HashMap<>();
        Arrays.asList(files).stream().filter(it -> it.isFile()).forEach(it1 -> {
            String name = it1.getName().split("-20")[0];
            if (res.get(name) == null) {
                res.put(name, new ArrayList<>(Arrays.asList(it1)));
            } else {
                res.get(name).add(it1);
            }
        });

        return res;
    }

    @Override
    public void run() {
        try {
            cleanJar(this.path);
        } catch (Throwable ex) {
            log.warn(ex.getMessage());
        }
    }
}
