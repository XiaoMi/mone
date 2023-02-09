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

    public static boolean isArch64() {
        String osArch = System.getProperty("os.arch","");
        return osArch.contains("arch64");
    }


    public static void sleep(long timeout) {
        SafeRun.run(() -> TimeUnit.SECONDS.sleep(timeout));
    }


}
