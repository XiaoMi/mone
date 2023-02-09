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

package com.xiaomi.youpin.tesla.agent.po;


import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author goodjava@qq.com
 */
@Data
public class PingReq {

    private String message;

    private String ip;

    private String hostName;

    private String uptime;

    private String containerUptime;

    private int port;

    private long time;

    /**
     * cpu 数量
     */
    private int cpu;

    /**
     * 内存大小
     */
    private long mem;


    /**
     * 被使用的cpu数量
     */
    private int useCpu;


    /**
     * 被使用的内存大小
     */
    private long useMem;

    /**
     * 是否是docker机器
     */
    private boolean docker;

    private Set<String> apps;

    /**
     * 被占用的端口号
     */
    private Set<Integer> ports;


    /**
     * 附加参数
     */
    private Map<String,String> attachments;


    /**
     * sre打上的标签
     */
    private SreLabel sreLabel;


    private List<PingAppInfo> appInfos;

}
