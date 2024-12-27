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

package com.xiaomi.youpin.tesla.ip.util;

import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/11 13:53
 */
public class FileUtils {

    private static String serverUrl = "http://127.0.0.1:9999";

    public static void upload(String name, String path) throws IOException {
        System.out.println("upload:" + name);
        String token = "";
        File file = new File(path);
    }


    public static void download(String name, String path) {
        System.out.println("download:" + name);
        File file = new File(path + name);
        String token = "";
    }

    @SneakyThrows
    public static void writeConfig(String content, String fileName) {
        String userHome = System.getProperty("user.home");
        String path = System.getProperty("user.home") + File.separator + ".athena.json";
        if (!new File(path).exists()) {
            com.google.common.io.Files.touch(new File(path));
        }
        Files.write(Paths.get(path), content.getBytes());
    }


}
