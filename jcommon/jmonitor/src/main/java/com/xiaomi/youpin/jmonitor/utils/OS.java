package com.xiaomi.youpin.jmonitor.utils;

/**
 * @author gaoyibo
 */

public class OS {
    private static String OS = System.getProperty("os.name").toLowerCase();

    public static OSEnum getOsName() {
        if (isWindows()) {
            return OSEnum.Win;
        } else if (isMac()) {
            return OSEnum.Mac;
        } else if (isUnix()) {
            return OSEnum.Unix;
        } else if (isSolaris()) {
            return OSEnum.Solaris;
        }

        return OSEnum.Others;
    }

    private static boolean isWindows() {
        return OS.contains("win");
    }

    private static boolean isMac() {
        return OS.contains("mac");
    }

    private static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0);
    }

    private static boolean isSolaris() {
        return OS.contains("sunos");
    }
}
