package com.xiaomi.youpin.docean.common;

/**
 * @author goodjava@qq.com
 * @date 2022/5/7
 */
public class FileUtils {


    public static String tmp() {
        return System.getProperty("java.io.tmpdir");
    }

    public static String home() {
        return System.getProperty("user.home");
    }

}
