package com.xiaomi.youpin.docean.mvc.html;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author goodjava@qq.com
 * @date 2024/2/24 14:10
 */
@Slf4j
public class Html {

    //读取指定html页面文件内容并返回
    public static String view(String file) {
        try {
            Path path = Paths.get(file);
            return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
            return "";
        }
    }

    //判断一个文件是否是.html文件,并且返回boolean值(class)
    public static boolean isHtmlFile(String filePath) {
        return filePath != null && filePath.toLowerCase().endsWith(".html");
    }

}
