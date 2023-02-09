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

package com.xiaomi.youpin.tesla.agent.common;

import java.io.*;

/**
 * @author dingpei
 */
public class FileUtils {

    /**
     * 覆盖写文件
     *
     * @param path
     * @param fileName
     * @param content
     */
    public static void writeFile(String path, String fileName, String content) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }

        // 如果文件不存在则创建
        File filename = new File(path + fileName);
        if (!filename.exists()) {
            try {
                filename.createNewFile();// 不存在直接创建
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 写入内容
        writeToBuffer(content, filename);
    }

    private static void writeToBuffer(String content, File filename) {
        try {
            FileWriter fw = new FileWriter(filename.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(String path, String fileName) {
        try {
            // 可为文本相对路劲，可以文件夹绝对路径
            String file = path + fileName;
            // StringBuffer用来接收解析完了之后的文本内容
            StringBuffer sb = new StringBuffer();
            // 自定义函数读文本 返回一个StringBuffer
            readToBuffer(sb, file);
            // StringBuffer转为String显示
            return sb.toString();
        } catch (IOException e) {
            // ignore
            return "";
        }
    }

    private static void readToBuffer(StringBuffer buffer, String filePath) throws IOException {
        InputStream is = new FileInputStream(filePath);
        String line; // 用来保存每行读取的内容
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine();
        while (line != null) {
            buffer.append(line);
            buffer.append("\n");
            line = reader.readLine();
        }
        reader.close();
        is.close();
    }


}
