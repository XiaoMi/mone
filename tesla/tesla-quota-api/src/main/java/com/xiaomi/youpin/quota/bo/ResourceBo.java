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

package com.xiaomi.youpin.quota.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author goodjava@qq.com
 */
@Data
public class ResourceBo implements Serializable {

    private String ip;

    private int cpu;

    private long price;

    private List<Integer> cpuCore;

    private long mem;

    private Set<Integer> ports;

    private String name;

    private String hostName;

    private Map<String,String> lables;

    private long bizId;

    private long projectId;

    /**
     * resource id
     */
    private int id;

    private String type;

    private int remainCpu;

    private List<Integer> systemCpu;

    private long systemMem;

    private long remainMem;

    private List<Integer> systemPorts;

    private List<String> owners;

    private int level;

    private int isOneApp;

    private double loadAverage;

    private int supportKeyCenter;

    private int status;

    private int rorder;

    private long utime;

    private Map<Long, BizResource> bizIds;


}
