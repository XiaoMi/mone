package com.xiaomi.data.push.common;

import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 1/4/21
 */
public class CommonUtils {

    public static String osName() {
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Mac OS")) {
            return "mac";
        } else if (osName.startsWith("Windows")) {
            return "windows";
        } else {
            return "linux";
        }
    }


    public static boolean isMac() {
        return osName().equals("mac");
    }

    public static boolean isWindows() {
        return osName().equals("windows");
    }


    public static void sleep(long timeout) {
        SafeRun.run(() -> TimeUnit.SECONDS.sleep(timeout));
    }


}
