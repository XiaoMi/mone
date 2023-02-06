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

package com.xiaomi.youpin.gwdash.bo;

import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author tsingfu
 */
public class MachineLabels extends HashMap<String, String> {

    public static String Docker = "docker";

    public static String Physical = "physical";

    public static String Type = "type";

    public static String Cpu = "cpu";

    public static String UseCpu = "useCpu";

    public static String Mem = "mem";

    public static String UseMem = "useMem";

    public static String Ip = "ip";

    public static String Apps = "apps";

    public static String Utime = "utime";

    /**
     * 是否开启keycenter
     */
    public static String Keycenter = "keycenter";

    /**
     * 是否开启外网
     */
    public static String Outbound = "outbound";

    /**
     * 被占用的端口号
     */
    public static String Ports = "ports";


    public static String projectLabelKey(String projectName, String key) {
        return Stream.of(key, projectName).collect(Collectors.joining("_"));
    }


}
