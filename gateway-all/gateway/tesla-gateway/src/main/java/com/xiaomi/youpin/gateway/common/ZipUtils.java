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

package com.xiaomi.youpin.gateway.common;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public abstract class ZipUtils {


    public static String readFile(String path, String name) {
        try {
            ZipFile zf = new ZipFile(path);
            InputStream in = new BufferedInputStream(new FileInputStream(path));
            Charset gbk = Charset.forName("utf8");
            ZipInputStream zin = new ZipInputStream(in, gbk);
            ZipEntry ze;
            StringBuilder sb = new StringBuilder();
            while ((ze = zin.getNextEntry()) != null) {
                if (ze.toString().equals(name)) {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(zf.getInputStream(ze)));
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append(System.lineSeparator());
                    }
                    br.close();
                }
            }
            zin.closeEntry();
            return sb.toString();
        } catch (Throwable ex) {
            log.error(ex.getMessage());
        }
        return "";
    }

}
