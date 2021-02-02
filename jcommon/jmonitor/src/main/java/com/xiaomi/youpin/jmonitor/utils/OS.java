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
